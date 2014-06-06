// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

import java.util.*;

/**
 * Callback interfaces for Updater progress.
 *
 * Created by randallb on 12/29/13.
 */
public class CallbackConsole implements Callback
{
    private final Map<String, TargetCallback> mCallbacksByTargets = new HashMap<>();
    private int mExpectedTargets;

    @Override
    public void starting( int pTargets )
    {
        mExpectedTargets = pTargets;
        mCallbacksByTargets.clear();
        System.out.println( "Update Run Started, Targets: " + pTargets );
    }

    @Override
    public Target start( String pTarget )
    {
        TargetCallback zCallback = new TargetCallback( pTarget );
        mCallbacksByTargets.put( pTarget, zCallback );
        return zCallback;
    }

    @Override
    public void finished()
    {
        int[] zOutComeCounts = new int[Outcome.values().length];
        int zUnfinished = 0;
        for ( TargetCallback zCallback : mCallbacksByTargets.values() )
        {
            Outcome zOutcome = zCallback.getOutcome();
            if ( zOutcome == null )
            {
                zUnfinished++;
            }
            else
            {
                zOutComeCounts[zOutcome.ordinal()]++;
            }
        }
        System.out.println();
        System.out.println( "    Update Run Completed, " + mCallbacksByTargets.size() + " Targets:" );
        System.out.println( "        Non-Critical Updates: " + zOutComeCounts[Outcome.Updated.ordinal()] );
        System.out.println( "            Critical Updates: " + zOutComeCounts[Outcome.CriticalUpdate.ordinal()] );
        System.out.println( "              Failed Updates: " + zOutComeCounts[Outcome.Failed.ordinal()] );
        System.out.println( "                  Unfinished: " + zUnfinished );
        System.out.println( "                  NotStarted: " + (mExpectedTargets - mCallbacksByTargets.size()) );
    }

    private static class TargetCallback implements Target
    {
        private Outcome mOutcome;

        private TargetCallback( String pTarget )
        {
            System.out.print( "    " + pTarget + ":" );
        }

        public Outcome getOutcome()
        {
            return mOutcome;
        }

        @Override
        public void completeWithCriticalUpdate()
        {
            System.out.println( " Critical Update" );
            mOutcome = Outcome.CriticalUpdate;
        }

        @Override
        public void completeWithNonCriticalUpdate()
        {
            System.out.println( " Non-Critical Update" );
            mOutcome = Outcome.Updated;
        }

        @Override
        public void completeNoUpdate()
        {
            System.out.println( " No update" );
            mOutcome = Outcome.NoUpdate;
        }

        @Override
        public void fail( String pMessage )
        {
            System.out.println( " Update FAILED, Message: " + pMessage );
            mOutcome = Outcome.Failed;
        }
    }
}
