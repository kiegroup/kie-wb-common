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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentBaseTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( PowerMockRunner.class )
@PrepareForTest( StringUtils.class )
public class AssignmentsEditorWidgetTest extends AssignmentBaseTest {

    private static final String TASK_NAME = "Get Address";

    private static final String ASSIGNMENTS_INFO = "|input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable||output1:com.test.Employee,output2:String|[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    public static final String DATA_INPUT_SET = "input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable";
    public static final String DATA_OUTPUT_SET = "output1:com.test.Employee,output2:String";
    public static final String ASSIGNMENTS = "[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    public static final List<String> DATATYPES = new ArrayList<String>( Arrays.asList("myorg.myproject1.Cardboard", "yourorg.materials.Paper", "org.documents.Articles" ) );

    @GwtMock
    private AssignmentsEditorWidget widget;

    @GwtMock
    private ActivityDataIOEditor activityDataIOEditor;

    @GwtMock
    private ActivityDataIOEditorView activityDataIOEditorView;

    @Mock
    UserTask userTask;

    @Mock
    TaskGeneralSet taskGeneralSet;

    @Mock
    Name taskName;

    @Captor
    private ArgumentCaptor<String> taskNameCaptor;

    @Captor
    private ArgumentCaptor<Boolean> hasInputVarsCaptor;

    @Captor
    private ArgumentCaptor<Boolean> isSingleInputVarCaptor;

    @Captor
    private ArgumentCaptor<Boolean> hasOutputVarsCaptor;

    @Captor
    private ArgumentCaptor<Boolean> isSingleOutputVarCaptor;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks( this );
        widget.activityDataIOEditor = activityDataIOEditor;
        activityDataIOEditor.view = activityDataIOEditorView;
        doCallRealMethod().when( widget ).parseAssignmentsInfo();
        doCallRealMethod().when( widget ).getVariableCountsString( anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString() );
        doCallRealMethod().when( widget ).showAssignmentsDialog();
        doCallRealMethod().when( widget ).showDataIOEditor( anyString() );
        doCallRealMethod().when( widget ).setBPMNModel( any( BPMNDefinition.class ) );
        doCallRealMethod().when( widget ).formatDataTypes( any( List.class ) );
        doCallRealMethod().when( widget ).getTaskName();
        when( userTask.getGeneral() ).thenReturn( taskGeneralSet );
        when( taskGeneralSet.getName() ).thenReturn( taskName );
        when( taskName.getValue() ).thenReturn( TASK_NAME );
        doCallRealMethod().when( activityDataIOEditor ).configureDialog( anyString(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean() );

        widget.setBPMNModel( userTask );
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testParseAssignmentsInfo() {
        widget.assignmentsInfo = ASSIGNMENTS_INFO;
        Map<String, String> assignmentsProperties = widget.parseAssignmentsInfo();
        assertEquals( DATA_INPUT_SET, assignmentsProperties.get( "datainputset" ) );
        assertEquals( DATA_OUTPUT_SET, assignmentsProperties.get( "dataoutputset" ) );
        assertEquals( ASSIGNMENTS, assignmentsProperties.get( "assignments" ) );
    }

    @Test
    public void testGetVariableCountsString() {
        String variableCountsString = widget.getVariableCountsString( null, "input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable",
                null, "output1:com.test.Employee,output2:String",
                "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String",
                "[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason",
                "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify" );
        assertEquals( "4 Data_Inputs, 2 Data_Outputs", variableCountsString );
    }

    @Test
    public void testShowAssignmentsDialog() {
        widget.setBPMNModel( userTask );
        widget.assignmentsInfo = ASSIGNMENTS_INFO;
        Map<String, String> assignmentsProperties = widget.parseAssignmentsInfo();

        widget.showAssignmentsDialog();

        verify( widget ).getDataTypes( );
    }

    @Test
    public void testShowDataIOEditor() {
        widget.showDataIOEditor( null );

        verify ( activityDataIOEditor ).configureDialog( taskNameCaptor.capture(),
                hasInputVarsCaptor.capture(), isSingleInputVarCaptor.capture(),
                hasOutputVarsCaptor.capture(), isSingleOutputVarCaptor.capture() );
        assertEquals( TASK_NAME, taskNameCaptor.getValue() );
        assertEquals( true, hasInputVarsCaptor.getValue() );
        assertEquals( false, isSingleInputVarCaptor.getValue() );
        assertEquals( true, hasOutputVarsCaptor.getValue() );
        assertEquals( false, isSingleOutputVarCaptor.getValue() );
    }

    @Test
    public void testFormatDataTypes() {
        String formattedDataTypes = widget.formatDataTypes( DATATYPES );
        assertEquals( "Articles [org.documents]:org.documents.Articles,Cardboard [myorg.myproject1]:myorg.myproject1.Cardboard,Paper [yourorg.materials]:yourorg.materials.Paper",  formattedDataTypes);
    }

    @Test
    public void testGetTaskName() {
        String taskName = widget.getTaskName();
        assertEquals( TASK_NAME, taskName);
    }
}

