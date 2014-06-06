// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater.utils;

import java.io.*;

@SuppressWarnings("UnusedDeclaration")
public class FileUtils
{
    public static final String UTF_8 = "UTF-8";

    public static boolean existsThenAssertMutable( File pTargetPath, String pWhy )
    {
        if ( !pTargetPath.exists() )
        {
            return false;
        }
        if ( !acceptableMutable( pTargetPath ) )
        {
            throw new IllegalArgumentException( pWhy + pTargetPath.getAbsolutePath() );
        }
        return true;
    }

    public static boolean acceptableMutable( File pTargetPath )
    {
        return pTargetPath.isFile() && pTargetPath.canRead() && pTargetPath.canWrite();
    }

    public static String path( String pBase, String... pSubDirs )
    {
        if ( pSubDirs != null )
        {
            for ( int i = 0; i < pSubDirs.length; i++ )
            {
                if ( !pBase.endsWith( "/" ) )
                {
                    pBase += "/";
                }
                pBase += Strings.validateNotNullOrEmpty( "Subdir[" + i + "] of : " + pBase, pSubDirs[i] );
            }
        }
        return pBase;
    }

    public static BufferedWriter createWriter( File pFile, boolean pAppend )
            throws IOException
    {
        Objects.assertNotNull( "File", pFile );
        return new BufferedWriter( new OutputStreamWriter( new FileOutputStream( pFile, pAppend ), UTF_8 ) );
    }

    public static BufferedReader createReader( File pFile )
            throws IOException
    {
        Objects.assertNotNull( "File", pFile );
        return new BufferedReader( new InputStreamReader( new FileInputStream( pFile ), UTF_8 ) );
    }

