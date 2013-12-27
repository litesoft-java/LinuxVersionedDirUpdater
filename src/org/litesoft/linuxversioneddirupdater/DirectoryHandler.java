package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;

/**
 * Directory Handler for the Main updater class
 * <p/>
 * Created by randallb on 12/26/13.
 */
public class DirectoryHandler
{
    private static final String INVALID_TARGET = "Invalid target path: ";
    private static final String INVALID_REMOTE = "Invalid remote path: ";
    private static final String UNABLE_TO_MAKE = "Unable to make: ";

    private final String mTarget;
    private final File mTargetPath;
    private final LinkFileHandler mLinkFileHandler;
    private final ZipFileHandler mZipFileHandler;
    private boolean mTargetPathExists = false;
    private String mLocalVersion;

    public DirectoryHandler( String pVersionedRootPath, String pTarget )
    {
        mTarget = pTarget;
        mTargetPath = checkLocalPathValidity( pVersionedRootPath );
        mLinkFileHandler = new LinkFileHandler( mTarget, mTargetPath );
        mZipFileHandler = new ZipFileHandler( mTarget, mTargetPath );
    }

    private File checkLocalPathValidity( String pVersionedRootPath )
    {
        File targetPath = new File( pVersionedRootPath, mTarget );

        if ( !targetPath.exists() )
        {
            return targetPath;
        }

        if ( !DirectoryUtils.acceptableMutableDirectory( targetPath ) )
        {
            throw new IllegalArgumentException( INVALID_TARGET + mTarget );
        }
        mTargetPathExists = true;

        File currentDirectory = new File( targetPath, "current" );

        if ( !currentDirectory.exists() )
        {
            return targetPath;
        }

        if ( !DirectoryUtils.acceptableMutableDirectory( currentDirectory ) )
        {
            throw new IllegalArgumentException( INVALID_TARGET + mTarget + "/current" );
        }

        File versionTextFile = new File( currentDirectory, "version.txt" );

        if ( !versionTextFile.exists() )
        {
            return targetPath;
        }

        if ( !versionTextFile.isFile() )
        {
            throw new IllegalArgumentException( INVALID_TARGET + mTarget + "/current/version.txt" );
        }

        mLocalVersion = getVersion( versionTextFile );

        return targetPath;
    }

    private String getVersion( File pVersionTextFile )
    {
        return Strings.getFirstEntry( FileUtils.loadTextFile( pVersionTextFile ) );
    }

    private String getVersion( String pURLToVersionTextFile )
    {
        return Strings.getFirstEntry( URLUtils.loadTextFile( pURLToVersionTextFile ) );
    }

    public boolean update( String pURL, String pDeploymentVersion )
    {
        String zURLToVersionTextFile = pURL + "/" + mTarget + "/" + pDeploymentVersion + ".txt";
        String remoteVersion = getVersion( zURLToVersionTextFile );
        if ( remoteVersion == null )
        {
            throw new IllegalArgumentException( INVALID_REMOTE + zURLToVersionTextFile );
        }

        if ( remoteVersion.equals( mLocalVersion ) )
        {
            return false;
        }
        updateLinkFileTo( pURL, remoteVersion );
        return true;
    }

    private void updateLinkFileTo( String pURL, String pRemoteVersion )
    {
        if ( !mTargetPathExists )
        {
            if ( !mTargetPath.mkdirs() )
            {
                throw new FileSystemException( UNABLE_TO_MAKE + mTargetPath );
            }
            mTargetPathExists = true;
        }
        String zLinkVersion = mLinkFileHandler.getLinkVersion();
        if ( !pRemoteVersion.equals( zLinkVersion ) )
        {
            ensureExplodedDirectory( pURL, pRemoteVersion );
            mLinkFileHandler.create( pRemoteVersion );
        }
    }

    private void ensureExplodedDirectory( String pURL, String pRemoteVersion )
    {
        if ( !mZipFileHandler.explodedDirectoryExists( pRemoteVersion ) )
        {
            ensureZipFile( pURL, pRemoteVersion );
            mZipFileHandler.explodeZip( pRemoteVersion );
        }
    }

    private void ensureZipFile( String pURL, String pRemoteVersion )
    {
        if ( !mZipFileHandler.zipExists( pRemoteVersion ) )
        {
            mZipFileHandler.fetchZip( pURL, pRemoteVersion );
        }
    }
}
