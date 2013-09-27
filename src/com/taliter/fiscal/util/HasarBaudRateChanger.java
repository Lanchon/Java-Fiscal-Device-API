package com.taliter.fiscal.util;

import java.io.*;
import javax.comm.*;

import com.taliter.fiscal.device.*;
import com.taliter.fiscal.device.hasar.*;
import com.taliter.fiscal.port.*;
import com.taliter.fiscal.port.serial.*;

/** A tool to change the baud rate of a Hasar fiscal device. */
public final class HasarBaudRateChanger
{
	private HasarBaudRateChanger() {}

	private static final long GUARD_TIME = 50;
	private static final int FREE_ACKS = 3;

	private static final int ASCII_ACK = 0x06;
	private static final int CMD_SET_BAUD_RATE = 0xA0;

	/** Change the baud rate of a Hasar fiscal device. The device must be open. */
	public static void changeBaudRate(FiscalDevice device, int baudRate) throws UnsupportedCommOperationException, IOException
	{
		changeBaudRate((HasarFiscalDevice) device, baudRate);
	}

	/** Change the baud rate of a Hasar fiscal device. The device must be open. */
	public static void changeBaudRate(HasarFiscalDevice device, int baudRate) throws UnsupportedCommOperationException, IOException
	{
		FiscalPort port = device.getFiscalPort();
		while (port instanceof LoggerFiscalPort) port = ((LoggerFiscalPort) port).getFiscalPort();
		changeBaudRate(device, (SerialFiscalPort) port, baudRate);
	}

	/** Change the baud rate of a Hasar fiscal device. The device must be open. */
	public static void changeBaudRate(HasarFiscalDevice device, SerialFiscalPort port, int baudRate) throws UnsupportedCommOperationException, IOException
	{
		int br = port.getBaudRate();
		if (br == baudRate)
		{
			device.synchronize();
			return;
		}
		boolean success = false;
		try
		{
			port.flushAndWait();
			sleep(GUARD_TIME);
			port.setBaudRate(baudRate);	// Check host baud rate support.
			port.setBaudRate(br);
			sleep(GUARD_TIME);
			device.synchronize();
			FiscalDeviceEventHandler h = device.getEventHandler();
			device.setEventHandler(null);	// Suppress event generation.
			try
			{
				FiscalPacket request = device.createFiscalPacket();
				request.setCommandCode(CMD_SET_BAUD_RATE);
				request.setInt(1, baudRate);
				try { device.execute(request); }
				catch (FiscalDeviceTimeoutException e)
				{
					OutputStream os = port.getOutputStream();
					for (int i = FREE_ACKS; i > 0; i--)
					{
						os.write(ASCII_ACK);
						port.flushAndWait();
					}
					sleep(GUARD_TIME);
					port.setBaudRate(baudRate);
					sleep(GUARD_TIME);
					for (int i = FREE_ACKS; i > 0; i--)
					{
						os.write(ASCII_ACK);
						port.flushAndWait();
					}
					device.synchronize();
					success = true;
					return;
				}
				port.flushAndWait();
				sleep(GUARD_TIME);
				port.setBaudRate(baudRate);
				sleep(GUARD_TIME);
				device.synchronize();
				success = true;
				return;
			}
			finally { device.setEventHandler(h); }
		}
		finally { if (!success) port.setBaudRate(br); }
	}

	private static void sleep(long ms)
	{
		try { Thread.sleep(ms); }
		catch (InterruptedException x) { Thread.currentThread().interrupt(); }
	}
}
