// This Source Code is in the Public Domain per: http://unlicense.org
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
    public static final int INVALID_FAILURES = 3;
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
        if ( args.length != 3 ) // validate that we have 3 params
        {
            System.out.println( "Please provide three parameters" );
            System.exit( INVALID_PARAMETER_COUNT );
        }

        try
        {
            if ( !new Updater( args[0], args[1], args[2] ).run( true, new CallbackConsole() ) )
            {
                System.exit( INVALID_FAILURES );
            }
        }
        catch ( IllegalArgumentException e )
        {
            e.printStackTrace(System.out);
            System.exit( INVALID_ARGUMENTS );
        }
    }

    /**
     * return True if there is No Failures!
     */
    public boolean run( boolean pVerbose, Callback pCallback )
    {
        boolean zFailures = false;
        pCallback.starting( mDirectoryHandlers.length );
        for ( DirectoryHandler zDirectoryHandler : mDirectoryHandlers )
        {
            zFailures |= (Outcome.Failed == zDirectoryHandler.update( pVerbose, mURL, mDeploymentVersion, pCallback ));
        }
        pCallback.finished();
        return !zFailures;
    }
}
