package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Zip File Handler for the Main updater class
 * <p/>
 * Created by randallb on 12/26/13.
 */
public class ZipFileHandler
{
    private static final String UNABLE_TO_MAKE = "Unable to make: ";
    private static final String EXPECTED_ZIP_FILE = "Expected Zip file: ";
    public static final String EXPECTED_VERSIONED_DIRECTORY = "Expected Versioned Directory: ";

    private final String mTarget;
    private final File mTargetPath;

    public ZipFileHandler( String pTarget, File pTargetPath )
    {
        mTarget = pTarget;
        mTargetPath = pTargetPath;
    }

    public boolean zipExists( String pRemoteVersion )
    {
        File zFile = new File( mTargetPath, pRemoteVersion + ".zip" );
        if ( zFile.exists() )
        {
            if ( !zFile.isFile() )
            {
                throw new IllegalArgumentException( EXPECTED_ZIP_FILE + zFile.getAbsolutePath() );
            }
            return true;
        }
        return false;
    }

    public boolean explodedDirectoryExists( String pRemoteVersion )
    {
        File zFile = new File( mTargetPath, pRemoteVersion );
        if ( zFile.exists() )
        {
            if ( !zFile.isDirectory() )
            {
                throw new IllegalArgumentException( EXPECTED_VERSIONED_DIRECTORY + zFile.getAbsolutePath() );
            }
            return true;
        }
        return false;
    }

    public void explodeZip( String pRemoteVersion )
    {
        // TODO: XXX
//        File zVersionedDir = new File( mTargetPath, pRemoteVersion + "-new" );
//        ZipFile zZipFile;
//        try
//        {
//            zZipFile = new ZipFile( new File( mTargetPath, pRemoteVersion + ".zip" ) );
//        }
//        catch ( IOException e )
//        {
//            throw new FileSystemException( e );
//        }
//
//        try
//        {
//            for ( Enumeration<? extends ZipEntry> zEnumeration = zZipFile.entries(); zEnumeration.hasMoreElements(); )
//            {
//                ZipEntry zEntry = zEnumeration.nextElement();
//                if ( !zEntry.isDirectory()) {
//                    String zName = zEntry.getName();
//                    File zOutputFile = new File( zVersionedDir, zName );
//                    DirectoryUtils.makeParentDirs(zOutputFile)
//                    if (!zOutputFile.getParentFile().mkdirs()) {
//
//                    }
//                    InputStream zInputStream = zZipFile.getInputStream( zEntry );
//                    IOUtils.copy( zInputStream, new FileOutputStream(  ) );
//
//                }
//            }
//        }
//        catch ( IOException e )
//        {
//            IOUtils.dispose( zZipFile );
//            throw new FileSystemException( e );
//        }
    }

    public void fetchZip( String pURL, String pRemoteVersion )
    {
        URLUtils.copyURLtoFile( pURL + "/" + mTarget + "/" + pRemoteVersion + ".zip", new File( mTargetPath, "asdhgashjfa.zip" ) );

        // TODO: XXX
    }
}
