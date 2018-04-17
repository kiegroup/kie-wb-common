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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MorphNodeCommandTest extends AbstractGraphCommandTest {

    private static final String CURRENT_DEFINITION = "current-definition";

    private static final String CURRENT_DEFINITION_ID = "current-definition-id";

    private static final String NEW_DEFINITION = "new-definition";

    private static final String NEW_DEFINITION_ID = "new-definition-id";

    private static final String NEW_DEFINITION_LABEL = "new-definition-label";

    @Mock
    private Node<Definition, Edge> candidate;

    @Mock
    private Definition content;

    @Mock
    private MorphDefinition morphDefinition;

    @Mock
    private MorphAdapter morphAdaptor;

    private Set<String> labels = new HashSet<>();

    private MorphNodeCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init(500, 500);

        this.tested = new MorphNodeCommand(candidate, morphDefinition, CURRENT_DEFINITION_ID);

        when(candidate.getLabels()).thenReturn(labels);
        when(candidate.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(CURRENT_DEFINITION);
        when(adapterRegistry.getMorphAdapter(any(Class.class))).thenReturn(morphAdaptor);

        when(definitionAdapter.getId(CURRENT_DEFINITION)).thenReturn(CURRENT_DEFINITION_ID);
        when(definitionAdapter.getLabels(CURRENT_DEFINITION)).thenReturn(Collections.emptySet());
        when(morphAdaptor.morph(CURRENT_DEFINITION, morphDefinition, CURRENT_DEFINITION_ID)).thenReturn(NEW_DEFINITION);

        when(definitionAdapter.getId(NEW_DEFINITION)).thenReturn(NEW_DEFINITION_ID);
        when(definitionAdapter.getLabels(NEW_DEFINITION)).thenReturn(Collections.singleton(NEW_DEFINITION_LABEL));
        when(morphAdaptor.morph(NEW_DEFINITION, morphDefinition, CURRENT_DEFINITION_ID)).thenReturn(CURRENT_DEFINITION);
    }

    @Test
    public void testAllow() {
        final CommandResult<RuleViolation> result = tested.allow(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
        verify(ruleManager,
               times(0)).evaluate(eq(ruleSet),
                                  any(RuleEvaluationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final CommandResult<RuleViolation> result = tested.execute(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());

        verify(content).setDefinition(eq(NEW_DEFINITION));
        assertEquals(2, labels.size());
        assertTrue(labels.contains(NEW_DEFINITION_ID));
        assertTrue(labels.contains(NEW_DEFINITION_LABEL));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo() {
        //Execute command and mock new state
        tested.execute(graphCommandExecutionContext);
        reset(content);
        when(content.getDefinition()).thenReturn(NEW_DEFINITION);

        final CommandResult<RuleViolation> result = tested.undo(graphCommandExecutionContext);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());

        verify(content).setDefinition(eq(CURRENT_DEFINITION));
        assertEquals(1, labels.size());
        assertTrue(labels.contains(CURRENT_DEFINITION_ID));
    }
}
