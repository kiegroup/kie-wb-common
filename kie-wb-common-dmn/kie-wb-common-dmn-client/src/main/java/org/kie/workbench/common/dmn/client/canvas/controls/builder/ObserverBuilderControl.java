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

package org.kie.workbench.common.dmn.client.canvas.controls.builder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenuService;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@DMNEditor
@Dependent
@Observer
public class ObserverBuilderControl extends org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ObserverBuilderControl {

    private final DMNDiagramsSession dmnDiagramsSession;
    private final DRDContextMenuService contextMenuService;
    static final int DEFAULT_MARGIN = 100;

    @Inject
    public ObserverBuilderControl(final ClientDefinitionManager clientDefinitionManager,
                                  final ClientFactoryService clientFactoryServices,
                                  final RuleManager ruleManager,
                                  final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                  final ClientTranslationMessages translationMessages,
                                  final GraphBoundsIndexer graphBoundsIndexer,
                                  final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                  final DMNDiagramsSession dmnDiagramsSession,
                                  final DRDContextMenuService contextMenuService) {
        super(clientDefinitionManager,
              clientFactoryServices,
              ruleManager,
              canvasCommandFactory,
              translationMessages,
              graphBoundsIndexer,
              canvasSelectionEvent);
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.contextMenuService = contextMenuService;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void updateElementFromDefinition(final Element element,
                                               final Object definition) {

        final Object content = element.getContent();
        if (!(content instanceof View)) {
            return;
        }

        final Object newDefinition = ((View) content).getDefinition();
        if (newDefinition instanceof HasName && definition instanceof HasName) {
            ((HasName) newDefinition).getName().setValue(((HasName) definition).getName().getValue());
        }

        if (newDefinition instanceof DynamicReadOnly && definition instanceof DynamicReadOnly) {
            ((DynamicReadOnly) newDefinition).setAllowOnlyVisualChange(((DynamicReadOnly) definition).isAllowOnlyVisualChange());
        }

        if (newDefinition instanceof HasVariable && definition instanceof HasVariable) {
            ((HasVariable) newDefinition).setVariable(((HasVariable) definition).getVariable());
        }

        if (newDefinition instanceof BusinessKnowledgeModel && definition instanceof BusinessKnowledgeModel) {
            ((BusinessKnowledgeModel) newDefinition).setEncapsulatedLogic(((BusinessKnowledgeModel) definition).getEncapsulatedLogic());
        }

        if (newDefinition instanceof HasExpression && definition instanceof HasExpression) {
            ((HasExpression) newDefinition).setExpression(((HasExpression) definition).getExpression());
        }

        if (newDefinition instanceof DMNElement && definition instanceof DMNElement) {
            final DMNElement dmnElement = (DMNElement) definition;
            if (!StringUtils.isEmpty(dmnElement.getId().getValue())) {
                ((DMNElement) newDefinition).getId().setValue(dmnElement.getId().getValue());
            }
        }

        final Optional<DMNDiagramElement> currentDMNDiagramElement = getDMNDiagramsSession().getCurrentDMNDiagramElement();
        if (currentDMNDiagramElement.isPresent() && newDefinition instanceof HasContentDefinitionId) {
            ((HasContentDefinitionId) newDefinition).setDiagramId(currentDMNDiagramElement.get().getId().getValue());
        }
    }

    DMNDiagramsSession getDMNDiagramsSession() {
        return dmnDiagramsSession;
    }

    @Override
    protected void onElementCreated(final Element element, final double x, final double y) {
        if (!dmnDiagramsSession.isGlobalGraphSelected()) {
            addElement(element, x, y);
        }
    }

    public void addElement(final Element element,
                           final double x,
                           final double y) {
        final String id = getContentDefinitionId(element);
        final Diagram drgDiagram = dmnDiagramsSession.getDRGDiagram();

        if (!hasElementWithContentId(id, drgDiagram)) {
            addElementToDRGDiagram((Node) element, x, y, drgDiagram);
        }
    }

    void addElementToDRGDiagram(final Node element,
                                final double x,
                                final double y,
                                final Diagram drgDiagram) {
        final DMNDiagramElement drgDmnDiagramElement = dmnDiagramsSession.getDRGDiagramElement();
        final Optional<Bounds> bounds = getBounds(element, x, y);
        contextMenuService.addNode(drgDmnDiagramElement, drgDiagram, element, bounds);
    }

    boolean hasElementWithContentId(final String contentId,
                                    final Diagram diagram) {
        return StreamSupport
                .stream(diagram.getGraph().nodes().spliterator(), false)
                .anyMatch((Object n) -> Objects.equals(getContentDefinitionId(n), contentId));
    }

    Optional<Bounds> getBounds(final Node node,
                               final double x,
                               final double y) {

        final Graph currentGraph = dmnDiagramsSession.getCurrentDiagram().get().getGraph();
        final Graph drgGraph = dmnDiagramsSession.getDRGDiagram().getGraph();
        final Optional<Node> drgNode = getReferenceNode(currentGraph, drgGraph);
        if (!drgNode.isPresent()) {
            return Optional.empty();
        }

        final Optional<Node> localNode = getNode(getContentDefinitionId(drgNode.get()), currentGraph);

        final Bounds localBounds = ((HasBounds) localNode.get().getContent()).getBounds();
        double offsetX = x - localBounds.getX();
        double offsetY = y - localBounds.getY();

        final double[] currentNodeSize = getNodeSize(node);
        final double w = currentNodeSize[0];
        final double h = currentNodeSize[1];

        final Bounds referenceBounds = ((HasBounds) drgNode.get().getContent()).getBounds();
        double baseX = referenceBounds.getX() + offsetX;
        if (baseX < 0) {
            baseX = DEFAULT_MARGIN;
        }
        double baseY = referenceBounds.getY() + offsetY;
        if (baseY < 0) {
            baseY = DEFAULT_MARGIN;
        }

        return Optional.of(Bounds.create(baseX,
                                         baseY,
                                         baseX + w,
                                         baseY + h));
    }

    double[] getNodeSize(final Node node) {
        return GraphUtils.getNodeSize((View) node.getContent());
    }

    Optional<Node> getNode(final String contentId, final Graph graph) {
        return StreamSupport
                .stream(graph.nodes().spliterator(), false)
                .filter((Object n) -> Objects.equals(getContentDefinitionId(n), contentId))
                .findFirst();
    }

    Optional<Node> getReferenceNode(final Graph currentGraph, final Graph drgGraph) {

        final List<String> contentIds = getContentIds(currentGraph);
        if (contentIds.isEmpty()) {
            return Optional.empty();
        }
        final Map<String, Integer> occurrencesCounter = countOccurrences(drgGraph, contentIds);
        final String selectedContentId = selectUniqueContentId(contentIds, occurrencesCounter);
        return getNode(selectedContentId, drgGraph);
    }

    String selectUniqueContentId(final List<String> contentIds,
                                 final Map<String, Integer> occurrencesCounter) {
        final List<Map.Entry<String, Integer>> candidates = occurrencesCounter
                .entrySet()
                .stream()
                .filter(k -> k.getValue() == 1)
                .collect(Collectors.toList());

        if (candidates.size() >= 1) {
            return candidates.get(0).getKey();
        } else {
            return contentIds.get(0);
        }
    }

    Map<String, Integer> countOccurrences(final Graph drgGraph,
                                          final List<String> contentIds) {

        final Map<String, Integer> occurrencesCounter = contentIds.stream().collect(Collectors.toMap(s -> s, s -> 0));

        drgGraph.nodes().forEach(n -> {
            final String contentId = getContentDefinitionId(n);
            if (contentIds.contains(contentId)) {
                occurrencesCounter.put(contentId, occurrencesCounter.get(contentId) + 1);
            }
        });

        return occurrencesCounter;
    }

    List<String> getContentIds(final Graph currentGraph) {
        return (List<String>) StreamSupport
                .stream(currentGraph.nodes().spliterator(), false)
                .filter((Object n) -> !(DefinitionUtils.getElementDefinition((Element) n) instanceof DMNDiagram))
                .map(n -> getContentDefinitionId(n))
                .collect(Collectors.toList());
    }

    String getContentDefinitionId(final Object node) {

        if (!(node instanceof Element)) {
            return null;
        }

        final Element element = (Element) node;
        if (!(element.getContent() instanceof View)) {
            return null;
        }

        final View content = (View) element.getContent();
        final Object definition = content.getDefinition();
        if (definition instanceof HasContentDefinitionId) {
            final String contentId = ((HasContentDefinitionId) definition).getContentDefinitionId();
            return contentId;
        }
        return null;
    }
}
