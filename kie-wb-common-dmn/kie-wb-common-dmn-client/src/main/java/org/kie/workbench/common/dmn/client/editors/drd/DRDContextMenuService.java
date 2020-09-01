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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class DRDContextMenuService {

    private static String NEW_DIAGRAM_NAME = "new-diagram";

    private final DMNDiagramsSession dmnDiagramsSession;

    private final FactoryManager factoryManager;

    private final Event<DMNDiagramSelected> selectedEvent;

    private final DMNDiagramUtils dmnDiagramUtils;

    @Inject
    public DRDContextMenuService(final DMNDiagramsSession dmnDiagramsSession,
                                 final FactoryManager factoryManager,
                                 final Event<DMNDiagramSelected> selectedEvent,
                                 final DMNDiagramUtils dmnDiagramUtils) {
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.factoryManager = factoryManager;
        this.selectedEvent = selectedEvent;
        this.dmnDiagramUtils = dmnDiagramUtils;
    }

    public List<DMNDiagramTuple> getDiagrams() {
        return dmnDiagramsSession.getDMNDiagrams();
    }

    public void addToNewDRD(final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {

        // TODO: https://issues.redhat.com/browse/KOGITO-2992
        // This action must create a new DRD and add the selectedNodes into it. Currently, it just creates the DRD.

        final DMNDiagramElement dmnElement = makeDmnDiagramElement();
        final Diagram stunnerElement = buildStunnerElement(dmnElement);

        addDmnDiagramElementToDRG(dmnElement);

        dmnDiagramsSession.add(dmnElement, stunnerElement);
        selectedEvent.fire(new DMNDiagramSelected(dmnElement));
    }

    public void addToExistingDRD(final DMNDiagramTuple dmnDiagram,
                                 final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {

        // TODO: https://issues.redhat.com/browse/KOGITO-2992
        // This action must add the selectedNodes into the DRD.

    }

    public void removeFromCurrentDRD(final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {

        // TODO: https://issues.redhat.com/browse/KOGITO-2992
        // This action must remove the selectedNodes from the DRD.

    }

    private void addDmnDiagramElementToDRG(final DMNDiagramElement dmnElement) {
        dmnDiagramUtils
                .getDefinitions(dmnDiagramsSession.getDRGDiagram())
                .getDiagramElements()
                .add(dmnElement);
    }

    private Diagram buildStunnerElement(final DMNDiagramElement dmnElement) {
        final String diagramId = dmnElement.getId().getValue();
        return factoryManager.newDiagram(diagramId,
                                         BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                         getMetadata());
    }

    private DMNDiagramElement makeDmnDiagramElement() {
        final DMNDiagramElement diagramElement = new DMNDiagramElement();
        diagramElement.getName().setValue(getUniqueName());
        return diagramElement;
    }

    private String getUniqueName() {

        final List<String> currentDiagramNames = getCurrentDiagramNames();

        if (currentDiagramNames.contains(NEW_DIAGRAM_NAME)) {
            return getUniqueName(2, currentDiagramNames);
        }

        return NEW_DIAGRAM_NAME;
    }

    private List<String> getCurrentDiagramNames() {
        return dmnDiagramsSession
                .getDMNDiagrams()
                .stream()
                .map(e -> e.getDMDNDiagram().getName().getValue())
                .collect(Collectors.toList());
    }

    private String getUniqueName(final int seeds,
                                 final List<String> currentDiagramNames) {

        final String newDiagramName = NEW_DIAGRAM_NAME + "-" + seeds;

        if (currentDiagramNames.contains(newDiagramName)) {
            return getUniqueName(seeds + 1, currentDiagramNames);
        }

        return newDiagramName;
    }

    private Metadata getMetadata() {
        return dmnDiagramsSession.getDRGDiagram().getMetadata();
    }
}
