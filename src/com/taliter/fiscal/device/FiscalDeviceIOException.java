package com.taliter.fiscal.device;

import java.io.*;

/** The base class of all fiscal device exceptions. */
public class FiscalDeviceIOException extends IOException
{
	private FiscalPacket request;
	private FiscalPacket response;

	public FiscalDeviceIOException() {}
	public FiscalDeviceIOException(String s) { super(s); }

	public FiscalDeviceIOException(FiscalPacket request, FiscalPacket response) { this(null, request, response); }
	public FiscalDeviceIOException(String s, FiscalPacket request, FiscalPacket response)
	{
		super(buildMessage(s, request, response));
		this.request = request;
		this.response = response;
	}

	private static String buildMessage(String s, FiscalPacket request, FiscalPacket response)
	{
		String d;
		if (request != null)
		{
			if (response != null) d = "request: " + request + ", response: " + response;
			else d = "request: " + request;
		}
		else
		{
			if (response != null) d = "response: " + response;
			else d = null;
		}
		if (d != null)
		{
			if (s != null) s += " (" + d + ")";
			else s = d;
		}
		return s;
	}

	/** Get the request associated with this exception, if any. */
	public FiscalPacket getRequestPacket() { return request; }
	/** Get the response associated with this exception, if any. */
	public FiscalPacket getResponsePacket() { return response; }
}
