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

import com.taliter.fiscal.device.FiscalPacket;
import com.taliter.fiscal.util.ByteFormatter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * An abstract base class for FiscalPacket implementations.
 */
public abstract class AbstractFiscalPacket implements FiscalPacket {

    /** Discard leap second*/
    private static final boolean DISCARD_LEAP_SECOND = true;

    /**Fiscal packet empty fields*/
    private static final byte[] EMPTY_FIELD = new byte[0];

    /**The fiscal packet fields*/
    private List fields = new ArrayList();

    /**
     * Compare two AbstractFiscalPacket objects.
     * @param object Compared object.
     * @return Returns true if the current object is equals to the passed object, or false if isn't.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof FiscalPacket)) {
            return false;
        }

        FiscalPacket p = (FiscalPacket) object;
        int s = getSize();
        if (s != p.getSize()) {
            return false;
        }
        for (int i = 0; i < s; i++) {
            if (!Arrays.equals(get(i), p.get(i))) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Creates a deep copy of this packet. Fields are themselves copied.
     * @return A copy of this instance.
     */
    @Override
    public Object clone() {
        AbstractFiscalPacket p;

        try {
            p = (AbstractFiscalPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e.toString());
        }

        List f = new ArrayList(fields.size());
        Iterator i = fields.iterator();

        while (i.hasNext()) {
            byte[] x = (byte[]) i.next();
            int l = x.length;
            byte[] y = new byte[l];
            System.arraycopy(x, 0, y, 0, l);
            f.add(y);
        }
        p.fields = f;

        return p;
    }
    
    /**
     * Convert the packet content to string.
     * @return The packet content as a string.
     */
    @Override
    public String toString() {
         return toHexString();
    }

    /**
     * Convert the packet content to hexadecimal notation.
     * @return The packet content as a string.
     */
    @Override
    public String toHexString() {
        StringBuffer b = new StringBuffer();
        b.append('{');
        for (int i = 0, s = getSize(); i < s; i++) {
            b.append(' ');
            ByteFormatter.toHexString(b, get(i));
        }
        b.append(' ').append('}');
        return b.toString();
    }
    /**
     * Convert the packet content to vector of escaped ASCII strings.
     * @return The packet content as a string.
     */
    @Override
    public String toASCIIString() {
        StringBuffer b = new StringBuffer();
        b.append('{');
        for (int i = 0, s = getSize(); i < s; i++) {
            b.append(' ');
            ByteFormatter.toASCIIString(b, get(i));
        }
        b.append(' ').append('}');
        return b.toString();
    }

    /**
     * Removes all the field of the packet.
     */
    @Override
    public void clear() {
        fields.clear();
    }

    /**
     * Set the field number in the packet. Adds empty fields to/removes fields from
     * the end of the packet depends on the current packet size.
     * @param size The new size of the fields.
     */
    @Override
    public void setSize(int size) {
        int s = fields.size();

        if (s < size) {
            do {
                fields.add(EMPTY_FIELD);
                s++;
            } while (s < size);
        } else if (s > size) {
            if (size > 0) {
                do {
                    s--;
                    fields.remove(s);
                } while (s > size);
            } else {
                if (size == 0) {
                    fields.clear();
                } else {
                    throw new IndexOutOfBoundsException();
                }
            }
        }
    }
    
    /**
     * Get the fields size.
     * @return The size of the fields.
     */
    @Override
    public int getSize() {
        return fields.size();
    }

    /**
     * Set an empty byte array at the specified field.
     * @param field The field index.
     */
    @Override
    public void clear(int field) {
        set(field, EMPTY_FIELD);
    }
    
