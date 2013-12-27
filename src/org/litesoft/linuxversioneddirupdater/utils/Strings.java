package org.litesoft.linuxversioneddirupdater.utils;

import java.util.*;

/**
 * Collection of methods to operate on String objects
 * Created by randallb on 12/27/13.
 */
public class Strings
{

    public static String[] validateAtLeastOne( String pWhat, String... pCheckForEmptyEntries )
    {
        if ( pCheckForEmptyEntries.length > 0 )
        {
            return pCheckForEmptyEntries;
        }

        throw new IllegalArgumentException( pWhat + " has no entries" );
    }

    public static String[] checkForEmptyEntries( String pWhat, String[] pCSVEntries )
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
    public static String[] parseSimpleCSV( String pCSVLine )
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

    public static String validateNotNullOrEmpty( String pWhat, String pParameter )
    {
        Objects.assertNotNull( pWhat, pParameter );

        pParameter = pParameter.trim();

        if ( pParameter.isEmpty() )
        {
            throw new IllegalArgumentException( pWhat + " is empty" );
        }

        return pParameter;
    }

    public static String[] deNull( String... pStrings )
    {
        return (pStrings == null) ? new String[0] : pStrings;
    }

    public static String getFirstEntry( String... pStrings )
    {
        return (deNull( pStrings ).length > 0) ? pStrings[0] : null;
    }
}
