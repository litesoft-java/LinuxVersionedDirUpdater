// This Source Code is in the Public Domain per: http://litesoft.org/License.txt
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class DirectoryUtils
{
    public static boolean existsThenAssertMutable( File pTargetPath, String pWhy )
    {
        if ( !pTargetPath.exists() )
        {
            return false;
        }
        if ( !acceptableMutable( pTargetPath ) )
        {
            throw new IllegalArgumentException( pWhy + pTargetPath.getAbsolutePath() );
        }
        return true;
    }

    public static boolean acceptableMutable( File pTargetPath )
    {
        return pTargetPath.isDirectory() && pTargetPath.canRead() && pTargetPath.canWrite();
    }

    public static void purge( File pDirectory )
            throws FileSystemException
    {
        Objects.assertNotNull( "Directory", pDirectory );
        if ( pDirectory.isDirectory() )
        {
            LowLevelDeleteAllEntries( pDirectory );
            if ( !pDirectory.delete() || pDirectory.exists() )
            {
                throw new FileSystemException( "Unable to delete: " + pDirectory.getAbsolutePath() );
            }
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void deleteAllEntries( File pDirectory )
            throws FileSystemException
    {
        Objects.assertNotNull( "Directory", pDirectory );
        if ( pDirectory.isDirectory() )
        {
            LowLevelDeleteAllEntries( pDirectory );
        }
    }

    private static void LowLevelDeleteAllEntries( File pDirectory )
            throws FileSystemException
    {
        String[] files = pDirectory.list();
        for ( String file : files )
        {
            File entry = new File( pDirectory, file );
            if ( entry.isDirectory() )
            {
                deleteIfExists( entry );
            }
            else
            {
                FileUtils.deleteIfExists( entry );
            }
        }
    }
}