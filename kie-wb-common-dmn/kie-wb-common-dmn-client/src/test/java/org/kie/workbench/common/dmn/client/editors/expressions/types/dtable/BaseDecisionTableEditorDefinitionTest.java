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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Optional;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class BaseDecisionTableEditorDefinitionTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private HitPolicyEditorView.Presenter hitPolicyEditor;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    protected GridCellTuple parent;

    @Mock
    protected HasExpression hasExpression;

    protected Optional<HasName> hasName = Optional.of(HasName.NOP);

    protected Graph<?, Node> graph = new GraphImpl<>(UUID.uuid(), new GraphNodeStoreImpl());

    protected DecisionTableEditorDefinition definition;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.definition = new DecisionTableEditorDefinition(gridPanel,
                                                            gridLayer,
                                                            definitionUtils,
                                                            sessionManager,
                                                            sessionCommandManager,
                                                            canvasCommandFactory,
                                                            editorSelectedEvent,
                                                            cellEditorControls,
                                                            listSelector,
                                                            translationService,
                                                            hitPolicyEditor,
                                                            new DecisionTableEditorDefinitionEnricher(sessionManager,
                                                                                                      new DMNGraphUtils(sessionManager)));

        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    protected void assertBasicEnrichment(final DecisionTable model) {
        assertThat(model.getHitPolicy()).isEqualTo(HitPolicy.ANY);
        assertThat(model.getPreferredOrientation()).isEqualTo(DecisionTableOrientation.RULE_AS_ROW);
    }

    protected void assertStandardInputClauseEnrichment(final DecisionTable model) {
        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(1);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(LiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_PREFIX + "1");
    }

    protected void assertStandardOutputClauseEnrichment(final DecisionTable model) {
        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1");
    }

    protected void assertStandardDecisionRuleEnrichment(final DecisionTable model,
                                                        final int inputClauseCount,
                                                        final int outputClauseCount) {
        final List<DecisionRule> rules = model.getRule();
        assertThat(rules.size()).isEqualTo(1);

        final DecisionRule rule = rules.get(0);
        assertThat(rule.getInputEntry().size()).isEqualTo(inputClauseCount);
        rule.getInputEntry().forEach(ie -> {
            assertThat(ie).isInstanceOf(UnaryTests.class);
            assertThat(ie.getText()).isEqualTo(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
        });

        assertThat(rule.getOutputEntry().size()).isEqualTo(outputClauseCount);
        rule.getOutputEntry().forEach(oe -> {
            assertThat(oe).isInstanceOf(LiteralExpression.class);
            assertThat(oe.getText()).isEqualTo(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
        });

        assertThat(rule.getDescription()).isNotNull();
        assertThat(rule.getDescription().getValue()).isEqualTo(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
    }

    protected void assertParentHierarchyEnrichment(final DecisionTable model,
                                                   final int inputClauseCount,
                                                   final int outputClauseCount) {
        final List<DecisionRule> rules = model.getRule();
        final DecisionRule rule = rules.get(0);

        final List<InputClause> inputClauses = model.getInput();
        assertThat(inputClauses.size()).isEqualTo(inputClauseCount);
        inputClauses.forEach(ic -> {
            assertThat(ic.getParent()).isEqualTo(model);
            assertThat(ic.getInputExpression().getParent()).isEqualTo(ic);
        });

        final List<OutputClause> outputClauses = model.getOutput();
        assertThat(outputClauses.size()).isEqualTo(outputClauseCount);
        outputClauses.forEach(oc -> assertThat(oc.getParent()).isEqualTo(model));

        assertThat(rule.getParent()).isEqualTo(model);
        final List<UnaryTests> inputEntries = rule.getInputEntry();
        assertThat(inputEntries.size()).isEqualTo(inputClauseCount);
        inputEntries.forEach(ie -> assertThat(ie.getParent()).isEqualTo(rule));

        final List<LiteralExpression> outputEntries = rule.getOutputEntry();
        assertThat(outputEntries.size()).isEqualTo(outputClauseCount);
        outputEntries.forEach(oe -> assertThat(oe.getParent()).isEqualTo(rule));
    }
}
