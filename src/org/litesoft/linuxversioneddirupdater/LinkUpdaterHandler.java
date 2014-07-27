// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;

/**
 * Link Updater (File) Handler for the Main updater class
 */
public class LinkUpdaterHandler {
    private static final String UPDATE_LNK = "updateLNK";
    private static final String LINK_STARTS_WITH = "ln -sfn ";
    private static final String LINK_STARTS_WITH_SLASH = LINK_STARTS_WITH + "/";

    private final String mTarget;
    private final File mTargetPath;

    public LinkUpdaterHandler( String pTarget, File pTargetPath ) {
        mTarget = pTarget;
        mTargetPath = pTargetPath;
    }

    public String getLinkVersion() {
        File zLink = new File( mTargetPath, UPDATE_LNK );
        if ( zLink.exists() ) {
            for ( String zLine : FileUtils.loadTextFile( zLink ) ) {
                if ( (zLine = zLine.trim()).startsWith( LINK_STARTS_WITH_SLASH ) ) {
                    int i = zLine.indexOf( "/" + mTarget + "/" );
                    if ( i != -1 ) {
                        String zStartVersion = zLine.substring( mTarget.length() + 2 + i );
                        if ( -1 != (i = zStartVersion.indexOf( "/ /" )) ) {
                            return zStartVersion.substring( 0, i );
                        }
                    }
                }
            }
        }
        return null;
    }

    public void create( String pRemoteVersion ) {
        String zTargetPath;
        try {
            zTargetPath = mTargetPath.getCanonicalPath();
        }
        catch ( IOException e ) {
            throw new FileSystemException( e );
        }
        AtomicFileSystemUpdateManager zUpdateManager = new AtomicFileSystemUpdateManager( zTargetPath, UPDATE_LNK );
        FileUtils.storeTextFile( zUpdateManager.getWriteFile(),
                                 "#!/bin/bash",
                                 LINK_STARTS_WITH + FileUtils.path( zTargetPath, pRemoteVersion ) + "/ " + FileUtils.path( zTargetPath, "current" ) );
        zUpdateManager.commit();
    }
}
