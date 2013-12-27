// This Source Code is in the Public Domain per: http://litesoft.org/License.txt
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class FileUtil
{
    public static final String UTF_8 = "UTF-8";

    public static BufferedWriter createWriter( File pFile, boolean pAppend )
            throws IOException
    {
        Objects.assertNotNull( "File", pFile );
        return new BufferedWriter( new OutputStreamWriter( new FileOutputStream( pFile, pAppend ), UTF_8 ) );
    }

    public static BufferedReader createReader( File pFile )
            throws IOException
    {
        Objects.assertNotNull( "File", pFile );
        return new BufferedReader( new InputStreamReader( new FileInputStream( pFile ), UTF_8 ) );
    }
}