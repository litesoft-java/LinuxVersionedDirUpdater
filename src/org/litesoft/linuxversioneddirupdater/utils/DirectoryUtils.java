// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class DirectoryUtils {
    public static void ensureExistsAndMutable( File pDirectory, String pWhy ) {
        if ( !pDirectory.exists() ) {
            makeDirs( pDirectory );
        }
        if ( !acceptableMutable( pDirectory ) ) {
            throw new IllegalArgumentException( pWhy + pDirectory.getAbsolutePath() );
        }
    }

    public static boolean existsThenAssertMutable( File pDirectory, String pWhy ) {
        if ( !pDirectory.exists() ) {
            return false;
        }
        if ( !acceptableMutable( pDirectory ) ) {
            throw new IllegalArgumentException( pWhy + pDirectory.getAbsolutePath() );
        }
        return true;
    }

    public static void makeDirs( File pDirectory ) {
        if ( !pDirectory.mkdirs() && !pDirectory.isDirectory() ) {
            throw new FileSystemException( "Unable to create: " + pDirectory.getAbsolutePath() );
        }
    }

    public static boolean acceptableMutable( File pDirectory ) {
        return pDirectory.isDirectory() && pDirectory.canRead() && pDirectory.canWrite();
    }

    public static void purge( File pDirectory )
            throws FileSystemException {
        Objects.assertNotNull( "Directory", pDirectory );
        if ( pDirectory.isDirectory() ) {
            LowLevelDeleteAllEntries( pDirectory );
            if ( !pDirectory.delete() || pDirectory.exists() ) {
                throw new FileSystemException( "Unable to delete: " + pDirectory.getAbsolutePath() );
            }
        }
    }

    private static void LowLevelDeleteAllEntries( File pDirectory )
            throws FileSystemException {
        String[] files = pDirectory.list();
        for ( String file : files ) {
            File entry = new File( pDirectory, file );
            if ( entry.isDirectory() ) {
                purge( entry );
            } else {
                FileUtils.deleteIfExists( entry );
            }
        }
    }
}
