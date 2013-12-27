package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

/**
 * Collection of methods to operate on IOstreams/readers/files/etc.
 * Created by randallb on 12/27/13.
 */
public class IOUtils
{

    public static void dispose( Closeable pCloseable )
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
