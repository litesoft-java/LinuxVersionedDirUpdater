// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;
import java.util.*;

/**
 * Collection of methods to operate on IOstreams/readers/files/etc.
 * Created by randallb on 12/27/13.
 */
public class IOUtils
{

    public static void dispose( Closeable pCloseable )
    {
        if ( pCloseable != null )
        {
            try
            {
                pCloseable.close();
            }
            catch ( IOException ignore )
            {
                //ignore it
            }
        }
    }

    public static String[] loadTextFileLines( BufferedReader pReader )
            throws IOException
    {
        List<String> lines = new LinkedList<>();
        try
        {
            for ( String line; null != (line = pReader.readLine()); )
            {
                lines.add( line );
            }
        }
        finally
        {
            dispose( pReader );
        }
        return lines.toArray( new String[lines.size()] );
    }

    public static void copy( InputStream pInputStream, OutputStream pOutputStream )
            throws IOException
    {
        byte[] buffer = new byte[4096];
        try
        {
            for ( int zBytesRead; -1 != (zBytesRead = pInputStream.read( buffer )); )
            {
                if ( zBytesRead != 0 )
                {
                    pOutputStream.write( buffer, 0, zBytesRead );
                }
            }
            Closeable zCloseable = pOutputStream;
            pOutputStream = null;
            zCloseable.close();
        }
        finally
        {
            dispose( pInputStream );
            dispose( pOutputStream );
        }
    }
}
