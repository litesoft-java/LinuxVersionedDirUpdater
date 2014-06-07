// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class FileSystemException extends RuntimeException {
    public FileSystemException( String message ) {
        super( message );
    }

    public FileSystemException( String message, Throwable cause ) {
        super( message, cause );
    }

    public FileSystemException( IOException cause ) {
        super( cause );
    }
}
