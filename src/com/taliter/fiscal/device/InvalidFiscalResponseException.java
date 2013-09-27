package com.taliter.fiscal.device;

/** Thrown when an invalid response is received.
A response is identified as invalid when its command code does not match that of the request,
and neither the request nor the response is a STATPRN packet. */
public class InvalidFiscalResponseException extends FiscalDeviceIOException
{
	public InvalidFiscalResponseException() {}
	public InvalidFiscalResponseException(String s) { super(s); }
	public InvalidFiscalResponseException(FiscalPacket request, FiscalPacket response) { super(request, response); }
	public InvalidFiscalResponseException(String s, FiscalPacket request, FiscalPacket response) { super(s, request, response); }
}
