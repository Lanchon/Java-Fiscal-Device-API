package com.taliter.fiscal.device.icl;

import com.taliter.fiscal.device.FiscalDeviceTimeoutException;
import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.device.hasar.BasicFiscalDevice;
import com.taliter.fiscal.port.FiscalPort;
import com.taliter.fiscal.port.FiscalPortTimeoutException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A BasicFiscalDevice implementation used to communicate with ICL fiscal printers 
 * @author nikolabintev@edabg.com
 */
public class ICLFiscalDevice extends BasicFiscalDevice {

    /** The first byte of fiscal packet*/
    private static final int ASCII_STX = 0x01;

    /** The first byte of fiscal packet*/
    private static final int ASCII_ETX = 0x03;

    /** Specifies the end of the data in the packet from the fiscal device response*/
    private static final int ASCII_POST4 = 0x04;

    /** Specifies the end of the status in the packet from the fiscal device response*/
    private static final int ASCII_POST5 = 0x05;

    /** Error code*/
    private static final int ASCII_NAK = 0x15;

    /** Code for execution of the fiscal device*/
    private static final int ASCII_SYN = 0x16;

    /** The minimum value of the fiscal packet serial number*/
    private static final int SN_MIN = 0x20;

    /** The maximum value of the fiscal packet serial number*/
    private static final int SN_MAX = 0x7F;

    /** The step of the serial number*/
    private static final int SN_STEP = 1;

    /** The serial number range*/
    private static final int SN_RANGE = SN_MAX - SN_MIN + 1;

    /**The seral number*/
    protected int serialNumber;
    
    /** 2 min maximum command execution duration*/
    private int maxExecutionTime;

    
    /**
     * Creates an ICL Fiscal device object
     * @param port Fiscal device port
     * @param timeout Fiscal device timeout
     * @param maxTries The maximum number of tries for executing a request
     * @param encoding The fiscal device encoding
     */
    public ICLFiscalDevice(FiscalPort port, int timeout, int maxTries, String encoding) {
        super(port, timeout, false, maxTries, encoding, 1997);
        
        this.serialNumber = 0x20;
        this.maxExecutionTime = 2*60;
    }

    /**
     * Get the value of maxExecutionTime
     * @return the value of maxExecutionTime
     */
    public int getMaxExecutionTime() {
        return maxExecutionTime;
    }

    /**
     * Set the value of maxExecutionTime
     * @param maxExecutionTime new value of maxExecutionTime
     */
    public void setMaxExecutionTime(int maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }
    
    /**
     * Creates fiscal packet object
     * @return Returns fiscal packet object
     */
    @Override
    public ICLFiscalPacket createFiscalPacket() {
        return new ICLFiscalPacket(getEncoding());
    }

    /**
     * Calculated the next serial number of the packet.
     *
     * @return The calculated serial number.
     */
    private int nextSerialNumber() {
        serialNumber += SN_STEP;
        if (serialNumber > SN_MAX) {
            serialNumber -= SN_RANGE;
        }
        return serialNumber;
    }

    /**
     * Calculates the checksum and formats the fiscal packet to byte array in
     * such way that would be understandable for the fiscal devices which use
     * ICL protocol.
     * @param packet The packet which should be formated.
     * @return The fiscal packet as a byte array.
     * @throws IOException
     */
    private byte[] formatPacket(FiscalPacket packet) throws IOException {
        int index = 0;

        int size = packet.getSize();
        int l = 9; // envelop bytes
        for (int i = 0; i < size; i++) {
            l += packet.get(i).length;
        }
        byte[] b = new byte[l];

        b[index++] = ASCII_STX;
        int csStartIndex = index;
        b[index++] = (byte) (0x20 + (l - 6));

        b[index++] = (byte) this.nextSerialNumber();

        //Get fields from packet(cmd and data)
        for (int i = 0; i < packet.getSize(); i++) {
            byte[] temp = packet.get(i);

            for (byte t : temp) {
                b[index++] = t;
            }
        }

        b[index++] = ASCII_POST5;

        int checkSum = 0;
        for (int i = csStartIndex; i < index; i++) {
            checkSum += (int) b[i] & 0xFF;
        }
        //Calculate 4 - byte BCC
        for (int i = 12; i >= 0; i -= 4) {
            if (i == 2) {
                i = 0;
            }

            b[index++] = (byte) (((checkSum >> i) & 0xF) + 0x30);
        }

        b[index++] = ASCII_ETX;

        return b;
    }

