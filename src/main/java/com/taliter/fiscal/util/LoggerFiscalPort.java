package com.taliter.fiscal.util;

import java.io.*;

import com.taliter.fiscal.port.*;

/** A wrapper FiscalPort that logs incomming and outgoing data. */
public class LoggerFiscalPort implements FiscalPort
{
	private final FiscalPort port;
	protected final PrintWriter log;
	private InputStream is;
	private OutputStream os;

	public LoggerFiscalPort(FiscalPort port, PrintWriter log)
	{
		this.port = port;
		this.log = log;
	}

	public LoggerFiscalPort(FiscalPort port, PrintStream log)
	{
		this.port = port;
		this.log = new PrintWriter(log, true);
	}

	/** Get the underlying fiscal port. */
	public FiscalPort getFiscalPort() { return port; }

	public void open() throws Exception
	{
		log.println("port: open");
		port.open();
	}

	public void close() throws Exception
	{
		log.println("port: close");
		is = null;
		os = null;
		port.close();
	}

	public boolean isOpen() { return port.isOpen(); }

	public void setTimeout(int ms) throws Exception
	{
		if (ms >= 0) log.println("port: setTimeout: " + ms + " ms");
		else log.println("port: setTimeout: disabled");
		port.setTimeout(ms);
	}

	public int getTimeout() throws Exception
	{
		return port.getTimeout();
	}

	public InputStream getInputStream() throws IOException
	{
		if (!port.isOpen()) throw new IllegalStateException("Port closed");
		if (is == null)
		{
			InputStream pis = port.getInputStream();
			if (pis == null) throw new NullPointerException();
			is = new FilterInputStream(pis)
			{
				public int read() throws IOException
				{
					byte[] b = new byte[1];
					int l = read(b, 0, 1);
					return l == 1 ? b[0] : -1;
				}
				public int read(byte[] b, int off, int len) throws IOException
				{
					int l;
					try { l = in.read(b, off, len); }
					catch (FiscalPortTimeoutException e)
					{
						log.println("port: read: timeout");
						throw e;
					}
					if (l > 0) log.println("port: read: " + ByteFormatter.toHexString(b, off, l));
					return l;
				}
			}
			;
		}
		return is;
	}

	public OutputStream getOutputStream() throws IOException
	{
		if (!port.isOpen()) throw new IllegalStateException("Port closed");
		if (os == null)
		{
			OutputStream pos = port.getOutputStream();
			if (pos == null) throw new NullPointerException();
			os = new FilterOutputStream(pos)
			{
				public void write(int b) throws IOException
				{
					write(new byte[] { (byte) b }, 0, 1);
				}
				public void write(byte[] b, int off, int len) throws IOException
				{
					if (len > 0) log.println("port: write: " + ByteFormatter.toHexString(b, off, len));
					out.write(b, off, len);
				}
				public void flush() throws IOException
				{
					log.println("port: flush");
					out.flush();
				}
				public void close() throws IOException
				{
					out.close();
				}
			}
			;
		}
		return os;
	}

	public void flushAndWait() throws IOException
	{
		log.println("port: flushAndWait");
		port.flushAndWait();
	}
}
