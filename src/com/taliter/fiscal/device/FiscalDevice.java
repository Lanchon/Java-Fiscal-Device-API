package com.taliter.fiscal.device;

import java.io.*;

/** An interface used to communicate with fiscal devices. */
public interface FiscalDevice
{
	/** Open the device. */
	public void open() throws Exception;
	/** Close the device. Does nothing if already closed. */
	public void close() throws Exception;

	/** Returns true if the device is open. */
	public boolean isOpen();

	/** Test and synchronize communication with the device. Done automatically after open(). */
	public void synchronize() throws IOException;

	/** Set the event handler. */
	public void setEventHandler(FiscalDeviceEventHandler eventHandler);
	/** Get the event handler. */
	public FiscalDeviceEventHandler getEventHandler();

	/** Create an empty packet. */
	public FiscalPacket createFiscalPacket();

	/** Execute a fiscal request. */
	public FiscalPacket execute(FiscalPacket request) throws IOException;
	/** Execute a fiscal request.
	@throws IllegalArgumentException if request == response. */
	public void execute(FiscalPacket request, FiscalPacket response) throws IOException;
}
