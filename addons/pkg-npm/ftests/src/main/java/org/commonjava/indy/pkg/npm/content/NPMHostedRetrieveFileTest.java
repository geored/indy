/**
 * Copyright (C) 2017 Red Hat, Inc. (yma@commonjava.org)
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

import org.apache.commons.io.IOUtils;
import org.commonjava.indy.client.core.helper.PathInfo;
import org.commonjava.indy.ftest.core.AbstractContentManagementTest;
import org.commonjava.indy.model.core.HostedRepository;
import org.commonjava.indy.model.core.StoreKey;
import org.commonjava.test.http.expect.ExpectationServer;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import static org.commonjava.indy.pkg.npm.model.NPMPackageTypeDescriptor.NPM_PKG_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NPMHostedRetrieveFileTest
                extends AbstractContentManagementTest
{
    @Rule
    public ExpectationServer server = new ExpectationServer( "repos" );

    @Override
    protected boolean createStandardTestStructures()
    {
        return false;
    }

    @Test
    public void test() throws Exception
    {

        final String packageContent =
                        "{\"name\": \"jquery\",\n" + "\"description\": \"JavaScript library for DOM operations\",\n"
                                        + "\"license\": \"MIT\"}";
        final String versionContent =
                        "{\"name\": \"jquery\",\n" + "\"url\": \"jquery.com\",\n" + "\"version\": \"2.1.0\"}";

        byte[] tgz = new byte[32];
        new Random().nextBytes( tgz );

        final String packagePath = "jquery/package.json";
        final String versionPath = "jquery/2.1.0/package.json";
        final String tarballPath = "jquery/2.1.0/package.tgz";

        final String packageMappingPath = "jquery";
        final String versionMappingPath = "jquery/2.1.0";
        final String tarballMappingPath = "jquery/-/jquery-2.1.0.tgz";

        final HostedRepository hostedRepository = new HostedRepository( NPM_PKG_KEY, STORE );
        final StoreKey storeKey = hostedRepository.getKey();

        client.stores().create( hostedRepository, "adding npm hosted repo", HostedRepository.class );

        client.content().store( storeKey, packagePath, new ByteArrayInputStream( packageContent.getBytes() ) );
        client.content().store( storeKey, versionPath, new ByteArrayInputStream( versionContent.getBytes() ) );
        client.content().store( storeKey, tarballPath, new ByteArrayInputStream( tgz ) );

        final PathInfo result1 = client.content().getInfo( storeKey, packageMappingPath );
        final PathInfo result2 = client.content().getInfo( storeKey, versionMappingPath );
        final PathInfo result3 = client.content().getInfo( storeKey, tarballMappingPath );

        assertThat( "no result", result1, notNullValue() );
        assertThat( "doesn't exist", result1.exists(), equalTo( true ) );

        assertThat( "no result", result2, notNullValue() );
        assertThat( "doesn't exist", result2.exists(), equalTo( true ) );

        assertThat( "no result", result3, notNullValue() );
        assertThat( "doesn't exist", result3.exists(), equalTo( true ) );

        final InputStream packageStream = client.content().get( storeKey, packageMappingPath );
        final InputStream tarballStream = client.content().get( storeKey, tarballMappingPath );

        assertThat( packageStream, notNullValue() );
        assertThat( tarballStream, notNullValue() );

        assertThat( IOUtils.toString( packageStream ), equalTo( packageContent ) );
        assertThat( IOUtils.toByteArray( tarballStream ), equalTo( tgz ) );

        packageStream.close();
        tarballStream.close();
    }
}
