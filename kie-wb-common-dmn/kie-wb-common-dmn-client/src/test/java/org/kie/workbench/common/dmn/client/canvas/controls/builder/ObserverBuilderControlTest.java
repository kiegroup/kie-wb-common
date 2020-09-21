/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.canvas.controls.builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ObserverBuilderControlTest {

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private ClientDefinitionManager clientDefinitionManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private ClientTranslationMessages translationMessages;

    @Mock
    private GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private DRDContextMenuService contextMenuService;

    private ObserverBuilderControl observerBuilderControl;

    @Before
    public void setup() {

        observerBuilderControl = spy(new ObserverBuilderControl(clientDefinitionManager,
                                                                clientFactoryServices,
                                                                ruleManager,
                                                                canvasCommandFactory,
                                                                translationMessages, graphBoundsIndexer,
                                                                canvasSelectionEvent,
                                                                dmnDiagramsSession,
                                                                contextMenuService));
        //doCallRealMethod().when(observerBuilderControl).updateElementFromDefinition(anyObject(), anyObject());
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.empty());
        doReturn(dmnDiagramsSession).when(observerBuilderControl).getDMNDiagramsSession();
    }

    @Test
    public void testUpdateNameFromDefinition() {

        final String expectedName = "expectedName";
        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final HasName newDefinition = mock(HasName.class);
        final Name newDefinitionName = mock(Name.class);
        final HasName definition = mock(HasName.class);
        final Name definitionName = mock(Name.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(newDefinition.getName()).thenReturn(newDefinitionName);
        when(definition.getName()).thenReturn(definitionName);

        when(definitionName.getValue()).thenReturn(expectedName);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinitionName).setValue(expectedName);
    }

    @Test
    public void testUpdateDynamicReadOnlyTrueFromDefinition() {
        testUpdateDynamicReadOnlyFromDefinition(true);
    }

    @Test
    public void testUpdateDynamicReadOnlyFalseFromDefinition() {
        testUpdateDynamicReadOnlyFromDefinition(false);
    }

    private void testUpdateDynamicReadOnlyFromDefinition(final boolean expectedDynamicReadOnlyValue) {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final DynamicReadOnly newDefinition = mock(DynamicReadOnly.class);
        final DynamicReadOnly definition = mock(DynamicReadOnly.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(definition.isAllowOnlyVisualChange()).thenReturn(expectedDynamicReadOnlyValue);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinition).setAllowOnlyVisualChange(expectedDynamicReadOnlyValue);
    }

    @Test
    public void testUpdateIdFromDefinition() {

        final String expectedId = "happyId";
        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final DMNElement newDefinition = mock(DMNElement.class);
        final Id newDefinitionId = mock(Id.class);
        final DMNElement definition = mock(DMNElement.class);
        final Id definitionId = mock(Id.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(newDefinition.getId()).thenReturn(newDefinitionId);
        when(definition.getId()).thenReturn(definitionId);
        when(definitionId.getValue()).thenReturn(expectedId);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinitionId).setValue(expectedId);
    }

    @Test
    public void testUpdateExpressionFromDefinition() {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final HasExpression newHasExpression = mock(HasExpression.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Expression expression = mock(Expression.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newHasExpression);
        when(hasExpression.getExpression()).thenReturn(expression);

        observerBuilderControl.updateElementFromDefinition(element, hasExpression);

        verify(newHasExpression).setExpression(expression);
    }

    @Test
    public void testUpdateEncapsulatedLogicFromDefinition() {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final BusinessKnowledgeModel newBusinessKnowledgeModel = mock(BusinessKnowledgeModel.class);
        final BusinessKnowledgeModel businessKnowledgeModel = mock(BusinessKnowledgeModel.class);
        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newBusinessKnowledgeModel);
        when(businessKnowledgeModel.getEncapsulatedLogic()).thenReturn(functionDefinition);
        when(newBusinessKnowledgeModel.getName()).thenReturn(new Name());
        when(businessKnowledgeModel.getName()).thenReturn(new Name());
        when(newBusinessKnowledgeModel.getId()).thenReturn(new Id());
        when(businessKnowledgeModel.getId()).thenReturn(new Id());

        observerBuilderControl.updateElementFromDefinition(element, businessKnowledgeModel);

        verify(newBusinessKnowledgeModel).setEncapsulatedLogic(functionDefinition);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateVariableFromDefinition() {

        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final HasVariable newHasVariable = mock(HasVariable.class);
        final HasVariable hasVariable = mock(HasVariable.class);
        final IsInformationItem isInformationItem = mock(IsInformationItem.class);

        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getDefinition()).thenReturn(newHasVariable);
        when(hasVariable.getVariable()).thenReturn(isInformationItem);

        observerBuilderControl.updateElementFromDefinition(element, hasVariable);

        verify(newHasVariable).setVariable(isInformationItem);
    }

    @Test
    public void testUpdateDMNDiagramIdFromSelectedDMNDiagram() {

        final DRGElement newDefinition = mock(DRGElement.class);
        final Element element = mock(Element.class);
        final View elementContent = mock(View.class);
        final Object definition = mock(Object.class);
        final String selectedDiagramId = "selected diagram id";
        final DMNDiagramElement selectedDiagram = mock(DMNDiagramElement.class);
        final Id id = mock(Id.class);

        when(id.getValue()).thenReturn(selectedDiagramId);
        when(selectedDiagram.getId()).thenReturn(id);
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.of(selectedDiagram));
        when(elementContent.getDefinition()).thenReturn(newDefinition);
        when(element.getContent()).thenReturn(elementContent);

        observerBuilderControl.updateElementFromDefinition(element, definition);

        verify(newDefinition).setDiagramId(selectedDiagramId);
    }

    @Test
    public void testOnElementCreatedWhenIsNotGlobalGraph() {

        final Element element = mock(Element.class);
        final double x = 10;
        final double y = 20;

        doNothing().when(observerBuilderControl).addElement(element, x, y);
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(false);

        observerBuilderControl.onElementCreated(element, x, y);

        verify(observerBuilderControl).addElement(element, x, y);
    }

    @Test
    public void testOnElementCreatedWhenIsGlobalGraph() {

        final Element element = mock(Element.class);
        final double x = 10;
        final double y = 20;

        doNothing().when(observerBuilderControl).addElement(element, x, y);
        when(dmnDiagramsSession.isGlobalGraphSelected()).thenReturn(true);

        observerBuilderControl.onElementCreated(element, x, y);

        verify(observerBuilderControl, never()).addElement(element, x, y);
    }

    @Test
    public void testAddElementWhenContentIsNotPresentInDRG() {

        final double x = 100;
        final double y = 200;
        final Node element = mock(Node.class);
        final String contentId = "contentId";
        final Diagram drgDiagram = mock(Diagram.class);

        doReturn(contentId).when(observerBuilderControl).getContentDefinitionId(element);
        doReturn(false).when(observerBuilderControl).hasElementWithContentId(contentId, drgDiagram);
        doNothing().when(observerBuilderControl).addElementToDRGDiagram(element, x, y, drgDiagram);

        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(drgDiagram);

        observerBuilderControl.addElement(element, x, y);

        verify(observerBuilderControl).addElementToDRGDiagram(element, x, y, drgDiagram);
    }

    @Test
    public void testAddElementWhenContentIsPresentInDRG() {

        final double x = 100;
        final double y = 200;
        final Node element = mock(Node.class);
        final String contentId = "contentId";
        final Diagram drgDiagram = mock(Diagram.class);

        doReturn(contentId).when(observerBuilderControl).getContentDefinitionId(element);
        doReturn(true).when(observerBuilderControl).hasElementWithContentId(contentId, drgDiagram);
        doNothing().when(observerBuilderControl).addElementToDRGDiagram(element, x, y, drgDiagram);

        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(drgDiagram);

        observerBuilderControl.addElement(element, x, y);

        verify(observerBuilderControl, never()).addElementToDRGDiagram(element, x, y, drgDiagram);
    }

    @Test
    public void testAddElementToDRGDiagram() {

        final DMNDiagramElement drgDmnDiagramElement = mock(DMNDiagramElement.class);
        final Node node = mock(Node.class);
        final double x = 100;
        final double y = 200;
        final Bounds bounds = mock(Bounds.class);
        final Optional<Bounds> optionalBounds = Optional.of(bounds);
        final Diagram drgDiagram = mock(Diagram.class);

        when(dmnDiagramsSession.getDRGDiagramElement()).thenReturn(drgDmnDiagramElement);

        doReturn(optionalBounds).when(observerBuilderControl).getBounds(node,
                                                                        x,
                                                                        y);

        observerBuilderControl.addElementToDRGDiagram(node, x, y, drgDiagram);

        verify(contextMenuService).addNode(drgDmnDiagramElement,
                                           drgDiagram,
                                           node,
                                           optionalBounds);
    }

    @Test
    public void testHasElementWithContentId() {

        final String anotherId = "another Id";
        final String contentId = "contentId";
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final Node node4 = mock(Node.class);
        final Node node5 = mock(Node.class);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node1);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node2);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node3);
        doReturn(contentId).when(observerBuilderControl).getContentDefinitionId(node4);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node5);
        when(graph.nodes()).thenReturn(Arrays.asList(node1, node2, node3, node4, node5));
        when(diagram.getGraph()).thenReturn(graph);

        final boolean hasElementsWithId = observerBuilderControl.hasElementWithContentId(contentId, diagram);
        assertTrue(hasElementsWithId);
    }

    @Test
    public void testHasElementWithContentIdWhenIsNot() {

        final String contentId = "contentId";
        final String anotherId = "another Id";
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final Node node4 = mock(Node.class);
        final Node node5 = mock(Node.class);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node1);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node2);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node3);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node4);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node5);
        when(graph.nodes()).thenReturn(Arrays.asList(node1, node2, node3, node4, node5));
        when(diagram.getGraph()).thenReturn(graph);

        final boolean hasElementsWithId = observerBuilderControl.hasElementWithContentId(contentId, diagram);
        assertFalse(hasElementsWithId);
    }

    @Test
    public void testGetContentDefinitionId() {

        final Element element = mock(Element.class);
        final View view = mock(View.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final String contentId = "contentId";

        when(element.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(hasContentDefinitionId);
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(contentId);

        final String actual = observerBuilderControl.getContentDefinitionId(element);

        assertEquals(contentId, actual);
    }

    @Test
    public void testGetContentDefinitionIdWhenIsNotElement() {

        final Object element = mock(Object.class);

        final String actual = observerBuilderControl.getContentDefinitionId(element);

        assertNull(actual);
    }

    @Test
    public void testGetContentDefinitionIdWhenIsNotHasContentDefinitionId() {

        final Element element = mock(Element.class);
        final View view = mock(View.class);
        final Object hasContentDefinitionId = mock(Object.class);

        when(element.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(hasContentDefinitionId);

        final String actual = observerBuilderControl.getContentDefinitionId(element);

        assertNull(actual);
    }

    @Test
    public void testGetContentDefinitionIdWhenIsNotView() {

        final Element element = mock(Element.class);
        final Object view = mock(View.class);

        when(element.getContent()).thenReturn(view);

        final String actual = observerBuilderControl.getContentDefinitionId(element);

        assertNull(actual);
    }

    @Test
    public void testGetBounds() {
        final Diagram currentDiagram = mock(Diagram.class);
        final Optional<Diagram> optionalCurrentDiagram = Optional.of(currentDiagram);
        final Graph currentGraph = mock(Graph.class);
        final Diagram drgDiagram = mock(Diagram.class);
        final Graph drgGraph = mock(Graph.class);
        final Node drgNode = mock(Node.class);
        final Node localNode = mock(Node.class);
        final Optional<Node> optionalDrgNode = Optional.of(drgNode);
        final Optional<Node> optionalLocalNode = Optional.of(localNode);
        final String localNodeContentId = "localNodeContentId";
        final HasBounds hasBounds = mock(HasBounds.class);
        final Bounds localBounds = mock(Bounds.class);
        final double localBoundsX = 200;
        final double localBoundsY = 400;
        final HasBounds drgNodeContent = mock(HasBounds.class);
        final Bounds referenceBounds = mock(Bounds.class);
        final double referenceX = 577;
        final double referenceY = 787;
        final Node node = mock(Node.class);
        final double width = 200;
        final double height = 400;
        final double[] currentNodeSize = new double[]{width, height};
        final double x = 600;
        final double y = 700;
        final double offsetX = x - localBoundsX;
        final double offsetY = y - localBoundsY;
        final double expectedUpperLeftX = referenceX + offsetX;
        final double expectedUpperLeftY = referenceY + offsetY;
        final double expectedLowerRightX = referenceX + offsetX + width;
        final double expectedLowerRightY = referenceY + offsetY + height;

        when(drgDiagram.getGraph()).thenReturn(drgGraph);
        when(dmnDiagramsSession.getDRGDiagram()).thenReturn(drgDiagram);
        when(currentDiagram.getGraph()).thenReturn(currentGraph);
        when(dmnDiagramsSession.getCurrentDiagram()).thenReturn(optionalCurrentDiagram);

        doReturn(optionalDrgNode).when(observerBuilderControl).getReferenceNode(currentGraph, drgGraph);
        doReturn(localNodeContentId).when(observerBuilderControl).getContentDefinitionId(drgNode);
        doReturn(optionalLocalNode).when(observerBuilderControl).getNode(localNodeContentId, currentGraph);

        when(localBounds.getX()).thenReturn(localBoundsX);
        when(localBounds.getY()).thenReturn(localBoundsY);
        when(hasBounds.getBounds()).thenReturn(localBounds);
        when(localNode.getContent()).thenReturn(hasBounds);

        when(referenceBounds.getX()).thenReturn(referenceX);
        when(referenceBounds.getY()).thenReturn(referenceY);
        when(drgNodeContent.getBounds()).thenReturn(referenceBounds);
        when(drgNode.getContent()).thenReturn(drgNodeContent);

        doReturn(currentNodeSize).when(observerBuilderControl).getNodeSize(node);

        final Optional<Bounds> bounds = observerBuilderControl.getBounds(node, x, y);

        assertTrue(bounds.isPresent());

        final Bound upperLeft = bounds.get().getUpperLeft();
        final Bound lowerRight = bounds.get().getLowerRight();

        assertEquals(expectedUpperLeftX, upperLeft.getX(), 0.01d);
        assertEquals(expectedUpperLeftY, upperLeft.getY(), 0.01d);
        assertEquals(expectedLowerRightX, lowerRight.getX(), 0.01d);
        assertEquals(expectedLowerRightY, lowerRight.getY(), 0.01d);
    }

    @Test
    public void testGetNode() {

        final String contentId = "contentId";
        final String anotherId = "another Id";
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);

        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node1);
        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node2);
        doReturn(contentId).when(observerBuilderControl).getContentDefinitionId(node3);

        when(graph.nodes()).thenReturn(Arrays.asList(node1, node2, node3));
        when(diagram.getGraph()).thenReturn(graph);

        final Optional<Node> actual = observerBuilderControl.getNode(contentId, graph);

        assertTrue(actual.isPresent());
        assertEquals(node3, actual.get());
    }

    @Test
    public void testGetNodeWhenItsNotPresent() {

        final String contentId = "contentId";
        final String anotherId = "another Id";
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);

        doReturn(anotherId).when(observerBuilderControl).getContentDefinitionId(node1);

        when(graph.nodes()).thenReturn(Arrays.asList(node1));
        when(diagram.getGraph()).thenReturn(graph);

        final Optional<Node> actual = observerBuilderControl.getNode(contentId, graph);

        assertFalse(actual.isPresent());
    }

    @Test
    public void testGetReferenceNode() {

        final Graph drgGraph = mock(Graph.class);
        final Graph currentGraph = mock(Graph.class);
        final List contentIds = mock(List.class);
        final Map occurrencesCounter = mock(Map.class);
        final String selectedContentId = "selected content id";
        final Node node = mock(Node.class);
        final Optional<Node> optionalNode = Optional.of(node);

        doReturn(contentIds).when(observerBuilderControl).getContentIds(currentGraph);
        doReturn(occurrencesCounter).when(observerBuilderControl).countOccurrences(drgGraph, contentIds);
        doReturn(selectedContentId).when(observerBuilderControl).selectUniqueContentId(contentIds, occurrencesCounter);
        doReturn(optionalNode).when(observerBuilderControl).getNode(selectedContentId, drgGraph);

        final Optional<Node> referenceNode = observerBuilderControl.getReferenceNode(currentGraph, drgGraph);
        assertTrue(referenceNode.isPresent());
        assertEquals(node, referenceNode.get());
    }

    @Test
    public void testSelectUniqueContentIdWhenHasUniqueNode() {

        final String contentId1 = "1";
        final String contentId2 = "2";
        final String contentId3 = "3";

        final List<String> contentsIds = Arrays.asList(contentId1, contentId2, contentId3);
        final Map<String, Integer> occurrencesCounter = new HashMap<>();
        occurrencesCounter.put(contentId1, 2);
        occurrencesCounter.put(contentId2, 1);
        occurrencesCounter.put(contentId3, 4);

        final String actualId = observerBuilderControl.selectUniqueContentId(contentsIds, occurrencesCounter);
        assertEquals(contentId2, actualId);
    }

    @Test
    public void testSelectUniqueContentIdWhenDoesNotHaveUniqueNode() {

        final String contentId1 = "1";
        final String contentId2 = "2";
        final String contentId3 = "3";

        final List<String> contentsIds = Arrays.asList(contentId1, contentId2, contentId3);
        final Map<String, Integer> occurrencesCounter = new HashMap<>();
        occurrencesCounter.put(contentId1, 2);
        occurrencesCounter.put(contentId2, 2);
        occurrencesCounter.put(contentId3, 4);

        final String actualId = observerBuilderControl.selectUniqueContentId(contentsIds, occurrencesCounter);
        assertEquals(contentId1, actualId);
    }

    @Test
    public void testSelectUniqueContentIdWhenHasMultipleUniqueNodes() {

        final String contentId1 = "1";
        final String contentId2 = "2";
        final String contentId3 = "3";

        final List<String> contentIds = Arrays.asList(contentId1, contentId2, contentId3);
        final Map<String, Integer> occurrencesCounter = new HashMap<>();
        occurrencesCounter.put(contentId1, 2);
        occurrencesCounter.put(contentId2, 1);
        occurrencesCounter.put(contentId3, 1);

        final String actualId = observerBuilderControl.selectUniqueContentId(contentIds, occurrencesCounter);
        assertTrue(actualId.equals(contentId2) || actualId.equals(contentId3));
        assertNotEquals(actualId, contentId1);
    }

    @Test
    public void testCountOccurrences() {

        final String contentId1 = "1";
        final String contentId2 = "2";
        final String contentId3 = "3";
        final String notPresentContentId = "not present";
        final List<String> contentIds = Arrays.asList(contentId1, contentId2, contentId3);
        final Graph graph = mock(Graph.class);
        final Node node1 = createNodeWithContentId(contentId1);
        final Node node2 = createNodeWithContentId(contentId2);
        final Node node3 = createNodeWithContentId(contentId2);
        final Node node4 = createNodeWithContentId(contentId3);
        final Node node5 = createNodeWithContentId(contentId3);
        final Node node6 = createNodeWithContentId(contentId3);
        final Node node7 = createNodeWithContentId(notPresentContentId);
        when(graph.nodes()).thenReturn(Arrays.asList(node1, node2, node3, node4, node5, node6, node7));

        final Map<String, Integer> occurrences = observerBuilderControl.countOccurrences(graph, contentIds);

        assertTrue(occurrences.containsKey(contentId1));
        assertTrue(occurrences.containsKey(contentId2));
        assertTrue(occurrences.containsKey(contentId3));
        assertFalse(occurrences.containsKey(notPresentContentId));
        assertEquals(1, (long) occurrences.get(contentId1));
        assertEquals(2, (long) occurrences.get(contentId2));
        assertEquals(3, (long) occurrences.get(contentId3));
    }

    @Test
    public void testGetContentIds() {

        final String contentId1 = "1";
        final String contentId2 = "2";
        final String contentId3 = "3";
        final Graph graph = mock(Graph.class);
        final Node node1 = createNodeWithContentId(contentId1);
        final Node node2 = createNodeWithContentId(contentId2);
        final Node node3 = createNodeWithContentId(contentId3);
        final Node rootDiagramNode = createRootDiagramNode();

        when(graph.nodes()).thenReturn(Arrays.asList(node1, node2, node3, rootDiagramNode));

        final List<String> contentIds = observerBuilderControl.getContentIds(graph);

        assertEquals(3, contentIds.size());
        assertTrue(contentIds.contains(contentId1));
        assertTrue(contentIds.contains(contentId2));
        assertTrue(contentIds.contains(contentId3));
    }

    private Node createRootDiagramNode() {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DMNDiagram dmnDiagram = mock(DMNDiagram.class);
        when(definition.getDefinition()).thenReturn(dmnDiagram);
        when(node.getContent()).thenReturn(definition);
        return node;
    }

    private Node createNodeWithContentId(final String contentId) {

        final Node node = mock(Node.class);
        final View nodeContent = mock(View.class);
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        when(hasContentDefinitionId.getContentDefinitionId()).thenReturn(contentId);
        when(nodeContent.getDefinition()).thenReturn(hasContentDefinitionId);
        when(node.getContent()).thenReturn(nodeContent);
        return node;
    }
}

