package com.taliter.fiscal.device.hasar;

import java.math.*;
import java.util.*;

import com.taliter.fiscal.device.*;
import com.taliter.fiscal.util.*;

/** An abstract base class for FiscalPacket implementations. */
public abstract class AbstractFiscalPacket implements FiscalPacket
{
	private static final boolean DISCARD_LEAP_SECOND = true;

	private static final byte[] EMPTY_FIELD = new byte[0];

	private List fields = new ArrayList();

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof FiscalPacket)) return false;
		FiscalPacket p = (FiscalPacket) o;
		int s = getSize();
		if (s != p.getSize()) return false;
		for (int i = 0; i < s; i++) if (!Arrays.equals(get(i), p.get(i))) return false;
		return true;
	}

	public Object clone()
	{
		AbstractFiscalPacket p;
		try { p = (AbstractFiscalPacket) super.clone(); }
		catch (CloneNotSupportedException e) { throw new Error(e.toString()); }
		List f = new ArrayList(fields.size());
		Iterator i = fields.iterator();
		while (i.hasNext())
		{
			byte[] x = (byte[]) i.next();
			int l = x.length;
			byte[] y = new byte[l];
			System.arraycopy(x, 0, y, 0, l);
			f.add(y);
		}
		p.fields = f;
		return p;
	}

	public String toString() { return toHexString(); }

	public String toHexString()
	{
		StringBuffer b = new StringBuffer();
		b.append('{');
		for (int i = 0, s = getSize(); i < s; i++)
		{
			b.append(' ');
			ByteFormatter.toHexString(b, get(i));
		}
		b.append(' ').append('}');
		return b.toString();
	}

	public String toASCIIString()
	{
		StringBuffer b = new StringBuffer();
		b.append('{');
		for (int i = 0, s = getSize(); i < s; i++)
		{
			b.append(' ');
			ByteFormatter.toASCIIString(b, get(i));
		}
		b.append(' ').append('}');
		return b.toString();
	}

	// Size

	public void clear() { fields.clear(); }

	public void setSize(int size)
	{
		int s = fields.size();
		if (s < size)
		{
			do { fields.add(EMPTY_FIELD); s++; }
			while (s < size);
		}
		else if (s > size)
		{
			if (size > 0)
			{
				do { s--; fields.remove(s); }
				while (s > size);
			}
			else
			{
				if (size == 0) fields.clear();
				else throw new IndexOutOfBoundsException();
			}
		}
	}

	public int getSize() { return fields.size(); }

	// Fields

	public void clear(int field) { set(field, EMPTY_FIELD); }

	public void set(int field, byte[] value)
	{
		if (value == null) throw new NullPointerException();
		int s = fields.size();
		if (field < s) fields.set(field, value);
		else
		{
			while (field > s) { fields.add(EMPTY_FIELD); s++; }
			fields.add(value);
		}
	}

	public byte[] get(int field) { return (byte[]) fields.get(field); }

	public int getLength(int field) { return get(field).length; }

	// Field Copies

	public void setCopy(int field, byte[] value) { setCopy(field, value, 0, value.length); }
	public void setCopy(int field, byte[] value, int offset, int length)
	{
		byte[] x = new byte[length];
		System.arraycopy(value, offset, x, 0, length);
		set(field, x);
	}

	public byte[] getCopy(int field)
	{
		byte[] x = get(field);
		int l = x.length;
		byte[] y = new byte[l];
		System.arraycopy(x, 0, y, 0, l);
		return y;
	}

	public int getCopy(int field, byte[] value) { return getCopy(field, value, 0, value.length); }
	public int getCopy(int field, byte[] value, int offset, int length)
	{
		byte[] x = get(field);
		int xl = x.length;
		System.arraycopy(x, 0, value, offset, length >= xl ? xl : length);
		return xl;
	}

	// Special Numeric Fields

	public void setByte(int field, int value)
	{
		if (value < 0 || value > 0xFF) throw new IllegalArgumentException();
		set(field, new byte[] { (byte) value });
	}

	public int getByte(int field)
	{
		byte[] v = get(field);
		if (v.length != 1) throw new NumberFormatException();
		return v[0] & 0xFF;
	}

	public void setHex16(int field, int value)	// NOTE: Does not use setString().
	{
		if (value < 0 || value > 0xFFFF) throw new IllegalArgumentException();
		byte[] v = new byte[4];
		int p = 0;
		for (int k = 12; k >= 0; k -= 4) v[p++] = (byte) Character.toUpperCase(Character.forDigit((value >> k) & 0xF, 0x10));
		set(field, v);
	}

	public int getHex16(int field)	// NOTE: Does not use getString().
	{
		byte[] v = get(field);
		if (v.length != 4) throw new NumberFormatException();
		int value = 0;
		int p = 0;
		for (int k = 12; k >= 0; k -= 4)
		{
			int d = Character.digit((char) (v[p++] & 0xFF), 0x10);
			if (d < 0) throw new NumberFormatException();
			value |= (d << k);
		}
		return value;
	}

	// Numeric Fields

	public void setInt(int field, int value) { setString(field, String.valueOf(value)); }
	public int getInt(int field) { return Integer.parseInt(getString(field)); }

	public void setLong(int field, long value) { setString(field, String.valueOf(value)); }
	public long getLong(int field) { return Long.parseLong(getString(field)); }

	public void setFloat(int field, float value) { setString(field, new BigDecimal(String.valueOf(value)).toString()); }
	public float getFloat(int field) { return Float.parseFloat(getString(field)); }

	public void setDouble(int field, double value) { setString(field, new BigDecimal(String.valueOf(value)).toString()); }
	public double getDouble(int field) { return Double.parseDouble(getString(field)); }

	public void setBigInteger(int field, BigInteger value) { setString(field, value.toString()); }
	public BigInteger getBigInteger(int field) { return new BigInteger(getString(field)); }

	public void setBigDecimal(int field, BigDecimal value) { setString(field, value.toString()); }
	public BigDecimal getBigDecimal(int field) { return new BigDecimal(getString(field)); }

	// Date And Time Fields

	public void setDateAndTime(int dateField, int timeField, int year, int month, int day, int hour, int minute, int second)
	{
		byte[] d = get(dateField);
		byte[] t = get(timeField);
		boolean success = false;
		try
		{
			setDate(dateField, year, month, day);
			setTime(timeField, hour, minute, second);
			success = true;
		}
		finally
		{
			if (!success)
			{
				set(dateField, d);
				set(timeField, t);
			}
		}
	}

	public void setDate(int field, Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setDate(field, calendar);
	}

	public Date getDate(int field)
	{
		Calendar calendar = Calendar.getInstance();
		getDate(field, calendar);
		return calendar.getTime();
	}

	public void setDateAndTime(int dateField, int timeField, Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setDateAndTime(dateField, timeField, calendar);
	}

	public Date getDateAndTime(int dateField, int timeField)
	{
		Calendar calendar = Calendar.getInstance();
		getDateAndTime(dateField, timeField, calendar);
		return calendar.getTime();
	}

	public void setDate(int field, Calendar calendar)
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		setDate(field, year, month, day);
	}

	public Calendar getDate(int field, Calendar calendar)
	{
		calendar.clear();
		calendar.set(Calendar.YEAR, getDateYear(field));
		calendar.set(Calendar.MONTH, getDateMonth(field) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, getDateDay(field));
		return calendar;
	}

	public void setDateAndTime(int dateField, int timeField, Calendar calendar)
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		if (DISCARD_LEAP_SECOND && (second == 60 || second == 61)) second = 59;
		setDateAndTime(dateField, timeField, year, month, day, hour, minute, second);
	}

	public Calendar getDateAndTime(int dateField, int timeField, Calendar calendar)
	{
		calendar.clear();
		calendar.set(Calendar.YEAR, getDateYear(dateField));
		calendar.set(Calendar.MONTH, getDateMonth(dateField) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, getDateDay(dateField));
		calendar.set(Calendar.HOUR_OF_DAY, getTimeHour(timeField));
		calendar.set(Calendar.MINUTE, getTimeMinute(timeField));
		calendar.set(Calendar.SECOND, getTimeSecond(timeField));
		return calendar;
	}
}
