// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

/**
 * Callback interfaces for Updater progress.
 */
public interface Callback {
    interface Target {
        public void completeWithCriticalUpdate( String pPendingUpdatedVersion );

        public void completeWithNonCriticalUpdate( String pPendingUpdatedVersion );

        public void completeNoUpdate();

        public void fail( String pMessage );
    }

    public void starting( int pTargets );

    public Target start( String pTarget, String pLocalVersion );

    public void finished();
}
