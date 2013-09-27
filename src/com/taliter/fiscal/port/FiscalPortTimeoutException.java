package com.taliter.fiscal.port;

import java.io.*;

/** Thrown to signal a receive timeout event. */
public class FiscalPortTimeoutException extends IOException
{
	public FiscalPortTimeoutException() {}
	public FiscalPortTimeoutException(String s) { super(s); }
}
