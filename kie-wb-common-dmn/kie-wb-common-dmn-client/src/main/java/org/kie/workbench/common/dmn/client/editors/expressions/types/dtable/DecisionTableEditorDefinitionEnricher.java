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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class DecisionTableEditorDefinitionEnricher {

    private SessionManager sessionManager;
    private DMNGraphUtils dmnGraphUtils;

    private static class InputClauseRequirement {

        private String text;
        private QName typeRef;

        public InputClauseRequirement(final String text,
                                      final QName typeRef) {
            this.text = text;
            this.typeRef = typeRef;
        }
    }

    public DecisionTableEditorDefinitionEnricher() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinitionEnricher(final SessionManager sessionManager,
                                                 final DMNGraphUtils dmnGraphUtils) {
        this.sessionManager = sessionManager;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public void enrichModelClass(final Optional<String> nodeUUID,
                                 final DecisionTable dtable) {
        dtable.setHitPolicy(HitPolicy.ANY);
        dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

        final InputClause ic = new InputClause();
        final LiteralExpression le = new LiteralExpression();
        le.setText(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
        ic.setInputExpression(le);
        dtable.getInput().add(ic);

        final OutputClause oc = new OutputClause();
        oc.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
        dtable.getOutput().add(oc);

        final DecisionRule dr = new DecisionRule();
        final UnaryTests drut = new UnaryTests();
        drut.setText(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
        dr.getInputEntry().add(drut);

        final LiteralExpression drle = new LiteralExpression();
        drle.setText(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
        dr.getOutputEntry().add(drle);

        final Description d = new Description();
        d.setValue(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
        dr.setDescription(d);

        dtable.getRule().add(dr);

        //Setup parent relationships
        ic.setParent(dtable);
        oc.setParent(dtable);
        dr.setParent(dtable);
        le.setParent(ic);
        drut.setParent(dr);
        drle.setParent(dr);

        if (nodeUUID.isPresent()) {
            enrichInputClauses(nodeUUID.get(), dtable);
        } else {
            enrichOutputClauses(dtable);
        }
    }

    @SuppressWarnings("unchecked")
    void enrichInputClauses(final String uuid,
                            final DecisionTable dtable) {
        final Graph<?, Node> graph = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph();
        final Node<?, Edge> node = graph.getNode(uuid);
        if (Objects.isNull(node)) {
            return;
        }

        //Get all InputData nodes feeding into this DecisionTable
        final List<InputData> inputDataSet = node.getInEdges().stream()
                .map(Edge::getSourceNode)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .filter(d -> d instanceof InputData)
                .map(d -> (InputData) d)
                .collect(Collectors.toList());
        if (inputDataSet.isEmpty()) {
            return;
        }

        //Extract individual components of InputData TypeRefs
        final Definitions definitions = dmnGraphUtils.getDefinitions();
        final List<InputClauseRequirement> inputClauseRequirements = new ArrayList<>();
        inputDataSet.forEach(id -> addInputClauseRequirement(id.getVariable().getTypeRef(),
                                                             definitions,
                                                             inputClauseRequirements,
                                                             id.getName().getValue()));

        //Add InputClause columns for each InputData TypeRef component
        dtable.getInput().clear();
        dtable.getRule().stream().forEach(dr -> dr.getInputEntry().clear());
        inputClauseRequirements.forEach(icr -> {
            final InputClause ic = new InputClause();
            final LiteralExpression le = new LiteralExpression();
            le.setText(icr.text);
            le.setTypeRef(icr.typeRef);
            ic.setInputExpression(le);
            dtable.getInput().add(ic);

            dtable.getRule().stream().forEach(dr -> {
                final UnaryTests drut = new UnaryTests();
                drut.setText(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
                dr.getInputEntry().add(drut);
                drut.setParent(dr);
            });

            ic.setParent(dtable);
            le.setParent(ic);
        });
    }

    private void addInputClauseRequirement(final QName typeRef,
                                           final Definitions definitions,
                                           final List<InputClauseRequirement> inputClauseRequirements,
                                           final String text) {
        if (QName.NULL_NS_URI.equals(typeRef.getNamespaceURI()) && QName.DEFAULT_NS_PREFIX.equals(typeRef.getPrefix())) {
            //Lookup and expand ItemDefinition from the QName's LocalPart
            definitions.getItemDefinition()
                    .stream()
                    .filter(itemDef -> itemDef.getName().getValue().equals(typeRef.getLocalPart()))
                    .findFirst()
                    .ifPresent(itemDefinition -> itemDefinition.getItemComponent()
                            .forEach(ic -> {
                                addInputClauseRequirement(ic.getTypeRef(),
                                                          definitions,
                                                          inputClauseRequirements,
                                                          text + "." + ic.getName().getValue());
                            }));
        } else {
            inputClauseRequirements.add(new InputClauseRequirement(text,
                                                                   typeRef));
        }
    }

    void enrichOutputClauses(final DecisionTable dtable) {
        if (dtable.getParent() instanceof ContextEntry) {
            final ContextEntry ce = (ContextEntry) dtable.getParent();
            dtable.getOutput().clear();
            dtable.getRule().stream().forEach(dr -> dr.getOutputEntry().clear());

            final OutputClause oc = new OutputClause();
            oc.setName(ce.getVariable().getName().getValue());
            dtable.getOutput().add(oc);

            dtable.getRule().stream().forEach(dr -> {
                final LiteralExpression drle = new LiteralExpression();
                drle.setText(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
                dr.getOutputEntry().add(drle);
                drle.setParent(dr);
            });

            oc.setParent(dtable);
        }
    }
}
