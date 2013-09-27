package com.taliter.fiscal.port;

import java.io.*;

/** A bidirectional stream channel. */
public interface FiscalPort
{
	/** Open the port. */
	public void open() throws Exception;
	/** Close the port. Does nothing if already closed. */
	public void close() throws Exception;

	/** Returns true if the port is open. */
	public boolean isOpen();

	/** Set the receive timeout. The port must be open. -1 means no timeout (default after open()). */
	public void setTimeout(int ms) throws Exception;
	/** Get the receive timeout. The port must be open. -1 means no timeout (default after open()). */
	public int getTimeout() throws Exception;

	/** Get the input stream. The port must be open. */
	public InputStream getInputStream() throws IOException;
	/** Get the output stream. The port must be open. */
	public OutputStream getOutputStream() throws IOException;

	/** Flush the output stream and wait until output is done. */
	public void flushAndWait() throws IOException;
}