    /**
     * Parse the answer from the fiscal device and set values to requestToReport fiscal packet.
     * @param packet Requested packet which is send to the printer.
     * @param x The first byte read from the printer response.
     * @param requestToReport The response packet in which the information from the fiscal device is stored.
     * @return Returns the serial number of the received response.
     * @throws IOException 
     */
    private int receivePacket(FiscalPacket packet, int x, FiscalPacket requestToReport) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        long t = System.currentTimeMillis();
        int sn = 0;
        
        int cs = 0;

        receive:
        for (;;) {
            if (System.currentTimeMillis() - t >= getTimeout() && getTimeout() >= 0) {
                throw new FiscalPortTimeoutException();
            }
            
            if(x == ASCII_STX) {
                // LEN
                x = in.read();
                cs += x;

                // SEQ
                x = in.read();
                
                if(x < SN_MIN) {
                    sn = ASCII_SYN;
                    break receive;
                }
                
                cs += x;
                sn = x;

                x = in.read();
                cs += x;
            }
            
            if(x == ASCII_ETX) {
                break receive;
            }
            
            if(x == requestToReport.getCommandCode()) {
                // CMD
                int s = 0;
                packet.clear();
                b.reset();
                
                b.write(x);
                packet.set(s++, b.toByteArray());
                b.reset();
                
                // DATA
                for (;;) {
                    x = in.read();
                    cs += x;

                    if (x != ASCII_POST4) {
                        b.write(x);
                        continue;
                    }

                    packet.set(s++, b.toByteArray());
                    b.reset();
                    
                    //continue receive;
                    break;
                }
                
                // STATUS
                for (;;) {
                    x = in.read();
                    cs += x;
                    
                    if(x != ASCII_POST5) {
                        b.write(x);
                        continue;
                    }
                    
                    packet.set(s++, b.toByteArray());
                    b.reset();
                    
                    break;
                }
                
                // CHECKSUM
                cs &= 0xffff;	

                int rcs = 0;
                
                for (int i = 12; i >= 0; i -= 4) {
                    x = in.read();
                    
                    rcs |= (x -= 0x30) << i;
                }

                x = in.read();

                //compare checksums
                if(cs != rcs) {
                    //Returns NAK if checksums are not equal
                    sn = ASCII_NAK;
                    break receive;
                }
                
            }
        }
        
        return sn;
    }
    
    /**
     * Executes the request to fiscal device.
     *
     * @param request The request packet.
     * @param response The response packet.
     * @param requestToReport The requestToReport packet.
     * @throws IOException
     */
    @Override
    protected void basicExecute(FiscalPacket request, FiscalPacket response, FiscalPacket requestToReport) throws IOException {
        if (in == null) {
            throw new IllegalStateException("Device not open!");
        }

        byte[] requestBytes = formatPacket(request);
        int tries = 0;

        send:
        for (;;) {
            if (tries >= getMaxTries()) // Should be a while if maxTries could be less than 1.
            {

                if (!onTimeout(requestToReport)) {
                    throw new FiscalDeviceTimeoutException(requestToReport, null);
                }

                tries = 0;
            }
            tries++;

            out.write(requestBytes);
            super.getFiscalPort().flushAndWait();

            // calc number of syn packet on every 60ms
            long synLimit = maxExecutionTime * 1000 / 60;

            int x;
            // wait to complete the operation from slave
            do {
                x = in.read();
                synLimit--;

                if ((synLimit < 0) && (x == ASCII_SYN)) {
                    throw new FiscalDeviceTimeoutException(requestToReport, null);
                }

            } while (x == ASCII_SYN); // && !timeout for excution of command

            // resend the packet
            if (x == ASCII_NAK) {
                continue send;
            }

            if (x != ASCII_STX) {
                continue send;
            }
            // returns the sn of received packet
            x = receivePacket(response, x, requestToReport);

            //compare sn of receiced packet and sn of requested packet
            if (x == requestBytes[2]) {
                break send;
            }
        }
    }
}
