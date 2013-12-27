// This Source Code is in the Public Domain per: http://litesoft.org/License.txt
package org.litesoft.util;

import java.io.*;
import java.util.*;

import org.litesoft.core.typeutils.*;
import org.litesoft.core.typeutils.Objects;
import org.litesoft.core.util.*;
import org.litesoft.core.util.stringmatching.*;

public class FileUtils extends FileUtil
{
    public static Reader[] filesToReaders( File pDir, String... pFileNames )
            throws FileNotFoundException
    {
        if ( Objects.isNullOrEmpty( pFileNames ) )
        {
            throw new IllegalArgumentException( "No Files provided" );
        }
        Reader[] readers = new Reader[pFileNames.length];
        for ( int i = 0; i < pFileNames.length; i++ )
        {
            File file = new File( pDir, pFileNames[i] );
            readers[i] = new InformativeFileReader( file );
        }
        return readers;
    }

    private static class InformativeFileReader extends FileReader implements ToStringInformative
    {
        private File mFile;

        private InformativeFileReader( File pFile )
                throws FileNotFoundException
        {
            super( pFile );
            mFile = pFile;
        }

        @Override
        public String toString()
        {
            return mFile.getAbsolutePath();
        }
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

    public static FilenameFilter createFileNameFilter( String... pParts )
    {
        StringMatcher zSM = StringMatcherFactory.createStartsWith( pParts );
        if ( zSM == null )
        {
            throw new IllegalArgumentException( "Must Provide something to Filter on!" );
        }
        return new StringMatcherFilenameFilter( zSM );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static String locateFile( String pFile )
            throws FileSystemException
    {
        try
        {
            File file = new File( pFile );
            while ( !file.isFile() )
            {
                File parent = file.getParentFile();
                if ( parent != null )
                {
                    File grandparent = parent.getParentFile();
                    if ( grandparent != null )
                    {
                        file = new File( grandparent, pFile );
                        continue;
                    }
                }
                throw new FileNotFoundException( pFile );
            }
            return file.getAbsolutePath();
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
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
                    Utils.dispose( is );
                }
            }
            return b;
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
        }
    }

    public static void appendTextFile( File pFile, String... pLines )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        File file = new File( pFile.getAbsolutePath() );
        addLines( file, true, pLines );
    }

    public static void store( File pFile, byte[] pBytes )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        File file = Directories.insureParent( new File( pFile.getAbsolutePath() + ".new" ) );
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
                    Utils.dispose( os );
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
                Utils.dispose( pWriter );
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
            List<String> lines = new LinkedList<String>();
            BufferedReader reader = createReader( pFile );
            boolean closed = false;
            try
            {
                for ( String line; null != (line = reader.readLine()); )
                {
                    lines.add( line );
                }
                closed = true;
                reader.close();
            }
            finally
            {
                if ( !closed )
                {
                    Utils.dispose( reader );
                }
            }
            return lines.toArray( new String[lines.size()] );
        }
        catch ( IOException e )
        {
            throw new FileSystemException( e );
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static String loadTextFileAsString( File pFile )
            throws FileSystemException
    {
        return loadTextFileAsString( pFile, null );
    }

    public static String loadTextFileAsString( File pFile, String pLineSeparator )
            throws FileSystemException
    {
        Objects.assertNotNull( "File", pFile );
        pLineSeparator = Strings.deNull( pLineSeparator, "\n" );
        try
        {
            if ( !pFile.exists() )
            {
                throw new FileNotFoundException( pFile.getAbsolutePath() );
            }

            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = createReader( pFile );
            boolean closed = false;
            try
            {
                int count = 0;
                for ( String line; null != (line = reader.readLine()); count++ )
                {
                    if ( count > 0 )
                    {
                        buffer.append( pLineSeparator );
                    }
                    buffer.append( line );
                }
                closed = true;
                reader.close();
            }
            finally
            {
                if ( !closed )
                {
                    Utils.dispose( reader );
                }
            }

            return buffer.toString();
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
        throw new FileSystemException( "Rename (" + pSourceFile.getAbsolutePath() + ") to (" + pDestinationFile.getAbsolutePath() + ") " + pAdditionalExplanation );
    }

    private static class StringMatcherFilenameFilter implements FilenameFilter
    {
        private StringMatcher mSM;

        public StringMatcherFilenameFilter( StringMatcher pSM )
        {
            mSM = pSM;
        }

        @Override
        public boolean accept( File dir, String name )
        {
            return mSM.matches( name );
        }
    }

    public static void main( String[] args )
            throws Exception
    {
        File zFile = new File( "PersistanceTier/design/NextGEN.xml" );
        String[] lines = loadTextFile( zFile );
        process( lines );
        storeTextFile( zFile, lines );
    }

    private static void process( String[] pLines )
    {
        for ( int i = 0; i < pLines.length; i++ )
        {
            String zLine = pLines[i];
            if ( zLine != null )
            {
                if ( zLine.trim().startsWith( "<column " ) )
                {
                    processColumn( pLines, i, findEONode( pLines, i ) );
                }
                else if ( zLine.trim().startsWith( "<foreignKey " ) )
                {
                    processForeignKey( pLines, i, findEONode( pLines, i ) );
                }
            }
        }
    }

    private static int findEONode( String[] pLines, int pFromIndex )
    {
        int zThruIndex = pFromIndex;
        while ( zThruIndex < pLines.length )
        {
            String zLine = pLines[zThruIndex];
            if ( (zLine != null) && zLine.trim().endsWith( ">" ) )
            {
                break;
            }
            zThruIndex++;
        }
        return zThruIndex;
    }

    private static boolean isAttributeOneOf( String pLine, String... pOneOf )
    {
        for ( String s : pOneOf )
        {
            if ( pLine.startsWith( s + "=" ) )
            {
                return true;
            }
        }
        return false;
    }

    private static void processColumn( String[] pLines, int pBegLine, int pEndLine )
    {
        for ( int i = pBegLine + 1; i < pEndLine; i++ )
        {
            String zLine = pLines[i];
            if ( zLine != null )
            {
                zLine = zLine.trim();
                if ( zLine.equals( "type=\"foreignKey\"" ) ) // No More Columns as ForeignKeys
                {
                    for ( int j = pBegLine; j <= pEndLine; j++ )
                    {
                        pLines[j] = null;
                    }
                    return;
                }
                if ( isAttributeOneOf( zLine, "fkonly", "isContainer", "IsContainer", "ReferenceSortColumn", "ThemDependsOnUs", "UsDependsOnThem", "table", "updateReferencedPO" ) )
                {
                    pLines[i] = null;
                }
            }
        }
    }

    private static void processForeignKey( String[] pLines, int pBegLine, int pEndLine )
    {
        for ( int i = pBegLine + 1; i < pEndLine; i++ )
        {
            String zLine = pLines[i];
            if ( zLine != null )
            {
                zLine = zLine.trim();
                if ( isAttributeOneOf( zLine, "fkonly", "isContainer", "IsContainer", "defaultValue", "enumeration", "len" ) )
                {
                    pLines[i] = null;
                }
            }
        }
    }
}
