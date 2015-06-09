package com.taliter.fiscal.device;

import java.io.*;

/** A FiscalDevice factory. */
public interface FiscalDeviceSource extends Serializable
{
	/** Returns a deep copy of this device source. */
	public Object clone();

	/** Create a FiscalDevice object. */
	public FiscalDevice getFiscalDevice() throws Exception;
}
