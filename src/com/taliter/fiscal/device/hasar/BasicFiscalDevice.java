package com.taliter.fiscal.device.hasar;

import java.io.*;

import com.taliter.fiscal.device.*;
import com.taliter.fiscal.port.*;

/** A basic FiscalDevice implementation that does not make assumptions about packet contents. */
public class BasicFiscalDevice implements FiscalDevice
{
	private static final boolean ALWAYS_ACKNOWLEDGE = false;
	private static final boolean REQUIRE_ACKNOWLEDGE = false;	// Must be false for Epson compatibility.
	private static final boolean MAX_TRIES_EXTENSION = true;

	private static final int ASCII_STX = 0x02;
	private static final int ASCII_ETX = 0x03;
	private static final int ASCII_ACK = 0x06;
	private static final int ASCII_DC1 = 0x11;	// XON
	private static final int ASCII_DC2 = 0x12;
	private static final int ASCII_DC3 = 0x13;	// XOFF
	private static final int ASCII_DC4 = 0x14;
	private static final int ASCII_NAK = 0x15;
	private static final int ASCII_ESC = 0x1B;
	private static final int ASCII_FS  = 0x1C;

	private static final int SN_MIN = 0x20;
	private static final int SN_MAX = 0x7F;
	private static final int SN_STEP = 1;
	private static final int SN_RANGE = SN_MAX - SN_MIN + 1;

	private FiscalPort port;
	private int timeout;
	private boolean extendedProtocol;
	private int maxTries;
	private String encoding;
	private int baseRolloverYear;
	protected FiscalDeviceEventHandler eventHandler;

	private InputStream in;
	private OutputStream out;
	private int serialNumber;
	private int receivedSerialNumber;
	private int receivedChecksum;

	public BasicFiscalDevice(FiscalPort port, int timeout, boolean extendedProtocol, int maxTries, String encoding, int baseRolloverYear)
	{
		//if (port == null) throw new NullPointerException();
		if (maxTries < 1) throw new IllegalArgumentException();
		if (baseRolloverYear < 0) throw new IllegalArgumentException();
		this.port = port;
		this.timeout = timeout;
		this.extendedProtocol = extendedProtocol;
		this.maxTries = maxTries;
		this.encoding = encoding;
		this.baseRolloverYear = baseRolloverYear;
		serialNumber = ((int) ((System.currentTimeMillis() & (-1L >>> 1)) % (SN_RANGE / SN_STEP))) * SN_STEP + SN_MIN;
	}

	public void open() throws Exception
	{
		if (in != null) throw new IllegalStateException("Device open");
		port.open();
		boolean success = false;
		try
		{
			port.setTimeout(timeout);
			in = new BufferedInputStream(port.getInputStream());
			out = port.getOutputStream();
			receivedSerialNumber = 0;
			success = true;
		}
		finally
		{
			if (!success)
			{
				in = null;	// WARNING: Not closing streams.
				out = null;
				port.close();
			}
		}
	}

	public void close() throws Exception
	{
		if (in != null)
		{
			in = null;	// WARNING: Not closing streams.
			out = null;
			port.close();
		}
	}

	public boolean isOpen() { return in != null; }

	public void synchronize() throws IOException { throw new UnsupportedOperationException(); }

	/** Set the fiscal port to use. The device must not be open. */
	public void setFiscalPort(FiscalPort port)
	{
		if (in != null) throw new IllegalStateException("Device open");
		//if (port == null) throw new NullPointerException();
		this.port = port;
	}

	/** Get the underlying fiscal port. */
	public FiscalPort getFiscalPort() { return port; }

	/** Set the protocol timeout. The device may be open. */
	public void setTimeout(int timeout) throws Exception
	{
		if (in != null) port.setTimeout(timeout);
		this.timeout = timeout;
	}

	/** Get the protocol timeout. */
	public int getTimeout() { return timeout; }

	/** True to use the extended (STATPRN) protocol. */
	public void setExtendedProtocol(boolean extendedProtocol) { this.extendedProtocol = extendedProtocol; }
	/** True to use the extended (STATPRN) protocol. */
	public boolean getExtendedProtocol() { return extendedProtocol; }

	/** Set the number of times requests are tried before timeouting. */
	public void setMaxTries(int maxTries) { if (maxTries < 1) throw new IllegalArgumentException(); this.maxTries = maxTries; }
	/** Get the number of times requests are tried before timeouting. */
	public int getMaxTries() { return maxTries; }

