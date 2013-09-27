package com.taliter.fiscal.port.serial;

import java.io.*;
import java.util.*;
import javax.comm.*;

import com.taliter.fiscal.port.*;

/** A FiscalPort implementation that uses the <code>javax.comm</code> API to communicate over serial ports. */
public class SerialFiscalPort implements FiscalPort
{
	// TODO: Handle these events and throw exceptions in read(): FramingError, OverrunError, ParityError
 	// TODO: Use the OutputEmpty event to wait in flushAndWait() (but see note below).

	/** Get an alphabetically sorted set of available serial port names. */

	public static SortedSet getPortNames()
	{
		SortedSet s = new TreeSet();
		Enumeration e = CommPortIdentifier.getPortIdentifiers();
		while (e.hasMoreElements())
		{
			CommPortIdentifier i = (CommPortIdentifier) e.nextElement();
			if (i.getPortType() == CommPortIdentifier.PORT_SERIAL) s.add(i.getName());
		}
		return s;
	}

	private final CommPortIdentifier portID;
	private final String appName;
	private final int openTimeout;
	private int baudRate;
	private final int dataBits;
	private final int stopBits;
	private final int parity;
	private final int flowControl;

	private SerialPort port;
	private InputStream in;
	private OutputStream out;

	public SerialFiscalPort(String portName, String appName, int openTimeout, int baudRate, int dataBits, int stopBits, int parity, int flowControl) throws NoSuchPortException, UnsupportedCommOperationException
	{
		portID = CommPortIdentifier.getPortIdentifier(portName);
		if (portID.getPortType() != CommPortIdentifier.PORT_SERIAL) throw new UnsupportedCommOperationException("Serial port expected");
		this.appName = appName;
		this.openTimeout = openTimeout;
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControl;
	}

	public void open() throws PortInUseException, UnsupportedCommOperationException, IOException
	{
		if (port != null) throw new IllegalStateException("Port open");
		CommPort commPort = portID.open(appName, openTimeout);
		boolean success = false;
		try
		{
			port = (SerialPort) commPort;
			port.setSerialPortParams(baudRate, dataBits, stopBits, parity);
			port.setFlowControlMode(flowControl);
			in = new FilterInputStream(port.getInputStream())
			{
				public int read() throws IOException
				{
					int i = this.in.read();
					if (i < 0) throw new FiscalPortTimeoutException();
					return i;
				}
				public int read(byte b[], int off, int len) throws IOException
				{
					int i = this.in.read(b, off, len);
					if (i <= 0) throw new FiscalPortTimeoutException();
					return i;
				}
			}
			;
			out = port.getOutputStream();
			success = true;
		}
		finally
		{
			if (!success)
			{
				in = null;	// WARNING: Not closing streams.
				out = null;
				port = null;
				commPort.close();
			}
		}
	}

	public void close() throws IOException
	{
		if (port != null)
		{
			try { flushAndWait(); }
			finally
			{
				SerialPort p = port;
				in = null;	// WARNING: Not closing streams.
				out = null;
				port = null;
				p.close();
			}
		}
	}

	public boolean isOpen() { return port != null; }

	/** Get the underlying serial port name. */
	public String getPortName() { return portID.getName(); }

	/** Get the underlying serial port or null if the port is not open. */
	public SerialPort getSerialPort() { return port; }

	/** Set the baud rate. The port may be open. */
	public void setBaudRate(int baudRate) throws UnsupportedCommOperationException
	{
		if (port != null) port.setSerialPortParams(baudRate, port.getDataBits(), port.getStopBits(), port.getParity());
		this.baudRate = baudRate;
	}

	/** Get the baud rate. */
	public int getBaudRate() { return baudRate; }

	public void setTimeout(int ms) throws UnsupportedCommOperationException
	{
		if (ms >= 0)
		{
			port.enableReceiveTimeout(ms);
			if (!port.isReceiveTimeoutEnabled()) throw new UnsupportedCommOperationException("enableReceiveTimeout()");
		}
		else
		{
			port.disableReceiveTimeout();
		}
	}

	public int getTimeout()
	{
		if (port.isReceiveTimeoutEnabled()) return port.getReceiveTimeout();
		else return -1;
	}

	public InputStream getInputStream() throws IOException { return in; }
	public OutputStream getOutputStream() throws IOException { return out; }

	// NOTE: The waiting should be based on the OutputEmpty event, but...
	// Sun's Java Communications API Version 2.0 implementation for Win32
	// does not return from the write method until all bytes are output,
	// so this complication is not necessary under this implementation.
	// In addition, it has a bug that generates spurious OutputEmpty
	// events making it unreliable to use these events. (However in this
	// case it wouldn't cause problems.)
	public void flushAndWait() throws IOException { out.flush(); }
}
