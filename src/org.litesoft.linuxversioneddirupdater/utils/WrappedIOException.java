package org.litesoft.util;

import java.io.*;

public class WrappedIOException extends RuntimeException
{
    public WrappedIOException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public WrappedIOException( IOException cause )
    {
        super( cause );
    }
}
