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

import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.port.FiscalPort;
import com.taliter.fiscal.port.FiscalPortSource;

/**
 * The implementation of FiscalDeviceSource interface provides the basic methods for setting the properties of Daisy fiscal device.
 */
public class DaisyFiscalDeviceSource implements FiscalDeviceSource
{
    
        /** Fiscal port source object*/
	private FiscalPortSource portSource;
        
        /** Daisy fiscal device timeout in milliseconds*/
	private int timeout = 3000;
        
        /** Daisy fiscal device maximum tries for executing a command*/
	private int maxTries = 3;
        
        /** Daisy fiscal device encoding*/
	private String encoding = "Windows-1251";

        /**
         * Create an instance of DaisyFiscalDeviceSource
         */
	public DaisyFiscalDeviceSource() {}
        
        /**
         * Create an instance of DaisyFiscalDeviceSource.
         * @param portSource The Daisy fiscal device's port.
         */
	public DaisyFiscalDeviceSource(FiscalPortSource portSource) { this.portSource = portSource; }

	/** 
         * Creates a deep copy of this device source. If set, the port source is cloned too. 
         * @return A copy of this instance.
         */
        @Override
	public Object clone()
	{
		DaisyFiscalDeviceSource ds;
		try { ds = (DaisyFiscalDeviceSource) super.clone(); }
		catch (CloneNotSupportedException e) { throw new Error(e.toString()); }
		if (ds.portSource != null) ds.portSource = (FiscalPortSource) ds.portSource.clone();
		return ds;
	}
        
        /**
         * Set the fiscal device's port source.
         * @param portSource Port source to set.
         */
        @Override
	public void setPortSource(FiscalPortSource portSource) { this.portSource = portSource; }

        /**
         * Get the Fiscal device's port source.
         * @return Returns the fiscal device's port source.
         */
        @Override
	public FiscalPortSource getPortSource() { return portSource; }

	/** 
         * Set the protocol timeout. 
         * @param timeout The timeout to set.
         */
	public void setTimeout(int timeout) { this. timeout= timeout; }
        
	/**
         * Get the protocol's timeout. 
         * @return Return the protocol's timeout.
         */
	public int getTimeout() { return timeout; }

	/** 
         * Set the number of times requests are tried before timeouting. 
         * @param maxTries The maximum number of tries.
         */
	public void setMaxTries(int maxTries) { this.maxTries = maxTries; }
        
	/** 
         * Get the number of times requests are tried before timeouting. 
         * @return The max number of tries.
         */
	public int getMaxTries() { return maxTries; }

	/** 
         * Set the encoding to use for strings in packets. 
         * @param encoding The encoding to set.
         */
	public void setEncoding(String encoding) { this.encoding = encoding; }
        
	/** 
         * Get the encoding to use in packets. 
         * @return The fiscal device encoding as a string.
         */
	public String getEncoding() { return encoding; }

	/** 
         * Create a Daisy FiscalDevice object. Uses the configured port source, if any. 
         * @return A Daisy fiscal device object.
         * @throws Exception
         */
        @Override
	public DaisyFiscalDevice getFiscalDevice() throws Exception { return getDaisyFiscalDevice(); }

	/** 
         * Create a DaisyFiscalDevice object. Uses the configured port source, if any. 
         * @return Daisy fiscal device object.
         * @throws Exception
         */
	public DaisyFiscalDevice getDaisyFiscalDevice() throws Exception
	{
		return new DaisyFiscalDevice(portSource != null ? portSource.getFiscalPort() : null, timeout, maxTries, encoding);
	}

	/** 
         * Create a DaisyFiscalDevice object. Uses the specified port, if any. 
         * @param port Fiscal port.
         * @return Daisy fiscal device object.
         */
	public DaisyFiscalDevice getDaisyFiscalDevice(FiscalPort port)
	{
		return new DaisyFiscalDevice(port, timeout, maxTries, encoding);
	}
}