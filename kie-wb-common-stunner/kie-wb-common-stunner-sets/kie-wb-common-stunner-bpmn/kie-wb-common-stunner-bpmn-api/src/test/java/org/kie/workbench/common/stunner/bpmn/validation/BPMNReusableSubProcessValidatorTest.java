/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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


package org.kie.workbench.common.stunner.bpmn.validation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertFalse;
import static org.jgroups.util.Util.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNReusableSubProcessValidatorTest {

    @Mock
    Element element;

    @Mock
    View view;

    @Mock
    ReusableSubprocess subprocess;

    @Mock
    DataIOSet dataIOSet;

    @Mock
    AssignmentsInfo assignmentsInfo;

    @Mock
    BPMNReusableSubProcessValidator tested;

    boolean hasNoInputs = true;

    boolean hasNoOutputs = true;

    @Before
    public void setUp() {
        when(element.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(subprocess);
        when(subprocess.getDataIOSet()).thenReturn(dataIOSet);
        when(dataIOSet.getAssignmentsinfo()).thenReturn(assignmentsInfo);

        tested = spy(new BPMNReusableSubProcessValidator() {
            @Override
            public String getMessageSubprocessWithoutDataIOAssignments() {
                return "No Message";
            }

            @Override
            public boolean hasNoAssignmentsDataInput(AssignmentsInfo assignmentsInfo) {
                return hasNoInputs;
            }

            @Override
            public boolean hasNoAssignmentsDataOutput(AssignmentsInfo assignmentsInfo) {
                return hasNoOutputs;
            }
        });
        doNothing().when(tested).addViolation(any(), any());
    }

    @Test
    public void testValidationTrue() {
        final boolean reusableSubProcess = tested.isReusableSubProcess(element);
        assertTrue("Must be a reusable Subprocess", reusableSubProcess);

        final boolean hasNoInputs = tested.hasNoDataInputsOutputs(element);
        assertTrue("Validation should be true", hasNoInputs);

        List<DomainViolation> violations = new ArrayList<>();
        tested.checkElementForViolations(violations, element);
        verify(tested, times(1)).addViolation(any(), any());
    }

    @Test
    public void testValidationFalse() {
        final boolean reusableSubProcess = tested.isReusableSubProcess(element);
        assertTrue("Must be a reusable Subprocess", reusableSubProcess);

        hasNoInputs = false;
        hasNoOutputs = false;

        final boolean hasNoInputs = tested.hasNoDataInputsOutputs(element);
        assertFalse("Validation should be false", hasNoInputs);

        List<DomainViolation> violations = new ArrayList<>();
        tested.checkElementForViolations(violations, element);
        verify(tested, times(0)).addViolation(any(), any());
    }
}