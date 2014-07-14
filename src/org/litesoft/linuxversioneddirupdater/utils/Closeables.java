// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class Closeables {
    public static void dispose( Closeable pCloseable ) {
        if ( pCloseable != null ) {
            try {
                pCloseable.close();
            }
            catch ( IOException e ) {
                // Whatever
            }
        }
    }
}
