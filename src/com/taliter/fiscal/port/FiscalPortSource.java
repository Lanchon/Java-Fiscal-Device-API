package com.taliter.fiscal.port;

import java.io.*;

/** A FiscalPort factory. */
public interface FiscalPortSource extends Serializable
{
	/** Returns a deep copy of this port source. */
	public Object clone();

	/** Create a FiscalPort object. */
	public FiscalPort getFiscalPort() throws Exception;
}
