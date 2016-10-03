/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.diagram;

import org.kie.workbench.common.stunner.core.api.DiagramManager;
import org.kie.workbench.common.stunner.core.backend.annotation.Application;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentationImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class ApplicationDiagramLookupManager
        extends AbstractLookupManager<Diagram, DiagramRepresentation, DiagramLookupRequest>
        implements DiagramLookupManager {

    DiagramManager<Diagram> appDiagramManager;

    @Inject
    public ApplicationDiagramLookupManager( @Application DiagramManager<Diagram> appDiagramManager ) {
        this.appDiagramManager = appDiagramManager;
    }

    @Override
    protected List<Diagram> getItems( final DiagramLookupRequest request ) {
        return new LinkedList<>( appDiagramManager.getItems() );
    }

    @Override
    protected boolean matches( final String criteria, final Diagram item ) {
        return true;
    }

    @Override
    protected DiagramRepresentation buildResult( final Diagram item ) {
        return new DiagramRepresentationImpl.DiagramRepresentationBuilder( item ).build();
    }

}
