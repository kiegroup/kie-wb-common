/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.BPMNGraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.AssignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.BooleanTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertySerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ColorTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.DoubleTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.EnumTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.IntegerTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.StringTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.TimerSettingsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.VariablesTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.bind.BackendBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MigrationDiagramMarshallerTest {

    private static final String BPMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);

    private static final String BPMN_BASIC = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/basic.bpmn";
    private static final String BPMN_EVALUATION = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/evaluation.bpmn";
    private static final String BPMN_LANES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/lanes.bpmn";
    private static final String BPMN_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryIntmEvent.bpmn";
    private static final String BPMN_NOT_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/notBoundaryIntmEvent.bpmn";
    private static final String BPMN_PROCESSVARIABLES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processVariables.bpmn";
    private static final String BPMN_USERTASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignments.bpmn";
    private static final String BPMN_BUSINESSRULETASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTaskAssignments.bpmn";
    private static final String BPMN_STARTNONEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startNoneEvent.bpmn";
    private static final String BPMN_STARTTIMEREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startTimerEvent.bpmn";
    private static final String BPMN_STARTSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startSignalEvent.bpmn";
    private static final String BPMN_STARTMESSAGEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startMessageEvent.bpmn";
    private static final String BPMN_STARTERROREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startErrorEvent.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateSignalEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_ERROR_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateErrorEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateSignalEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateMessageEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateMessageEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateTimerEvent.bpmn";
    private static final String BPMN_ENDSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endSignalEvent.bpmn";
    private static final String BPMN_ENDMESSAGEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endMessageEvent.bpmn";
    private static final String BPMN_ENDNONEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endNoneEvent.bpmn";
    private static final String BPMN_ENDTERMINATEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endTerminateEvent.bpmn";
    private static final String BPMN_PROCESSPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processProperties.bpmn";
    private static final String BPMN_BUSINESSRULETASKRULEFLOWGROUP = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTask.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubprocessCalledElement.bpmn";
    private static final String BPMN_EMBEDDED_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubprocess.bpmn";
    private static final String BPMN_SCRIPTTASK = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/scriptTask.bpmn";
    private static final String BPMN_USERTASKASSIGNEES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignees.bpmn";
    private static final String BPMN_USERTASKPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskProperties.bpmn";
    private static final String BPMN_SEQUENCEFLOW = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/sequenceFlow.bpmn";
    private static final String BPMN_XORGATEWAY = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/xorGateway.bpmn";
    private static final String BPMN_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/timerEvent.bpmn";
    private static final String BPMN_SIMULATIONPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/simulationProperties.bpmn";
    private static final String BPMN_MAGNETDOCKERS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetDockers.bpmn";
    private static final String BPMN_MAGNETSINLANE = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetsInLane.bpmn";
    private static final String BPMN_ENDERROR_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endErrorEvent.bpmn";

    @Mock
    private DefinitionManager definitionManager;
    @Mock
    private AdapterManager adapterManager;
    @Mock
    private AdapterRegistry adapterRegistry;
    @Mock
    private RuleManager rulesManager;
    @Mock
    private CloneManager cloneManager;
    @Mock
    private ApplicationFactoryManager applicationFactoryManager;

    private EdgeFactory<Object> connectionEdgeFactory;
    private NodeFactory<Object> viewNodeFactory;
    private GraphFactory bpmnGraphFactory;
    private TestScopeModelFactory testScopeModelFactory;
    private TaskTypeMorphDefinition taskMorphDefinition;

    private BPMNDiagramMarshaller oldMarshaller;
    private BPMNDirectDiagramMarshaller newMarshaller;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        definitionManager = mock(DefinitionManager.class);
        adapterManager = mock(AdapterManager.class);
        adapterRegistry = mock(AdapterRegistry.class);
        rulesManager = mock(RuleManager.class);
        cloneManager = mock(CloneManager.class);

        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        DefinitionUtils definitionUtils1 = new DefinitionUtils(definitionManager,
                                                               applicationFactoryManager);
        testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        final BackendDefinitionAdapter definitionAdapter = new BackendDefinitionAdapter(definitionUtils1);
        final BackendDefinitionSetAdapter definitionSetAdapter = new BackendDefinitionSetAdapter(definitionAdapter);
        final BackendPropertySetAdapter propertySetAdapter = new BackendPropertySetAdapter();
        final BackendPropertyAdapter propertyAdapter = new BackendPropertyAdapter();
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        GraphCommandManager commandManager1 = new GraphCommandManagerImpl(null,
                                                                          null,
                                                                          null);
        GraphCommandFactory commandFactory1 = new GraphCommandFactory();
        connectionEdgeFactory = new EdgeFactoryImpl(definitionManager);
        viewNodeFactory = new NodeFactoryImpl(definitionUtils1);
        bpmnGraphFactory = new GraphFactoryImpl(definitionManager);
        doAnswer(invocationOnMock -> {
            String id = (String) invocationOnMock.getArguments()[0];
            return testScopeModelFactory.build(id);
        }).when(applicationFactoryManager).newDefinition(anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String id = (String) invocationOnMock.getArguments()[1];
            if (BPMNDefinitionSet.class.getName().equals(id)) {
                return bpmnGraphFactory.build(uuid, BPMN_DEF_SET_ID);
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    return viewNodeFactory.build(uuid, model);
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    return connectionEdgeFactory.build(uuid, model);
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            Class type = (Class) invocationOnMock.getArguments()[1];
            String id = BindableAdapterUtils.getGenericClassName(type);
            if (BPMNDefinitionSet.class.equals(type)) {
                return bpmnGraphFactory.build(uuid, BPMN_DEF_SET_ID);
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    return viewNodeFactory.build(uuid, model);
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    return connectionEdgeFactory.build(uuid, model);
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      any(Class.class));
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String defSetId = (String) invocationOnMock.getArguments()[1];
            final Graph graph = (Graph) applicationFactoryManager.newElement(uuid,
                                                                             defSetId);
            final DiagramImpl result = new DiagramImpl(uuid,
                                                       new MetadataImpl.MetadataImplBuilder(defSetId).build());
            result.setGraph(graph);
            return result;
        }).when(applicationFactoryManager).newDiagram(anyString(),
                                                      anyString(),
                                                      any(Metadata.class));
        // Bpmn 2 oryx stuff.
        Bpmn2OryxIdMappings oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager);
        StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();
        BooleanTypeSerializer booleanTypeSerializer = new BooleanTypeSerializer();
        ColorTypeSerializer colorTypeSerializer = new ColorTypeSerializer();
        DoubleTypeSerializer doubleTypeSerializer = new DoubleTypeSerializer();
        IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
        EnumTypeSerializer enumTypeSerializer = new EnumTypeSerializer(definitionUtils1);
        AssignmentsTypeSerializer assignmentsTypeSerializer = new AssignmentsTypeSerializer();
        VariablesTypeSerializer variablesTypeSerializer = new VariablesTypeSerializer();
        TimerSettingsTypeSerializer timerSettingsTypeSerializer = new TimerSettingsTypeSerializer();
        List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();
        propertySerializers.add(stringTypeSerializer);
        propertySerializers.add(booleanTypeSerializer);
        propertySerializers.add(colorTypeSerializer);
        propertySerializers.add(doubleTypeSerializer);
        propertySerializers.add(integerTypeSerializer);
        propertySerializers.add(enumTypeSerializer);
        propertySerializers.add(assignmentsTypeSerializer);
        propertySerializers.add(variablesTypeSerializer);
        propertySerializers.add(timerSettingsTypeSerializer);
        Bpmn2OryxPropertyManager oryxPropertyManager = new Bpmn2OryxPropertyManager(propertySerializers);
        Bpmn2OryxManager oryxManager = new Bpmn2OryxManager(oryxIdMappings,
                                                            oryxPropertyManager);
        oryxManager.init();
        // Marshalling factories.
        BPMNGraphObjectBuilderFactory objectBuilderFactory = new BPMNGraphObjectBuilderFactory(definitionManager,
                                                                                               oryxManager);
        taskMorphDefinition = new TaskTypeMorphDefinition();
        Collection<MorphDefinition> morphDefinitions = new ArrayList<MorphDefinition>() {{
            add(taskMorphDefinition);
        }};
        BackendBindableMorphAdapter<Object> morphAdapter =
                new BackendBindableMorphAdapter(definitionUtils1,
                                                applicationFactoryManager,
                                                cloneManager,
                                                morphDefinitions);
        when(adapterRegistry.getMorphAdapter(eq(UserTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(NoneTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(ScriptTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(BusinessRuleTask.class))).thenReturn(morphAdapter);
        GraphIndexBuilder<?> indexBuilder = new MapIndexBuilder();
        when(rulesManager.evaluate(any(RuleSet.class),
                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations());
        // The tested BPMN marshaller.
        oldMarshaller = new BPMNDiagramMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                                  objectBuilderFactory,
                                                  definitionManager,
                                                  indexBuilder,
                                                  oryxManager,
                                                  applicationFactoryManager,
                                                  rulesManager,
                                                  commandManager1,
                                                  commandFactory1);

        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        // initApplicationFactoryManagerAlt();
        when(rulesManager.evaluate(any(RuleSet.class),
                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations());

        DefinitionUtils definitionUtils = new DefinitionUtils(definitionManager,
                                                              applicationFactoryManager);
        TestScopeModelFactory testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        mockAdapterManager(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        mockAdapterRegistry(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        applicationFactoryManager = new MockApplicationFactoryManager(
                new GraphFactoryImpl(definitionManager),
                testScopeModelFactory,
                new EdgeFactoryImpl(definitionManager),
                new NodeFactoryImpl(definitionUtils)
        );

        GraphCommandManagerImpl commandManager = new GraphCommandManagerImpl(null,
                                                                             null,
                                                                             null);
        GraphCommandFactory commandFactory = new GraphCommandFactory();

        // The tested BPMN marshaller.
        newMarshaller = new BPMNDirectDiagramMarshaller(definitionManager, rulesManager, applicationFactoryManager, commandFactory, commandManager);
    }

    private void mockAdapterRegistry(BackendDefinitionAdapter definitionAdapter, BackendDefinitionSetAdapter definitionSetAdapter, BackendPropertySetAdapter propertySetAdapter, BackendPropertyAdapter propertyAdapter) {
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
    }

    private void mockAdapterManager(BackendDefinitionAdapter definitionAdapter, BackendDefinitionSetAdapter definitionSetAdapter, BackendPropertySetAdapter propertySetAdapter, BackendPropertyAdapter propertyAdapter) {
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
    }

    @Test
    public void testUnmarshallOldStaff() throws Exception {
        String[] oldStaff = {BPMN_BASIC, BPMN_EVALUATION, BPMN_LANES, BPMN_BOUNDARY_EVENTS, BPMN_NOT_BOUNDARY_EVENTS, BPMN_PROCESSVARIABLES, BPMN_USERTASKASSIGNMENTS, BPMN_BUSINESSRULETASKASSIGNMENTS, BPMN_STARTNONEEVENT, BPMN_STARTTIMEREVENT, BPMN_STARTSIGNALEVENT, BPMN_STARTMESSAGEEVENT, BPMN_STARTERROREVENT, BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING, BPMN_INTERMEDIATE_ERROR_EVENTCATCHING, BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING, BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING, BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING, BPMN_INTERMEDIATE_TIMER_EVENT, BPMN_ENDSIGNALEVENT, BPMN_ENDMESSAGEEVENT, BPMN_ENDNONEEVENT, BPMN_ENDTERMINATEEVENT, BPMN_PROCESSPROPERTIES, BPMN_BUSINESSRULETASKRULEFLOWGROUP, BPMN_REUSABLE_SUBPROCESS, BPMN_SCRIPTTASK, BPMN_USERTASKASSIGNEES, BPMN_USERTASKPROPERTIES, BPMN_SEQUENCEFLOW, BPMN_XORGATEWAY, BPMN_TIMER_EVENT, BPMN_SIMULATIONPROPERTIES, BPMN_MAGNETDOCKERS, BPMN_MAGNETSINLANE, BPMN_ENDERROR_EVENT};

        Diagram<Graph, Metadata> oldDiagram;
        Diagram<Graph, Metadata> newDiagram;

        for (String fileName : oldStaff) {
            oldDiagram = Unmarshalling.unmarshall(oldMarshaller, fileName);
            newDiagram = Unmarshalling.unmarshall(newMarshaller, fileName);

            // Doesn't work, due to old Marshaller and new Marshaller have different BPMNDefinitionSet uuids
            // assertEquals(oldDiagram.getGraph(), newDiagram.getGraph());

            // Let's check nodes only.
            assertEquals(oldDiagram.getGraph().nodes(), newDiagram.getGraph().nodes());
        }
    }

    @Test
    public void testUnmarshallEmbeddedSubprocess() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_EMBEDDED_SUBPROCESS);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_EMBEDDED_SUBPROCESS);

        // Doesn't work, due to old Marshaller and new Marshaller have different BPMNDefinitionSet uuids
        // assertEquals(oldDiagram.getGraph(), newDiagram.getGraph());

        // Let's check nodes only.
        assertEquals(oldDiagram.getGraph().nodes(), newDiagram.getGraph().nodes());
    }
}
