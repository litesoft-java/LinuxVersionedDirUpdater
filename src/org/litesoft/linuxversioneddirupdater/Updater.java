package org.litesoft.linuxversioneddirupdater;

import org.litesoft.linuxversioneddirupdater.utils.*;

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

        mURL = Strings.validateNotNullOrEmpty( "URL", pURL );
        mDeploymentVersion = Strings.validateNotNullOrEmpty( "DeploymentVersion", pDeploymentVersion );
        mDirectoryHandlers = createDirectoryHandlers( Strings.validateAtLeastOne(
                "CSVTargets", Strings.checkForEmptyEntries(
                "CSVTargets", Strings.parseSimpleCSV( Strings.validateNotNullOrEmpty( "CSVTargets", pCSVTargets ) ) ) ) );
    }

    private DirectoryHandler[] createDirectoryHandlers( String[] pTargets )
    {
        DirectoryHandler[] zDirectoryHandlers = new DirectoryHandler[pTargets.length];
        for ( int i = 0; i < pTargets.length; i++ )
        {
            zDirectoryHandlers[i] = new DirectoryHandler( VERSIONED_ROOT_PATH, pTargets[i] );
        }
        return zDirectoryHandlers;
    }

    public static void main( String[] args )
    {
        //validate that we have 3 params

        if ( args.length != 3 )
        {
            System.err.println( "Please provide three parameters" );
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
        System.out.println( "Success" );
    }

    private void run()
    {
        for ( DirectoryHandler zDirectoryHandler : mDirectoryHandlers )
        {
            zDirectoryHandler.update( mURL, mDeploymentVersion );
        }
    }
}
