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
package com.taliter.fiscal.device.icl;

import com.taliter.fiscal.device.FiscalDevice;
import com.taliter.fiscal.device.FiscalDeviceEventHandler;
import com.taliter.fiscal.device.FiscalDeviceTimeoutException;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.port.FiscalPort;
import com.taliter.fiscal.port.FiscalPortTimeoutException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * BasicFiscalDevice inherits FiscalDevice interface. This implementation provides
 * the basic fiscal device operations as open, close, synchronize and so on.
 */
public class BasicFiscalDevice implements FiscalDevice {

    /** The first byte of fiscal packet*/
    private static final int ASCII_STX = 0x01;
    
    /** The first byte of fiscal packet*/
    private static final int ASCII_ETX = 0x03;
    
    /** Specifies the end of the data in the packet from the fiscal device response*/
    private static final int ASCII_POST4 = 0x04;
    
    /** Specifies the end of the status in the packet from the fiscal device response*/
    private static final int ASCII_POST5 = 0x05;

    /** Error code*/
    private static final int ASCII_NAK = 0x15;
    
    /** Code for execution of the fiscal device*/
    private static final int ASCII_SYN = 0x16;
    
    /** The minimum value of the fiscal packet serial number*/
    private static final int SN_MIN = 0x20;
    
    /** The maximum value of the fiscal packet serial number*/
    private static final int SN_MAX = 0x7F;
    
    /** The step of the serial number*/
    private static final int SN_STEP = 1;
    
    /** The serial number range*/
    private static final int SN_RANGE = SN_MAX - SN_MIN + 1;

    /** Fiscal port*/
    private FiscalPort port;
    
    /** The fiscal device timeout*/
    private int timeout;
    
    /** The maximum tries for executing a command.*/
    private int maxTries;

    /** 2 min maximum command execution duration*/
    private int maxExecutionTime;
    
    /** Fiscal device encoding*/
    private String encoding;
    
    /** Fiscal device event handler.*/
    protected FiscalDeviceEventHandler eventHandler;

    /** Input stream for reading messages from the fiscal device*/
    private InputStream in;
    
    /** Output stream for sending messages to the fiscal device*/
    private OutputStream out;
    
    /** The fiscal packet serial number*/
    private int serialNumber;
    

    /**
     * Creates an instance of BasicFiscalDevice class.
     * @param port Fiscal device port.
     * @param timeout The time to execute the request. In milliseconds.
     * @param maxTries The number of tries to execute the request.
     * @param encoding The fiscal device encoding.
     */
    public BasicFiscalDevice(FiscalPort port, int timeout, int maxTries, String encoding) {
        if (port == null) throw new NullPointerException();
        if (maxTries < 1) {
            throw new IllegalArgumentException();
        }
        this.port = port;
        this.timeout = timeout;
        this.maxTries = maxTries;
        this.encoding = encoding;
        this.serialNumber = 0x20;
        this.maxExecutionTime = 2*60;
    }

    /**
     * Opens a connection with the fiscal device.
     * @throws Exception 
     */
    @Override
    public void open() throws Exception {
        if (in != null) {
            throw new IllegalStateException("Device open");
        }
        port.open();
        boolean success = false;
        try {
            port.setTimeout(timeout);
            in = new BufferedInputStream(port.getInputStream());
            out = port.getOutputStream();
            success = true;
        } finally {
            if (!success) {
                in = null;	// WARNING: Not closing streams.
                out = null;
                port.close();
            }
        }
    }

    /**
     * Close the connection with the fiscal device. 
     * Does nothing if the connection is already closed.
     * @throws Exception 
     */
    @Override
    public void close() throws Exception {
        if (in != null) {
            in = null;	// WARNING: Not closing streams.
            out = null;
            port.close();
        }
    }

    /**
     * Check whether there is an established connection.
     * @return Returns true if there is a connection or false if there isn't.
     */
    @Override
    public boolean isOpen() {
        return in != null;
    }

    /**
     * Tests and synchronizes the communication with the fiscal device. Done automatically when the connection is opened.
     * @throws IOException 
     */

    @Override
    public void synchronize() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Set the fiscal port to use. The device must not be open.
     * @param port The port which should be set.
     */
    public void setFiscalPort(FiscalPort port) {
        if (in != null) {
            throw new IllegalStateException("Device open");
        }
        if (port == null) throw new NullPointerException();
        this.port = port;
    }

    /**
     * Get the underlying fiscal port.
     * @return The port which fiscal device use.
     */
    public FiscalPort getFiscalPort() {
        return port;
    }

