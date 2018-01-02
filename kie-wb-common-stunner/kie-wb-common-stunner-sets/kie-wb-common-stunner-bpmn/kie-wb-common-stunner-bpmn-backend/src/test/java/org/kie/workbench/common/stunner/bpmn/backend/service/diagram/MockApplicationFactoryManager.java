package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.BPMNDiagramMarshallerTest.BPMN_DEF_SET_ID;

public class MockApplicationFactoryManager extends ApplicationFactoryManager {
    final GraphFactory bpmnGraphFactory;
    final TestScopeModelFactory testScopeModelFactory;
    final EdgeFactory<Object> connectionEdgeFactory;
    final NodeFactory<Object> viewNodeFactory;


    public MockApplicationFactoryManager(
            GraphFactory bpmnGraphFactory,
            TestScopeModelFactory testScopeModelFactory,
            EdgeFactory<Object> connectionEdgeFactory,
            NodeFactory<Object> viewNodeFactory) {

        this.bpmnGraphFactory = bpmnGraphFactory;
        this.testScopeModelFactory = testScopeModelFactory;
        this.connectionEdgeFactory = connectionEdgeFactory;
        this.viewNodeFactory = viewNodeFactory;
    }

    @Override
    public <T> T newDefinition(String id) {
        return (T) testScopeModelFactory.build(id);
    }

    @Override
    public Element<?> newElement(String uuid, String id) {
        if (BPMNDefinitionSet.class.getName().equals(id)) {
            Graph graph = (Graph) bpmnGraphFactory.build(uuid,
                    BPMN_DEF_SET_ID);
            return graph;
        }
        Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
        if (null != model) {
            Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
            if (element.isAssignableFrom(NodeFactory.class)) {
                Node node = viewNodeFactory.build(uuid,
                        model);
                return node;
            } else if (element.isAssignableFrom(EdgeFactory.class)) {
                Edge edge = connectionEdgeFactory.build(uuid,
                        model);
                return edge;
            }
        }
        return null;

    }

    @Override
    public Element<?> newElement(String uuid, Class<?> type) {
        String id = BindableAdapterUtils.getGenericClassName(type);
        if (BPMNDefinitionSet.class.equals(type)) {
            Graph graph = (Graph) bpmnGraphFactory.build(uuid,
                    BPMN_DEF_SET_ID);
            return graph;
        }
        Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
        if (null != model) {
            Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
            if (element.isAssignableFrom(NodeFactory.class)) {
                Node node = viewNodeFactory.build(uuid,
                        model);
                return node;
            } else if (element.isAssignableFrom(EdgeFactory.class)) {
                Edge edge = connectionEdgeFactory.build(uuid,
                        model);
                return edge;
            }
        }
        return null;
    }

    @Override
    public <M extends Metadata, D extends Diagram> D newDiagram(String uuid, String defSetId, M metadata) {
        final Graph graph = (Graph) this.newElement(uuid,
                defSetId);
        final DiagramImpl result = new DiagramImpl(uuid,
                new MetadataImpl.MetadataImplBuilder(defSetId).build());
        result.setGraph(graph);
        return (D) result;
    }
}
