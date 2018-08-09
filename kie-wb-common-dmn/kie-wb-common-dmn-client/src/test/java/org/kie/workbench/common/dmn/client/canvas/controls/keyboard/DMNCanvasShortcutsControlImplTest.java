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

package org.kie.workbench.common.dmn.client.canvas.controls.keyboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.BaseCanvasShortcutsControlImplTest;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;

import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCanvasShortcutsControlImplTest extends BaseCanvasShortcutsControlImplTest {

    @Override
    public AbstractCanvasShortcutsControlImpl getCanvasShortcutsControl() {
        return spy(new DMNCanvasShortcutsControlImpl(toolboxDomainLookups,
                                                     definitionsCacheRegistry,
                                                     generalCreateNodeAction));
    }

    @Override
    public List<TestedTuple> getTestedTuples() {
        return Arrays.asList(new TestedTuple(DmnNode.DecisionNode.definition,
                                             KeyDownEvent.Key.D,
                                             expectedPositiveReactionOnNode(DmnNode.DecisionNode)),
                             new TestedTuple(new Decision(),
                                             KeyDownEvent.Key.E,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(new InputData(),
                                             KeyDownEvent.Key.D,
                                             expectedPositiveReactionOnNode(DmnNode.DecisionNode)),
                             new TestedTuple(new KnowledgeRequirement(),
                                             KeyDownEvent.Key.D,
                                             expectedPositiveReactionOnNode(null)));
    }

    private Map<Object, Boolean> expectedPositiveReactionOnNode(final DmnNode node) {
        final Map<Object, Boolean> reactions = new HashMap<>();
        Stream.of(DmnNode.values()).forEach(dmnNode -> reactions.put(dmnNode.definition, false));

        if (node != null) {
            reactions.put(node.definition, true);
        }

        return reactions;
    }

    public enum DmnNode {
        DecisionNode(new Decision()),
        BusinessKnowledgeModelNode(new BusinessKnowledgeModel()),
        KnowledgeRequirementNode(new KnowledgeRequirement()),
        TextAnnotationNode(new TextAnnotation()),
        InputDataNode(new InputData());

        private Object definition;

        DmnNode(final Object definition) {
            this.definition = definition;
        }
    }
}
