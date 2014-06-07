// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

/**
 * Outcome for both Callbacks and a particular Target Directory Handler.
 * <p/>
 * Created by randallb on 12/29/13.
 */
public enum Outcome {
    NoUpdate, Updated, CriticalUpdate, Failed
}
