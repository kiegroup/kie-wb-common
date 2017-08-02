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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssociationConverterTest {

    @Mock
    private TextAnnotation textAnnotation;

    @Mock
    private Decision decision;

    @Mock
    private BusinessKnowledgeModel bkm;

    @Mock
    private KnowledgeSource knowledgeSource;

    @Mock
    private InputData inputData;

    @Mock
    private DRGElement drgElement;

    @Mock
    private Association decisionAssociation;

    @Mock
    private Association bkmAssociation;

    @Mock
    private Association knowledgeSourceAssociation;

    @Mock
    private Association inputDataAssociation;

    @Mock
    private Association drgElementAssociation;

    @Mock
    private View<TextAnnotation> textAnnotationView;

    @Mock
    private Node<View<TextAnnotation>, Edge<View<Association>, ?>> textAnnotationNode;

    @Mock
    private Node<View<Decision>, Edge> decisionNode;

    @Mock
    private Node<View<BusinessKnowledgeModel>, Edge> bkmNode;

    @Mock
    private Node<View<KnowledgeSource>, Edge> knowledgeSourceNode;

    @Mock
    private Node<View<InputData>, Edge> inputDataNode;

    @Mock
    private Node<View<DRGElement>, Edge> drgElementNode;

    @Mock
    private View<Decision> decisionView;

    @Mock
    private View<BusinessKnowledgeModel> bkmView;

    @Mock
    private View<KnowledgeSource> knowledgeSourceView;

    @Mock
    private View<InputData> inputDataView;

    @Mock
    private View<DRGElement> drgElementView;

    @Mock
    private View<Association> decisionAssociationView;

    @Mock
    private View<Association> bkmAssociationView;

    @Mock
    private View<Association> knowledgeSourceAssociationView;

    @Mock
    private View<Association> inputDataAssociationView;

    @Mock
    private View<Association> drgElementAssociationView;

    @Mock
    private Edge<View<Association>, Node<View<Decision>, Edge>> edgeFromDecision;

    @Mock
    private Edge<View<Association>, Node<View<BusinessKnowledgeModel>, Edge>> edgeFromBkm;

    @Mock
    private Edge<View<Association>, Node<View<KnowledgeSource>, Edge>> edgeFromKnowledgeSource;

    @Mock
    private Edge<View<Association>, Node<View<InputData>, Edge>> edgeFromInputData;

    @Mock
    private Edge<View<Association>, Node> outgoingEdge;

    /*
     * Construct TextAnnotation with 4 incoming edges and 1 outgoing edge
     */
    @Before
    public void setUp() throws Exception {
        when(textAnnotationNode.getContent()).thenReturn(textAnnotationView);
        when(textAnnotationView.getDefinition()).thenReturn(textAnnotation);
        when(textAnnotation.getId()).thenReturn(new Id("textAnnotationId"));

        when(edgeFromDecision.getSourceNode()).thenReturn(decisionNode);
        when(edgeFromBkm.getSourceNode()).thenReturn(bkmNode);
        when(edgeFromKnowledgeSource.getSourceNode()).thenReturn(knowledgeSourceNode);
        when(edgeFromInputData.getSourceNode()).thenReturn(inputDataNode);
        when(outgoingEdge.getTargetNode()).thenReturn(drgElementNode);

        when(decisionNode.getContent()).thenReturn(decisionView);
        when(bkmNode.getContent()).thenReturn(bkmView);
        when(knowledgeSourceNode.getContent()).thenReturn(knowledgeSourceView);
        when(inputDataNode.getContent()).thenReturn(inputDataView);
        when(drgElementNode.getContent()).thenReturn(drgElementView);

        when(decisionView.getDefinition()).thenReturn(decision);
        when(bkmView.getDefinition()).thenReturn(bkm);
        when(knowledgeSourceView.getDefinition()).thenReturn(knowledgeSource);
        when(inputDataView.getDefinition()).thenReturn(inputData);
        when(drgElementView.getDefinition()).thenReturn(drgElement);

        when(decision.getId()).thenReturn(new Id("decisionId"));
        when(bkm.getId()).thenReturn(new Id("bkmId"));
        when(knowledgeSource.getId()).thenReturn(new Id("knowledgeSourceId"));
        when(inputData.getId()).thenReturn(new Id("inputDataId"));
        when(drgElement.getId()).thenReturn(new Id("drgElementId"));

        when(edgeFromDecision.getContent()).thenReturn(decisionAssociationView);
        when(edgeFromBkm.getContent()).thenReturn(bkmAssociationView);
        when(edgeFromKnowledgeSource.getContent()).thenReturn(knowledgeSourceAssociationView);
        when(edgeFromInputData.getContent()).thenReturn(inputDataAssociationView);
        when(outgoingEdge.getContent()).thenReturn(drgElementAssociationView);

        when(decisionAssociationView.getDefinition()).thenReturn(decisionAssociation);
        when(bkmAssociationView.getDefinition()).thenReturn(bkmAssociation);
        when(knowledgeSourceAssociationView.getDefinition()).thenReturn(knowledgeSourceAssociation);
        when(inputDataAssociationView.getDefinition()).thenReturn(inputDataAssociation);
        when(drgElementAssociationView.getDefinition()).thenReturn(drgElementAssociation);

        when(decisionAssociation.getDescription()).thenReturn(new Description("decisionAssociation"));
        when(bkmAssociation.getDescription()).thenReturn(new Description("bkmAssociation"));
        when(knowledgeSourceAssociation.getDescription()).thenReturn(new Description("knowledgeSourceAssociation"));
        when(inputDataAssociation.getDescription()).thenReturn(new Description("inputDataAssociation"));
        when(drgElementAssociation.getDescription()).thenReturn(new Description("drgElementAssociation"));

        when(decisionAssociation.getId()).thenReturn(new Id("decisionAssociationId"));
        when(bkmAssociation.getId()).thenReturn(new Id("bkmAssociationId"));
        when(knowledgeSourceAssociation.getId()).thenReturn(new Id("knowledgeSourceAssociationId"));
        when(inputDataAssociation.getId()).thenReturn(new Id("inputDataAssociationId"));
        when(drgElementAssociation.getId()).thenReturn(new Id("drgElementAssociationId"));

        when(textAnnotationNode.getInEdges()).thenReturn(Arrays.asList(edgeFromDecision,
                                                                       edgeFromBkm,
                                                                       edgeFromKnowledgeSource,
                                                                       edgeFromInputData));
        when(textAnnotationNode.getOutEdges()).thenReturn(Arrays.asList(outgoingEdge));
    }

    @Test
    public void testDmnFromWBNoEdges() throws Exception {
        when(textAnnotationNode.getInEdges()).thenReturn(Collections.emptyList());
        when(textAnnotationNode.getOutEdges()).thenReturn(Collections.emptyList());

        List<org.kie.dmn.model.v1_1.Association> associations = AssociationConverter.dmnFromWB(textAnnotationNode);

        assertEquals(0,
                     associations.size());
    }

    @Test
    public void testDmnFromWBIncomingEdges() throws Exception {
        List<org.kie.dmn.model.v1_1.Association> associations = AssociationConverter.dmnFromWB(textAnnotationNode);

        assertEquals(5,
                     associations.size());

        org.kie.dmn.model.v1_1.Association fromDecision = associations.get(0);
        org.kie.dmn.model.v1_1.Association fromBkm = associations.get(1);
        org.kie.dmn.model.v1_1.Association fromKnowledgeSource = associations.get(2);
        org.kie.dmn.model.v1_1.Association fromInputData = associations.get(3);
        org.kie.dmn.model.v1_1.Association outgoing = associations.get(4);

        assertIncomingAssociation(fromDecision,
                                  "decision");
        assertIncomingAssociation(fromBkm,
                                  "bkm");
        assertIncomingAssociation(fromKnowledgeSource,
                                  "knowledgeSource");
        assertIncomingAssociation(fromInputData,
                                  "inputData");
        assertOutgoingAssociation(outgoing,
                                  "drgElement");
    }

    private void assertIncomingAssociation(org.kie.dmn.model.v1_1.Association association,
                                           String baseOfId) {
        assertEquals(baseOfId + "AssociationId",
                     association.getId());
        assertEquals(baseOfId + "Association",
                     association.getDescription());
        assertEquals("#" + baseOfId + "Id",
                     association.getSourceRef().getHref());
        assertEquals("#textAnnotationId",
                     association.getTargetRef().getHref());
    }

    private void assertOutgoingAssociation(org.kie.dmn.model.v1_1.Association association,
                                           String baseOfId) {
        assertEquals(baseOfId + "AssociationId",
                     association.getId());
        assertEquals(baseOfId + "Association",
                     association.getDescription());
        assertEquals("#textAnnotationId",
                     association.getSourceRef().getHref());
        assertEquals("#" + baseOfId + "Id",
                     association.getTargetRef().getHref());
    }
}
