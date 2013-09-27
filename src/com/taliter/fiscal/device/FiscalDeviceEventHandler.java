package com.taliter.fiscal.device;

/** A handler for events that can be triggered by a fiscal device. */
public interface FiscalDeviceEventHandler
{
	/** The device is ready or has issued a STATPRN response. */
	public int STATUS_NORMAL = 0x00;
	/** The device is executing a long operation. */
	public int STATUS_WORKING = 0x12;
	/** The device is out of paper. */
	public int STATUS_PAPER_OUT = 0x14;

	/** Invoked before throwing a FiscalDeviceTimeoutException.
	@return true to force a retry cycle. */
	public boolean onTimeout(FiscalDevice source, FiscalPacket request);
	/** Invoked periodically during long operations while waiting for a response.
	STATUS_NORMAL is always reported after such operations, and also before processing a STATPRN response. */
	public void onStatus(FiscalDevice source, FiscalPacket request, int status);
	/** Invoked when a STATPRN response is received and the FiscalDevice is configured to handle such responses.
	@return false to abort the request. */
	public boolean onExtendedStatus(FiscalDevice source, FiscalPacket request, FiscalPacket status);
	/** Invoked after every successful request execution. */
	public void onExecute(FiscalDevice source, FiscalPacket request, FiscalPacket response);
}
