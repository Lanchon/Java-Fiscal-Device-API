package com.taliter.fiscal.util;

import java.io.*;

import com.taliter.fiscal.port.*;

/** A LoggerFiscalPort factory. */
public class LoggerFiscalPortSource implements FiscalPortSource, Cloneable
{
	private FiscalPortSource portSource;
	private transient PrintWriter printWriter;
	private transient PrintStream printStream;

	public LoggerFiscalPortSource() {}

	public LoggerFiscalPortSource(FiscalPortSource portSource)
	{
		this.portSource = portSource;
	}

	public LoggerFiscalPortSource(FiscalPortSource portSource, PrintWriter printWriter)
	{
		this.portSource = portSource;
		this.printWriter = printWriter;
	}

	public LoggerFiscalPortSource(FiscalPortSource portSource, PrintStream printStream)
	{
		this.portSource = portSource;
		this.printStream = printStream;
	}

	public Object clone()
	{
		try { return super.clone(); }
		catch (CloneNotSupportedException e) { throw new Error(e.toString()); }
	}

	/** Set the underlying FiscalPortSource. */
	public void setPortSource(FiscalPortSource portSource) { this.portSource = portSource; }
	/** Get the underlying FiscalPortSource. */
	public FiscalPortSource getPortSource() { return portSource; }

	/** Set the logging PrintWriter. */
	public void setPrintWriter(PrintWriter printWriter) { this.printWriter = printWriter; printStream = null; }
	/** Get the logging PrintWriter. */
	public PrintWriter getPrintWriter() { return printWriter; }

	/** Set the logging PrintStream. */
	public void setPrintStream(PrintStream printStream) { this.printStream = printStream; printWriter = null; }
	/** Get the logging PrintStream. */
	public PrintStream getPrintStream() { return printStream; }

	/** Create a LoggerFiscalPort object. */
	public FiscalPort getFiscalPort() throws Exception { return getLoggerFiscalPort(); }

	/** Create a LoggerFiscalPort object. */
	public LoggerFiscalPort getLoggerFiscalPort() throws Exception
	{
		if (printWriter != null) return new LoggerFiscalPort(portSource.getFiscalPort(), printWriter);
		else if (printStream != null) return new LoggerFiscalPort(portSource.getFiscalPort(), printStream);
		else throw new NullPointerException("Undefined log printer");
	}
}
