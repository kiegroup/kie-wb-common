/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditor;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.kogito.client.PromiseMock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.createDataTypeDisplayName;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultImportsEditorTest extends ImportsEditorTest<DefaultImport> {

    private static final String SERVER_DATA_TYPE_1 = "org.test.type1";
    private static final String SERVER_DATA_TYPE_2 = "org.test.type2";
    private static final String SERVER_DATA_TYPE_3 = "org.test.type3";
    private static final String ERROR = "ERROR";

    private SessionManager sessionManager;
    private DataTypeNamesService dataTypeNamesService;
    private Event<NotificationEvent> notification;

    private Metadata metadata = mock(Metadata.class);
    private Path validPath;
    private Path invalidPath;
    private List<String> serverDataTypes;

    private DefaultImportsEditor concreteTested;

    @Override
    public void setUp() {
        sessionManager = mock(SessionManager.class);
        dataTypeNamesService = mock(DataTypeNamesService.class);
        notification = mock(Event.class);

        super.setUp();

        ClientSession session = mock(ClientSession.class);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        CanvasHandler canvasHandler = mock(CanvasHandler.class);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        Diagram diagram = mock(Diagram.class);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        metadata = mock(Metadata.class);
        when(diagram.getMetadata()).thenReturn(metadata);
        validPath = mock(Path.class);
        invalidPath = mock(Path.class);
        when(metadata.getPath()).thenReturn(validPath);

        serverDataTypes = new ArrayList<String>() {
            {
                add(SERVER_DATA_TYPE_1);
                add(SERVER_DATA_TYPE_2);
                add(SERVER_DATA_TYPE_3);
            }
        };
        doReturn(PromiseMock.success(serverDataTypes)).when(dataTypeNamesService).call(eq(validPath));
        doReturn(PromiseMock.error(new Throwable(ERROR))).when(dataTypeNamesService).call(eq(invalidPath));

        concreteTested = (DefaultImportsEditor) tested;
    }

    @Test
    public void init() {
        concreteTested.init();
        verify(concreteTested).loadDefaultDataTypes();
        verify(concreteTested).loadServerDataTypes();
    }

    @Test
    public void createImport() {
        DefaultImport result = tested.createImport();
        assertNull(result.getClassName());
    }

    @Test
    public void getDataTypes() {
        concreteTested.dataTypes = new TreeMap<>();
        concreteTested.dataTypes.put(SERVER_DATA_TYPE_1, createDataTypeDisplayName(SERVER_DATA_TYPE_1));
        concreteTested.dataTypes.put(SERVER_DATA_TYPE_2, createDataTypeDisplayName(SERVER_DATA_TYPE_2));
        concreteTested.dataTypes.put(SERVER_DATA_TYPE_3, createDataTypeDisplayName(SERVER_DATA_TYPE_3));

        Map<String, String> result = concreteTested.getDataTypes();
        assertEquals(concreteTested.dataTypes, result);
    }

    @Test
    public void getDataType() {
        final String testValue1 = createDataTypeDisplayName(SERVER_DATA_TYPE_1);
        final String testValue2 = SERVER_DATA_TYPE_2;

        concreteTested.dataTypes = new TreeMap<>();
        concreteTested.dataTypes.put(SERVER_DATA_TYPE_1, testValue1);

        String result1 = concreteTested.getDataType(testValue1);
        String result2 = concreteTested.getDataType(testValue2);

        assertEquals(SERVER_DATA_TYPE_1, result1);
        assertEquals(SERVER_DATA_TYPE_2, result2);
    }

    @Test
    public void loadDefaultDataTypes() {
        concreteTested.dataTypes = new TreeMap<>();
        concreteTested.loadDefaultDataTypes();

        assertEquals(5, concreteTested.dataTypes.size());
        assertTrue(concreteTested.dataTypes.containsKey("Boolean"));
        assertTrue(concreteTested.dataTypes.containsKey("Float"));
        assertTrue(concreteTested.dataTypes.containsKey("Integer"));
        assertTrue(concreteTested.dataTypes.containsKey("Object"));
        assertTrue(concreteTested.dataTypes.containsKey("String"));
    }

    @Test
    public void loadServerDataTypes() {
        when(metadata.getPath()).thenReturn(validPath);
        DefaultImportsEditor tested1 = new DefaultImportsEditor(sessionManager,
                                                                dataTypeNamesService,
                                                                notification);
        tested1.loadServerDataTypes();

        when(metadata.getPath()).thenReturn(invalidPath);
        DefaultImportsEditor tested2 = new DefaultImportsEditor(sessionManager,
                                                                dataTypeNamesService,
                                                                notification);
        tested2.loadServerDataTypes();

        assertEquals(3, tested1.dataTypes.size());
        assertTrue(tested1.dataTypes.containsKey(SERVER_DATA_TYPE_1));
        assertTrue(tested1.dataTypes.containsKey(SERVER_DATA_TYPE_2));
        assertTrue(tested1.dataTypes.containsKey(SERVER_DATA_TYPE_3));

        assertEquals(0, tested2.dataTypes.size());
        verify(concreteTested.notification, times(1)).fire(any(NotificationEvent.class));
    }

    @Test
    public void addDataTypes() {
        concreteTested.dataTypes = new TreeMap<>();

        List<String> dataTypes1 = new ArrayList<>();
        dataTypes1.add(SERVER_DATA_TYPE_1);

        List<String> dataTypes2 = new ArrayList<>();
        dataTypes2.add(SERVER_DATA_TYPE_2);
        String dt2DisplayName = createDataTypeDisplayName(SERVER_DATA_TYPE_2);

        concreteTested.addDataTypes(dataTypes1, false);
        concreteTested.addDataTypes(dataTypes2, true);

        assertEquals(2, concreteTested.dataTypes.size());
        assertTrue(concreteTested.dataTypes.containsKey(SERVER_DATA_TYPE_1));
        assertTrue(concreteTested.dataTypes.containsValue(SERVER_DATA_TYPE_1));
        assertTrue(concreteTested.dataTypes.containsKey(SERVER_DATA_TYPE_2));
        assertTrue(concreteTested.dataTypes.containsValue(dt2DisplayName));
    }

    @Override
    protected DefaultImport mockImport() {
        return mock(DefaultImport.class);
    }

    @Override
    protected ImportsEditorView<DefaultImport> mockView() {
        return mock(DefaultImportsEditorViewImpl.class);
    }

    @Override
    protected ImportsEditor<DefaultImport> spyEditor() {
        return spy(new DefaultImportsEditor(sessionManager,
                                            dataTypeNamesService,
                                            notification));
    }
}