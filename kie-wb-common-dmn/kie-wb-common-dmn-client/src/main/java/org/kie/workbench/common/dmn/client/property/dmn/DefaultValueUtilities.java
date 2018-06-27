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

package org.kie.workbench.common.dmn.client.property.dmn;

import java.util.Optional;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class DefaultValueUtilities {

    public static void updateNewNodeName(final Graph<?, Node> graph,
                                         final DMNModelInstrumentedBase dmnModel) {
        if (dmnModel instanceof BusinessKnowledgeModel) {
            updateBusinessKnowledgeModelDefaultName(graph, (BusinessKnowledgeModel) dmnModel);
        } else if (dmnModel instanceof Decision) {
            updateDecisionDefaultName(graph, (Decision) dmnModel);
        } else if (dmnModel instanceof InputData) {
            updateInputDataDefaultName(graph, (InputData) dmnModel);
        } else if (dmnModel instanceof KnowledgeSource) {
            updateKnowledgeSourceDefaultName(graph, (KnowledgeSource) dmnModel);
        } else if (dmnModel instanceof TextAnnotation) {
            updateTextAnnotationDefaultText(graph, (TextAnnotation) dmnModel);
        } else {
            throw new IllegalArgumentException("Default Name for '" + dmnModel.getClass().getSimpleName() + "' is not support.");
        }
    }

    public static Optional<Integer> extractIndex(final String text,
                                                 final String prefix) {
        if (text == null) {
            return Optional.empty();
        }

        if (!text.startsWith(prefix)) {
            return Optional.empty();
        }

        final String suffix = text.substring(prefix.length());
        try {
            return Optional.of(Integer.parseInt(suffix));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    private static void updateBusinessKnowledgeModelDefaultName(final Graph<?, Node> graph,
                                                                final BusinessKnowledgeModel bkm) {
        bkm.getName().setValue(bkm.getClass().getSimpleName() + "-" + getNodeCount(graph, bkm));
    }

    private static void updateDecisionDefaultName(final Graph<?, Node> graph,
                                                  final Decision decision) {
        decision.getName().setValue(decision.getClass().getSimpleName() + "-" + getNodeCount(graph, decision));
    }

    private static void updateInputDataDefaultName(final Graph<?, Node> graph,
                                                   final InputData inputData) {
        inputData.getName().setValue(inputData.getClass().getSimpleName() + "-" + getNodeCount(graph, inputData));
    }

    private static void updateKnowledgeSourceDefaultName(final Graph<?, Node> graph,
                                                         final KnowledgeSource knowledgeSource) {
        knowledgeSource.getName().setValue(knowledgeSource.getClass().getSimpleName() + "-" + getNodeCount(graph, knowledgeSource));
    }

    private static void updateTextAnnotationDefaultText(final Graph<?, Node> graph,
                                                        final TextAnnotation textAnnotation) {
        textAnnotation.getText().setValue(textAnnotation.getClass().getSimpleName() + "-" + getNodeCount(graph, textAnnotation));
    }

    private static long getNodeCount(final Graph<?, Node> graph,
                                     final DMNModelInstrumentedBase dmnModel) {
        return StreamSupport
                .stream(graph.nodes().spliterator(), false)
                .filter(node -> node.getContent() instanceof View)
                .map(node -> (View) node.getContent())
                .filter(view -> view.getDefinition() instanceof DMNModelInstrumentedBase)
                .map(view -> (DMNModelInstrumentedBase) view.getDefinition())
                .filter(dmn -> dmn.getClass().equals(dmnModel.getClass()))
                .count();
    }
}