	/** Set the encoding to use for strings in newly created packets. */
	public void setEncoding(String encoding) { this.encoding = encoding; }
	/** Get the encoding to use for strings in newly created packets. */
	public String getEncoding() { return encoding; }

	/** Set the base roll-over year to use for dates in newly created packets. Valid years are from baseRolloverYear to baseRolloverYear + 99 inclusive. */
	public void setBaseRolloverYear(int baseRolloverYear) { if (baseRolloverYear < 0) throw new IllegalArgumentException(); this.baseRolloverYear = baseRolloverYear; }
	/** Get the base roll-over year to use for dates in newly created packets. Valid years are from baseRolloverYear to baseRolloverYear + 99 inclusive. */
	public int getBaseRolloverYear() { return baseRolloverYear; }

	public void setEventHandler(FiscalDeviceEventHandler eventHandler) { this.eventHandler = eventHandler; }
	public FiscalDeviceEventHandler getEventHandler() { return eventHandler; }

	public FiscalPacket createFiscalPacket() { return new HasarFiscalPacket(encoding, baseRolloverYear); }

	private int nextSerialNumber()
	{
		serialNumber += SN_STEP;
		if (serialNumber > SN_MAX) serialNumber -= SN_RANGE;
		return serialNumber;
	}

	private byte[] formatPacket(FiscalPacket packet) throws IOException
	{
		// Calculate length.
		int size = packet.getSize();
		int l = extendedProtocol ? 8 : 7;
		for (int i = 0; i < size; i++) l += (i != 0 ? 1 : 0) + packet.get(i).length;

		// Fomart packet.
		byte[] b = new byte[l];
		int p = 0;
		int cs = ASCII_STX + ASCII_ETX;
		b[p++] = (byte) ASCII_STX;
		b[p++] = (byte) serialNumber; cs += serialNumber;
		if (extendedProtocol) { b[p++] = (byte) ASCII_ESC; cs += ASCII_ESC; }
		for (int i = 0; i < size; i++)
		{
			if (i != 0) { b[p++] = (byte) ASCII_FS; cs += ASCII_FS; }
			byte[] f = packet.get(i);
			int fl = f.length;
			for (int j = 0; j < fl; j++)
			{
				int x = f[j] & 0xFF;
				if (x < 0x20) throw new IllegalArgumentException("Invalid value in byte " + j + " of field " + i + " (" + x + ")");
				b[p++] = (byte) x; cs += x;
			}
		}
		b[p++] = (byte) ASCII_ETX;
		for (int k = 12; k >= 0; k -= 4) b[p++] = (byte) Character.toUpperCase(Character.forDigit((cs >> k) & 0xF, 0x10));
		if (p != l) throw new ArrayIndexOutOfBoundsException();
		return b;
	}

