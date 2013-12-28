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
    private final LinkUpdaterHandler mLinkUpdaterHandler;
    private final VersionedDirectoryHandler mVersionedDirectoryHandler;
    private boolean mTargetPathExists = false;
    private String mLocalVersion;

    public DirectoryHandler( String pVersionedRootPath, String pTarget )
    {
        mTarget = pTarget;
        mTargetPath = checkLocalPathValidity( pVersionedRootPath );
        mLinkUpdaterHandler = new LinkUpdaterHandler( mTarget, mTargetPath );
        mVersionedDirectoryHandler = new VersionedDirectoryHandler( mTarget, mTargetPath );
    }

    private File checkLocalPathValidity( String pVersionedRootPath )
    {
        File versionedTargetDirectory = new File( pVersionedRootPath, mTarget );

        if ( DirectoryUtils.existsThenAssertMutable( versionedTargetDirectory, INVALID_TARGET ) )
        {
            mTargetPathExists = true;

            File currentDirectory = new File( versionedTargetDirectory, "current" );

            if ( DirectoryUtils.existsThenAssertMutable( currentDirectory, INVALID_TARGET ) )
            {
                File versionTextFile = new File( currentDirectory, "version.txt" );

                if ( FileUtils.existsThenAssertMutable( versionTextFile, INVALID_TARGET ) )
                {
                    mLocalVersion = getVersion( versionTextFile );
                }
            }
        }
        return versionedTargetDirectory;
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
        String zLinkVersion = mLinkUpdaterHandler.getLinkVersion();
        if ( !pRemoteVersion.equals( zLinkVersion ) )
        {
            mVersionedDirectoryHandler.ensureDirectory( pURL, pRemoteVersion );
            mLinkUpdaterHandler.create( pRemoteVersion );
        }
    }
}
