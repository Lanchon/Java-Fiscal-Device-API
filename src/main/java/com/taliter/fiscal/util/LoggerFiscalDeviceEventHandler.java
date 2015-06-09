package com.taliter.fiscal.util;

import java.io.*;

import com.taliter.fiscal.device.*;

/** A wrapper FiscalDeviceEventHandler that logs events.
The wrapped event handler is optional (may be null). */
public class LoggerFiscalDeviceEventHandler implements FiscalDeviceEventHandler
{
	private final FiscalDeviceEventHandler eventHandler;
	protected final PrintWriter log;

	public LoggerFiscalDeviceEventHandler(PrintWriter log) { this(null, log); }
	public LoggerFiscalDeviceEventHandler(FiscalDeviceEventHandler eventHandler, PrintWriter log)
	{
		this.eventHandler = eventHandler;
		this.log = log;
	}

	public LoggerFiscalDeviceEventHandler(PrintStream log) { this(null, log); }
	public LoggerFiscalDeviceEventHandler(FiscalDeviceEventHandler eventHandler, PrintStream log)
	{
		this.eventHandler = eventHandler;
		this.log = new PrintWriter(log, true);
	}

	/** Get the underlying event handler. */
	public FiscalDeviceEventHandler getFiscalDeviceEventHandler() { return eventHandler; }

	public boolean onTimeout(FiscalDevice source, FiscalPacket request)
	{
		log.println("event: onTimeout (request: " + request + ")");
		return eventHandler != null ? eventHandler.onTimeout(source, request) : false;
	}

	public void onStatus(FiscalDevice source, FiscalPacket request, int status)
	{
		String s;
		switch (status)
		{
			case STATUS_NORMAL:
				s = "normal";
				break;
			case STATUS_WORKING:
				s = "working";
				break;
			case STATUS_PAPER_OUT:
				s = "paper out";
				break;
			default:
				s = String.valueOf(status);
		}
		log.println("event: onStatus: " + s + " (request: " + request + ")");
		if (eventHandler != null) eventHandler.onStatus(source, request, status);
	}

	public boolean onExtendedStatus(FiscalDevice source, FiscalPacket request, FiscalPacket status)
	{
		log.println("event: onExtendedStatus: " + status + " (request: " + request + ")");
		return eventHandler != null ? eventHandler.onExtendedStatus(source, request, status) : true;
	}

	public void onExecute(FiscalDevice source, FiscalPacket request, FiscalPacket response)
	{
		log.println("event: onExecute (request: " + request + ", response: " + response + ")");
		if (eventHandler != null) eventHandler.onExecute(source, request, response);
	}
}
