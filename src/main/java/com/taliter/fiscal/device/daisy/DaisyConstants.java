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

/** 
 * A set of Daisy fiscal device command codes.
 */
public interface DaisyConstants {
        
    /**
     * Opens fiscal check. (0x30)   
     */
    public int CMD_OPEN_FISCAL_CHECK            = 0x30;

    /**
     * Closes the fiscal check. (0x48)
     */
    public int CMD_CLOSE_FISCAL_CHECK           = 0x38;

    /**
     * Prints fiscal text. (0x36)
     */
    public int CMD_PRINT_FISCAL_TEXT            = 0x36;

    /**
     * Sale. (0x31)
     */
    public int CMD_SELL                         = 0x31;

    /**
     * Sale by department. (0x8a)
     */
    public int CMD_SELL_DEPT                    = 0x8a;

    /**
     * Cancel the fiscal check. (0x82)
     */
    public int CMD_CANCEL_FISCAL_CHECK          = 0x82;
        
    /**
     * Print duplicate check. (0x6d)
     */
    public int CMD_PRINT_CHECK_DUBLICATE        = 0x6d;
        
    /**
     * Opens non-fiscal check. (0x26)
     */
    public int CMD_OPEN_NONFISCAL_CHECK         = 0x26;

    /**
     * Closes the non-fiscal check. (0x27)
     */
    public int CMD_CLOSE_NONFISCAL_CHECK        = 0x27;
        
    /**
     * Prints non-fiscal text.
     */
    public int CMD_PRINT_NONFISCAL_TEXT         = 0x2a;
        
    /**
     * Subtotal. (0x33)
     */
    public int CMD_SUBTOTAL                     = 0x33;
        
    /**
     * Total. (0x35)
     */
    public int CMD_TOTAL                        = 0x35;
        
    /**
     * Information about the current check. (0x67)
     */
    public int CMD_CURRENT_CHECK_INFO           = 0x67;
        
    /**
     * Information about the last saved daily report in the fiscal device. (0x40)
     */
    public int CMD_LAST_FISCAL_RECORD           = 0x40;

    /**
     * Prints the diagnostic information about the fiscal device. (0x47)
     */
    public int CMD_PRINT_DIAGNOSTIC_INFO        = 0x47;
        
    /**
     * The serial number of last printed document. (0x71)
     */
    public int CMD_LAST_DOC_NUM                 = 0x71;
        
    /**
     * Fiscal device status. (0x4a)
     */
    public int CMD_PRINTER_STATUS               = 0x4a;

    /**
     * Diagnostic information about the fiscal device. (0x5a)
     */
    public int CMD_DIAGNOSTIC_INFO              = 0x5a;
        
    /**
     * Fiscal device's date and time. (0x3e)
     */
    public int CMD_DATETIME_INFO                = 0x3e;

    /**
     * Sets the date and time of fiscal device. (0x3d)
     */
    public int CMD_SET_DATETIME                 = 0x3d;
        
    /**
     * Prints daily report. (0x45)
     */
    public int CMD_REPORT_DAILY                 = 0x45;
        
    /**
     * Prints report by start and end date. (0x5e)
     */
    public int CMD_REPORT_BY_DATE               = 0x5e;
        
    /**
     * Prints short report by start and end date. (0x4f)
     */
    public int CMD_REPORT_BY_DATE_SHORT         = 0x4f;
        
    /**
     * Moves the paper. (0x2c)
     */
    public int CMD_PAPER_FEED                   = 0x2c;

    /**
     * Sets operator's name. (0x66)
     */
    public int CMD_SET_OPERATOR                 = 0x66;

    /**
     * Resets the sales by operator. (0x68)
     */
    public int CMD_RESET_BY_OPERATOR            = 0x68;
        
    /**
     * Fiscal device's constants. (0x80)
     */
    public int CMD_GET_CONSTANTS                = 0x80;
}
