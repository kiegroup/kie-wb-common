package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.util.UUID;

import java.io.InputStream;

public class Unmarshalling {

    public static InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static Diagram<Graph, Metadata> unmarshall(BPMNDirectDiagramMarshaller tested, String fileName) throws Exception {
        InputStream is = loadStream(fileName);
        return unmarshall(tested, is);
    }

    public static Diagram<Graph, Metadata> unmarshall(BPMNDirectDiagramMarshaller tested, InputStream is) throws Exception {
        Metadata metadata =
                new MetadataImpl.MetadataImplBuilder(
                        BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class)).build();
        DiagramImpl diagram = new DiagramImpl(UUID.uuid(), metadata);
        Graph<DefinitionSet, Node>  graph = tested.unmarshall(metadata, is);
        diagram.setGraph(graph);

        return diagram;
    }

}
