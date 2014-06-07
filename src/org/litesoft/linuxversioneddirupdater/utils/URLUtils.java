// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;
import java.net.*;

/**
 * IO related utils for URLS.
 * <p/>
 * Created by randallb on 12/27/13.
 */
public class URLUtils {
    public static String[] loadTextFile( String pURLToTextFile )
            throws FileSystemException {
        Strings.validateNotNullOrEmpty( "URLToTextFile", pURLToTextFile );
        try {
            return IOUtils.loadTextFileLines( new BufferedReader( new InputStreamReader( getInputStream( pURLToTextFile ) ) ) );
        }
        catch ( IOException e ) {
            throw new FileSystemException( e );
        }
    }

    public static void copyURLtoFile( String pURL, File pFile )
            throws FileSystemException {
        Strings.validateNotNullOrEmpty( "URL", pURL );
        Objects.assertNotNull( "File", pFile );
        InputStream zInputStream = getInputStream( pURL );
        OutputStream zOutputStream;
        try {
            zOutputStream = new FileOutputStream( pFile );
        }
        catch ( IOException e ) {
            IOUtils.dispose( zInputStream );
            throw new FileSystemException( e );
        }
        try {
            IOUtils.copy( zInputStream, zOutputStream ); // Will close on completion
        }
        catch ( IOException e ) {
            throw new FileSystemException( e );
        }
    }

    private static InputStream getInputStream( String pURLToTextFile ) {
        try {
            return new URL( pURLToTextFile ).openStream();
        }
        catch ( IOException e ) {
            throw new FileSystemException( e );
        }
    }
}