    /**
     * Set the protocol timeout. The device may be open.
     * @param timeout The fiscal device timeout (in milliseconds).
     * @throws java.lang.Exception
     */
    public void setTimeout(int timeout) throws Exception {
        if (in != null) {
            port.setTimeout(timeout);
        }
        this.timeout = timeout;
    }

    /**
     * Get the protocol timeout.
     * @return The fiscal device timeout.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Get the value of maxExecutionTime
     * @return the value of maxExecutionTime
     */
    public int getMaxExecutionTime() {
        return maxExecutionTime;
    }

    /**
     * Set the value of maxExecutionTime
     * @param maxExecutionTime new value of maxExecutionTime
     */
    public void setMaxExecutionTime(int maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    /**
     * Set the number of times requests are tried before timeouting.
     * @param maxTries The new value of fiscal device maxTries.
     */
    public void setMaxTries(int maxTries) {
        if (maxTries < 1) {
            throw new IllegalArgumentException();
        }
        this.maxTries = maxTries;
    }

    /**
     * Get the number of times requests are tried before timeouting.
     * @return The fiscal device maxTries.
     */
    public int getMaxTries() {
        return maxTries;
    }

    /**
     * Set the encoding to use for strings in newly created packets.
     * @param encoding The new encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get the encoding to use for strings in newly created packets.
     * @return The encoding used in the packets.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set an event handler of the fiscal device.
     * @param eventHandler The new event handler of the device.
     */
    @Override
    public void setEventHandler(FiscalDeviceEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Get the fiscal device event handler.
     * @return The fiscal device event handler.
     */
    @Override
    public FiscalDeviceEventHandler getEventHandler() {
        return eventHandler;
    }

    /**
     * Creates new fiscal packet.
     * @return The created fiscal packet.
     */
    @Override
    public FiscalPacket createFiscalPacket() {
        return new ICLFiscalPacket(encoding);
    }
    
    /**
     * Calculated the next serial number of the packet.
     * @return The calculated serial number.
     */
    private int nextSerialNumber() {
        serialNumber += SN_STEP;
        if (serialNumber > SN_MAX) {
            serialNumber -= SN_RANGE;
        }
        return serialNumber;
    }

    /**
     * Calculates the checksum and formats the fiscal packet to byte array in such way that would be understandable for 
     * the fiscal devices which use ICL protocol.
     * @param packet The packet which should be formated.
     * @return The fiscal packet as a byte array.
     * @throws IOException 
     */
    private byte[] formatPacket(FiscalPacket packet) throws IOException {
        int index = 0;
        
        int size = packet.getSize();
        int l = 9; // envelop bytes
        for (int i = 0; i < size; i++) l += packet.get(i).length;
        byte[] b = new byte[l];

        b[index++] = ASCII_STX;
        int csStartIndex = index;
        b[index++] = (byte) (0x20 + (l - 6));
        
        b[index++] = (byte) this.nextSerialNumber();
        
        //Get fields from packet(cmd and data)
        for (int i = 0; i < packet.getSize(); i++) {
            byte[] temp = packet.get(i);
            
            for (byte t : temp) {
                b[index++] = t;
            }
        }
        
        b[index++] = ASCII_POST5;

        int checkSum = 0;
        for (int i = csStartIndex; i < index; i++) {
            checkSum += (int)b[i] & 0xFF;
        }
        //Calculate 4 - byte BCC
        for (int i = 12; i >= 0; i -= 4) {
            if (i == 2)
                i = 0;
            
            b[index++] = (byte) (((checkSum >> i) & 0xF) + 0x30);
        }
        
        b[index++] = ASCII_ETX;

        return b;
    }
    
    /**
     * Parse the answer from the fiscal device and set values to requestToReport fiscal packet.
     * @param packet Requested packet which is send to the printer.
     * @param x The first byte read from the printer response.
     * @param requestToReport The response packet in which the information from the fiscal device is stored.
     * @return Returns the serial number of the received response.
     * @throws IOException 
     */
    private int receivePacket(FiscalPacket packet, int x, FiscalPacket requestToReport) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        long t = System.currentTimeMillis();
        int sn = 0;
        
        int cs = 0;

        receive:
        for (;;) {
            if (System.currentTimeMillis() - t >= timeout && timeout >= 0) {
                throw new FiscalPortTimeoutException();
            }
            
            if(x == ASCII_STX) {
                // LEN
                x = in.read();
                cs += x;

                // SEQ
                x = in.read();
                
                if(x < SN_MIN) {
                    sn = ASCII_SYN;
                    break receive;
                }
                
                cs += x;
                sn = x;

                x = in.read();
                cs += x;
            }
            
            if(x == ASCII_ETX) {
                break receive;
            }
            
            if(x == requestToReport.getCommandCode()) {
                // CMD
                int s = 0;
                packet.clear();
                b.reset();
                
                b.write(x);
                packet.set(s++, b.toByteArray());
                b.reset();
                
                // DATA
                for (;;) {
                    x = in.read();
                    cs += x;

                    if (x != ASCII_POST4) {
                        b.write(x);
                        continue;
                    }

                    packet.set(s++, b.toByteArray());
                    b.reset();
                    
                    //continue receive;
                    break;
                }
                
                // STATUS
                for (;;) {
                    x = in.read();
                    cs += x;
                    
                    if(x != ASCII_POST5) {
                        b.write(x);
                        continue;
                    }
                    
                    packet.set(s++, b.toByteArray());
                    b.reset();
                    
                    break;
                }
                
                // CHECKSUM
                cs &= 0xffff;	

                int rcs = 0;
                
                for (int i = 12; i >= 0; i -= 4) {
                    x = in.read();
                    
                    rcs |= (x -= 0x30) << i;
                }

                x = in.read();

                //compare checksums
                if(cs != rcs) {
                    //Returns NAK if checksums are not equal
                    sn = ASCII_NAK;
                    break receive;
                }
                
            }
        }
        
        return sn;
    }
    
    
    /**
     * Executes a fiscal request
     * @param request Fiscal request
     * @return Fiscal response.
     * @throws IOException 
     */
    @Override
    public FiscalPacket execute(FiscalPacket request) throws IOException {
        FiscalPacket response = createFiscalPacket();
        execute(request, response);
        return response;
    }

    /**
     * Execute fiscal request.
     * @param request Fiscal request
     * @param response Fiscal response
     * @throws IOException 
     */
    @Override
    public void execute(FiscalPacket request, FiscalPacket response) throws IOException {
        if (request == null) {
            throw new NullPointerException();
        }
        if (response == null) {
            throw new NullPointerException();
        }
        if (request == response) {
            throw new IllegalArgumentException();
        }
        basicExecute(request, response, request);
        onExecute(request, response);
    }
    
    /**
     * Executes the request to fiscal device.
     * @param request The request packet.
     * @param response The response packet.
     * @param requestToReport The requestToReport packet.
     * @throws IOException 
     */
    protected void basicExecute(FiscalPacket request, FiscalPacket response, FiscalPacket requestToReport) throws IOException {
        if (in == null) {
            throw new IllegalStateException("Device not open!");
        }
        
        byte[] requestBytes = formatPacket(request);
        int tries = 0;

        send:
        for (;;) {
            if (tries >= maxTries) // Should be a while if maxTries could be less than 1.
            {

                if (!onTimeout(requestToReport)) {
                    throw new FiscalDeviceTimeoutException(requestToReport, null);
                }
                
                tries = 0;
            }
            tries++;            
            
            out.write(requestBytes);
            port.flushAndWait();
            

            // calc number of syn packet on every 60ms
            long synLimit = maxExecutionTime * 1000 / 60;


            int x;
            // wait to complete the operation from slave
            do {
                x = in.read();
                synLimit--;

                
                if ((synLimit < 0) && (x == ASCII_SYN))
                    throw new FiscalDeviceTimeoutException(requestToReport, null);

            } while (x == ASCII_SYN); // && !timeout for excution of command
            
            // resend the packet
            if(x == ASCII_NAK) {
                continue send;
            }
            
            if(x != ASCII_STX) {
                continue send;
            }
            // returns the sn of received packet
            x = receivePacket(response, x, requestToReport);
            
            
            //compare sn of receiced packet and sn of requested packet
            if(x == requestBytes[2]) {
                break send;
            }
        }
    }
	// Empty Handlers


    /**
     * onTimeout() event dispatcher.
     * @param request The request fiscal packet.
     * @return Returns true if the event handler is set.
     */
    protected boolean onTimeout(FiscalPacket request) {
        return eventHandler != null ? eventHandler.onTimeout(this, request) : false;
    }	// Retry if true.

    /**
     * onStatus() event dispatcher. Invoked periodically during long operations while waiting for a response.
     * @param request Fiscal packet request.
     * @param status STATUS_NORMAL.
     */
    protected void onStatus(FiscalPacket request, int status) {
        if (eventHandler != null) {
            eventHandler.onStatus(this, request, status);
        }
    }

    /**
     * onExecute() event dispatcher. Invoked after every successful request execution.
     * @param request The request fiscal packet.
     * @param response The fiscal device's response.
     */
    protected void onExecute(FiscalPacket request, FiscalPacket response) {
        if (eventHandler != null) {
            eventHandler.onExecute(this, request, response);
        }
    }
}