    public static void deleteIfExists( File pFile )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        if ( pFile.isFile() )
        {
            if ( !pFile.delete() || pFile.exists() )
            {
                throw new FileSystemException( "Unable to delete: " + pFile.getAbsolutePath() );
            }
        }
    }

    public static byte[] load( File pFile, int pMaxAllowedSize )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        try
        {
            if ( !pFile.exists() )
            {
                throw new FileNotFoundException( pFile.getAbsolutePath() );
            }
            long fSize = pFile.length();
            if ( fSize > pMaxAllowedSize )
            {
                throw new FileSystemException( "File (" + pFile + ") Too large (" + fSize + "), but max set to: " + pMaxAllowedSize );
            }
            int fileSize = (int) fSize;
            byte[] b = new byte[fileSize];
            InputStream is = new FileInputStream( pFile );
            boolean closed = false;
            try
            {
                int offset = 0;
                while ( offset < fileSize )
                {
                    int i = is.read( b, offset, fileSize - offset );
                    if ( i < 1 )
                    {
                        throw new FileSystemException( "Unable to read the entire file (" + pFile + ").  Expected " + fileSize + ", but only got: " + offset );
                    }
                    offset += i;
                }
                if ( -1 != is.read() )
                {
                    throw new FileSystemException( "Read beyond file (" + pFile + ") size (" + fileSize + ")!" );
                }
                closed = true;
                is.close();
            }
            finally
            {
                if ( !closed )
                {
                    IOUtils.dispose( is );
                }
            }
            return b;
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
        }
    }

    public static void store( File pFile, byte[] pBytes )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        File file = new File( pFile.getAbsolutePath() + ".new" );
        try
        {
            OutputStream os = new FileOutputStream( file );
            boolean closed = false;
            try
            {
                if ( (pBytes != null) && (pBytes.length != 0) )
                {
                    os.write( pBytes );
                }
                closed = true;
                os.close();
            }
            finally
            {
                if ( !closed )
                {
                    IOUtils.dispose( os );
                }
            }
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
        }
        rollIn( file, pFile, new File( pFile.getAbsolutePath() + ".bak" ) );
    }

    public static void storeTextFile( File pFile, String... pLines )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        File file = new File( pFile.getAbsolutePath() + ".new" );
        addLines( file, false, pLines );
        rollIn( file, pFile, new File( pFile.getAbsolutePath() + ".bak" ) );
    }

    private static void addLines( File pFile, boolean pAppend, String... pLines )
            throws FileSystemException
    {
        try
        {
            addLines( createWriter( pFile, pAppend ), pLines );
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
        }
    }

    private static void addLines( BufferedWriter pWriter, String... pLines )
            throws IOException
    {
        boolean closed = false;
        try
        {
            for ( String line : Strings.deNull( pLines ) )
            {
                if ( line != null )
                {
                    pWriter.write( line );
                    pWriter.write( '\n' );
                }
            }
            closed = true;
            pWriter.close();
        }
        finally
        {
            if ( !closed )
            {
                IOUtils.dispose( pWriter );
            }
        }
    }

    public static String[] loadTextFile( File pFile )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        try
        {
            if ( !pFile.exists() )
            {
                throw new FileNotFoundException( pFile.getAbsolutePath() );
            }
            return IOUtils.loadTextFileLines( createReader( pFile ) );
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
        }
    }

    public static void rollIn( File pNewFile, File pTargetFile, File pBackupFile )
            throws FileSystemException
    {
        Objects.assertNotNull( "NewFile", pNewFile );
        Objects.assertNotNull( "TargetFile", pTargetFile );
        Objects.assertNotNull( "BackupFile", pBackupFile );
        if ( !pNewFile.exists() )
        {
            throw new FileSystemException( "Does not Exist: " + pNewFile.getPath() );
        }
        boolean targetExisted = false;
        if ( pTargetFile.exists() )
        {
            targetExisted = true;
            deleteIfExists( pBackupFile );
            renameFromTo( pTargetFile, pBackupFile );
        }
        try
        {
            renameFromTo( pNewFile, pTargetFile );
        }
        catch ( FileSystemException e )
        {
            if ( targetExisted )
            {
                attemptToRollBack( pNewFile, pTargetFile, pBackupFile );
            }
            throw e;
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private static void attemptToRollBack( File pNewFile, File pTargetFile, File pBackupFile )
    {
        switch ( (pNewFile.exists() ? 0 : 4) + (pTargetFile.exists() ? 0 : 2) + (pBackupFile.exists() ? 0 : 1) )
        {
            // What Happen'd
            case 0: // There: ------------- Nobody --------------
            case 2: // There:            pTargetFile
            case 4: // There: pNewFile
            case 6: // There: pNewFile & pTargetFile
            case 7: // There: pNewFile & pTargetFile & pBackupFile
                return;
            case 3: // There:            pTargetFile & pBackupFile
                pTargetFile.renameTo( pNewFile );
                // Fall Thru
            case 1: // There:                          pBackupFile
            case 5: // There: pNewFile               & pBackupFile
                pBackupFile.renameTo( pTargetFile );
                break;
        }
    }

    /**
     * This method will rename the pSourceFile to the pDestinationFile name.
     * <p/>
     * It is Inherently fragile when dealing with multiple processes playing
     * in the same file system name spaces.  It should therefore NOT be used
     * in a multi-process creation-consumption shared file system name space.
     * <p/>
     * Specifically there are two windows of opportunities for multiple processes
     * to mess with the "as linear" assumptions
     */
    private static void renameFromTo( File pSourceFile, File pDestinationFile )
            throws FileSystemException
    {
        Objects.assertNotNull( "SourceFile", pSourceFile );
        Objects.assertNotNull( "DestinationFile", pDestinationFile );
        // Win 1 Start
        if ( !pSourceFile.exists() )
        {
            throw new FileSystemException( "SourceFile does not exist: " + pSourceFile.getAbsolutePath() );
        }
        if ( pDestinationFile.exists() )
        {
            throw new FileSystemException( "DestinationFile already exists: " + pDestinationFile.getAbsolutePath() );
        }
        // Win 2 Start
        if ( !pSourceFile.renameTo( pDestinationFile ) )    // Win 1 End
        {
            throw renameFailed( pSourceFile, pDestinationFile, "Failed" );
        }
        boolean sThere = pSourceFile.exists();
        boolean dThere = pDestinationFile.exists();
        // Win 2 End
        if ( sThere )
        {
            throw renameFailed( pSourceFile, pDestinationFile, "claims Succeess, but Source still there" + (dThere ? " and so is the Destination!" : "!") );
        }
        if ( !dThere )
        {
            throw renameFailed( pSourceFile, pDestinationFile, "claims Succeess, but the Destination is NOT there!" );
        }
    }

    private static FileSystemException renameFailed( File pSourceFile, File pDestinationFile, String pAdditionalExplanation )
    {
        throw new FileSystemException(
                "Rename (" + pSourceFile.getAbsolutePath() + ") to (" + pDestinationFile.getAbsolutePath() + ") " + pAdditionalExplanation );
    }

    public static void makeDirs( File pFile )
    {
        DirectoryUtils.makeDirs( pFile.getParentFile() );
    }
}