	private int receivePacket(FiscalPacket packet, FiscalPacket requestToReport) throws IOException { return receivePacket(packet, in.read(), requestToReport); }
	private int receivePacket(FiscalPacket packet, int x, FiscalPacket requestToReport) throws IOException
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		long t = System.currentTimeMillis();
		receive: for (;;)
		{
			int st = FiscalDeviceEventHandler.STATUS_NORMAL;
			try
			{
				while (x != ASCII_STX)	// STX
				{
					if (x == ASCII_DC2) onStatus(requestToReport, st = FiscalDeviceEventHandler.STATUS_WORKING);
					else if (x == ASCII_DC4) onStatus(requestToReport, st = FiscalDeviceEventHandler.STATUS_PAPER_OUT);
					else
					{
						receivedUnexpectedByte(x);
						if (System.currentTimeMillis() - t >= timeout && timeout >= 0) throw new FiscalPortTimeoutException();
					}
					x = in.read();
				}
			}
			finally { if (st != FiscalDeviceEventHandler.STATUS_NORMAL) onStatus(requestToReport, FiscalDeviceEventHandler.STATUS_NORMAL); }
			int cs = x;
			x = in.read(); cs += x;
			if (x < 0x20)	// SN
			{
				receivedInvalidSerialNumberByte(x);
				continue receive;
			}
			int sn = x;
			x = in.read(); cs += x;
			if (x == ASCII_ESC)	// ESC
			{
				if (!extendedProtocol) if (!receivedUnexpectedExtendedProtocol(sn)) continue receive;
				x = in.read(); cs += x;
			}
			else
			{
				if (extendedProtocol) if (!receivedUnexpectedStandardProtocol(sn)) continue receive;
			}
			int s = 0;	// Fields
			packet.clear();
			b.reset();
			for (;;)
			{
				if (x >= 0x20)
				{
					b.write(x);
					x = in.read(); cs += x;
					continue;
				}
				packet.set(s++, b.toByteArray());
				b.reset();
				if (x == ASCII_FS)
				{
					x = in.read(); cs += x;
					continue;
				}
				if (x == ASCII_ETX) break;
				receivedInvalidFieldByte(sn, packet, x);
				continue receive;
			}
			cs &= 0xFFFF;	// Checksum
			int rcs = 0;
			for (int k = 12; k >= 0; k -= 4)
			{
				x = in.read();
				int d = Character.digit((char) x, 0x10);
				if (d < 0)
				{
					receivedInvalidChecksumByte(sn, packet, rcs, 3 - k / 4, x, cs);
					continue receive;
				}
				rcs |= (d << k);
			}
			if (rcs != cs)
			{
				if (in.available() == 0)
				{
					out.write(ASCII_NAK);
					out.flush();
				}
				receivedInvalidChecksum(sn, packet, rcs, cs);
				x = in.read();
				continue receive;
			}
			if (ALWAYS_ACKNOWLEDGE || in.available() == 0)
			{
				out.write(ASCII_ACK);
				out.flush();
			}
			if (receivedSerialNumber == sn && receivedChecksum == cs)
			{
				receivedRepeatedPacket(sn, packet);
				x = in.read();
				continue receive;
			}
			receivedSerialNumber = sn;
			receivedChecksum = cs;
			return sn;
		}
	}

	public FiscalPacket execute(FiscalPacket request) throws IOException
	{
		FiscalPacket response = createFiscalPacket();
		execute(request, response);
		return response;
	}

	public void execute(FiscalPacket request, FiscalPacket response) throws IOException
	{
		if (request == null) throw new NullPointerException();
		if (response == null) throw new NullPointerException();
		if (request == response) throw new IllegalArgumentException();
		basicExecute(request, response, request);
		onExecute(request, response);
	}

	protected void basicExecute(FiscalPacket request, FiscalPacket response, FiscalPacket requestToReport) throws IOException
	{
		// Assume request != response.
		if (in == null) throw new IllegalStateException("Device not open");
		boolean unexpectedPacket = false;

		// Handle a previous response.
		if (in.available() != 0)
		{
			for (;;)
			{
				int sn;
				try { sn = receivePacket(response, requestToReport); }
				catch (FiscalPortTimeoutException e) { break; }
				receivedUnexpectedPacket(sn, response);
				unexpectedPacket = true;
			}
		}

		// Format request.
		nextSerialNumber();
		if (receivedSerialNumber == serialNumber) nextSerialNumber();
		//if (receivedSerialNumber == serialNumber) receivedSerialNumber = 0;	// This is too much!
		byte[] requestBytes = formatPacket(request);

		// Try sending and receiving.
		int tries = 0;
		send: for (;;)
		{
			if (tries >= maxTries)	// Should be a while if maxTries could be less than 1.
			{
				if (!onTimeout(requestToReport)) throw new FiscalDeviceTimeoutException(requestToReport, null);
				tries = 0;
			}
			tries++;

			// Send request and handle acknowledgement.
			out.write(requestBytes);
			port.flushAndWait();
			int x;
			for (;;)
			{
				try { x = in.read(); }
				catch (FiscalPortTimeoutException e) { continue send; }
				if (x != ASCII_DC3) break;
				receivedLongRequestWarning();
			}
			if (x == ASCII_NAK) continue send;
			if (REQUIRE_ACKNOWLEDGE || (x != ASCII_STX && x != ASCII_DC2 && x != ASCII_DC4))
			{
				if (x != ASCII_ACK)
				{
					// WARNING: Will reissue requests.
					int sn;
					try { sn = receivePacket(response, x, requestToReport); }
					catch (FiscalPortTimeoutException e) { continue send; }
					for (;;)
					{
						if (sn == serialNumber) receivedSerialNumber = 0;
						receivedUnexpectedPacket(sn, response);
						if (MAX_TRIES_EXTENSION && !unexpectedPacket && tries >= maxTries) tries--;
						unexpectedPacket = true;
						try { sn = receivePacket(response, requestToReport); }
						catch (FiscalPortTimeoutException e) { continue send; }
					}
				}
				try { x = in.read(); }
				catch (FiscalPortTimeoutException e) { continue send; }
			}

			// Receive response and handle acknowledgement.
			int sn;
			try { sn = receivePacket(response, x, requestToReport); }
			catch (FiscalPortTimeoutException e) { continue send; }
			if (sn != serialNumber)
			{
				// WARNING: Will reissue requests.
				for (;;)
				{
					receivedUnexpectedPacket(sn, response);
					if (MAX_TRIES_EXTENSION && !unexpectedPacket && tries >= maxTries) tries--;
					unexpectedPacket = true;
					try { sn = receivePacket(response, requestToReport); }
					catch (FiscalPortTimeoutException e) { continue send; }
					if (sn == serialNumber) receivedSerialNumber = 0;
				}
			}
			return;
		}
	}

	// Empty Handlers

	protected void receivedUnexpectedByte(int x) {}
	protected void receivedInvalidSerialNumberByte(int x) {}
	protected boolean receivedUnexpectedExtendedProtocol(int sn) { return true; }	// Ignore if true.
	protected boolean receivedUnexpectedStandardProtocol(int sn) { return true; }	// Ignore if true.
	protected void receivedInvalidFieldByte(int sn, FiscalPacket packet, int x) {}
	protected void receivedInvalidChecksumByte(int sn, FiscalPacket packet, int rcs, int rcsByte, int x, int ccs) {}
	protected void receivedInvalidChecksum(int sn, FiscalPacket packet, int rcs, int ccs) {}
	protected void receivedRepeatedPacket(int sn, FiscalPacket packet) {}
	protected void receivedUnexpectedPacket(int sn, FiscalPacket packet) {}
	protected void receivedLongRequestWarning() {}

