package com.taliter.fiscal.util;

import java.io.*;

import com.taliter.fiscal.device.*;
import com.taliter.fiscal.device.hasar.*;
import com.taliter.fiscal.port.*;

/** A tool to detect the baud rate and extended protocol support of a Hasar fiscal device. */
public final class HasarAutomaticDetection
{
	private HasarAutomaticDetection() {}

	private static final int[] BAUD_RATES = new int[] { 115200, 57600, 38400, 19200, 9600, 4800, 2400, 1200 };
	private static final int MAX_TRIES = 1;

	/** Open a Hasar fiscal device detecting the baud rate. The device must be closed. */
	public static void openDeviceDetectingBaudRate(FiscalDevice device) throws Exception
	{
		openDeviceDetectingBaudRate((HasarFiscalDevice) device);
	}

	/** Open a Hasar fiscal device detecting the baud rate. The device must be closed. */
	public static void openDeviceDetectingBaudRate(HasarFiscalDevice device) throws Exception
	{
		FiscalPort port = device.getFiscalPort();
		boolean xp = device.getExtendedProtocol();
		int mt = device.getMaxTries();
		try
		{
			device.setExtendedProtocol(false);
			device.setMaxTries(MAX_TRIES);
			Exception e1 = null;
			try { device.open(); }
			catch (Exception e) { e1 = e; }
			if (e1 == null) return;
			int br = port.getBaudRate();
			boolean success = false;
			try
			{
				for (int i = 0; i < BAUD_RATES.length; i++)
				{
					if (BAUD_RATES[i] == br) continue;
					Exception e2 = null;
					try
					{
						port.setBaudRate(BAUD_RATES[i]);
						device.open();
					}
					catch (Exception e) { e2 = e; }
					if (e2 == null)
					{
						success = true;
						return;
					}
				}
			}
			finally { if (!success) port.setBaudRate(br); }
			throw e1;
		}
		finally
		{
			device.setExtendedProtocol(xp);
			device.setMaxTries(mt);
		}
	}

	/** Detect whether a Hasar fiscal device supports the extended (STATPRN) protocol. The device must be open. */
	public static void detectProtocol(FiscalDevice device) throws Exception
	{
		detectProtocol((HasarFiscalDevice) device);
	}

	/** Detect whether a Hasar fiscal device supports the extended (STATPRN) protocol. The device must be open. */
	public static void detectProtocol(HasarFiscalDevice device) throws IOException
	{
		boolean xp = device.getExtendedProtocol();
		boolean success = false;
		try
		{
			device.setExtendedProtocol(true);
			try { device.synchronize(); }
			catch (FiscalDeviceTimeoutException e)
			{
				device.setExtendedProtocol(false);
				device.synchronize();
			}
			success = true;
		}
		finally { if (!success) device.setExtendedProtocol(xp); }
	}
}
