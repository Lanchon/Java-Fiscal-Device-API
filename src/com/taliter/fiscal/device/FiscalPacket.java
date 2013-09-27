package com.taliter.fiscal.device;

import java.io.*;
import java.math.*;
import java.util.*;

/** A request to or response from a fiscal device.
A packet is a vector of byte arrays called fields. Fields cannot be null.
Fields are accessed using a zero-based index up to getSize() - 1.
All operations that set values of fields may grow the size of the vector as needed,
adding empty fields if necessary. */
public interface FiscalPacket extends Serializable
{
	/** Normal text style. */
	public int STYLE_NORMAL = 0;
	/** Emphasized text style. */
	public int STYLE_EMPHASIZED = 1;
	/** Double height text style. */
	public int STYLE_DOUBLE_HEIGHT = 2;
	/** Double width text style. */
	public int STYLE_DOUBLE_WIDTH = 4;
	/** Underlined text style. */
	public int STYLE_UNDERLINED = 8;

	/** Returns true if this and the specified packet have the same size
	and their corresponding fields have the same sequence of bytes. */
	public boolean equals(Object o);
	/** Returns a deep copy of this packet. Fields are themselves copied. */
	public Object clone();
	/** Returns a string representation of the packet contents. */
	public String toString();
	/** Returns a string representation of the packet contents in hexadecimal notation. */
	public String toHexString();
	/** Returns a string representation of the packet contents as a vector of escaped ASCII strings. */
	public String toASCIIString();

	// Size

	/** Remove all fields. */
	public void clear();

	/** Set the number of fields. Add empty fields to the end or remove fields from the end of the packet as needed. */
	public void setSize(int size);
	/** Get the number of fields. */
	public int getSize();

	// Fields

	/** Replace field with empty byte array. */
	public void clear(int field);

	/** Set the value of a field. A reference to the passed value is kept (value is not copied). */
	public void set(int field, byte[] value);
	/** Get the value of a field. A reference to the value is returned (the value is not copied). */
	public byte[] get(int field);

	/** Get the length of the specified byte array. */
	public int getLength(int field);

	// Field Copies

	/** Set the value of a field to a copy of the passed value. */
	public void setCopy(int field, byte[] value);
	/** Set the value of a field to a copy of the passed value. */
	public void setCopy(int field, byte[] value, int offset, int length);
	/** Get a copy of the value of a field. */
	public byte[] getCopy(int field);
	/** Copy at most <code>value.length</code> bytes of the value of a field to the provided array.
	@return the real length of the field which may be more than what was actually copied. */
	public int getCopy(int field, byte[] value);
	/** Copy at most <code>length</code> bytes of the value of a field to the provided array.
	@return the real length of the field which may be more than what was actually copied. */
	public int getCopy(int field, byte[] value, int offset, int length);

	// String Fields

	/** Set a string field. The style used is STYLE_NORMAL. */
	public void setString(int field, String value);
	/** Set a string field in the specified style. */
	public void setString(int field, String value, int style);
	/** Get a string field. */
	public String getString(int field);
	/** Get the style of a string field. */
	public int getStringStyle(int field);

	// Special Numeric Fields

	/** Set a field of 1 byte. */
	public void setByte(int field, int value);
	/** Get a field of 1 byte. */
	public int getByte(int field);

	/** Set a field of 4 bytes representing the ASCII codes of the passed value in upper-case hexadecimal notation,
	most significant nibble first. The value is truncated to 16 bits. */
	public void setHex16(int field, int value);
	/** Get a 16-bit value from a field of 4 bytes representing the ASCII codes of the value in case insensitive hexadecimal notation,
	most significant nibble first. */
	public int getHex16(int field);

	// Numeric Fields

	/** Set a numeric field from an integer. */
	public void setInt(int field, int value);
	/** Get a numeric field as an integer. */
	public int getInt(int field);

	/** Set a numeric field from a long. */
	public void setLong(int field, long value);
	/** Get a numeric field as a long. */
	public long getLong(int field);

	/** Set a numeric field from a float. */
	public void setFloat(int field, float value);
	/** Get a numeric field as a float. */
	public float getFloat(int field);

	/** Set a numeric field from a double. */
	public void setDouble(int field, double value);
	/** Get a numeric field as a double. */
	public double getDouble(int field);

	/** Set a numeric field from a BigInteger. */
	public void setBigInteger(int field, BigInteger value);
	/** Get a numeric field as a BigInteger. */
	public BigInteger getBigInteger(int field);

	/** Set a numeric field from a BigDecimal. */
	public void setBigDecimal(int field, BigDecimal value);
	/** Get a numeric field as a BigDecimal. */
	public BigDecimal getBigDecimal(int field);

	// Date And Time Fields

	/** Set a date field by specifying year (full 4 digit value), month of year (1 to 12) and day of month (1 to 31). */
	public void setDate(int field, int year, int month, int day);
	/** Get the year part of a date field (full 4 digit value). */
	public int getDateYear(int field);
	/** Get the month of year part of a date field (1 to 12). */
	public int getDateMonth(int field);
	/** Get the day of month part of a date field (1 to 31). */
	public int getDateDay(int field);

	/** Set a time field by specifying hour (0 to 23), minute (0 to 59) and second (0 to 59). */
	public void setTime(int field, int hour, int minute, int second);
	/** Get the hour part of a time field (0 to 23). */
	public int getTimeHour(int field);
	/** Get the minute part of a time field (0 to 59). */
	public int getTimeMinute(int field);
	/** Get the second part of a time field (0 to 59). */
	public int getTimeSecond(int field);

	/** Set a date field and a time field by specifying year (full 4 digit value), month of year (1 to 12), day of month (1 to 31),
	hour (0 to 23), minute (0 to 59) and second (0 to 59). */
	public void setDateAndTime(int dateField, int timeField, int year, int month, int day, int hour, int minute, int second);

	/** Set a date field from a Date object. Uses default calendar and time-zone. */
	public void setDate(int field, Date date);
	/** Get a date field as a Date object. Uses default calendar and time-zone. */
	public Date getDate(int field);

	/** Set a date field and a time field from a Date object. Uses default calendar and time-zone. */
	public void setDateAndTime(int dateField, int timeField, Date date);
	/** Get a date field and a time field as a Date object. Uses default calendar and time-zone. */
	public Date getDateAndTime(int dateField, int timeField);

	/** Set a date field from a Calendar object. */
	public void setDate(int field, Calendar calendar);
	/** Get the contents of a date field into the passed Calendar object.
	@return the passed calendar. */
	public Calendar getDate(int field, Calendar calendar);

	/** Set a date field and a time field from a Calendar object. */
	public void setDateAndTime(int dateField, int timeField, Calendar calendar);
	/** Get the contents of a date field and a time field into the passed Calendar object.
	@return the passed calendar. */
	public Calendar getDateAndTime(int dateField, int timeField, Calendar calendar);

	// Special Fields

	/** Set the command code of this packet. */
	public void setCommandCode(int value);
	/** Get the command code of this packet. */
	public int getCommandCode();

	/** Set the printer status of this response packet. */
	public void setPrinterStatus(int value);
	/** Get the printer status of this response packet. */
	public int getPrinterStatus();

	/** Set the fiscal status of this response packet. */
	public void setFiscalStatus(int value);
	/** Get the fiscal status of this response packet. */
	public int getFiscalStatus();
}