/*
	// Debug Handlers

	protected void receivedUnexpectedByte(int x) { System.err.println("device: receivedUnexpectedByte (x: " + x + ")"); }
	protected void receivedInvalidSerialNumberByte(int x) { System.err.println("device: receivedInvalidSerialNumberByte (x: " + x + ")"); }
	protected boolean receivedUnexpectedExtendedProtocol(int sn) { System.err.println("device: receivedUnexpectedExtendedProtocol (sn: " + sn + ")"); return true; }	// Ignore if true.
	protected boolean receivedUnexpectedStandardProtocol(int sn) { System.err.println("device: receivedUnexpectedStandardProtocol (sn: " + sn + ")"); return true; }	// Ignore if true.
	protected void receivedInvalidFieldByte(int sn, FiscalPacket packet, int x) { System.err.println("device: receivedInvalidFieldByte (sn: " + sn + ", packet: " + packet + ", x: " + x + ")"); }
	protected void receivedInvalidChecksumByte(int sn, FiscalPacket packet, int rcs, int rcsByte, int x, int ccs) { System.err.println("device: receivedInvalidChecksumByte (sn: " + sn + ", packet: " + packet + ", rcs: " + rcs + ", rcsByte: " + rcsByte + ", x: " + x + ", ccs: " + ccs + ")"); }
	protected void receivedInvalidChecksum(int sn, FiscalPacket packet, int rcs, int ccs) { System.err.println("device: receivedInvalidChecksum (sn: " + sn + ", packet: " + packet + ", rcs: " + rcs + ", ccs: " + ccs + ")"); }
	protected void receivedRepeatedPacket(int sn, FiscalPacket packet) { System.err.println("device: receivedRepeatedPacket (sn: " + sn + ", packet: " + packet + ")"); }
	protected void receivedUnexpectedPacket(int sn, FiscalPacket packet) { System.err.println("device: receivedUnexpectedPacket (sn: " + sn + ", packet: " + packet + ")"); }
	protected void receivedLongRequestWarning() { System.err.println("device: receivedLongRequestWarning"); }
*/

	/** onTimeout() event dispacther. */
	protected boolean onTimeout(FiscalPacket request) { return eventHandler != null ? eventHandler.onTimeout(this, request) : false; }	// Retry if true.
	/** onStatus() event dispacther. */
	protected void onStatus(FiscalPacket request, int status) { if (eventHandler != null) eventHandler.onStatus(this, request, status); }
	/** onExecute() event dispacther. */
	protected void onExecute(FiscalPacket request, FiscalPacket response) { if (eventHandler != null) eventHandler.onExecute(this, request, response); }
}
