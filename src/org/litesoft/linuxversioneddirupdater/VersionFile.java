package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;

public class VersionFile {
    private final File mVersionTextFile;

    public VersionFile( File pVersionTextFile ) {
        mVersionTextFile = pVersionTextFile;
    }

    public static String getFromURL( String pURLToVersionTextFile ) {
        return getVersionFromTextLines( URLUtils.loadTextFile( pURLToVersionTextFile ) );
    }

    public String get() {
        return getVersionFromTextLines( FileUtils.loadTextFile( mVersionTextFile ) );
    }

    public void set( String pVersion ) {
        FileUtils.storeTextFile( mVersionTextFile, pVersion ); // First Line!
    }

    private static String getVersionFromTextLines( String[] pLines ) {
        return Strings.getFirstEntry( pLines ); // First Line
    }
}
