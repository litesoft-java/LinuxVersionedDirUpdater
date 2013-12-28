// This Source Code is in the Public Domain per: http://litesoft.org/License.txt
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class DirectoryUtils
{
    public static boolean existsThenAssertMutable( File pTargetPath, String pWhy )
    {
        if (!pTargetPath.exists()) {
            return false;
        }
        if (!acceptableMutable( pTargetPath )) {
            throw new IllegalArgumentException( pWhy + pTargetPath.getAbsolutePath() );
        }
        return true;
    }

    public static boolean acceptableMutable( File pTargetPath )
    {
        return pTargetPath.isDirectory() && pTargetPath.canRead() && pTargetPath.canWrite();
    }

    public static void purge( File pDirectory )
    {

    }
}