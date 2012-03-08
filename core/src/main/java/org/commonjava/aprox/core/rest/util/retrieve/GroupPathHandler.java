/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.aprox.core.rest.util.retrieve;

import java.io.InputStream;
import java.util.List;

import org.commonjava.aprox.core.model.ArtifactStore;
import org.commonjava.aprox.core.model.DeployPoint;
import org.commonjava.aprox.core.model.Group;
import org.commonjava.aprox.core.rest.RESTWorkflowException;
import org.commonjava.aprox.core.rest.StoreInputStream;

public interface GroupPathHandler
{

    boolean canHandle( String path );

    StoreInputStream retrieve( Group group, List<? extends ArtifactStore> stores, String path )
        throws RESTWorkflowException;

    DeployPoint store( Group group, List<? extends ArtifactStore> stores, String path, InputStream stream )
        throws RESTWorkflowException;

}
