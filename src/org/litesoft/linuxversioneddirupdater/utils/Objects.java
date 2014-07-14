// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

/**
 * Collection of methods to operate on objects
 */
public class Objects {
    public static <T> T assertNotNull( String pWhat, T pToCheck ) {
        if ( pToCheck == null ) {
            throw new IllegalArgumentException( pWhat + " is null" );
        }
        return pToCheck;
    }
}
