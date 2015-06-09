package com.taliter.fiscal.port.serial;

import javax.comm.*;

import com.taliter.fiscal.port.*;

/**
A SerialFiscalPort factory.

<p>Most of the parameters of this class are defined in the <code>javax.comm</code> package.

<p>Defaults:

<p><code>portName = null;<br>
appName = "SerialFiscalPort";<br>
openTimeout = 2000;	// In milliseconds.<br>
baudRate = 9600;<br>
dataBits = SerialPort.DATABITS_8;<br>
stopBits = SerialPort.STOPBITS_1;<br>
parity = SerialPort.PARITY_NONE;<br>
flowControl = SerialPort.FLOWCONTROL_NONE;</code>

<p>The set of available serial port names can be obtained via <code>SerialFiscalPort.getPortNames()</code>.
*/
public class SerialFiscalPortSource implements FiscalPortSource, Cloneable
{
	private String portName;
	private String appName = "SerialFiscalPort";
	private int openTimeout = 2000;	// In milliseconds.
	private int baudRate = 9600;
	private int dataBits = SerialPort.DATABITS_8;
	private int stopBits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;
	private int flowControl = SerialPort.FLOWCONTROL_NONE;

	public SerialFiscalPortSource() {}
	public SerialFiscalPortSource(String portName) { this.portName = portName; }

	public Object clone()
	{
		try { return super.clone(); }
		catch (CloneNotSupportedException e) { throw new Error(e.toString()); }
	}

	/** Set the name of the serial port to use. */
	public void setPortName(String portName) { this.portName = portName; }
	/** Get the name of the serial port to use. */
	public String getPortName() { return portName; }

	/** Set the name to report as the requesting application for serial port contention negotiation during open(). */
	public void setAppName(String appName) { this.appName = appName; }
	/** Get the name to report as the requesting application for serial port contention negotiation during open(). */
	public String getAppName() { return appName; }

	/** Set the timeout for serial port contention negotiation during open(). */
	public void setOpenTimeout(int openTimeout) { this.openTimeout = openTimeout; }
	/** Get the timeout for serial port contention negotiation during open(). */
	public int getOpenTimeout() { return openTimeout; }

	/** Set the baud rate. */
	public void setBaudRate(int baudRate) { this.baudRate = baudRate; }
	/** Get the baud rate. */
	public int getBaudRate() { return baudRate; }

	/** Set the number of data bits. */
	public void setDataBits(int dataBits) { this.dataBits = dataBits; }
	/** Get the number of data bits. */
	public int getDataBits() { return dataBits; }

	/** Set the number of stop bits. */
	public void setStopBits(int stopBits) { this.stopBits = stopBits; }
	/** Get the number of stop bits. */
	public int getStopBits() { return stopBits; }

	/** Set the type of parity if any. */
	public void setParity(int parity) { this.parity = parity; }
	/** Get the type of parity if any. */
	public int getParity() { return parity; }

	/** Set the type of flow control if any. */
	public void setFlowControl(int flowControl) { this.flowControl = flowControl; }
	/** Get the type of flow control if any. */
	public int getFlowControl() { return flowControl; }

	/** Create a SerialFiscalPort object. */
	public FiscalPort getFiscalPort() throws NoSuchPortException, UnsupportedCommOperationException { return getSerialFiscalPort(); }

	/** Create a SerialFiscalPort object. */
	public SerialFiscalPort getSerialFiscalPort() throws NoSuchPortException, UnsupportedCommOperationException
	{
		return new SerialFiscalPort(portName, appName, openTimeout, baudRate, dataBits, stopBits, parity, flowControl);
	}
}
