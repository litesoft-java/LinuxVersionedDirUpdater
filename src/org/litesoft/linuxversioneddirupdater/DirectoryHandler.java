package org.litesoft.linuxversioneddirupdater;

import com.sun.javaws.exceptions.*;
import oracle.jrockit.jfr.*;

import java.io.*;

/**
 * Directory Handler for the Main updater class
 * <p/>
 * Created by randallb on 12/26/13.
 */
public class DirectoryHandler
{
    private static final String INVALID_TARGET = "Invalid target path";
    private String mTarget;
    private boolean mTargetPathExists;
    private boolean mCurrentPathExists;
    private boolean mVersionTextExists;
    private String mCurrent;
    private String mVersionText;
    private String mCurrentTimeStamp;
    private File mTargetPath;

    public DirectoryHandler( String pVersionedRootPath, String pTarget )
    {
        mTarget = pTarget;
        mTargetPath = checkLocalPathValidity( pVersionedRootPath );
        // TODO: XXX

    }

    private File checkLocalPathValidity( String pVersionedRootPath )
    {
        File targetPath = new File( pVersionedRootPath, mTarget );

        if ( !targetPath.exists() )
        {
            mTargetPathExists = mCurrentPathExists = mVersionTextExists = false;
            return targetPath;
        }

        if ( !acceptableDirectory( targetPath ) )
        {
            throw new IllegalArgumentException( INVALID_TARGET + mTarget );
        }
        mTargetPathExists = true;

        File currentDirectory = new File(targetPath, "current");

        if(!currentDirectory.exists())
        {
            mCurrentPathExists = mVersionTextExists = false;
            return targetPath;
        }

        if ( !acceptableDirectory( currentDirectory ) )
        {
            throw new IllegalArgumentException( INVALID_TARGET + mTarget + "/current" );
        }

        mCurrentPathExists = true;


        File versionTextFile = new File(currentDirectory, "version.txt");

        if ( !versionTextFile.exists() )
        {
            mVersionTextExists = false;
            return targetPath;
        }

        if ( !versionTextFile.isFile() )
        {
            throw new IllegalArgumentException( INVALID_TARGET + mTarget + "/current/version.txt");
        }

        mVersionTextExists = true;

        mCurrentTimeStamp = getTimeStamp(versionTextFile);

        return targetPath;
    }

    private String getTimeStamp( File pVersionTextFile )
    {
        //TODO:  gather readfirstlineoffile
        return null;
    }

    private boolean acceptableDirectory( File pTargetPath )
    {
        //TODO: validate that it is readable and writeable
        return pTargetPath.isDirectory();
    }

    public boolean update( String pURL, String pDeploymentVersion )
    {
     //   qualifyURL( pURL );

        //check the deployment version against what is in place
        //if it is the same, nothing needs to be done
        //if it is missing then perform the update

        return false; // TODO: XXX
    }

    //is there a directory, if not, we need to create that directory immediately
}
