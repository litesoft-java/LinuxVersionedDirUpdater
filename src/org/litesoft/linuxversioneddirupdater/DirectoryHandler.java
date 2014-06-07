// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;

/**
 * Directory Handler for the Main updater class
 * <p/>
 * Created by randallb on 12/26/13.
 */
public class DirectoryHandler {
    private static final String INVALID_TARGET = "Invalid target path: ";
    private static final String INVALID_REMOTE = "Invalid remote path: ";

    private final boolean mCritical;
    private final String mTarget;
    private final File mTargetDirectory;
    private final LinkUpdaterHandler mLinkUpdaterHandler;
    private final VersionedDirectoryHandler mVersionedDirectoryHandler;
    private String mLocalVersion;

    public DirectoryHandler( String pVersionedRootPath, String pTarget ) {
        mCritical = pTarget.endsWith( "!" );
        mTarget = mCritical ? pTarget.substring( 0, pTarget.length() - 1 ) : pTarget;
        mTargetDirectory = checkLocalPathValidity( pVersionedRootPath );
        mLinkUpdaterHandler = new LinkUpdaterHandler( mTarget, mTargetDirectory );
        mVersionedDirectoryHandler = new VersionedDirectoryHandler( mTarget, mTargetDirectory );
    }

    private File checkLocalPathValidity( String pVersionedRootPath ) {
        File versionedTargetDirectory = new File( pVersionedRootPath, mTarget );

        if ( DirectoryUtils.existsThenAssertMutable( versionedTargetDirectory, INVALID_TARGET ) ) {
            File currentDirectory = new File( versionedTargetDirectory, "current" );

            if ( DirectoryUtils.existsThenAssertMutable( currentDirectory, INVALID_TARGET ) ) {
                File versionTextFile = new File( currentDirectory, "version.txt" );

                if ( FileUtils.existsThenAssertMutable( versionTextFile, INVALID_TARGET ) ) {
                    mLocalVersion = getVersion( versionTextFile );
                }
            }
        }
        return versionedTargetDirectory;
    }

    private String getVersion( File pVersionTextFile ) {
        return Strings.getFirstEntry( FileUtils.loadTextFile( pVersionTextFile ) );
    }

    private String getVersion( String pURLToVersionTextFile ) {
        return Strings.getFirstEntry( URLUtils.loadTextFile( pURLToVersionTextFile ) );
    }

    public Outcome update( boolean pVerbose, String pURL, String pDeploymentVersion, Callback pCallback ) {
        Callback.Target zCallback = pCallback.start( mTarget );
        try {
            String zURLToVersionTextFile = pURL + "/" + mTarget + "/" + pDeploymentVersion + ".txt";
            String remoteVersion = getVersion( zURLToVersionTextFile );
            if ( remoteVersion == null ) {
                throw new IllegalArgumentException( INVALID_REMOTE + zURLToVersionTextFile );
            }

            if ( remoteVersion.equals( mLocalVersion ) ) {
                zCallback.completeNoUpdate();
                return Outcome.NoUpdate;
            }
            updateLinkFileTo( pURL, remoteVersion );
            if ( mCritical ) {
                zCallback.completeWithCriticalUpdate();
                return Outcome.CriticalUpdate;
            }
            zCallback.completeWithNonCriticalUpdate();
            return Outcome.Updated;
        }
        catch ( RuntimeException e ) {
            zCallback.fail( e.getMessage() );
            if ( pVerbose ) {
                e.printStackTrace( System.out );
            }
            return Outcome.Failed;
        }
    }

    private void updateLinkFileTo( String pURL, String pRemoteVersion ) {
        DirectoryUtils.ensureExistsAndMutable( mTargetDirectory, INVALID_TARGET );

        String zLinkVersion = mLinkUpdaterHandler.getLinkVersion();
        if ( !pRemoteVersion.equals( zLinkVersion ) ) {
            mVersionedDirectoryHandler.ensureDirectory( pURL, pRemoteVersion );
            mLinkUpdaterHandler.create( pRemoteVersion );
        }
    }
}
