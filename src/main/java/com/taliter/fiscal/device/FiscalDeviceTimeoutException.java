package com.taliter.fiscal.device;

/** Thrown when a request timeout occurs.
A request timeout occurs when a request is tried the maximum number of times and is not answered
causing the FiscalDeviceEventHandler.onTimeout() event to be triggered, and the event returns false. */
public class FiscalDeviceTimeoutException extends FiscalDeviceIOException
{
	public FiscalDeviceTimeoutException() {}
	public FiscalDeviceTimeoutException(String s) { super(s); }
	public FiscalDeviceTimeoutException(FiscalPacket request, FiscalPacket response) { super(request, response); }
	public FiscalDeviceTimeoutException(String s, FiscalPacket request, FiscalPacket response) { super(s, request, response); }
}
