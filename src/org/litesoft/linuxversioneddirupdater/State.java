// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

public class State {
    private final String mDeploymentVersion;
    private final VersionedTargetTriad[] mTargetVersions;

    /* PackageFriendly */
    State( String pDeploymentVersion, VersionedTargetTriad[] pTargetVersions ) {
        mDeploymentVersion = pDeploymentVersion;
        mTargetVersions = pTargetVersions;
    }

    public String getDeploymentVersion() {
        return mDeploymentVersion;
    }

    public VersionedTargetTriad[] getTargetVersions() {
        return mTargetVersions;
    }

    public boolean isRunnable() {
        for ( VersionedTargetTriad zTriad : getTargetVersions() ) {
            if ( !zTriad.hasLocalVersion() ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append( "DeploymentVersion: " ).append( getDeploymentVersion() );

        int zMaxTargetLength = 0;
        int zMasVersionLength = 0;
        for ( VersionedTargetTriad zTriad : getTargetVersions() ) {
            zMaxTargetLength = Math.max( zMaxTargetLength, Strings.length( zTriad.getTarget() ) );
            zMasVersionLength = Math.max( zMasVersionLength, Strings.length( zTriad.getLocalVersion() ) );
        }
        for ( VersionedTargetTriad zTriad : getTargetVersions() ) {
            zTriad.appendTo( sb.append( "\n    " ), zMaxTargetLength, zMasVersionLength );
        }
        return sb.toString();
    }
}
