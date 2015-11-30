/*
 * Copyright (C) 2015 EDA Ltd.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 *
 * @author nikolabintev@edabg.com
 */
package com.taliter.fiscal.device.daisy;

import com.taliter.fiscal.device.FiscalDeviceIOException;
import com.taliter.fiscal.device.FiscalDeviceSource;
import com.taliter.fiscal.device.icl.ICLFiscalPrinter;
import com.taliter.fiscal.device.icl.ICLFiscalPacket;
import com.taliter.fiscal.port.serial.SerialFiscalPortSource;
import com.taliter.fiscal.util.LoggerFiscalDeviceEventHandler;
import com.taliter.fiscal.util.LoggerFiscalPortSource;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * DaisyFiscalPrinter implements ICLFiscalPrinter interface.
 * This implementation provides the basic functionality of
 * Daisy fiscal devices.
 */
public class DaisyFiscalPrinter implements ICLFiscalPrinter{
    
    /** Daisy fiscal device object*/
    private DaisyFiscalDevice device;
    
    /**
     * Creates an instance of DaisyFiscalDevice
     * @param port The port in which the fiscal device is connected to.
     * @param baudRate serial port baud rate
     * @throws FiscalDeviceIOException
     * @throws Exception
     */
    public DaisyFiscalPrinter(String port, int baudRate) throws FiscalDeviceIOException, Exception {
        
        FiscalDeviceSource deviceSource = new DaisyFiscalDeviceSource(new SerialFiscalPortSource(port));
        boolean logComm = false;
        if (logComm) deviceSource.setPortSource(new LoggerFiscalPortSource(deviceSource.getPortSource(), System.out));

        device = (DaisyFiscalDevice) deviceSource.getFiscalDevice();
        device.getFiscalPort().setBaudRate(baudRate);
        device.setEventHandler(new LoggerFiscalDeviceEventHandler(System.out));
	
        // Open the device         
        device.open();
        
    }

    /**
     * Creates an instance of DaisyFiscalDevice
     * @param port The port in which the fiscal device is connected to.
     * By default baudRate is 9600 bps
     * @throws FiscalDeviceIOException
     * @throws Exception
     */
    public DaisyFiscalPrinter(String port) throws FiscalDeviceIOException, Exception {
        this(port, 9600);
    }
    
    /**
     * Close the connection with the fiscal device.
     * @throws Exception 
     */
    public void close() throws Exception {
        device.close();
    }
    
    @Override
    public void cmdPrintDiagnosticInfo() throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        request.setCommandCode(DaisyConstants.CMD_PRINT_DIAGNOSTIC_INFO);
        try {
            this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public int cmdLastDocNum() throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();

        request.setCommandCode(DaisyConstants.CMD_LAST_DOC_NUM);
        ICLFiscalPacket response;
        try {
            response = (ICLFiscalPacket) this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        return Integer.parseInt(response.getString(1));
    }

    @Override
    public LinkedHashMap<String, Object> cmdOpenFiscalCheck(String operator, String password, boolean invoice) throws FiscalDeviceIOException{
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        
        String data = operator + "," + password + "," + "1";
        
        if(invoice) {
            data += "," + "I";
        }

        request.setCommandCode(DaisyConstants.CMD_OPEN_FISCAL_CHECK);
        request.setString(1, data);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket)this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }

        String rString = responsePacket.toASCIIString(1);
        
