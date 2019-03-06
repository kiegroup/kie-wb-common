/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.submarine.api.factory.impl;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineMetadata;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineDiagramImpl;

/**
 * The default factory for SubmarineDiagrams.
 */
@ApplicationScoped
public class SubmarineDiagramFactory
        implements DiagramFactory<SubmarineMetadata, SubmarineDiagramImpl> {

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return SubmarineMetadata.class;
    }

    @Override
    public SubmarineDiagramImpl build(final String name,
                                      final SubmarineMetadata metadata,
                                      final Graph<DefinitionSet, ?> graph) {
        return new SubmarineDiagramImpl(name,
                                        graph,
                                        metadata);
    }

    @Override
    public boolean accepts(final String source) {
        return true;
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
