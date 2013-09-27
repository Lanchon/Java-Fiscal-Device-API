package com.taliter.fiscal.device.hasar;

import com.taliter.fiscal.device.*;
import com.taliter.fiscal.port.*;

/**
A HasarFiscalDevice factory.

<p>Defaults:

<p><code>portSource = null;<br>
timeout = 1200;	// In milliseconds.<br>
extendedProtocol = false;<br>
handleExtendedProtocol = true;<br>
maxTries = 3;<br>
encoding = "Cp437";	// MS-DOS United States, Australia, New Zealand, South Africa.<br>
baseRolloverYear = 1997;</code>
*/
public class HasarFiscalDeviceSource implements FiscalDeviceSource
{
	private FiscalPortSource portSource;
	private int timeout = 1200;	// In milliseconds.
	private boolean extendedProtocol = false;
	private boolean handleExtendedProtocol = true;
	private int maxTries = 3;
	private String encoding = "Cp437";	// MS-DOS United States, Australia, New Zealand, South Africa.
	//private String encoding = "Cp850";	// MS-DOS Latin-1.
	//private String encoding = "ISO8859_1";	// ISO 8859-1, Latin alphabet No. 1.
	private int baseRolloverYear = 1997;

	public HasarFiscalDeviceSource() {}
	public HasarFiscalDeviceSource(FiscalPortSource portSource) { this.portSource = portSource; }

	/** Returns a deep copy of this device source. If set, the port source is cloned too. */
	public Object clone()
	{
		HasarFiscalDeviceSource ds;
		try { ds = (HasarFiscalDeviceSource) super.clone(); }
		catch (CloneNotSupportedException e) { throw new Error(e.toString()); }
		if (ds.portSource != null) ds.portSource = (FiscalPortSource) ds.portSource.clone();
		return ds;
	}

	/** Set the FiscalPort factory. */
	public void setPortSource(FiscalPortSource portSource) { this.portSource = portSource; }
	/** Get the FiscalPort factory. */
	public FiscalPortSource getPortSource() { return portSource; }

	/** Set the protocol timeout. */
	public void setTimeout(int timeout) { this. timeout= timeout; }
	/** Get the protocol timeout. */
	public int getTimeout() { return timeout; }

	/** True to use the extended (STATPRN) protocol. */
	public void setExtendedProtocol(boolean extendedProtocol) { this.extendedProtocol = extendedProtocol; }
	/** True to use the extended (STATPRN) protocol. */
	public boolean getExtendedProtocol() { return extendedProtocol; }

	/** True to handle STATPRN responses via the FiscalDeviceEventHandler.onExtendedStatus() event.
	False to return STATPRN responses from the FiscalDevice.execute() methods. */
	public void setHandleExtendedProtocol(boolean handleExtendedProtocol) { this.handleExtendedProtocol = handleExtendedProtocol; }
	/** True to handle STATPRN responses via the FiscalDeviceEventHandler.onExtendedStatus() event.
	False to return STATPRN responses from the FiscalDevice.execute() methods. */
	public boolean getHandleExtendedProtocol() { return handleExtendedProtocol; }

	/** Set the number of times requests are tried before timeouting. */
	public void setMaxTries(int maxTries) { this.maxTries = maxTries; }
	/** Get the number of times requests are tried before timeouting. */
	public int getMaxTries() { return maxTries; }

	/** Set the encoding to use for strings in packets. */
	public void setEncoding(String encoding) { this.encoding = encoding; }
	/** Get the encoding to use for strings in packets. */
	public String getEncoding() { return encoding; }

	/** Set the base roll-over year to use for dates in packets. Valid years are from baseRolloverYear to baseRolloverYear + 99 inclusive. */
	public void setBaseRolloverYear(int baseRolloverYear) { this.baseRolloverYear = baseRolloverYear; }
	/** Get the base roll-over year to use for dates in packets. Valid years are from baseRolloverYear to baseRolloverYear + 99 inclusive. */
	public int getBaseRolloverYear() { return baseRolloverYear; }

	/** Create a HasarFiscalDevice object. Uses the configured port source, if any. */
	public FiscalDevice getFiscalDevice() throws Exception { return getHasarFiscalDevice(); }

	/** Create a HasarFiscalDevice object. Uses the configured port source, if any. */
	public HasarFiscalDevice getHasarFiscalDevice() throws Exception
	{
		return new HasarFiscalDevice(portSource != null ? portSource.getFiscalPort() : null, timeout, extendedProtocol, handleExtendedProtocol, maxTries, encoding, baseRolloverYear);
	}

	/** Create a HasarFiscalDevice object. Uses the specified port, if any. */
	public HasarFiscalDevice getHasarFiscalDevice(FiscalPort port)
	{
		return new HasarFiscalDevice(port, timeout, extendedProtocol, handleExtendedProtocol, maxTries, encoding, baseRolloverYear);
	}
}
