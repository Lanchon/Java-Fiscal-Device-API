import java.util.*;

import com.taliter.fiscal.device.*;
import com.taliter.fiscal.device.hasar.*;
import com.taliter.fiscal.port.serial.*;
import com.taliter.fiscal.util.*;

/** A simple example that obtains the date and time from a Hasar fiscal printer in COM1. */
public class Sample implements HasarConstants
{
	public static void main(String[] args) throws Exception
	{
		// Source or factory objects encapsulate all configuration data and are serializable.
		// Normally they would be obtained from some persistent representation such as XML,
		// but here we will just instantiate what we need for simplicity.
		FiscalDeviceSource deviceSource = new HasarFiscalDeviceSource(new SerialFiscalPortSource("COM1"));

		// Obtain an interface to the device.
		FiscalDevice device = deviceSource.getFiscalDevice();

		// Plug an event handler that logs the events triggered by the device.
		device.setEventHandler(new LoggerFiscalDeviceEventHandler(System.out));

		device.open();	// Open the device and synchronize communications.

		try		// Close the device even in the event of an error.
		{
			FiscalPacket request = device.createFiscalPacket();	// Create a request packet.
			request.setCommandCode(CMD_GET_DATE_TIME);			// Set the appropriate command code.
			FiscalPacket response = device.execute(request);	// Execute the request.
			Date date = response.getDateAndTime(3, 4);			// Get the date and time from the response
																// using the default calendar and time-zone
																// to interpret the returned fields.

			System.out.println("Date: " + date);	// Output the returned date.
		}
		finally { device.close(); }		// Close the device.
	}
}
