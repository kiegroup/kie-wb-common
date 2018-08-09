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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

@Dependent
public class BPMNCanvasShortcutsControlImpl extends AbstractCanvasShortcutsControlImpl {

    @Inject
    public BPMNCanvasShortcutsControlImpl(final ToolboxDomainLookups toolboxDomainLookups,
                                          final DefinitionsCacheRegistry definitionsCacheRegistry,
                                          final GeneralCreateNodeAction createNodeAction) {
        super(toolboxDomainLookups, definitionsCacheRegistry, createNodeAction);
    }

    @Override
    public void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (selectedNodeId() != null) {
            if (KeysMatcher.doKeysMatch(keys, KeyboardEvent.Key.T) && !selectedNodeIsEndEvent()) {
                appendNode(selectedNodeId(),
                           (definition) -> definition instanceof NoneTask);
            }

            if (KeysMatcher.doKeysMatch(keys, KeyboardEvent.Key.G) && !selectedNodeIsEndEvent()) {
                appendNode(selectedNodeId(),
                           (definition) -> definition instanceof ParallelGateway);
            }

            if (KeysMatcher.doKeysMatch(keys, KeyboardEvent.Key.E) && !selectedNodeIsEndEvent()) {
                appendNode(selectedNodeId(),
                           (definition) -> definition instanceof EndNoneEvent);
            }

            if (KeysMatcher.doKeysMatch(keys, KeyboardEvent.Key.S) && !selectedNodeIsEndEvent()) {
                appendNode(selectedNodeId(),
                           (definition) -> definition instanceof EmbeddedSubprocess);
            }
        }
    }

    private boolean selectedNodeIsEndEvent() {
        if (selectedNodeElement().getContent() instanceof Definition) {
            return ((Definition) selectedNodeElement().getContent()).getDefinition() instanceof BaseEndEvent;
        } else {
            return false;
        }
    }
}
