// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Zip File Handler for the Main updater class
 */
public class VersionedDirectoryHandler {
    private static final String EXPECTED_ZIP_FILE = "Expected Zip file: ";
    public static final String EXPECTED_VERSIONED_DIRECTORY = "Expected Versioned Directory: ";

    private final String mTarget;
    private final File mTargetPath;

    public VersionedDirectoryHandler( String pTarget, File pTargetPath ) {
        mTarget = pTarget;
        mTargetPath = pTargetPath;
    }

    public void ensureDirectory( String pURL, String pRemoteVersion ) {
        new VersionHelper( pRemoteVersion ).ensureDirectory( pURL );
    }

    private class VersionHelper {
        private final String mRemoteVersion;
        private final String mZipFileName;
        private final File mVersionedZipFile;
        private final File mVersionedDirectory;

        private VersionHelper( String pRemoteVersion ) {
            mRemoteVersion = pRemoteVersion;
            mVersionedZipFile = new File( mTargetPath, mZipFileName = mRemoteVersion + ".zip" );
            mVersionedDirectory = new File( mTargetPath, mRemoteVersion );
        }

        public void ensureDirectory( String pURL ) {
            if ( !DirectoryUtils.existsThenAssertMutable( mVersionedDirectory, EXPECTED_VERSIONED_DIRECTORY ) ) {
                ensureZipFile( pURL );
                explodeZip();
            }
            FileUtils.deleteIfExists( mVersionedZipFile );
        }

        private void ensureZipFile( String pURL ) {
            if ( !FileUtils.existsThenAssertMutable( mVersionedZipFile, EXPECTED_ZIP_FILE ) ) {
                AtomicFileSystemUpdateManager zUpdateManager = new AtomicFileSystemUpdateManager( mTargetPath, mZipFileName );
                URLUtils.copyURLtoFile( pURL + "/" + mTarget + "/" + mZipFileName, zUpdateManager.getWriteFile() );
                zUpdateManager.commit();
            }
        }

        private void explodeZip() {
            AtomicFileSystemUpdateManager zUpdateManager = new AtomicFileSystemUpdateManager( mTargetPath, mRemoteVersion );
            File zVersionedDir = zUpdateManager.getWriteFile();
            ZipFile zZipFile = null;
            try {
                zZipFile = new ZipFile( mVersionedZipFile );
                for ( Enumeration<? extends ZipEntry> zEnumeration = zZipFile.entries(); zEnumeration.hasMoreElements(); ) {
                    ZipEntry zEntry = zEnumeration.nextElement();
                    String zName = zEntry.getName();
                    File zDirEntry = new File( zVersionedDir, zName );
                    if ( zEntry.isDirectory() ) {
                        DirectoryUtils.makeDirs( zDirEntry );
                    } else // File!?
                    {
                        FileUtils.makeDirs( zDirEntry );
                        InputStream zInputStream = zZipFile.getInputStream( zEntry );
                        IOUtils.copy( zInputStream, new FileOutputStream( zDirEntry ) );
                    }
                }
                FileUtils.storeTextFile( new File( zVersionedDir, "version.txt" ), mRemoteVersion );
            }
            catch ( IOException e ) {
                throw new FileSystemException( e );
            }
            finally {
                Closeables.dispose( zZipFile );
            }
            zUpdateManager.commit();
        }
    }
}