    /**
     * Set a new value at a specified field. The new value is passed by reference.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    @Override
    public void set(int field, byte[] value) {
        if (value == null) {
            throw new NullPointerException();
        }

        int s = fields.size();

        if (field < s) {
            fields.set(field, value);
        } else {
            while (field > s) {
                fields.add(EMPTY_FIELD);
                s++;
            }

            fields.add(value);
        }
    }

    /**
     * Get the value from specified field.
     * @param field The field index.
     * @return A reference to the value of the specified field.
     */
    @Override
    public byte[] get(int field) {
        return (byte[]) fields.get(field);
    }

    /**
     * Get the length of the value from specified field.
     * @param field The field index.
     * @return The byte array length.
     */
    @Override
    public int getLength(int field) {
        return get(field).length;
    }

    // Field Copies
    /**
     * Set a copy of the byte array at a specified field.
     * @param field The field index.
     * @param value The new value as a byte array.
     */
    @Override
    public void setCopy(int field, byte[] value) {
        setCopy(field, value, 0, value.length);
    }

    /**
     * Set a copy of the byte array from a specified index and with specified length.
     * @param field The field index.
     * @param value The value as a byte array.
     * @param offset The first index of the array from which the copying should start.
     * @param length The number of elements which should be copied.
     */
    @Override
    public void setCopy(int field, byte[] value, int offset, int length) {
        byte[] x = new byte[length];
        System.arraycopy(value, offset, x, 0, length);
        set(field, x);
    }

    /**
     * Get a copy of the value from a specified field.
     * @param field The field index.
     * @return The copy of the value of the specified field.
     */
    @Override
    public byte[] getCopy(int field) {
        byte[] x = get(field);
        int l = x.length;
        byte[] y = new byte[l];
        System.arraycopy(x, 0, y, 0, l);
        return y;
    }
    
    /**
     * Get the value from a specified field and copy it to the provided array.
     * @param field The field index.
     * @param value The array in which should copy.
     * @return The real length of the value, and not this in the passed array.
     */
    @Override
    public int getCopy(int field, byte[] value) {
        return getCopy(field, value, 0, value.length);
    }

    /**
     * Get the value from a specified field and copy it to the provided array
     * from a specified index and with specified length.
     * @param field The field index.
     * @param value The array in which should copy.
     * @param offset The first index of the array from which the copying should start.
     * @param length The number of elements which should be copied.
     * @return The real length of the value, and not this in the passed array.
     */
    @Override
    public int getCopy(int field, byte[] value, int offset, int length) {
        byte[] x = get(field);
        int xl = x.length;
        System.arraycopy(x, 0, value, offset, length >= xl ? xl : length);
        return xl;
    }

    /**
     * Set a one byte value at the specified field 
     * @param field The field index.
     * @param value The new value of the field.
     */
    @Override
    public void setByte(int field, int value) {
        if (value < 0 || value > 0xFF) {
            throw new IllegalArgumentException();
        }
        set(field, new byte[]{(byte) value});
    }

    /**
     * Get a value with only one byte from specified field.
     * @param field The field index.
     * @return One byte value.
     */
    @Override
    public int getByte(int field) {
        byte[] v = get(field);
        if (v.length != 1) {
            throw new NumberFormatException();
        }
        return v[0] & 0xFF;
    }
    
    /**
     * Set a four bytes ASCII code of the passed value (in upper-case hexadecimal notation, most significant nibble first) at the specified field.
     * @param field The field index.
     * @param value The new value. It is truncated to 16 bits.    
     */
    @Override
    public void setHex16(int field, int value) // NOTE: Does not use setString().
    {
        if (value < 0 || value > 0xFFFFFFFF) {
            throw new IllegalArgumentException();
        }
        byte[] v = new byte[6];
        int p = 0;
        for (int k = 20; k >= 0; k -= 4) {
//            v[p++] = (byte) Character.toUpperCase(Character.forDigit((value >> k) & 0xF, 0x10));
            v[p++] = (byte) ((value >> k) & 0xF);
        }
        set(field, v);
    }
    
