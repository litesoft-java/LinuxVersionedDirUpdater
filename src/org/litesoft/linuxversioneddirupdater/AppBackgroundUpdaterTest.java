// This Source Code is in the Public Domain per: http://unlicense.org
package org.litesoft.linuxversioneddirupdater;

/**
 * Helper class for managing the Updater in the background for an App.
 * <p/>
 * Created by randallb on 12/29/13.
 */
public class AppBackgroundUpdaterTest {
    private static int sRuns = 0;

    public static void main( String[] args ) {
        new AppBackgroundUpdater( new Updater( args[0], args[1], args[2] ), false ) {
            @Override
            protected void statsReady() {
                sRuns++;
                if ( areCriticalUpdates() ) {
                    System.out.println( "Would start the process for Critical Updates" );
                } else if ( areNonCriticalUpdates() ) {
                    System.out.println( "Would schedule overnight restart" );
                } else if ( areFailed() || areNotStarted() || areUnfinished() ) {
                    System.out.println( "Some kind of problem, well 'assume' that it was transient..." );
                }
                if ( sRuns < 3 ) {
                    System.out.print( "\nWaiting" );
                    for ( int i = 0; i < 15; i++ ) {
                        try {
                            Thread.sleep( 200 );
                        }
                        catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        System.out.print( '.' );
                    }
                    System.out.println();
                    runAgain();
                }
            }
        };
    }
}
