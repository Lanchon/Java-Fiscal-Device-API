package com.taliter.fiscal.device;

/** Thrown when a request is aborted. 
Requests are aborted when a STATPRN response is received, the FiscalDevice is configured to handle such responses
by triggering the FiscalDeviceEventHandler.onExtendedStatus() event, and the event returns false. */
public class AbortedFiscalRequestException extends FiscalDeviceIOException
{
	public AbortedFiscalRequestException() {}
	public AbortedFiscalRequestException(String s) { super(s); }
	public AbortedFiscalRequestException(FiscalPacket request, FiscalPacket response) { super(request, response); }
	public AbortedFiscalRequestException(String s, FiscalPacket request, FiscalPacket response) { super(s, request, response); }
}
