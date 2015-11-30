/*
 * Copyright (C) 2015 EDA Ltd.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 * 
 * @author nikolabintev@edabg.com
 */
package com.taliter.fiscal.device.daisy;

import com.taliter.fiscal.device.FiscalDeviceEventHandler;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.InvalidFiscalResponseException;
import com.taliter.fiscal.device.icl.BasicFiscalDevice;
import com.taliter.fiscal.port.FiscalPort;
import java.io.IOException;

/**
 * A BasicFiscalDevice implementation used to communicate with Daisy fiscal devices.
 */
public class DaisyFiscalDevice extends BasicFiscalDevice {

    /**
     * Creates an instance of DaisyFiscalDevice.
     * @param port The port in which the fiscal device is connected.
     * @param timeout The time to execute the request. In milliseconds.
     * @param maxTries The number of tries to execute the request.
     * @param encoding Fiscal device's encoding.
     */
    public DaisyFiscalDevice(FiscalPort port, int timeout, int maxTries, String encoding) {
        super(port, timeout, maxTries, encoding);
    }
    
    /**
     * Opens a connection with the daisy fiscal device.
     * @throws Exception 
     */
    @Override
    public void open() throws Exception {
        super.open();
        boolean success = false;
        try {
            synchronize();
            success = true;
        } finally {
            if (!success) {
                close();
            }
        }
    }
    /**
     * Tests and synchronizes the communication with the fiscal device. Done automatically when the connection is opened.
     * @throws IOException 
     */
    @Override
    public void synchronize() throws IOException {
        FiscalDeviceEventHandler h = eventHandler;
        eventHandler = null;	// Suppress event generation.
        try {
            
            FiscalPacket request = createFiscalPacket();
            request.setCommandCode(DaisyConstants.CMD_PRINTER_STATUS);
            FiscalPacket response = createFiscalPacket();
            basicExecute(request, response, request);	// The first try may hit the last used serial number
            basicExecute(request, response, request);	// and get its corresponding response.

            if (DaisyConstants.CMD_PRINTER_STATUS != response.getCommandCode()) {
                throw new InvalidFiscalResponseException(request, response);	// Will not tolerate STATPRN here.
           }

        } finally {
            eventHandler = h;
        }
    }
    /**
     * Executes the request sent to the fiscal device.
     * @param request The request which should be executed.
     * @param response The response that fiscal device returns.
     * @throws IOException 
     */
    @Override
    public void execute(FiscalPacket request, FiscalPacket response) throws IOException {
        super.execute(request, response);
    }

    /**
     * onExtendedStatus() event dispatcher.
     * @param request
     * @param status
     * @return 
     */
    protected boolean onExtendedStatus(FiscalPacket request, FiscalPacket status) // Abort if false.
    {
        // Make sure the event lasts at least for a timeout period if true is returned.
        boolean retry;
        long time;
        if (eventHandler != null) {
            long startTime = System.currentTimeMillis();
            retry = eventHandler.onExtendedStatus(this, request, status);
            if (retry) {
                time = System.currentTimeMillis() - startTime;
                int timeout = getTimeout();
                if (time < timeout) {
                    time = timeout - time;
                } else {
                    time = 0;
                }
            } else {
                time = 0;
            }
        } else {
            retry = true;
            time = getTimeout();
        }
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return retry;
    }
}
