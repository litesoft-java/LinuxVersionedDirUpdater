// This Source Code is in the Public Domain per: http://litesoft.org/License.txt
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class DirectoryUtils
{
    public static boolean acceptableMutableDirectory( File pTargetPath )
    {
        return pTargetPath.isDirectory() && pTargetPath.canRead() && pTargetPath.canWrite();
    }
}