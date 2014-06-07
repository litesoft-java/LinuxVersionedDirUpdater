// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

/**
 * Callback interfaces for Updater progress.
 * <p/>
 * Created by randallb on 12/29/13.
 */
public interface Callback {
    interface Target {
        public void completeWithCriticalUpdate();

        public void completeWithNonCriticalUpdate();

        public void completeNoUpdate();

        public void fail( String pMessage );
    }

    public void starting( int pTargets );

    public Target start( String pTarget );

    public void finished();
}
