// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;

/**
 * Directory Handler for the Main updater class
 */
public class DirectoryHandler {
    private static final String INVALID_TARGET = "Invalid target path: ";
    private static final String INVALID_REMOTE = "Invalid remote path: ";

    private final boolean mCritical;
    private final String mTarget, mLocalVersion;
    private final File mTargetDirectory;
    private final LinkUpdaterHandler mLinkUpdaterHandler;
    private final VersionedDirectoryHandler mVersionedDirectoryHandler;
    private transient String mPendingUpdatedVersion;

    public DirectoryHandler( String pVersionedRootPath, String pTarget ) {
        mCritical = pTarget.startsWith( "!" );
        mTarget = mCritical ? pTarget.substring( 1 ) : pTarget;
        Pair zPair = checkLocalPathValidity( pVersionedRootPath );
        mLocalVersion = zPair.version;
        mTargetDirectory = zPair.dir;
        mLinkUpdaterHandler = new LinkUpdaterHandler( mTarget, mTargetDirectory );
        mVersionedDirectoryHandler = new VersionedDirectoryHandler( mTarget, mTargetDirectory );
    }

    private Pair checkLocalPathValidity( String pVersionedRootPath ) {
        File zVersionedTargetDirectory = new File( pVersionedRootPath, mTarget );

        if ( DirectoryUtils.existsThenAssertMutable( zVersionedTargetDirectory, INVALID_TARGET ) ) {
            File currentDirectory = new File( zVersionedTargetDirectory, "current" );

            if ( currentDirectory.isDirectory() ) {
                File versionTextFile = new File( currentDirectory, "version.txt" );

                if ( FileUtils.existsThenAssertReadable( versionTextFile, INVALID_TARGET ) ) {
                    String zLocalVersion = new VersionFile( versionTextFile ).get();
                    return new Pair( zVersionedTargetDirectory, zLocalVersion );
                }
            }
        }
        return new Pair( zVersionedTargetDirectory );
    }

    private static class Pair {
        private final File dir;
        private final String version;

        private Pair( File pDir, String pVersion ) {
            dir = pDir;
            version = pVersion;
        }

        private Pair( File pDir ) {
            this( pDir, null );
        }
    }

    /**
     * Note: probably called under a different Thread!
     */
    public VersionedTargetTriad getState() {
        return new VersionedTargetTriad( mTarget, mLocalVersion, mPendingUpdatedVersion );
    }

    public Outcome update( boolean pVerbose, String pURL, String pDeploymentVersion, Callback pCallback ) {
        Callback.Target zCallback = pCallback.start( mTarget, mLocalVersion );
        try {
            String zURLToVersionTextFile = pURL + "/" + mTarget + "/" + pDeploymentVersion + ".txt";
            String zRemoteVersion = VersionFile.getFromURL( zURLToVersionTextFile );
            if ( zRemoteVersion == null ) {
                throw new IllegalArgumentException( INVALID_REMOTE + zURLToVersionTextFile );
            }

            if ( zRemoteVersion.equals( mLocalVersion ) ) {
                mPendingUpdatedVersion = null;
                zCallback.completeNoUpdate();
                return Outcome.NoUpdate;
            }
            updateLinkFileTo( pURL, zRemoteVersion );
            mPendingUpdatedVersion = zRemoteVersion;
            if ( mCritical ) {
                zCallback.completeWithCriticalUpdate( mPendingUpdatedVersion );
                return Outcome.CriticalUpdate;
            }
            zCallback.completeWithNonCriticalUpdate( mPendingUpdatedVersion );
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
