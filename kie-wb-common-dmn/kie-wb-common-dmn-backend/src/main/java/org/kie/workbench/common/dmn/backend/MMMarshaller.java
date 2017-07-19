/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 */
package org.kie.workbench.common.dmn.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.DRGElement;
import org.kie.dmn.model.v1_1.Decision;
import org.kie.dmn.model.v1_1.Definitions;
import org.kie.dmn.model.v1_1.InformationRequirement;
import org.kie.dmn.model.v1_1.InputData;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

@ApplicationScoped
public class MMMarshaller implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;
    private FactoryManager factoryManager;

    protected MMMarshaller() {
        this(null, null);
    }

    @Inject
    public MMMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller, FactoryManager factoryManager) {
        this.diagramMetadataMarshaller = diagramMetadataMarshaller;
        this.factoryManager = factoryManager;
    }

    @Override
    public Graph unmarshall(final Metadata metadata,
                            final InputStream input) throws IOException {
        
        DMNMarshaller marshaller = DMNMarshallerFactory.newDefaultMarshaller();
        Definitions dmnXml = marshaller.unmarshal(new InputStreamReader( input ));
        
        Map<String, Entry<DRGElement, Node>> elems = 
                dmnXml.getDrgElement().stream().collect( Collectors.toMap( DRGElement::getId, dmn -> new SimpleEntry<>( dmn, dmnToStunner(dmn) ) ) );
        
        for ( Entry<DRGElement, Node> kv : elems.values() ) {
            DRGElement elem = kv.getKey();
            Node currentNode = kv.getValue();
            if ( elem instanceof Decision ) {
                Decision decision = (Decision) elem;
                for ( InformationRequirement ir : decision.getInformationRequirement() ) {
                    if ( ir.getRequiredInput() != null ) {
                        String reqInputID = getId( ir.getRequiredInput() );
                        Node requiredNode = elems.get( reqInputID ).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(), org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
                        connectEdge( myEdge, requiredNode, currentNode);
                    }
                    if ( ir.getRequiredDecision() != null ) {
                        String reqInputID = getId( ir.getRequiredDecision() );
                        Node requiredNode = elems.get( reqInputID ).getValue();
                        Edge myEdge = factoryManager.newElement(UUID.uuid(), org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement.class).asEdge();
                        connectEdge( myEdge, requiredNode, currentNode);
                    }
                }
            }
        }
       
        Node dmnDiagramRoot = factoryManager.newElement(UUID.uuid(), DMNDiagram.class).asNode();
        elems.values().stream().map( kv -> kv.getValue() ).forEach( node -> connectRootWithChild(dmnDiagramRoot, node) );
        
        Graph graph = factoryManager.newDiagram("prova", BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class), null).getGraph();
        elems.values().stream().map( kv -> kv.getValue() ).forEach( graph::addNode );
        
        return graph;
    }
    
    private String getId(DMNElementReference er) {
        String href = er.getHref();
        return href.contains( "#" ) ? href.substring( href.indexOf( '#' ) + 1 ) : href;
    }
    
    private Node dmnToStunner(DRGElement dmn) {
        if ( dmn instanceof InputData ) {
            Node node = factoryManager.newElement(UUID.uuid(), org.kie.workbench.common.dmn.api.definition.v1_1.InputData.class)
                    .asNode();
            ((View) node.getContent()).setDefinition( org.kie.workbench.common.dmn.api.definition.v1_1.InputData.from( (InputData) dmn ) );
            return node;
        } else if ( dmn instanceof Decision ) {
            Node node = factoryManager.newElement(UUID.uuid(), org.kie.workbench.common.dmn.api.definition.v1_1.InputData.class)
                    .asNode();
            ((View) node.getContent()).setDefinition( org.kie.workbench.common.dmn.api.definition.v1_1.Decision.from( (Decision) dmn ) );
            return node;
        } else {
            throw new UnsupportedOperationException("TODO"); // TODO 
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectRootWithChild(Node dmnDiagramRoot, Node child) {
        final String uuid = org.kie.workbench.common.stunner.core.util.UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge, dmnDiagramRoot, child);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectEdge( Edge edge, Node source, Node target ) {
        edge.setSourceNode( source );
        edge.setTargetNode( target );
        source.getOutEdges().add( edge );
        target.getInEdges().add( edge );
    }

    @Override
    public String marshall(final Diagram<Graph, Metadata> diagram) throws IOException {
        throw new UnsupportedOperationException("TODO"); // TODO 
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return diagramMetadataMarshaller;
    }
}