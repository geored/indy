/**
 * Copyright (C) 2011-2018 Red Hat, Inc. (https://github.com/Commonjava/indy)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.pkg.npm.content;

import org.commonjava.maven.galley.util.UrlUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

public class DecoratorUtils
{
    /**
     * Replace tarball urls with context urls, e.g., "https://registry.npmjs.org/jquery/-/jquery-1.5.1.tgz" to
     * "http://${indy}/api/content/npm/remote/test/jquery/-/jquery-1.5.1.tgz".
     */
    public static String updatePackageJson( String raw, String contextURL ) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int index;
        while ( ( index = raw.indexOf( "\"tarball\"" ) ) >= 0 )
        {
            int colon = raw.indexOf( ":", index );
            String s = raw.substring( 0, colon + 1 );
            sb.append( s );

            raw = raw.substring( colon + 1 );
            int quote = raw.indexOf( "\"" );
            s = raw.substring( 0, quote ); // blanks between : and "
            sb.append( s );

            int nextQuote = raw.indexOf( "\"", quote + 1 );
            String url = raw.substring( quote + 1, nextQuote );
            String path = getPath(url);

            url = UrlUtils.buildUrl( contextURL, path );
            sb.append( "\"" + url + "\"" );
            raw = raw.substring( nextQuote + 1 );
        }
        sb.append( raw );
        return sb.toString();
    }

    private static String getPath( String url ) throws IOException
    {
        URL url1;
        try
        {
            url1 = new URL( url );
        }
        catch ( MalformedURLException e )
        {
            throw new IOException( "Failed to parse URL " + url, e ); // should not happen
        }

        String[] pathParts = url1.getPath().split( "\\/" );
        if ( pathParts.length < 1 )
        {
            return "";
        }
        else if ( pathParts.length == 1 )
        {
            return pathParts[0];
        }

        String lastPart = pathParts[pathParts.length - 1];
        if ( ( "package.json".equals( lastPart ) || lastPart.endsWith( "tgz" ) ) && pathParts.length > 2 )
        {
            return String.format( "%s/%s/%s", pathParts[pathParts.length - 3], pathParts[pathParts.length - 2],
                                  pathParts[pathParts.length - 1] );
        }
        else if ( "-".equals( lastPart ) )
        {
            return String.format( "%s/%s", pathParts[pathParts.length - 2], pathParts[pathParts.length - 1] );
        }

        return lastPart;
    }

}