    /**
     * Get the 16-bit value from specified field of four bytes ASCII codes of the value
     * in case insensitive hexadecimal notation, most significant nibble first.
     * @param field The field index.
     * @return The 16-bit value of the specified field.
     */
    @Override
    public int getHex16(int field) // NOTE: Does not use getString().
    {
        byte[] v = get(field);
        if (v.length != 6) {
            throw new NumberFormatException();
        }
        int value = 0;
        int p = 0;
        for (int k = 20; k >= 0; k -= 4) {
            v[p] &= 0xFF;
            value |= v[p] << k;

            p++;
        }
        return value;
    }

    /**
     * Set a 32-bit number at a specified field.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    @Override
    public void setInt(int field, int value) {
        setString(field, String.valueOf(value));
    }

    /**
     * Get the value from specified field as a 32-bit number.
     * @param field The field index.
     * @return A 32-bit number of the specified field.
     */
    @Override
    public int getInt(int field) {
        return Integer.parseInt(getString(field));
    }
    
    /**
     * Set a 64-bit number at a specified field.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    @Override
    public void setLong(int field, long value) {
        setString(field, String.valueOf(value));
    }
    
    /**
     * Get the value from specified field as a 64-bit number.
     * @param field The field index.
     * @return A 64-bit number of the specified field.
     */
    @Override
    public long getLong(int field) {
        return Long.parseLong(getString(field));
    }

    /**
     * Set a floating point number at a specified field.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    @Override
    public void setFloat(int field, float value) {
        setString(field, new BigDecimal(String.valueOf(value)).toString());
    }

    /**
     * Get the value from specified field as a floating point number.
     * @param field The field index.
     * @return A floating point number of the specified field.
     */
    @Override
    public float getFloat(int field) {
        return Float.parseFloat(getString(field));
    }

    /**
     * Set a double precision floating point number at a specified field.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    @Override
    public void setDouble(int field, double value) {
        setString(field, new BigDecimal(String.valueOf(value)).toString());
    }
    
    
    /**
     * Get the value from a specified field as a double precision floating point number.
     * @param field The field index.
     * @return A double precision floating point number of the specified value.
     */
    @Override
    public double getDouble(int field) {
        return Double.parseDouble(getString(field));
    }

    /**
     * Set a BigInteger value at a specified field.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    @Override
    public void setBigInteger(int field, BigInteger value) {
        setString(field, value.toString());
    }

    /**
     * Get the value from a specified field as a BigInteger.
     * @param field The field index.
     * @return The value of the specified field as a BigInteger.
     */
    @Override
    public BigInteger getBigInteger(int field) {
        return new BigInteger(getString(field));
    }
    
    /**
     * Set a setBigDecimal value at a specified field.
     * @param field The field index.
     * @param value The new value of the specified field.
     */
    
    @Override
    public void setBigDecimal(int field, BigDecimal value) {
        setString(field, value.toString());
    }

   
    /**
     * Get the value from a specified field as a getBigDecimal.
     * @param field The field index.
     * @return The value of the specified field as a getBigDecimal.
     */
    @Override
    public BigDecimal getBigDecimal(int field) {
        return new BigDecimal(getString(field));
    }

    /**
     * An abstract method to set up the date and time of the fiscal device.
     * @param dateField The index of the date field in the fiscal packet.
     * @param year The value of the year in the new date. (4 digits value)
     * @param month The value of the month in the new date. (1 to 12)
     * @param day The value of the day of month in the new date. (1 to 31)
     * @param hour The value of the hour in the new time. (0 to 23)
     * @param minute The value of the minutes in the new time. (0 to 59)
     * @param second The value of the seconds in the new time. (0 to 59)
     */
    public abstract void setDateAndTime(int dateField, int year, int month, int day, int hour, int minute, int second);

    /**
     * An abstract method to get the fiscal device's date and time.
     * @param dateField The field index
     * @return A Calendar object with the fiscal device's date and time values.
     */
    public abstract Calendar getDateAndTime(int dateField);

