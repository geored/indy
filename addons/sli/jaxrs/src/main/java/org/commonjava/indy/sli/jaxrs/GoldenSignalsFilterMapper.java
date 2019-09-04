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
package org.commonjava.indy.sli.jaxrs;

import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.commonjava.indy.bind.jaxrs.IndyDeploymentProvider;
import org.commonjava.indy.bind.jaxrs.ResourceManagementFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.ws.rs.core.Application;

@ApplicationScoped
public class GoldenSignalsFilterMapper
        extends IndyDeploymentProvider
{
    @Inject
    private GoldenSignalsFilter goldenSignalsFilter;

    @Override
    public DeploymentInfo getDeploymentInfo( final String contextRoot, final Application application )
    {
        DeploymentInfo di = new DeploymentInfo();

        FilterInfo filterInfo = Servlets.filter( "SLI Reporting", GoldenSignalsFilter.class,
                                                    new ImmediateInstanceFactory<GoldenSignalsFilter>(
                                                            this.goldenSignalsFilter ) );

        di.addFilter( filterInfo )
          .addFilterUrlMapping( filterInfo.getName(), "/api/folo/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/content/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/promotion/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/admin/stores/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/browse/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/remote/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/hosted/*", DispatcherType.REQUEST )
          .addFilterUrlMapping( filterInfo.getName(), "/api/group/*", DispatcherType.REQUEST );

        return di;
    }
}