package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

/**
 * Collection of methods to operate on objects
 * Created by randallb on 12/27/13.
 */
public class Objects
{
    public static <T> T assertNotNull( String pWhat, T pToCheck )
    {
        if ( pToCheck == null )
        {
            throw new IllegalArgumentException( pWhat + " is null" );
        }
        return pToCheck;
    }
}
