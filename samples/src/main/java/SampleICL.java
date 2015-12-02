import com.taliter.fiscal.device.icl.ICLFiscalDevice;
import com.taliter.fiscal.device.icl.ICLFiscalDeviceSource;
import com.taliter.fiscal.device.icl.ICLFiscalPacket;
import com.taliter.fiscal.port.serial.SerialFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalDeviceEventHandler;

/**
 * A simple example that prints a non-fiscal check. Tested on Daisy ProfiPrint and Datecs FP-2000.
 */
public class SampleICL {
    private static int CMD_OPEN_NONFISCAL_CHECK = 0x26;
    private static int CMD_PRINT_NONFISCAL_TEXT = 0x2a;
    private static int CMD_CLOSE_NONFISCAL_CHECK = 0x27;
    
    public static void main(String[] args) throws Exception {

        // Create ICL fiscal device source on port "COM1"
        ICLFiscalDeviceSource deviceSource = new ICLFiscalDeviceSource(new SerialFiscalPortSource("COM1"));

        // Creates ICL fiscal device object.
        ICLFiscalDevice device = deviceSource.getFiscalDevice();

        device.getFiscalPort().setBaudRate(115200);
        // Plug an event handler that logs the events triggered by the device.
        device.setEventHandler(new LoggerFiscalDeviceEventHandler(System.out));
        
        // Open the device.        
        device.open();

        // Close the device even in the event of an error.
        try {
            // Create a request packet.
            ICLFiscalPacket request = device.createFiscalPacket();

            request.setCommandCode(CMD_OPEN_NONFISCAL_CHECK);
            device.execute(request);
            
            request.clear();

            request.setCommandCode(CMD_PRINT_NONFISCAL_TEXT);
            request.setString(1, "Нефискален текст.");
            device.execute(request);
            request.setString(1, "Non-fiscal text.");
            device.execute(request);

            request.clear();
            request.setCommandCode(CMD_CLOSE_NONFISCAL_CHECK);
            device.execute(request);
                    
        } finally {
            // Close the device.
            device.close();
        }
    }
}
