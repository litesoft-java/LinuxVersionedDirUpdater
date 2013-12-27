package org.litesoft.linuxversioneddirupdater;

import com.sun.org.apache.xpath.internal.*;

import java.util.*;

/**
 * Master updater
 * <p/>
 * Created by randallb on 12/26/13.
 */
public class Updater
{

    public static final int INVALID_PARAMETER_COUNT = 1;
    public static final int INVALID_ARGUMENTS = 2;
    public static final String VERSIONED_ROOT_PATH = "/versioned";
    private final String mURL;
    private final String mDeploymentVersion;
    private final DirectoryHandler[] mDirectoryHandlers;

    public Updater( String pURL, String pDeploymentVersion, String pCSVTargets )
    {

        mURL = validateNotNullOrEmpty( "URL", pURL );
        mDeploymentVersion = validateNotNullOrEmpty( "DeploymentVersion", pDeploymentVersion );
        mDirectoryHandlers = validateDirectoryStructure( validateAtLeastOne(
                "CSVTargets", checkForEmptyEntries(
                "CSVTargets", parseSimpleCSV( validateNotNullOrEmpty( "CSVTargets", pCSVTargets ) ) ) ) );
    }

    private DirectoryHandler[] validateDirectoryStructure( String[] pTargets )
    {
        DirectoryHandler[] zDirectoryHandlers = new DirectoryHandler[pTargets.length];
        for ( int i = 0; i < pTargets.length; i++ )
        {
            zDirectoryHandlers[i] = new DirectoryHandler( VERSIONED_ROOT_PATH, pTargets[i] );
        }
        return zDirectoryHandlers;
    }

    private String[] validateAtLeastOne( String pWhat, String[] pCheckForEmptyEntries )
    {
        if ( pCheckForEmptyEntries.length > 0 )
        {
            return pCheckForEmptyEntries;
        }

        throw new IllegalArgumentException( pWhat + " has no entries" );
    }

    private String[] checkForEmptyEntries( String pWhat, String[] pCSVEntries )
    {
        for ( int i = 0; i < pCSVEntries.length; i++ )
        {
            if ( pCSVEntries[i].isEmpty() )
            {
                throw new IllegalArgumentException( pWhat + "[" + i + "] is Empty" );
            }
        }

        return pCSVEntries;
    }

    /**
     * simple means no leading/trailing spaces per entry and no quoted entries or new lines
     *
     * @param pCSVLine not null
     *
     * @return 1 or more entries
     */
    private String[] parseSimpleCSV( String pCSVLine )
    {
        //look at the string - commas are the separators

        int commaAt = pCSVLine.indexOf( ',' );

        if ( commaAt == -1 )
        {
            return new String[]{pCSVLine.trim()};
        }

        List<String> paramCollector = new ArrayList<>();

        paramCollector.add( pCSVLine.substring( 0, commaAt ).trim() );

        int from = commaAt + 1;

        while ( (commaAt = pCSVLine.indexOf( ',', from )) != -1 )
        {
            paramCollector.add( pCSVLine.substring( from, commaAt ).trim() );

            from = commaAt + 1;
        }

        paramCollector.add( pCSVLine.substring( from ).trim() );

        return paramCollector.toArray( new String[paramCollector.size()] );
    }

    private String validateNotNullOrEmpty( String pWhat, String pParameter )
    {
        if ( pParameter == null )
        {
            throw new IllegalArgumentException( pWhat + " is null" );
        }

        pParameter = pParameter.trim();

        if ( pParameter.isEmpty() )
        {
            throw new IllegalArgumentException( pWhat + " is empty" );
        }

        return pParameter;
    }

    public static void main( String[] args )
    {
        //validate that we have 3 params

        if ( args.length != 3 )
        {
            System.err.println("Please provide three parameters");
            System.exit( INVALID_PARAMETER_COUNT );
        }

        try
        {
            new Updater( args[0], args[1], args[2] ).run();
        }
        catch ( IllegalArgumentException e )
        {
            e.printStackTrace();
            System.exit( INVALID_ARGUMENTS );
        }
        System.out.println("Success");
    }

    private void run()
    {
        for ( DirectoryHandler zDirectoryHandler : mDirectoryHandlers )
        {
            zDirectoryHandler.update( mURL, mDeploymentVersion );
        }
    }
}
