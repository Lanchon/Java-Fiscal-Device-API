package com.taliter.fiscal.device.icl;

import static com.taliter.fiscal.device.FiscalPacket.STYLE_NORMAL;
import com.taliter.fiscal.device.hasar.AbstractFiscalPacket;
import com.taliter.fiscal.util.ByteFormatter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This implementation describes the packet which is used by ICL fiscal device for communication.
 * ICLFiscalPacket is based on list of fields each of which is represented by byte array.
 * In the first field (index 0) is stored the command code, in the second field (index 1)
 * is stored a byte array containing the data received from the fiscal device or sent to the fiscal device. 
 * In the third field (index 2) is stored a byte array containing the fiscal device status.
 * @author nikolabintev@edabg.com
 */
public class ICLFiscalPacket extends AbstractFiscalPacket {
    
    /**Fiscal packet encoding*/
    private String encoding;
    
    /**Creates fiscal packet object
     * @param encoding fiscal packet encoding
     */
    public ICLFiscalPacket(String encoding) {
        this.encoding = encoding;
    }
    
    /**
     * Get the Fiscal packet's encoding
     * @return The encoding as a string
     */
    public String getEncoding() {
        return this.encoding;
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
     * Get fiscal device's date.
     * @param field The field index.
     * @return 
     */
    @Override
    public Date getDate(int field) {
        return getDateAndTime(field);
    }
    
    
    /**
     * Get the fiscal device's date and time.
     * @param field The field index 
     * @return A Calendar object with the fiscal device's date and time values
     */
    public Date getDateAndTime(int field) {
        String v = getString(field);
        
        if(v.length() != 17) {
            throw new NumberFormatException();
        }
        
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy hh:mm:ss"); 
        Date date = null;
        try {
            date = df.parse(v);
        } catch (ParseException ex) {
            throw new NumberFormatException();
        }
        
        return date;
    }

    /**
     * Set the fiscal device's date
     * @param field The field index
     * @param year The year (4 digits)
     * @param month The month (1 to 12)
     * @param day  The day of month (1 to 31)
     */
    public void setDate(int field, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        setDateAndTime(field, year, month, day, hour, minute, second);
    }

    /**
     * Get the year from the fiscal device date.
     * @param field The field index.
     * @return The year.
     */
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
     * Get the month of the year from the fiscal device date.
     * @param field The field index.
     * @return The month of the year.
     */
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
     * Get the day of the month from the fiscal device date. 
     * @param field The field index.
     * @return The day of the month.
     */
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
    public void setCommandCode(int value) {
        setByte(0, value);
    }

    /**
     * Get the command code of the packet.
     * @return The command code.
     */
    public int getCommandCode() {
        return getByte(0);
    }

    /**
     * Set fiscal status of this response packet.
     * @param value The value of the status.
     */
    public void setPrinterStatus(int value) {
        setHex16(2,value);
    }

    /**
     * Get the fiscal device status.
     * @return The fiscal device status as a 16-bit number.
     */
    public int getPrinterStatus() {
        return getHex16(2);
    }

    /**
     * Set fiscal status of this response packet.
     * @param value The value of the status.
     */
    public void setFiscalStatus(int value) {
        setHex16(2, value);
    }

    /**
     * Get the fiscal device status.
     * @return The fiscal device status as a 16-bit number.
     */
    public int getFiscalStatus() {
        return getHex16(2);
    }
}