    /**
     * Set the date and time field by year, month, day of month, hour, minute and second.
     * @param dateField The index of the date field in the fiscal packet.
     * @param timeField The index of the time field in the fiscal packet.
     * @param year The value of the year in the new date. (4 digits value)
     * @param month The value of the month in the new date. (1 to 12)
     * @param day The value of the day of month in the new date. (1 to 31)
     * @param hour The value of the hour in the new time. (0 to 23)
     * @param minute The value of the minutes in the new time. (0 to 59)
     * @param second The value of the seconds in the new time. (0 to 59)
     */
    @Override
    public void setDateAndTime(int dateField, int timeField, int year, int month, int day, int hour, int minute, int second) {
        setDateAndTime(dateField, year, month, day, hour, minute, second);
    }

    /**
     * Set the date field from a Date object. Uses default calendar and time-zone.
     * @param field The field index.
     * @param date The new date object.
     */
    @Override
    public void setDate(int field, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        setDateAndTime(field, year, month, day, hour, minute, second);
    }

    /**
     * Set the date and time fields from a Date object. Uses default calendar and time-zone.
     * @param dateField The dateField index.
     * @param timeField The timeField index.
     * @param date The new date object.
     */
    @Override
    public void setDateAndTime(int dateField, int timeField, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        setDateAndTime(dateField, year, month, day, hour, minute, second);
    }

    /**
     * Get the date and time fields as a Date object.
     * @param dateField The date field index.
     * @param timeField The time field index.
     * @return The fiscal device's date and time as a Date object.
     */
    @Override
    public Date getDateAndTime(int dateField, int timeField) {
        Calendar c = getDateAndTime(dateField);
        c.set(Calendar.MONTH, c.get(Calendar.MONTH));

        return c.getTime();
    }

    /**
     * Set a date field from a Calendar object.
     * @param field
     * @param calendar 
     */
    @Override
    public void setDate(int field, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        setDateAndTime(field, year, month, day, hour, minute, second);
    }

    /**
     * Gets the date from specified field and pass it to the Calendar object.
     * @param field The field index.
     * @param calendar The Calendar object.
     * @return The fiscal device's date as a Calendar object.
     */
    @Override
    public Calendar getDate(int field, Calendar calendar) {
        calendar.clear();
        calendar.set(Calendar.YEAR, getDateYear(field));
        calendar.set(Calendar.MONTH, getDateMonth(field));
        calendar.set(Calendar.DAY_OF_MONTH, getDateDay(1));

        return calendar;
    }

    /**
     * Set the fiscal device's date and time from a Calendar object.
     * @param dateField The date field index.
     * @param timeField The time field index.
     * @param calendar The source of the new date and time.
     */
    @Override
    public void setDateAndTime(int dateField, int timeField, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (DISCARD_LEAP_SECOND && (second == 60 || second == 61)) {
            second = 59;
        }
        setDateAndTime(dateField, year, month, day, hour, minute, second);
    }

    /**
     * Gets the date and time from a specified field and pass it to the Calendar object.
     * @param dateField The date field index.
     * @param timeField The time field index.
     * @param calendar The Calendar object.
     * @return The fiscal device's date and time as a Calendar object.
     */
    @Override
    public Calendar getDateAndTime(int dateField, int timeField, Calendar calendar) {

        calendar.clear();
        calendar.set(Calendar.YEAR, getDateYear(dateField));
        calendar.set(Calendar.MONTH, getDateMonth(dateField));
        calendar.set(Calendar.DAY_OF_MONTH, getDateDay(dateField));
        calendar.set(Calendar.HOUR_OF_DAY, getTimeHour(dateField));
        calendar.set(Calendar.MINUTE, getTimeMinute(dateField));
        calendar.set(Calendar.SECOND, getTimeSecond(dateField));

        return calendar;
    }
}
