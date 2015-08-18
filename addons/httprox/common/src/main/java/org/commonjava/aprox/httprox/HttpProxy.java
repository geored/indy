/**
 * Copyright (C) 2011 Red Hat, Inc. (jdcasey@commonjava.org)
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
package org.commonjava.aprox.httprox;

import org.commonjava.aprox.action.AproxLifecycleException;
import org.commonjava.aprox.action.ShutdownAction;
import org.commonjava.aprox.action.StartupAction;
import org.commonjava.aprox.boot.BootOptions;
import org.commonjava.aprox.httprox.conf.HttproxConfig;
import org.commonjava.aprox.httprox.handler.ProxyAcceptHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

@ApplicationScoped
public class HttpProxy
    implements StartupAction, ShutdownAction
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @Inject
    private HttproxConfig config;

    @Inject
    private BootOptions bootOptions;

    @Inject
    private ProxyAcceptHandler acceptHandler;

    private AcceptingChannel<StreamConnection> server;

    protected HttpProxy()
    {
    }

    public HttpProxy( final HttproxConfig config, final BootOptions bootOptions, ProxyAcceptHandler acceptHandler )
    {
        this.config = config;
        this.bootOptions = bootOptions;
        this.acceptHandler = acceptHandler;
    }

    @Override
    public void start()
        throws AproxLifecycleException
    {
        if ( !config.isEnabled() )
        {
            logger.info( "HTTProx proxy is disabled." );
            return;
        }

        String bind = bootOptions.getBind();
        if ( bind == null )
        {
            bind = "0.0.0.0";
        }

        logger.info( "Starting HTTProx proxy on: {}:{}", bind, config.getPort() );

        XnioWorker worker;
        try
        {
            worker = Xnio.getInstance()
                         .createWorker( OptionMap.EMPTY );

            final InetSocketAddress addr = new InetSocketAddress( bind, config.getPort() );

            server = worker.createStreamConnectionServer( addr, acceptHandler, OptionMap.EMPTY );

            server.resumeAccepts();
            logger.info( "HTTProxy listening on: {}", addr );
        }
        catch ( IllegalArgumentException | IOException e )
        {
            throw new AproxLifecycleException( "Failed to start HTTProx general content proxy: %s", e, e.getMessage() );
        }
    }

    @Override
    public void stop()
    {
        if ( server != null )
        {
            try
            {
                logger.info( "stopping server" );
                server.suspendAccepts();
                server.close();
            }
            catch ( final IOException e )
            {
                logger.error( "Failed to stop: " + e.getMessage(), e );
            }
        }
    }

    @Override
    public String getId()
    {
        return "httproxy-listener";
    }

    @Override
    public int getStartupPriority()
    {
        return 1;
    }

    @Override
    public int getShutdownPriority()
    {
        return 99;
    }

}