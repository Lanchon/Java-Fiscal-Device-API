package com.taliter.fiscal.util;

/** A formatter for bytes and byte arrays. */
public final class ByteFormatter
{
	private ByteFormatter() {}

	// To Hex

	public static String toHexString(byte[] b) { return toHexString(b, 0, b.length); }
	public static String toHexString(byte[] b, int off, int len) { return toHexString(new StringBuffer(), b, off, len).toString(); }
	public static StringBuffer toHexString(StringBuffer buffer, byte[] b) { return toHexString(buffer, b, 0, b.length); }
	public static StringBuffer toHexString(StringBuffer buffer, byte[] b, int off, int len)
	{
		if (off < 0 || len < 0 || off > b.length - len) throw new ArrayIndexOutOfBoundsException();
		buffer.append('{');
		for (int i = off, l = off + len; i < l; i++) 
		{
			buffer.append(' ');
			toHex(buffer, b[i]);
		}
		buffer.append(' ');
		buffer.append('}');
		return buffer;
	}

	public static String toHex(int b) { return toHex(new StringBuffer(), b).toString(); }
	public static StringBuffer toHex(StringBuffer buffer, int b)
	{
		buffer.append(Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 0x10)));
		buffer.append(Character.toUpperCase(Character.forDigit(b & 0xF, 0x10)));
		return buffer;
	}

	// To ASCII

	public static String toASCIIString(byte[] b) { return toASCIIString(b, 0, b.length); }
	public static String toASCIIString(byte[] b, int off, int len) { return toASCIIString(new StringBuffer(), b, off, len).toString(); }
	public static StringBuffer toASCIIString(StringBuffer buffer, byte[] b) { return toASCIIString(buffer, b, 0, b.length); }
	public static StringBuffer toASCIIString(StringBuffer buffer, byte[] b, int off, int len)
	{
		if (off < 0 || len < 0 || off > b.length - len) throw new ArrayIndexOutOfBoundsException();
		buffer.append('"');
		for (int i = off, l = off + len; i < l; i++) toASCII(buffer, b[i]);
		buffer.append('"');
		return buffer;
	}

	public static String toASCII(int b) { return toASCII(new StringBuffer(), b).toString(); }
	public static StringBuffer toASCII(StringBuffer buffer, int b)
	{
		if (b == '\\') buffer.append('\\').append('\\');
		else if (b == '"') buffer.append('\\').append('"');
		else if (b >= 0x20 && b <= 0x7F) buffer.append((char) b);
		else toHex(buffer.append('\\').append('x'), b);
		return buffer;
	}
}
