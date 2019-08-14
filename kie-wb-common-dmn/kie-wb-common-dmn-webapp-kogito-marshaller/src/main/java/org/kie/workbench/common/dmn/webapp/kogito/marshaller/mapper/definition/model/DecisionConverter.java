/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.JsArrayLikeUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.HrefBuilder.getHref;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class DecisionConverter implements NodeConverter<JSITDecision, org.kie.workbench.common.dmn.api.definition.model.Decision> {

    private FactoryManager factoryManager;

    public DecisionConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<Decision>, ?> nodeFromDMN(final JSITDecision dmn,
                                               final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<Decision>, ?> node = (Node<View<Decision>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                 getDefinitionId(Decision.class)).asNode();
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(), dmn);
        final Expression expression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression(),
                                                                            hasComponentWidthsConsumer);
        final Decision decision = new Decision(id,
                                               description,
                                               name,
                                               new Question(),
                                               new AllowedAnswers(),
                                               informationItem,
                                               expression,
                                               new BackgroundSet(),
                                               new FontSet(),
                                               new GeneralRectangleDimensionsSet());
        decision.setQuestion(QuestionPropertyConverter.wbFromDMN(dmn.getQuestion()));
        decision.setAllowedAnswers(AllowedAnswersPropertyConverter.wbFromDMN(dmn.getAllowedAnswers()));
        node.getContent().setDefinition(decision);

        if (informationItem != null) {
            informationItem.setParent(decision);
        }
        if (expression != null) {
            expression.setParent(decision);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, decision);
        return node;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSITDecision dmnFromNode(final Node<View<Decision>, ?> node,
                                    final Consumer<ComponentWidths> componentWidthsConsumer) {
        final Decision source = node.getContent().getDefinition();
        final JSITDecision d = new JSITDecision();
        d.setId(source.getId().getValue());
        d.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        d.setName(source.getName().getValue());
        final JSITInformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        if (variable != null) {
            variable.setParent(d);
        }
        d.setVariable(variable);
        final JSITExpression expression = ExpressionPropertyConverter.dmnFromWB(source.getExpression(),
                                                                                componentWidthsConsumer);
        if (expression != null) {
            expression.setParent(d);
        }
        d.setExpression(expression);
        d.setQuestion(QuestionPropertyConverter.dmnFromWB(source.getQuestion()));
        d.setAllowedAnswers(AllowedAnswersPropertyConverter.dmnFromWB(source.getAllowedAnswers()));

        // DMN spec table 2: Requirements connection rules
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        final JSITInformationRequirement iReq = new JSITInformationRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredDecision(ri);
                        JsArrayLikeUtils.add(d.getInformationRequirement(), iReq);
                    } else if (drgElement instanceof BusinessKnowledgeModel) {
                        final JSITKnowledgeRequirement iReq = new JSITKnowledgeRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredKnowledge(ri);
                        JsArrayLikeUtils.add(d.getKnowledgeRequirement(), iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        final JSITAuthorityRequirement iReq = new JSITAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredAuthority(ri);
                        JsArrayLikeUtils.add(d.getAuthorityRequirement(), iReq);
                    } else if (drgElement instanceof InputData) {
                        final JSITInformationRequirement iReq = new JSITInformationRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredInput(ri);
                        JsArrayLikeUtils.add(d.getInformationRequirement(), iReq);
                    } else if (drgElement instanceof DecisionService) {
                        if (e.getContent() instanceof Child) {
                            // Stunner relationship of this Decision be encapsulated by the DecisionService, not managed here.
                        } else if (e.getContent() instanceof View && ((View) e.getContent()).getDefinition() instanceof KnowledgeRequirement) {
                            final JSITKnowledgeRequirement iReq = new JSITKnowledgeRequirement();
                            iReq.setId(e.getUUID());
                            final JSITDMNElementReference ri = new JSITDMNElementReference();
                            ri.setHref(getHref(drgElement));
                            iReq.setRequiredKnowledge(ri);
                            JsArrayLikeUtils.add(d.getKnowledgeRequirement(), iReq);
                        } else {
                            throw new UnsupportedOperationException("wrong model definition.");
                        }
                    } else {
                        throw new UnsupportedOperationException("wrong model definition.");
                    }
                }
            }
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, d);

        return d;
    }
}
