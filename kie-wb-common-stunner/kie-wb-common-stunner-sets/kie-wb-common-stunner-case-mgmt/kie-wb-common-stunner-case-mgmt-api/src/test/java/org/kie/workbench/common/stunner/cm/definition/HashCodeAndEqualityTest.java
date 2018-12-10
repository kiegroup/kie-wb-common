/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.cm.definition.property.subprocess.Case;
import org.kie.workbench.common.stunner.cm.definition.property.task.CaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.ProcessReusableSubprocessTaskExecutionSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class HashCodeAndEqualityTest {

    @Test
    public void testCaseManagementDiagramEquals() {
        CaseManagementDiagram a = new CaseManagementDiagram();
        CaseManagementDiagram b = new CaseManagementDiagram();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testCaseManagementDiagramHashCode() {
        CaseManagementDiagram a = new CaseManagementDiagram();
        CaseManagementDiagram b = new CaseManagementDiagram();
        assertDefObjectHashCode(a, b);
    }

    @Test
    public void testCaseReusableSubprocessEquals() {
        CaseReusableSubprocess a = new CaseReusableSubprocess();
        CaseReusableSubprocess b = new CaseReusableSubprocess();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testCaseReusableSubprocessHashCode() {
        CaseReusableSubprocess a = new CaseReusableSubprocess();
        CaseReusableSubprocess b = new CaseReusableSubprocess();
        assertDefObjectHashCode(a, b);
    }

    @Test
    public void testProcessReusableSubprocessEquals() {
        ProcessReusableSubprocess a = new ProcessReusableSubprocess();
        ProcessReusableSubprocess b = new ProcessReusableSubprocess();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testProcessReusableSubprocessHashCode() {
        ProcessReusableSubprocess a = new ProcessReusableSubprocess();
        ProcessReusableSubprocess b = new ProcessReusableSubprocess();
        assertDefObjectHashCode(a, b);
    }

    @Test
    public void testAdHocSubprocessEquals() {
        AdHocSubprocess a = new AdHocSubprocess();
        AdHocSubprocess b = new AdHocSubprocess();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testAdhocSubprocessHashCode() {
        AdHocSubprocess a = new AdHocSubprocess();
        AdHocSubprocess b = new AdHocSubprocess();
        assertDefObjectHashCode(a, b);
    }

    @Test
    public void testCaseEquals() {
        Case a = new Case();
        Case b = new Case();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testCaseHashCode() {
        Case a = new Case();
        Case b = new Case();
        assertDefObjectHashCode(a, b);
    }

    @Test
    public void testCaseReusableSubprocessTaskExecutionSetEquals() {
        CaseReusableSubprocessTaskExecutionSet a = new CaseReusableSubprocessTaskExecutionSet();
        CaseReusableSubprocessTaskExecutionSet b = new CaseReusableSubprocessTaskExecutionSet();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testCaseReusableSubprocessTaskExecutionSetHashCode() {
        CaseReusableSubprocessTaskExecutionSet a = new CaseReusableSubprocessTaskExecutionSet();
        CaseReusableSubprocessTaskExecutionSet b = new CaseReusableSubprocessTaskExecutionSet();
        assertDefObjectHashCode(a, b);
    }

    @Test
    public void testProcessReusableSubprocessTaskExecutionSetEquals() {
        ProcessReusableSubprocessTaskExecutionSet a = new ProcessReusableSubprocessTaskExecutionSet();
        ProcessReusableSubprocessTaskExecutionSet b = new ProcessReusableSubprocessTaskExecutionSet();
        assertDefObjectEquals(a, b);
    }

    @Test
    public void testProcessReusableSubprocessTaskExecutionSetHashCode() {
        ProcessReusableSubprocessTaskExecutionSet a = new ProcessReusableSubprocessTaskExecutionSet();
        ProcessReusableSubprocessTaskExecutionSet b = new ProcessReusableSubprocessTaskExecutionSet();
        assertDefObjectHashCode(a, b);
    }

    private void assertDefObjectEquals(final Object a, final Object b) {
        assertNotEquals(a, 19);
        assertNotEquals(a, null);
        assertEquals(a, b);
    }

    private void assertDefObjectHashCode(final Object a, final Object b) {
        assertTrue(a.hashCode() - b.hashCode() == 0);
    }
}
