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

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.uberfire.backend.vfs.Path;

import static java.util.Collections.emptyList;

@ApplicationScoped
public class DMNDiagramsSession implements GraphsProvider {

    private ManagedInstance<DMNDiagramsSessionState> dmnDiagramsSessionStates;

    private DMNGraphUtils dmnGraphUtils;

    private Map<String, DMNDiagramsSessionState> dmnSessionStatesByPathURI = new HashMap<>();

    public DMNDiagramsSession() {
        // CDI
    }

    @Inject
    public DMNDiagramsSession(final ManagedInstance<DMNDiagramsSessionState> dmnDiagramsSessionStates,
                              final DMNGraphUtils dmnGraphUtils) {
        this.dmnDiagramsSessionStates = dmnDiagramsSessionStates;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public void destroyState(final Metadata metadata) {
        dmnSessionStatesByPathURI.remove(getSessionKey(metadata));
    }

    public DMNDiagramsSessionState setState(final Metadata metadata,
                                            final Map<String, Diagram> diagramsByDiagramElementId,
                                            final Map<String, DMNDiagramElement> dmnDiagramsByDiagramElementId) {

        final DMNDiagramsSessionState state = dmnDiagramsSessionStates.get();

        state.getDiagramsByDiagramId().putAll(diagramsByDiagramElementId);
        state.getDMNDiagramsByDiagramId().putAll(dmnDiagramsByDiagramElementId);

        dmnSessionStatesByPathURI.put(getSessionKey(metadata), state);

        return state;
    }

    public DMNDiagramsSessionState getSessionState() {
        return dmnSessionStatesByPathURI.get(getCurrentSessionKey());
    }

    public String getCurrentSessionKey() {
        return Optional
                .ofNullable(dmnGraphUtils.getDiagram())
                .map(diagram -> getSessionKey(diagram.getMetadata()))
                .orElse("");
    }

    public String getSessionKey(final Metadata metadata) {
        return Optional
                .ofNullable(metadata)
                .map(Metadata::getPath)
                .map(Path::toURI)
                .orElse("");
    }

    public void add(final DMNDiagramElement dmnDiagram,
                    final Diagram stunnerDiagram) {
        final String diagramId = dmnDiagram.getId().getValue();
        getSessionState().getDiagramsByDiagramId().put(diagramId, stunnerDiagram);
        getSessionState().getDMNDiagramsByDiagramId().put(diagramId, dmnDiagram);
    }

    public void remove(final DMNDiagramElement dmnDiagram) {
        final String diagramId = dmnDiagram.getId().getValue();
        getSessionState().getDiagramsByDiagramId().remove(diagramId);
        getSessionState().getDMNDiagramsByDiagramId().remove(diagramId);
    }

    @Override
    public Diagram getDiagram(final String dmnDiagramElementId) {
        return getSessionState().getDiagram(dmnDiagramElementId);
    }

    public DMNDiagramElement getDMNDiagramElement(final String dmnDiagramElementId) {
        return getSessionState().getDMNDiagramElement(dmnDiagramElementId);
    }

    public DMNDiagramTuple getDiagramTuple(final String dmnDiagramElementId) {
        return getSessionState().getDiagramTuple(dmnDiagramElementId);
    }

    public List<DMNDiagramTuple> getDMNDiagrams() {
        return getSessionState().getDMNDiagrams();
    }

    public void onDMNDiagramSelected(final @Observes DMNDiagramSelected selected) {
        final DMNDiagramElement selectedDiagramElement = selected.getDiagramElement();
        if (belongsToCurrentSessionState(selectedDiagramElement)) {
            getSessionState().setCurrentDMNDiagramElement(selectedDiagramElement);
        }
    }

    public boolean belongsToCurrentSessionState(final DMNDiagramElement diagramElement) {
        return getDMNDiagramElement(diagramElement.getId().getValue()) != null;
    }

    public Optional<DMNDiagramElement> getCurrentDMNDiagramElement() {
        return getSessionState().getCurrentDMNDiagramElement();
    }

    public Optional<Diagram> getCurrentDiagram() {
        return getSessionState().getCurrentDiagram();
    }

    public Diagram getDRGDiagram() {
        return getSessionState().getDRGDiagram();
    }

    public DMNDiagramElement getDRGDMNDiagramElement() {
        return getSessionState().getDRGDMNDiagramElement();
    }

    private DMNDiagramTuple getDRGDiagramTuple() {
        return getSessionState().getDRGDiagramTuple();
    }

    public void clear() {
        getSessionState().clear();
    }

    public List<DRGElement> getModelDRGElements() {
        return Optional.ofNullable(getSessionState()).map(DMNDiagramsSessionState::getModelDRGElements).orElse(emptyList());
    }

    public List<Import> getModelImports() {
        return Optional.ofNullable(getSessionState()).map(DMNDiagramsSessionState::getModelImports).orElse(emptyList());
    }

    @Override
    public boolean isGlobalGraphSelected() {
        return getCurrentDMNDiagramElement().map(DRGDiagramUtils::isDRG).orElse(false);
    }

    @Override
    public List<Graph> getGraphs() {
        return getDMNDiagrams()
                .stream()
                .map(tuple -> tuple.getStunnerDiagram().getGraph())
                .collect(Collectors.toList());
    }

    public List<Node> getAllNodes() {
        final List<Node> result = new ArrayList<>();
        for (final DMNDiagramTuple tuple : getDMNDiagrams()) {
            final Diagram diagram = tuple.getStunnerDiagram();
            result.addAll(dmnGraphUtils.getNodeStream(diagram).collect(Collectors.toList()));
        }
        return result;
    }
}
