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
package com.taliter.fiscal.device.icl;

import com.taliter.fiscal.device.FiscalDeviceIOException;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * ICLFiscalPrinter interface describes the basic operations of ICL protocol.
 */
public interface ICLFiscalPrinter {
        
    /**
     * Open a fiscal check.
     * @param operatorNum Operator's number.
     * @param password Operator's password.
     * @param invoice - if it's true invoice is issued.
     * @return Returns the number of fiscal and non fiscal checks as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdOpenFiscalCheck(String operatorNum, String password, boolean invoice) throws FiscalDeviceIOException;

    /**
     * Close the fiscal check.
     * @return Returns the number of fiscal and non fiscal checks as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdCloseFiscalCheck() throws FiscalDeviceIOException;

    /**
     * Print a fiscal text. Fiscal check should be opened first. 
     * @param text The text that will be printed.
     * @throws FiscalDeviceIOException
     */
    public void cmdPrintFiscalText(String text) throws FiscalDeviceIOException;

    /**
     * Sale. A fiscal check should be opened first.
     * @param text Sale description text.
     * @param sellDescription Additional sale description text.
     * @param taxGroup One symbol for tax group (А, Б, В, Г, Д, Е, Ж, З).
     * @param sign "+" or "-".
     * @param price Price (to 8 digits).
     * @param quantity Quantity of sale (to 8 digits).
     * @param discount The value of discount/add (depends on the sign).
     * @param inPercent Determines whether the value of discount is in percent or netto.
     * @throws FiscalDeviceIOException
     */
    public void cmdSell(String text, String sellDescription, String taxGroup, String sign, String price, String quantity, String discount, boolean inPercent) throws FiscalDeviceIOException;

    /**
     * Sale by department. A fiscal check should be opened first.
     * @param sign "+" or "-"
     * @param dept Department number (2 digits).
     * @param price - Price (to 8 digits).
     * @param quantity - Quantity of sale (to 8 digits).
     * @param discount The value of discount/add (depends on the sign).
     * @param inPercent Determines whether the value of discount is in percent or netto.
     * @throws FiscalDeviceIOException
     */
    public void cmdSellDept(String sign, String dept, String price, String quantity, String discount, boolean inPercent) throws FiscalDeviceIOException;
    
    /**
     * Cancel the check.
     * @return Returns the number of all non fiscal and fiscal checks as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdCancelFiscalCheck() throws FiscalDeviceIOException;

    /**
     * Print a duplicate of the last fiscal check.
     * @throws FiscalDeviceIOException
     */
    public void cmdPrintCheckDuplicate() throws FiscalDeviceIOException;       

    /**
     * Opens non fiscal check.
     * @return Returns the number of fiscal and non fiscal checks as LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdOpenNonFiscalCheck() throws FiscalDeviceIOException;

    /**
     * Close the non fiscal check.
     * @return Returns the number of fiscal and non fiscal checks as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdCloseNonFiscalCheck() throws FiscalDeviceIOException;

    /**
     * On receipt of this command fiscal device prints a non fiscal text. Non fiscal check should be opened to be executed this command. 
     * @param text The text that will be printed.
     * @throws FiscalDeviceIOException
     */
    public void cmdPrintNonFiscalText(String text) throws FiscalDeviceIOException;

