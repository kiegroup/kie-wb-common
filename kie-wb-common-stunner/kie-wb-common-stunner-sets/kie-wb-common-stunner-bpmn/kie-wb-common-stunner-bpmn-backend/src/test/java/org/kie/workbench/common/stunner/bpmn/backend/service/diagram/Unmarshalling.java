package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDiagramMarshallerNoJson;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;

import java.io.InputStream;

public class Unmarshalling {

    public static InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static Diagram<Graph, Metadata> unmarshall(BPMNDiagramMarshallerNoJson tested, String fileName) throws Exception {
        InputStream is = loadStream(fileName);
        return unmarshall(tested, is);
    }

    public static Diagram<Graph, Metadata> unmarshall(BPMNDiagramMarshallerNoJson tested, InputStream is) throws Exception {
        Metadata metadata =
                new MetadataImpl.MetadataImplBuilder(
                        BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class))
                        .build();
        DiagramImpl result = new DiagramImpl(
                org.kie.workbench.common.stunner.core.util.UUID.uuid(),
                metadata);
        Graph graph = tested.unmarshall(metadata, is);
        result.setGraph(graph);
        // Update diagram's metadata attributes.
        tested.updateRootUUID(result.getMetadata(), graph);
        tested.updateTitle(result.getMetadata(), graph);
        return result;
    }

}
