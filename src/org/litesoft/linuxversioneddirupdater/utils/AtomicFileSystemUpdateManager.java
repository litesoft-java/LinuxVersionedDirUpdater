// This Source Code is in the Public Domain per: http://litesoft.org/License.txt
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

public class AtomicFileSystemUpdateManager
{
    private static final String UNABLE_TO_MAKE = "Unable to make: ";
    private static final String INVALID_PATH = "Invalid Directory: ";

    public static final String WRITE_SUFFIX = "-new";
    public static final String PREVIOUS_SUFFIX = "-prev";

    private final File mParentDirectory;
    private final String mChild;

    public AtomicFileSystemUpdateManager( File pParentDirectory, String pChild )
    {
        mParentDirectory = Objects.assertNotNull( "ParentDirectory", pParentDirectory );
        mChild = Strings.validateNotNullOrEmpty( "Child", pChild );
        File zFile = getWriteFile();
        if ( zFile.exists() )
        {
            if ( zFile.isDirectory() )
            {
                DirectoryUtils.purge( zFile );
            }
            else if ( zFile.isFile() )
            {
                FileUtils.deleteIfExists( zFile );
            }
            else
            {
                throw new IllegalStateException( "Neither a File nor a Directory: " + zFile.getPath() );
            }
        }
        if ( !DirectoryUtils.existsThenAssertMutable( mParentDirectory, INVALID_PATH ) && !mParentDirectory.mkdirs() )
        {
            throw new IllegalArgumentException( UNABLE_TO_MAKE + mParentDirectory.getPath() );
        }
    }

    public AtomicFileSystemUpdateManager( String pParentDirectory, String pChild )
    {
        this( new File( pParentDirectory ), pChild );
    }

    public File getWriteFile()
    {
        return new File( mParentDirectory, mChild + WRITE_SUFFIX );
    }

    public File getCurrentFile()
    {
        return new File( mParentDirectory, mChild );
    }

    private File getPreviousFile()
    {
        return new File( mParentDirectory, mChild + PREVIOUS_SUFFIX );
    }

    public void commit()
            throws FileSystemException
    {
        FileUtils.rollIn( zLink, new File( zTargetPath, UPDATE_LNK ), new File( zTargetPath, UPDATE_LNK + ".bak" ) );
    }
}