    /**
     * The subtotal of the fiscal check.
     * @param toPrint if it's true the subtotal is printed.
     * @param toDisplay if it's true the subtotal is displayed on the fiscal device display.
     * @param discountPercent [Optional] The discount/add in percent.
     * @return Returns the subtotal.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdSubTotal(boolean toPrint, boolean toDisplay, String discountPercent) throws FiscalDeviceIOException;

    /**
     * Prints the total of the fiscal check.
     * @return Returns the result of command execution and payment information as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdTotal() throws FiscalDeviceIOException;

    /**
     * Prints the total of the fiscal check.
     * @param firstRowText Text of the first row.
     * @param secondRowText Text of the second row
     * @param paymentType [Optional] Payment type: "P" - in cash;
     * @param amount [Optional] Amount for payment.
     * @return Returns the result of command execution and payment information as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdTotal(String firstRowText, String secondRowText, String paymentType, String amount) throws FiscalDeviceIOException;

    /**
     * This command shows whether it is possible to correct registered sales, and information on the accumulated turnovers in the individual tax groups.
     * @return Returns received data as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdCurrentCheckInfo() throws FiscalDeviceIOException;

    /**
     * On receipt of this command fiscal device returns information about the last daily report in the fiscal memory.
     * @param type The type of returned data. "T" - amounts with VAT; "N" - amounts without VAT. By default is "N".
     * @return Returns received data as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdLastFiscalRecord(String type) throws FiscalDeviceIOException;

    /**
     *  Prints diagnostic information of the fiscal device.
     * @throws FiscalDeviceIOException
     */
    public void cmdPrintDiagnosticInfo() throws FiscalDeviceIOException;

    /**
     *  Last number of the printed document.
     * @return Last document number.
     * @throws FiscalDeviceIOException
     */
    public int cmdLastDocNum()  throws FiscalDeviceIOException;

    /**
     *  Fiscal device status.
     * @return Returns the fiscal device status as a LinkedHashMap.
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdPrinterStatus() throws FiscalDeviceIOException;

    /**
     *  Diagnostic information.
     * @param toCalculate if it's true check sum is calculated.
     * @return Returns the diagnostic information of fiscal device as a LinkedHashMap
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdDiagnosticInfo(boolean toCalculate) throws FiscalDeviceIOException;

    /**
     * Get the fiscal device's date and time.
     * @return Returns date and time of fiscal device.
     * @throws FiscalDeviceIOException
     */
    public Date cmdGetDateTime() throws FiscalDeviceIOException;

    /**
     * Sets date and time of fiscal device. The date can't be earlier than last entry in the fiscal memory. 
     * @param year 
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @throws FiscalDeviceIOException
     */
    public void cmdSetDateTime(int year, int month, int day, int hour, int minute, int second) throws FiscalDeviceIOException;

    /**
     * Prints daily report.
     * @param type The report type. Values: 0 or 1 - Z report; 2 or 3 - X report; 8 - Z report by departments; 9 - X report by departments
     * @param toReset Specifies whether to reset Z report or not.
     * @return
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdReportDaily(String type, boolean toReset) throws FiscalDeviceIOException;

    /**
     *  Prints a report by dates. Date format: DDMMYY
     * @param detailed if it's true the report is extended
     * @param startDate start date of report
     * @param endDate end date of report
     * @throws FiscalDeviceIOException
     */
    public void cmdReportByDates(boolean detailed, String startDate, String endDate) throws FiscalDeviceIOException;

    /**
     *  Paper feed.
     * @param lines number of lines
     * @throws FiscalDeviceIOException
     */
    public void cmdPaperFeed(String lines) throws FiscalDeviceIOException;

    /**
     *  Sets operator's name. This command can't be executed if the sales by this operator are not reset.
     * @param clerkNum operator's number
     * @param password operator's password
     * @param name operator's new name
     * @throws FiscalDeviceIOException
     */
    public void cmdSetOperator(String clerkNum, String password, String name) throws FiscalDeviceIOException;

    /**
     *  Executes custom command.
     * @param cmd the command code
     * @param params the command parameters
     * @return Returns the response as a string.
     * @throws FiscalDeviceIOException
     */
    public String customCmd(int cmd, Object[] params) throws FiscalDeviceIOException;

    /**
     *  Resets the sales by operator.
     * @param clerkNum operator's number
     * @param password operator's password
     * @throws FiscalDeviceIOException
     */
    public void  cmdResetByOperator(String clerkNum, String password) throws FiscalDeviceIOException;

    /**
     * Get the fiscal device constants.
     * @return Returns the fiscal device constants as a LinkedHashMap
     * @throws FiscalDeviceIOException
     */
    public LinkedHashMap<String, Object> cmdGetConstants() throws FiscalDeviceIOException;
}
