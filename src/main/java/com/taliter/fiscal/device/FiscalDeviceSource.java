package com.taliter.fiscal.device;

import java.io.*;

import com.taliter.fiscal.port.*;

/** A FiscalDevice factory. */
public interface FiscalDeviceSource extends Serializable
{
	/** Returns a deep copy of this device source. */
	public Object clone();

	/** Set the FiscalPort factory.
	@throws UnsupportedOperationException if this type of device does not communicate over a FiscalPort. */
	public void setPortSource(FiscalPortSource portSource) throws UnsupportedOperationException;
	/** Get the FiscalPort factory.
	@throws UnsupportedOperationException if this type of device does not communicate over a FiscalPort. */
	public FiscalPortSource getPortSource() throws UnsupportedOperationException;

	/** Create a FiscalDevice object. */
	public FiscalDevice getFiscalDevice() throws Exception;
}
