// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

public class VersionedTargetTriad {
    private final String mTarget, mLocalVersion, mPendingUpdatedVersion;

    /* PackageFriendly */
    VersionedTargetTriad( String pTarget, String pLocalVersion, String pPendingUpdatedVersion ) {
        mTarget = pTarget;
        mLocalVersion = pLocalVersion;
        mPendingUpdatedVersion = pPendingUpdatedVersion;
    }

    public String getTarget() {
        return mTarget;
    }

    public boolean hasLocalVersion() {
        return (mLocalVersion != null);
    }

    public String getLocalVersion() {
        return mLocalVersion;
    }

    public boolean hasPendingUpdatedVersion() {
        return (mPendingUpdatedVersion != null);
    }

    public String getPendingUpdatedVersion() {
        return mPendingUpdatedVersion;
    }

    @Override
    public String toString() {
        return appendTo( new StringBuilder(), 0, 0 ).toString();
    }

    public StringBuilder appendTo( StringBuilder sb, int pMaxTargetLength, int pMaxLocalVersionLength ) {
        sb.append( getTarget() );
        if ( hasLocalVersion() ) {
            sb.append( ": " );
            pad( sb, getTarget().length(), pMaxTargetLength );
            sb.append( getLocalVersion() );
            if ( hasPendingUpdatedVersion() ) {
                pad( sb, getLocalVersion().length(), pMaxLocalVersionLength );
                sb.append( " -> " );
                sb.append( getPendingUpdatedVersion() );
            }
        }
        return sb;
    }

    private void pad( StringBuilder sb, int pCurrentLength, int pDesiredLength ) {
        while ( pDesiredLength > pCurrentLength++ ) {
            sb.append( ' ' );
        }
    }
}
