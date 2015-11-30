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

import com.taliter.fiscal.util.ByteFormatter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the AbstractFiscalPacket interface.
 * Provides the properties and methods needed for the communication 
 * with fiscal devices which work with ICL protocol.
 */
public class ICLFiscalPacket extends AbstractFiscalPacket {

    /**
     * The encoding of fiscal packet as a string.
     */
    private final String encoding;

    /**
     * Crates an instance of the ICLFiscalPacket class.
     * @param encoding The encoding of the fiscal packet.
     */
    public ICLFiscalPacket(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get fiscal packet encoding.
     * @return The encoding as a string.
     */
    public String getEncoding() {
        return encoding;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append('{');
        for (int i = 0, s = getSize(); i < s; i++) {
            b.append(' ');
            if (i == 0) {
                byte[] f = get(i);
                if (f.length == 1) {
                    ByteFormatter.toHex(b, f[0]);
                } else {
                    ByteFormatter.toHexString(b, f);
                }
            } else {
                ByteFormatter.toASCIIString(b, get(i));
            }
        }
        b.append(' ').append('}');
        return b.toString();
    }
    
    /**
     * Convert the value of a specified field to vector of escaped ASCII strings.
     * @param field The field index.
     * @return The value as a string.
     */
    public String toASCIIString(int field) {
        String data = ByteFormatter.toASCIIString(get(field));
        
        if(data.contains("\"")) {
            data = data.replaceAll("\"", "");
        }
        
        return data;
    }
    

    /**
     * Set a string value at a specified field.
     * @param field The field index.
     * @param value The string value.
     */
    @Override
    public void setString(int field, String value) {
        byte[] f;
        try {
            f = value.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding (" + encoding + ")");
        }
        set(field, f);
    }
    
    /**
     * Set a string value at a specified field.
     * @param field The field index.
     * @param value The string value.
     * @param style The sting style.
     */
    @Override
    public void setString(int field, String value, int style) {
        byte[] f;
        try {
            f = value.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding (" + encoding + ")");
        }
        if (style != STYLE_NORMAL) {
            if (style < 0 || style > 15) {
                throw new IllegalArgumentException();
            }
            int l = f.length;
            byte[] x = new byte[l + 1];
            x[0] = (byte) (0xF0 | style);
            System.arraycopy(f, 0, x, 1, l);
            f = x;
        }
        set(field, f);
    }

    /**
     * Get the value at a specified field as a string
     * @param field The field value
     * @return The value as a string.
     */
    @Override
    public String getString(int field) {
        byte[] f = get(field);
        try {
            return f.length == 0 || (f[0] & 0xF0) != 0xF0 ? new String(f, encoding) : new String(f, 1, f.length - 1, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding (" + encoding + ")");
        }
    }

    /**
     * Get the string style at a specified field.
     * @param field The field index.
     * @return The string style as an Integer.
     */
    @Override
    public int getStringStyle(int field) {
        byte[] f = get(field);
        return f.length == 0 || (f[0] & 0xF0) != 0xF0 ? STYLE_NORMAL : f[0] & 0xF;
    }

    /**
     * Set the fiscal device's date and time.
     * @param dateField - The index of the date field in the fiscal packet. 
     * @param year - The value of the year in the new date. (4 digits value) 
     * @param month - The value of the month in the new date. (1 to 12) 
     * @param day - The value of the day of month in the new date. (1 to 31) 
     * @param hour - The value of the hour in the new time. (0 to 23) 
     * @param minute - The value of the minutes in the new time. (0 to 59) 
     * @param second - The value of the seconds in the new time. (0 to 59) 
     */
    // Date And Time Fields
    @Override
    public void setDateAndTime(int dateField, int year, int month, int day, int hour, int minute, int second) {
        
        if (month < 1 || month > 12 || day < 1 || day > 31) {
            throw new IllegalArgumentException();
        }
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) {
            throw new IllegalArgumentException();
        }
        
        String y = (Integer.toString(year)).substring(2, 4);
        String m = month < 10 ? m = "0" + month :Integer.toString(month);
        String d = day < 10 ? d = "0" + day :Integer.toString(day);

        String h = hour < 10 ? h = "0" + hour :Integer.toString(hour);
        String min = minute < 10 ? min = "0" + minute :Integer.toString(minute);
        String sec = second < 10 ? sec = "0" + second :Integer.toString(second);
        
        if(month < 10) {
            m = "0" + month;
        }
        
        if(day < 10) {
            d = "0" + day;
        }
        
        if(hour < 10) {
            h = "0" + hour;
        }
        
        String dateTime = d + "-" + m + "-" + y + " " + h + ":" + min + ":" + sec;
        
        this.setString(dateField, dateTime);
    }

    /**
     * Get the fiscal device's date and time.
     * @param field The field index 
     * @return A Calendar object with the fiscal device's date and time values
     */
    @Override
    public Calendar getDateAndTime(int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        
        String v = getString(field);

        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        int year = Integer.parseInt(v.substring(6, 8));
        int month = Integer.parseInt(v.substring(3, 5)) - 1;
        int day = Integer.parseInt(v.substring(0, 2));
        int hour = Integer.parseInt(v.substring(9, 11));
        int minute = Integer.parseInt(v.substring(12, 14));
        int second = Integer.parseInt(v.substring(15, 17));

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar;
    }
    
    /**
     * Set the fiscal device's date
     * @param field The field index
     * @param year The year (4 digits)
     * @param month The month (1 to 12)
     * @param day  The day of month (1 to 31)
     */
    @Override
    public void setDate(int field, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        setDateAndTime(field, year, month, day, hour, minute, second);
    }

    /**
     * Get the fiscal device's date.
     * @param field The field index.
     * @return The fiscal device date as a Date object.
     */
    @Override
    public Date getDate(int field) {
        Date date = null;
        
        String v = getString(field);
        DateFormat df = new SimpleDateFormat("dd-MM-yy");

        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        try {
            date = df.parse(v.substring(0, 8));
        } catch (ParseException ex) {
            Logger.getLogger(ICLFiscalPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return date;
    }
    
    /**
     * Get the year.
     * @param field The field index.
     * @return The year.
     */
    @Override
    public int getDateYear(int field) {
        String v = getString(field);
        
        if (v.length() != 17) {
            throw new NumberFormatException();
        }

        int year = Integer.parseInt(v.substring(6, 8));

        if (year < 0) {
            throw new NumberFormatException();
        }

        return year;
    }

    /**
     * Get the month of the year.
     * @param field The field index.
     * @return The month of the year.
     */
    @Override
    public int getDateMonth(int field) {
        String v = getString(field);
        
        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        int month = Integer.parseInt(v.substring(3, 5));
        
        if (month < 0) {
            throw new NumberFormatException();
        }
        
        return month;
    }
    
    /**
     * Get the day of the month. 
     * @param field The field index.
     * @return The day of the month.
     */
    @Override
    public int getDateDay(int field) {
        String v = getString(field);
        
        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        int day = Integer.parseInt(v.substring(0, 2));
        
        if (day < 0) {
            throw new NumberFormatException();
        }
        return day;
    }

    /**
     * Set the time of the fiscal device.
     * @param field The field index.
     * @param hour from 0 to 23
     * @param minute from 0 to 59
     * @param second from 0 to 59
     */
    @Override
    public void setTime(int field, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        setDateAndTime(field, year, month, day, hour, minute, second);
    }

    
    /**
     * Get the hour part of the time field (0 to 59)
     * @param field The field index.
     * @return The hour part of the time.
     */
    @Override
    public int getTimeHour(int field) {
        String v = getString(field);
        
        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        int h = Integer.parseInt(v.substring(9, 11));
        
        if (h < 0) {
            throw new NumberFormatException();
        }
        
        return h;
    }

    /**
     * Get the minute part of the time field (0 to 59)
     * @param field The field index.
     * @return The minute part of the time.
     */
    @Override
    public int getTimeMinute(int field) {
        String v = getString(field);
        
        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        int m = Integer.parseInt(v.substring(12, 14));
        
        if (m < 0) {
            throw new NumberFormatException();
        }
        
        return m;
    }

    /**
     * Get the second part of a time field (0 to 59)
     * @param field The field index.
     * @return The second part of the time.
     */
    @Override
    public int getTimeSecond(int field) {
        String v = getString(field);
        
        if (v.length() != 17) {
            throw new NumberFormatException();
        }
        
        int s = Integer.parseInt(v.substring(15, 17));
        
        if (s < 0) {
            throw new NumberFormatException();
        }
        
        return s;
    }

    /**
     * Set the command code of the packet.
     * @param value The command code value.
     */
    @Override
    public void setCommandCode(int value) {
        setByte(0, value);
    }

    /**
     * Get the command code of the packet.
     * @return The command code.
     */
    @Override
    public int getCommandCode() {
        return getByte(0);
    }

    /**
     * Set fiscal status of this response packet.
     * @param value The value of the status.
     */
    @Override
    public void setPrinterStatus(int value) {
        setHex16(2,value);
    }

    /**
     * Get the fiscal device status.
     * @return The fiscal device status as a 16-bit number.
     */
    @Override
    public int getPrinterStatus() {
        return getHex16(2);
    }
    
    /**
     * Set fiscal status of this response packet.
     * @param value The value of the status.
     */
    @Override
    public void setFiscalStatus(int value) {
        setHex16(2, value);
    }

    /**
     * Get the fiscal device status.
     * @return The fiscal device status as a 16-bit number.
     */
    @Override
    public int getFiscalStatus() {
        return getHex16(2);
    }
    
    /**
     * Get the fiscal device status.
     * @return The fiscal device status as a byte array.
     */
    public byte[] getFPStatus() {
        return get(2);
    }
}