        if(!rString.isEmpty()) {
            String[] rData = rString.split(",");

            response.put("AllReceipt", Integer.parseInt(rData[0]));
            response.put("FiscReceipt", Integer.parseInt(rData[1]));
        }
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdCloseFiscalCheck() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_CLOSE_FISCAL_CHECK);
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket)this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String rString = responsePacket.toASCIIString(1);
        
        if(!rString.isEmpty()) {
            String[] rData = rString.split(",");

            response.put("AllReceipt", Integer.parseInt(rData[0]));
            response.put("FiscReceipt", Integer.parseInt(rData[1]));
        }
        
        return response;
    }

    @Override
    public void cmdPrintFiscalText(String text) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_PRINT_FISCAL_TEXT);
        request.setString(1, text);
        
        try {
            device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public LinkedHashMap<String, Object> cmdOpenNonFiscalCheck() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_OPEN_NONFISCAL_CHECK);
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket)this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        response.put("CheckCount", responsePacket.getString(1));
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdCloseNonFiscalCheck() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_CLOSE_NONFISCAL_CHECK);
        ICLFiscalPacket responsePacket;
        
        try {
            responsePacket = (ICLFiscalPacket)this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        response.put("CheckCount", responsePacket.getString(1));
        
        return response;
    }

    @Override
    public void cmdPrintNonFiscalText(String text) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        try {
            request.setCommandCode(DaisyConstants.CMD_PRINT_NONFISCAL_TEXT);
            request.setString(1, text);
        
            device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public void cmdSell(String sellText, String descriptionText, String taxGroup, String sign, String price, String quantity, String discount, boolean inPercent) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        String cr = "\n";
        String tab = "\t";
        String multiply = "*";

        String data = sellText;
        
        if(!descriptionText.isEmpty()) {
            data += cr + descriptionText;
        }
        
        if(taxGroup.isEmpty()) {
            taxGroup = "Б";
        }
        

        if(discount == null || discount.isEmpty()) {
            discount = "0";
        }

        data += tab + taxGroup + sign + price + multiply + quantity;

        if(inPercent) {
            data += "," + discount;
        } else {
            data += "$" + discount;
        }
        
        request.setCommandCode(DaisyConstants.CMD_SELL);
        request.setString(1, data);

        try {
            device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }
    
    @Override
    public void cmdSellDept(String sign, String dept, String price, String quantity, String discount, boolean inPercent) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();

        if(discount == null || discount.isEmpty()) {
            discount = "0";
        }
        
        String data = sign + dept + "@" + price + "*" + quantity;
        
        if(inPercent) {
            data += "," + discount;
        } else {
            data += "$" + discount;
        }
        
        
        request.setCommandCode(DaisyConstants.CMD_SELL_DEPT);
        request.setString(1, data);
        
        try {
            device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    
    @Override
    public LinkedHashMap<String, Object> cmdSubTotal(boolean toPrint, boolean toDisplay, String discountPercent) throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        String data = "";
        
        data += toPrint ? "1" : "0";
        data += toDisplay ? "1" : "0";
        
        if(!discountPercent.isEmpty())
            data += "," + discountPercent;

        request.setCommandCode(DaisyConstants.CMD_SUBTOTAL);
        request.setString(1, data);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket)this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String[] rdata = responsePacket.toASCIIString(1).split(",");
        
        response.put("SubTotal", rdata[0]);
        response.put("A", rdata[1]);
        response.put("B", rdata[2]);
        response.put("C", rdata[3]);
        response.put("D", rdata[4]);
        response.put("E", rdata[5]);
        response.put("F", rdata[6]);
        response.put("G", rdata[7]);
        response.put("H", rdata[8]);
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdTotal() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        String data = "\t";
        request.setCommandCode(DaisyConstants.CMD_TOTAL);
        request.setString(1, data);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
       
        byte[] rdata = responsePacket.get(1);
        
        response.put("PaidCode", Integer.toHexString(rdata[0]));
        
        String amount = "";
        for (int i = 1; i < rdata.length; i++) {
            amount += (char) rdata[i];
        }
        
        response.put("PaidCode", (char) rdata[0]);
        response.put("Amount", Double.parseDouble(amount));
        
        return response;
    }
 
    @Override
    public LinkedHashMap<String, Object> cmdTotal(String firstRowText, String secondRowText, String paymentType, String amount) throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        String data = firstRowText;
        
        if(!secondRowText.isEmpty()) {
            data += "\n" + secondRowText;
        }
        
        data += "\t" + paymentType + amount;

        request.setCommandCode(DaisyConstants.CMD_TOTAL);
        request.setString(1, data);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }

        byte[] rdata = responsePacket.get(1);
        
        response.put("PaidCode", Integer.toHexString(rdata[0]));
        
        String am = "";
        for (int i = 1; i < rdata.length; i++) {
            am += (char) rdata[i];
        }
        
        response.put("PaidCode", (char) rdata[0]);
        response.put("Amount", Double.parseDouble(am));
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdCurrentCheckInfo() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_CURRENT_CHECK_INFO);
        
        ICLFiscalPacket responsePacket; 
        try {
            responsePacket = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }

        String[] data = responsePacket.toASCIIString(1).split(",");
        
        response.put("CanVoid", data[0]);
        response.put("A", data[1]);
        response.put("B", data[2]);
        response.put("C", data[3]);
        response.put("D", data[4]);
        response.put("E", data[5]);
        response.put("F", data[6]);
        response.put("G", data[7]);
        response.put("H", data[8]);
        response.put("InvoiceFlag", data[9]);
        response.put("InvoiceNo", data[10]);
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdLastFiscalRecord(String type) throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        
        if(type == null || type.isEmpty()) {
            type = "N";
        }
        
        request.setCommandCode(DaisyConstants.CMD_LAST_FISCAL_RECORD);
        request.setString(1, type);

        ICLFiscalPacket responsePacket; 
        try {
            responsePacket = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String responseString = responsePacket.toASCIIString(1);
        
        if(!responseString.isEmpty()) {
            String[] data = responseString.split("");
        
            response.put("Number", data[0]);
            response.put("SpaceGr", data[1]);
            response.put("A", data[2]);
            response.put("B", data[3]);
            response.put("C", data[4]);
            response.put("D", data[5]);
            response.put("E", data[6]);
            response.put("F", data[7]);
            response.put("G", data[8]);
            response.put("H", data[9]);
            response.put("Date", data[10]);
        }
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdPrinterStatus() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_PRINTER_STATUS);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String[] data = responsePacket.toASCIIString(1).split("\\\\");
        
        response.put("S0", data[1]);
        response.put("S1", data[2]);
        response.put("S2", data[3]);
        response.put("S3", data[4]);
        response.put("S4", data[5]);
        response.put("S5", data[6]);
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdDiagnosticInfo(boolean toCalculate) throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_DIAGNOSTIC_INFO);
        if(toCalculate) {
            request.setByte(1, 1);
        }
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String[] data = responsePacket.toASCIIString(1).split(",");
        String[] basicData = data[0].split(" ");
        
        response.put("FirmwareVersion", basicData[0]);
        response.put("FirmwareDate", basicData[1]);
        response.put("FirmwareTime", basicData[2]);
        response.put("CheckSum", data[1]);
        response.put("SW", data[2]);
        response.put("Country", data[3]);
        response.put("SerialNumber", data[4]);
        response.put("FiscalModulNum", data[5]);
        
        return response;
    }

    @Override
    public Date cmdGetDateTime() throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_DATETIME_INFO);
        
        ICLFiscalPacket response;
        try {
            response = (ICLFiscalPacket) device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        return response.getDateAndTime(1).getTime();
    }

    @Override
    public void cmdSetDateTime(int year, int month, int day, int hour, int minute, int second) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_SET_DATETIME);
        request.setDateAndTime(1, year, month, day, hour, minute, second);
        
        try {
            device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }
    
    @Override
    public void cmdPrintCheckDuplicate() throws FiscalDeviceIOException{
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_PRINT_CHECK_DUBLICATE);
        request.setString(1, "1");
        
        try {
            device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public LinkedHashMap<String, Object> cmdCancelFiscalCheck() throws FiscalDeviceIOException{
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_CANCEL_FISCAL_CHECK);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket)this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        if(responsePacket.get(1).length > 0) {
            String[] rData = responsePacket.toASCIIString(1).split(",");
        
            response.put("AllReceipt", Integer.parseInt(rData[0]));
            response.put("FiscReceipt", Integer.parseInt(rData[1]));
        }
        
        return response;
    }

    @Override
    public LinkedHashMap<String, Object> cmdReportDaily(String item, boolean toReset) throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        if(item == null || item.isEmpty()) {
            item = "0";
        }
       
        String data = item;
        
        if(!toReset) 
            data += "N";
        
        request.setCommandCode(DaisyConstants.CMD_REPORT_DAILY);
        request.setString(1, data);
        
        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket) this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String[] rData = responsePacket.toASCIIString(1).split(",");
        
        response.put("Closure", Integer.parseInt(rData[0]));
        response.put("Total", Long.parseLong(rData[1]));
        response.put("A", Long.parseLong(rData[2]));
        response.put("B", Long.parseLong(rData[3]));
        response.put("C", Long.parseLong(rData[4]));
        response.put("D", Long.parseLong(rData[5]));
        response.put("E", Long.parseLong(rData[6]));
        response.put("F", Long.parseLong(rData[7]));
        response.put("G", Long.parseLong(rData[8]));
        response.put("H", Long.parseLong(rData[9]));
        
        return response;
    }

    @Override
    public void cmdReportByDates(boolean detailed, String startDate, String endDate) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        int cmd = detailed ? DaisyConstants.CMD_REPORT_BY_DATE : DaisyConstants.CMD_REPORT_BY_DATE_SHORT;

        String data = startDate + "," + endDate;
        
        
        request.setCommandCode(cmd);
        request.setString(1, data);
        
        try {
            this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public void cmdPaperFeed(String lines) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_PAPER_FEED);
        request.setString(1, lines);
        
        try {
            this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public void cmdSetOperator(String clerkNum, String password, String name) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();

        String data = clerkNum + "," + password + "," + name;
 
        
        request.setCommandCode(DaisyConstants.CMD_SET_OPERATOR);
        request.setString(1, data);
        
        try {
            this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public String customCmd(int cmd, Object[] params) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        String data = "";
        
        for (Object param : params) {
            data += param;
        }
        
        request.setCommandCode(cmd);
        if(!data.isEmpty())
            request.setString(1, data);
        
        ICLFiscalPacket response;
        try {
            response = (ICLFiscalPacket) this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        byte[] rdata = response.get(1);
        
        String responseString = "";
        for (byte b : rdata) {
            responseString += (char) b;
        }
        
        return responseString;
    }
    
    @Override
    public void cmdResetByOperator(String clerkNumber, String password) throws FiscalDeviceIOException {
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        String data = clerkNumber + "," + password;
        
        request.setCommandCode(DaisyConstants.CMD_RESET_BY_OPERATOR);
        request.setString(1, data);
        
        try {
            this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
    }

    @Override
    public LinkedHashMap<String, Object> cmdGetConstants() throws FiscalDeviceIOException {
        LinkedHashMap<String, Object> response = new LinkedHashMap();
        ICLFiscalPacket request = (ICLFiscalPacket) device.createFiscalPacket();
        
        request.setCommandCode(DaisyConstants.CMD_GET_CONSTANTS);

        ICLFiscalPacket responsePacket;
        try {
            responsePacket = (ICLFiscalPacket) this.device.execute(request);
        } catch (IOException ex) {
            throw new FiscalDeviceIOException(ex.getMessage());
        }
        
        String[] rData = responsePacket.toASCIIString(1).split(",");
        
        
        response.put("LogoWidth", Integer.parseInt(rData[0]));
        response.put("LogoHeight", Integer.parseInt(rData[1]));
        response.put("PaymentCount", Integer.parseInt(rData[2]));
        response.put("TaxGroupCount", Integer.parseInt(rData[3]));
/*      
        response.put("NotUsingInBG", rData[4]);
        response.put("NotUsingInBG", rData[5]);
*/
        response.put("FirstTaxGroup", rData[6]);
        response.put("InternalArithmetic", Integer.parseInt(rData[7]));
        response.put("SymbolsRowCount", Integer.parseInt(rData[8]));
        response.put("SymbolsCommentedRowCount", Integer.parseInt(rData[9]));
        
        response.put("NameLength", Integer.parseInt(rData[10]));
        response.put("FDIdLength", Integer.parseInt(rData[11]));
        response.put("FMNumberLength", Integer.parseInt(rData[12]));
        response.put("TaxNumberLength", Integer.parseInt(rData[13]));
        response.put("BulstatLength", Integer.parseInt(rData[14]));
        response.put("DepartmentsCount", Integer.parseInt(rData[15]));
        response.put("ItemCount", Integer.parseInt(rData[16]));

        response.put("StockFlag", Integer.parseInt(rData[17]));
        response.put("BarcodeFlag", Integer.parseInt(rData[18]));
        
        response.put("CommodityGroupCount", Integer.parseInt(rData[19]));
        response.put("OperatorsCount", Integer.parseInt(rData[20]));
        response.put("PaymentNameLength", Integer.parseInt(rData[21]));
        response.put("FuelsCount", Integer.parseInt(rData[22]));

        /*
        response.put("NotUsing", rData[23]);
        response.put("NotUsing", rData[24]);
        */
        return response;
    }
}
