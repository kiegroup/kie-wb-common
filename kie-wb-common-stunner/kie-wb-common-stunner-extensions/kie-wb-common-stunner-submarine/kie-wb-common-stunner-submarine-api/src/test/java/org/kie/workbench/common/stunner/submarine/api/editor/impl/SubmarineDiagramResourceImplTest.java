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

package org.kie.workbench.common.stunner.submarine.api.editor.impl;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineDiagramImpl;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineMetadataImpl;
import org.kie.workbench.common.stunner.submarine.api.editor.DiagramType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class SubmarineDiagramResourceImplTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenProjectDiagramIsDifferent() {
        final SubmarineDiagramImpl projectDiagram1 = new SubmarineDiagramImpl("Diagram", makeGraph(), makeMetadata());
        final SubmarineDiagramImpl projectDiagram2 = new SubmarineDiagramImpl("Diagram_", makeGraph(), makeMetadata());
        final SubmarineDiagramResourceImpl projectDiagramResource1 = new SubmarineDiagramResourceImpl(projectDiagram1);
        final SubmarineDiagramResourceImpl projectDiagramResource2 = new SubmarineDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenXmlDiagramIsDifferent() {
        final SubmarineDiagramResourceImpl projectDiagramResource1 = new SubmarineDiagramResourceImpl("<xml>");
        final SubmarineDiagramResourceImpl projectDiagramResource2 = new SubmarineDiagramResourceImpl("<xml />");

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    public void testEqualsWhenTypeIsDifferent() {
        final SubmarineDiagramResourceImpl projectDiagramResource1 = new SubmarineDiagramResourceImpl(null, null, DiagramType.PROJECT_DIAGRAM);
        final SubmarineDiagramResourceImpl projectDiagramResource2 = new SubmarineDiagramResourceImpl(null, null, DiagramType.XML_DIAGRAM);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenProjectDiagramIsDifferentGraph() {
        final GraphImpl graphOne = makeGraph();
        final GraphImpl graphTwo = makeGraph();

        graphTwo.addNode(new NodeImpl("unique id"));

        final SubmarineDiagramImpl projectDiagram1 = new SubmarineDiagramImpl("Diagram", graphOne, makeMetadata());
        final SubmarineDiagramImpl projectDiagram2 = new SubmarineDiagramImpl("Diagram", graphTwo, makeMetadata());
        final SubmarineDiagramResourceImpl projectDiagramResource1 = new SubmarineDiagramResourceImpl(projectDiagram1);
        final SubmarineDiagramResourceImpl projectDiagramResource2 = new SubmarineDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenProjectDiagramIsDifferentMetadata() {
        final SubmarineMetadataImpl metadataOne = spy(makeMetadata());
        final SubmarineMetadataImpl metadataTwo = spy(makeMetadata());

        doReturn("moduleOne").when(metadataOne).getTitle();
        doReturn("moduleTwo").when(metadataTwo).getTitle();

        final SubmarineDiagramImpl projectDiagram1 = new SubmarineDiagramImpl("Diagram", makeGraph(), metadataOne);
        final SubmarineDiagramImpl projectDiagram2 = new SubmarineDiagramImpl("Diagram", makeGraph(), metadataTwo);
        final SubmarineDiagramResourceImpl projectDiagramResource1 = new SubmarineDiagramResourceImpl(projectDiagram1);
        final SubmarineDiagramResourceImpl projectDiagramResource2 = new SubmarineDiagramResourceImpl(projectDiagram2);

        assertNotEquals(projectDiagramResource1, projectDiagramResource2);
        assertNotEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEqualsWhenObjectsAreEqual() {
        final SubmarineDiagramImpl projectDiagram1 = new SubmarineDiagramImpl("Diagram", makeGraph(), makeMetadata());
        final SubmarineDiagramImpl projectDiagram2 = new SubmarineDiagramImpl("Diagram", makeGraph(), makeMetadata());
        final SubmarineDiagramResourceImpl projectDiagramResource1 = new SubmarineDiagramResourceImpl(projectDiagram1);
        final SubmarineDiagramResourceImpl projectDiagramResource2 = new SubmarineDiagramResourceImpl(projectDiagram2);

        assertEquals(projectDiagramResource1, projectDiagramResource2);
        assertEquals(projectDiagramResource1.hashCode(), projectDiagramResource2.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInstanceCreationWhenTypeIsNull() {
        new SubmarineDiagramResourceImpl(null, null, null);
    }

    private GraphImpl makeGraph() {
        return new GraphImpl("Graph", new GraphNodeStoreImpl());
    }

    private SubmarineMetadataImpl makeMetadata() {
        return new SubmarineMetadataImpl();
    }
}
