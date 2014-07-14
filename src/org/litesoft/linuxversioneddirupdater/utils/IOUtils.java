// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;
import java.util.*;

/**
 * Collection of methods to operate on IOstreams/readers/files/etc.
 */
public class IOUtils {

    public static final String UTF_8 = "UTF-8";

    public static BufferedReader createReader( InputStream pInputStream )
            throws IOException {
        return new BufferedReader( new InputStreamReader( pInputStream, UTF_8 ) );
    }

    public static BufferedWriter createWriter( OutputStream pOS )
            throws IOException {
        return new BufferedWriter( new OutputStreamWriter( pOS, UTF_8 ) );
    }

    public static String[] loadTextFileLines( BufferedReader pReader )
            throws IOException {
        List<String> lines = new LinkedList<>();
        try {
            for ( String line; null != (line = pReader.readLine()); ) {
                lines.add( line );
            }
        }
        finally {
            Closeables.dispose( pReader );
        }
        return lines.toArray( new String[lines.size()] );
    }

    public static void copy( InputStream pInputStream, OutputStream pOutputStream )
            throws IOException {
        byte[] buffer = new byte[4096];
        try {
            for ( int zBytesRead; -1 != (zBytesRead = pInputStream.read( buffer )); ) {
                if ( zBytesRead != 0 ) {
                    pOutputStream.write( buffer, 0, zBytesRead );
                }
            }
            Closeable zCloseable = pOutputStream;
            pOutputStream = null;
            zCloseable.close();
        }
        finally {
            Closeables.dispose( pInputStream );
            Closeables.dispose( pOutputStream );
        }
    }
}
