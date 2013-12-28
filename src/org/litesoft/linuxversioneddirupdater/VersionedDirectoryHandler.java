package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;

/**
 * Zip File Handler for the Main updater class
 * <p/>
 * Created by randallb on 12/26/13.
 */
public class VersionedDirectoryHandler
{
    private static final String UNABLE_TO_MAKE = "Unable to make: ";
    private static final String EXPECTED_ZIP_FILE = "Expected Zip file: ";
    public static final String EXPECTED_VERSIONED_DIRECTORY = "Expected Versioned Directory: ";

    private final String mTarget;
    private final File mTargetPath;

    public VersionedDirectoryHandler( String pTarget, File pTargetPath )
    {
        mTarget = pTarget;
        mTargetPath = pTargetPath;
    }

    public boolean zipExists( String pRemoteVersion )
    {
        return FileUtils.existsThenAssertMutable( new File( mTargetPath, pRemoteVersion + ".zip" ), EXPECTED_ZIP_FILE );
    }

    public boolean explodedDirectoryExists( String pRemoteVersion )
    {
        return DirectoryUtils.existsThenAssertMutable( new File( mTargetPath, pRemoteVersion ), EXPECTED_VERSIONED_DIRECTORY );
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

    public void ensureDirectory( String pURL, String pRemoteVersion )
    {
        if ( !explodedDirectoryExists( pRemoteVersion ) )
        {
            ensureZipFile( pURL, pRemoteVersion );
            explodeZip( pRemoteVersion );
        }
    }

    private void ensureZipFile( String pURL, String pRemoteVersion )
    {
        if ( !zipExists( pRemoteVersion ) )
        {
            fetchZip( pURL, pRemoteVersion );
        }
    }
}
