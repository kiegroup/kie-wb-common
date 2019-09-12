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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.background.BgColour;
import org.kie.workbench.common.dmn.api.property.background.BorderColour;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITArtifact;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDI;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDecisionServiceDividerLine;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNLabel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.AssociationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DecisionConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DecisionServiceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.DefinitionsConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.IdPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.InputDataConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.ItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.TextAnnotationConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ColorUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.FontSetPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.ArrayUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.NameSpaceUtils;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.heightOfShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.lowerRightBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.widthOfShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.xOfShape;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.yOfShape;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class DMNMarshallerKogito {

    private static final String INFO_REQ_ID = getDefinitionId(InformationRequirement.class);
    private static final String KNOWLEDGE_REQ_ID = getDefinitionId(KnowledgeRequirement.class);
    private static final String AUTH_REQ_ID = getDefinitionId(AuthorityRequirement.class);
    private static final String ASSOCIATION_ID = getDefinitionId(Association.class);

    private static final double CENTRE_TOLERANCE = 1.0;

    private final FactoryManager factoryManager;
    private final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper;

    private InputDataConverter inputDataConverter;
    private DecisionConverter decisionConverter;
    private BusinessKnowledgeModelConverter bkmConverter;
    private KnowledgeSourceConverter knowledgeSourceConverter;
    private TextAnnotationConverter textAnnotationConverter;
    private DecisionServiceConverter decisionServiceConverter;

    protected DMNMarshallerKogito() {
        this(null, null);
    }

    @Inject
    public DMNMarshallerKogito(final FactoryManager factoryManager,
                               final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper) {
        this.factoryManager = factoryManager;
        this.dmnMarshallerImportsHelper = dmnMarshallerImportsHelper;

        this.inputDataConverter = new InputDataConverter(factoryManager);
        this.decisionConverter = new DecisionConverter(factoryManager);
        this.bkmConverter = new BusinessKnowledgeModelConverter(factoryManager);
        this.knowledgeSourceConverter = new KnowledgeSourceConverter(factoryManager);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager);
        this.decisionServiceConverter = new DecisionServiceConverter(factoryManager);
    }

    private static Optional<JSIDMNDiagram> findDMNDiagram(final JSITDefinitions dmnXml) {
        if (dmnXml.getDMNDI() == null) {
            return Optional.empty();
        }
        final JsArrayLike<JSIDMNDiagram> elems = dmnXml.getDMNDI().getDMNDiagram();
        if (elems.getLength() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(Js.uncheckedCast(elems.getAt(0)));
        }
    }

    // ==================================
    // UNMARSHALL
    // ==================================

    @SuppressWarnings("unchecked")
    public Graph unmarshall(final Metadata metadata,
                            final JSITDefinitions jsiDefinitions) {
        final Map<String, HasComponentWidths> hasComponentWidthsMap = new HashMap<>();
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {
            if (Objects.nonNull(uuid)) {
                hasComponentWidthsMap.put(uuid, hcw);
            }
        };

        final JsArrayLike<JSITDRGElement> jsitDRGElements = JSITDefinitions.getDrgElement(jsiDefinitions);
        final List<JSITDRGElement> diagramDrgElements = JsUtils.toList(jsitDRGElements);
        final Optional<JSIDMNDiagram> dmnDDDiagram = findDMNDiagram(jsiDefinitions);

        // Get external DMN model information
        final Map<JSITImport, JSITDefinitions> importDefinitions = dmnMarshallerImportsHelper.getImportDefinitions(metadata,
                                                                                                                   JsUtils.toList(jsiDefinitions.getImport()));

        // Get external PMML model information
        final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments = dmnMarshallerImportsHelper.getPMMLDocuments(metadata,
                                                                                                                JsUtils.toList(jsiDefinitions.getImport()));

        // Map external DRGElements
        final List<JSIDMNShape> dmnShapes = new ArrayList<>();
        final List<JSITDRGElement> importedDrgElements = new ArrayList<>();
        if (dmnDDDiagram.isPresent()) {
            final JSIDMNDiagram jsidmnDiagram = Js.uncheckedCast(dmnDDDiagram.get());
            dmnShapes.addAll(getUniqueDMNShapes(jsidmnDiagram));
            importedDrgElements.addAll(getImportedDrgElementsByShape(dmnShapes,
                                                                     importDefinitions,
                                                                     jsiDefinitions));
        }

        // Combine all explicit and imported elements into one
        final List<JSITDRGElement> drgElements = new ArrayList<>();
        final Set<JSITDecisionService> dmnDecisionServices = new HashSet<>();
        drgElements.addAll(diagramDrgElements);
        drgElements.addAll(importedDrgElements);

        // Remove DRGElements that doesn't have any local or imported shape.
        removeDrgElementsWithoutShape(drgElements, dmnShapes);

        // Main conversion from DMN to Stunner
        final Map<String, Entry<JSITDRGElement, Node>> elems = new HashMap<>();
        for (int i = 0; i < drgElements.size(); i++) {
            final JSITDRGElement drgElement = Js.uncheckedCast(drgElements.get(i));
            final String id = drgElement.getId();
            final Node stunnerNode = dmnToStunner(drgElement,
                                                  hasComponentWidthsConsumer,
                                                  importedDrgElements);
            elems.put(id,
                      new AbstractMap.SimpleEntry<>(drgElement, stunnerNode));
        }

        // Stunner rely on relative positioning for Edge connections, so need to cycle on DMNShape first.
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            ddExtAugmentStunner(dmnDDDiagram, kv.getValue());
        }

        // Setup Node Relationships and Connections all based on absolute positioning
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            final JSITDRGElement element = Js.uncheckedCast(kv.getKey());
            final Node currentNode = kv.getValue();

            // For imported nodes, we don't have its connections
            if (isImportedDRGElement(importedDrgElements, element)) {
                continue;
            }

            // DMN spec table 2: Requirements connection rules
            if (JSITDecision.instanceOf(element)) {
                final JSITDecision decision = Js.uncheckedCast(element);
                final JsArrayLike<JSITInformationRequirement> jsiInformationRequirements = JSITDecision.getInformationRequirement(decision);
                for (int i = 0; i < jsiInformationRequirements.getLength(); i++) {
                    final JSITInformationRequirement ir = Js.uncheckedCast(jsiInformationRequirements.getAt(i));
                    if (ir.getRequiredInput() != null) {
                        final String reqInputID = getId(ir.getRequiredInput());
                        final Node requiredNode = getRequiredNode(elems, reqInputID);
                        final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                      INFO_REQ_ID).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), jsiDefinitions);
                    }
                    if (ir.getRequiredDecision() != null) {
                        final String reqInputID = getId(ir.getRequiredDecision());
                        final Node requiredNode = getRequiredNode(elems, reqInputID);
                        final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ir),
                                                                      INFO_REQ_ID).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ir.getId(), jsiDefinitions);
                    }
                }
                final JsArrayLike<JSITKnowledgeRequirement> jsiKnowledgeRequirements = JSITDecision.getKnowledgeRequirement(decision);
                for (int i = 0; i < jsiKnowledgeRequirements.getLength(); i++) {
                    final JSITKnowledgeRequirement kr = Js.uncheckedCast(jsiKnowledgeRequirements.getAt(i));
                    final String reqInputID = getId(kr.getRequiredKnowledge());
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(kr),
                                                                  KNOWLEDGE_REQ_ID).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, kr.getId(), jsiDefinitions);
                }
                final JsArrayLike<JSITAuthorityRequirement> jsiAuthorityRequirements = JSITDecision.getAuthorityRequirement(decision);
                for (int i = 0; i < jsiAuthorityRequirements.getLength(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.getAt(i));
                    final String reqInputID = getId(ar.getRequiredAuthority());
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ar),
                                                                  AUTH_REQ_ID).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, ar.getId(), jsiDefinitions);
                }
            } else if (JSITBusinessKnowledgeModel.instanceOf(element)) {
                final JSITBusinessKnowledgeModel bkm = Js.uncheckedCast(element);
                final JsArrayLike<JSITKnowledgeRequirement> jsiKnowledgeRequirements = JSITBusinessKnowledgeModel.getKnowledgeRequirement(bkm);
                for (int i = 0; i < jsiKnowledgeRequirements.getLength(); i++) {
                    final JSITKnowledgeRequirement kr = Js.uncheckedCast(jsiKnowledgeRequirements.getAt(i));
                    final String reqInputID = getId(kr.getRequiredKnowledge());
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(kr),
                                                                  KNOWLEDGE_REQ_ID).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, kr.getId(), jsiDefinitions);
                }
                final JsArrayLike<JSITAuthorityRequirement> jsiAuthorityRequirements = JSITBusinessKnowledgeModel.getAuthorityRequirement(bkm);
                for (int i = 0; i < jsiAuthorityRequirements.getLength(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.getAt(i));
                    final String reqInputID = getId(ar.getRequiredAuthority());
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ar),
                                                                  AUTH_REQ_ID).asEdge();
                    connectEdge(myEdge,
                                requiredNode,
                                currentNode);
                    setConnectionMagnets(myEdge, ar.getId(), jsiDefinitions);
                }
            } else if (JSITKnowledgeSource.instanceOf(element)) {
                final JSITKnowledgeSource ks = Js.uncheckedCast(element);
                final JsArrayLike<JSITAuthorityRequirement> jsiAuthorityRequirements = JSITKnowledgeSource.getAuthorityRequirement(ks);
                for (int i = 0; i < jsiAuthorityRequirements.getLength(); i++) {
                    final JSITAuthorityRequirement ar = Js.uncheckedCast(jsiAuthorityRequirements.getAt(i));
                    if (ar.getRequiredInput() != null) {
                        final String reqInputID = getId(ar.getRequiredInput());
                        final Node requiredNode = getRequiredNode(elems, reqInputID);
                        final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ar),
                                                                      AUTH_REQ_ID).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ar.getId(), jsiDefinitions);
                    }
                    if (ar.getRequiredDecision() != null) {
                        final String reqInputID = getId(ar.getRequiredDecision());
                        final Node requiredNode = getRequiredNode(elems, reqInputID);
                        final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ar),
                                                                      AUTH_REQ_ID).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ar.getId(), jsiDefinitions);
                    }
                    if (ar.getRequiredAuthority() != null) {
                        final String reqInputID = getId(ar.getRequiredAuthority());
                        final Node requiredNode = getRequiredNode(elems, reqInputID);
                        final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(ar),
                                                                      AUTH_REQ_ID).asEdge();
                        connectEdge(myEdge,
                                    requiredNode,
                                    currentNode);
                        setConnectionMagnets(myEdge, ar.getId(), jsiDefinitions);
                    }
                }
            } else if (JSITDecisionService.instanceOf(element)) {
                final JSITDecisionService ds = Js.uncheckedCast(element);
                dmnDecisionServices.add(ds);
                final JsArrayLike<JSITDMNElementReference> jsiEncapsulatedDecisions = JSITDecisionService.getEncapsulatedDecision(ds);
                for (int i = 0; i < jsiEncapsulatedDecisions.getLength(); i++) {
                    final JSITDMNElementReference er = Js.uncheckedCast(jsiEncapsulatedDecisions.getAt(i));
                    final String reqInputID = getId(er);
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    connectDSChildEdge(currentNode, requiredNode);
                }
                final JsArrayLike<JSITDMNElementReference> jsiOutputDecisions = JSITDecisionService.getOutputDecision(ds);
                for (int i = 0; i < jsiOutputDecisions.getLength(); i++) {
                    final JSITDMNElementReference er = Js.uncheckedCast(jsiOutputDecisions.getAt(i));
                    final String reqInputID = getId(er);
                    final Node requiredNode = getRequiredNode(elems, reqInputID);
                    connectDSChildEdge(currentNode, requiredNode);
                }
            }
        }

        final Map<String, Node<View<TextAnnotation>, ?>> textAnnotations = new HashMap<>();
        final JsArrayLike<JSITArtifact> jsiArtifacts = JSITDefinitions.getArtifact(jsiDefinitions);
        for (int i = 0; i < jsiArtifacts.getLength(); i++) {
            final JSITArtifact jsiArtifact = Js.uncheckedCast(jsiArtifacts.getAt(i));
            if (JSITTextAnnotation.instanceOf(jsiArtifact)) {
                final String id = jsiArtifact.getId();
                final JSITTextAnnotation jsiTextAnnotation = Js.uncheckedCast(jsiArtifact);
                final Node<View<TextAnnotation>, ?> textAnnotation = textAnnotationConverter.nodeFromDMN(jsiTextAnnotation,
                                                                                                         hasComponentWidthsConsumer);
                textAnnotations.put(id,
                                    textAnnotation);
            }
        }
        textAnnotations.values().forEach(n -> ddExtAugmentStunner(dmnDDDiagram, n));

        final List<JSITAssociation> associations = new ArrayList<>();
        for (int i = 0; i < jsiArtifacts.getLength(); i++) {
            final JSITArtifact jsiArtifact = Js.uncheckedCast(jsiArtifacts.getAt(i));
            if (JSITAssociation.instanceOf(jsiArtifact)) {
                final JSITAssociation jsiAssociation = Js.uncheckedCast(jsiArtifact);
                associations.add(jsiAssociation);
            }
        }
        for (int i = 0; i < associations.size(); i++) {
            final JSITAssociation jsiAssociation = Js.uncheckedCast(associations.get(i));
            final String sourceId = getId(jsiAssociation.getSourceRef());
            final Node sourceNode = Optional.ofNullable(elems.get(sourceId)).map(Entry::getValue).orElse(textAnnotations.get(sourceId));

            final String targetId = getId(jsiAssociation.getTargetRef());
            final Node targetNode = Optional.ofNullable(elems.get(targetId)).map(Entry::getValue).orElse(textAnnotations.get(targetId));

            @SuppressWarnings("unchecked")
            final Edge<View<Association>, ?> myEdge = (Edge<View<Association>, ?>) factoryManager.newElement(idOfDMNorWBUUID(jsiAssociation),
                                                                                                             ASSOCIATION_ID).asEdge();

            final Id id = IdPropertyConverter.wbFromDMN(jsiAssociation.getId());
            final Description description = new Description(jsiAssociation.getDescription());
            final Association definition = new Association(id, description);
            myEdge.getContent().setDefinition(definition);

            connectEdge(myEdge,
                        sourceNode,
                        targetNode);
            setConnectionMagnets(myEdge, jsiAssociation.getId(), jsiDefinitions);
        }

        //Ensure all locations are updated to relative for Stunner
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            PointUtils.convertToRelativeBounds(kv.getValue());
        }

        final Graph graph = factoryManager.newDiagram("prova",
                                                      BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                      metadata).getGraph();
        elems.values().stream().map(Entry::getValue).forEach(graph::addNode);
        textAnnotations.values().forEach(graph::addNode);

        final Node<?, ?> dmnDiagramRoot = findDMNDiagramRoot(graph);
        final Definitions definitionsStunnerPojo = DefinitionsConverter.wbFromDMN(jsiDefinitions,
                                                                                  importDefinitions,
                                                                                  pmmlDocuments);
        loadImportedItemDefinitions(definitionsStunnerPojo, importDefinitions);
        ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition().setDefinitions(definitionsStunnerPojo);

        //Only connect Nodes to the Diagram that are not referenced by DecisionServices
        final List<String> references = new ArrayList<>();
        final List<JSITDecisionService> lstDecisionServices = new ArrayList<>(dmnDecisionServices);
        for (int iDS = 0; iDS < lstDecisionServices.size(); iDS++) {
            final JSITDecisionService jsiDecisionService = Js.uncheckedCast(lstDecisionServices.get(iDS));
            final JsArrayLike<JSITDMNElementReference> jsiEncapsulatedDecisions = JSITDecisionService.getEncapsulatedDecision(jsiDecisionService);
            if (Objects.nonNull(jsiEncapsulatedDecisions)) {
                for (int i = 0; i < jsiEncapsulatedDecisions.getLength(); i++) {
                    final JSITDMNElementReference jsiEncapsulatedDecision = Js.uncheckedCast(jsiEncapsulatedDecisions.getAt(i));
                    references.add(jsiEncapsulatedDecision.getHref());
                }
            }

            final JsArrayLike<JSITDMNElementReference> jsiOutputDecisions = JSITDecisionService.getOutputDecision(jsiDecisionService);
            if (Objects.nonNull(jsiOutputDecisions)) {
                for (int i = 0; i < jsiOutputDecisions.getLength(); i++) {
                    final JSITDMNElementReference jsiOutputDecision = Js.uncheckedCast(jsiOutputDecisions.getAt(i));
                    references.add(jsiOutputDecision.getHref());
                }
            }
        }

        final Map<JSITDRGElement, Node> elementsToConnectToRoot = new HashMap<>();
        for (Entry<JSITDRGElement, Node> kv : elems.values()) {
            final JSITDRGElement element = Js.uncheckedCast(kv.getKey());
            final Node node = kv.getValue();
            if (!references.contains("#" + element.getId())) {
                elementsToConnectToRoot.put(element, node);
            }
        }
        elementsToConnectToRoot.values().forEach(node -> connectRootWithChild(dmnDiagramRoot, node));
        textAnnotations.values().forEach(node -> connectRootWithChild(dmnDiagramRoot, node));

        //Copy ComponentWidths information
        final Optional<JSITComponentsWidthsExtension> extension = findComponentsWidthsExtension(dmnDDDiagram);
        extension.ifPresent(componentsWidthsExtension -> {
            //This condition is required because a node with ComponentsWidthsExtension
            //can be imported from another diagram but the extension is not imported or present in this diagram.
            //TODO: This will be fixed in this JIRA: https://issues.jboss.org/browse/DROOLS-3934
            if (Objects.nonNull(componentsWidthsExtension.getComponentWidths())) {
                hasComponentWidthsMap.entrySet().forEach(es -> {
                    final JsArrayLike<JSITComponentWidths> jsiComponentWidths = componentsWidthsExtension.getComponentWidths();
                    for (int i = 0; i < jsiComponentWidths.getLength(); i++) {
                        final JSITComponentWidths jsiWidths = Js.uncheckedCast(jsiComponentWidths.getAt(i));
                        if (Objects.equals(jsiWidths.getDmnElementRef(), es.getKey())) {
                            final List<Double> widths = es.getValue().getComponentWidths();
                            if (Objects.nonNull(jsiWidths.getWidth())) {
                                widths.clear();
                                for (int w = 0; w < jsiWidths.getWidth().getLength(); w++) {
                                    final double width = Js.castToDouble(jsiWidths.getWidth().getAt(w));
                                    widths.add(width);
                                }
                            }
                        }
                    }
                });
            }
        });

        return graph;
    }

    void removeDrgElementsWithoutShape(final List<JSITDRGElement> drgElements,
                                       final List<JSIDMNShape> dmnShapes) {
        // DMN 1.1 doesn't have DMNShape, so we include all DRGElements and create all the shapes.
        if (dmnShapes.size() == 0) {
            return;
        }

        drgElements.removeIf(element -> dmnShapes.stream().noneMatch(s -> Objects.equals(s.getDmnElementRef().getLocalPart(),
                                                                                         element.getId())));
    }

    void updateIDsWithAlias(final Map<String, String> indexByUri,
                            final List<JSITDRGElement> importedDrgElements) {

        if (importedDrgElements.isEmpty()) {
            return;
        }

        final QName defaultNamespace = new QName(XMLConstants.NULL_NS_URI,
                                                 "Namespace",
                                                 XMLConstants.DEFAULT_NS_PREFIX);

        for (JSITDRGElement element : importedDrgElements) {
            final String namespaceAttribute = element.getOtherAttributes().getOrDefault(defaultNamespace, "");
            if (!StringUtils.isEmpty(namespaceAttribute)) {
                if (indexByUri.containsKey(namespaceAttribute)) {
                    final String alias = indexByUri.get(namespaceAttribute);
                    changeAlias(alias, element);
                }
            }
        }
    }

    private void changeAlias(final String alias,
                             final JSITDRGElement drgElement) {
        if (drgElement.getId().contains(":")) {
            final String id = drgElement.getId().split(":")[1];
            drgElement.setId(alias + ":" + id);
        }
    }

    private Node getRequiredNode(final Map<String, Entry<JSITDRGElement, Node>> elems,
                                 final String reqInputID) {
        if (elems.containsKey(reqInputID)) {
            return elems.get(reqInputID).getValue();
        } else {

            final Optional<String> match = elems.keySet().stream()
                    .filter(k -> k.contains(reqInputID))
                    .findFirst();
            if (match.isPresent()) {
                return elems.get(match.get()).getValue();
            }
        }

        return null;
    }

    private List<JSITDRGElement> getImportedDrgElementsByShape(final List<JSIDMNShape> dmnShapes,
                                                               final Map<JSITImport, JSITDefinitions> importDefinitions,
                                                               final JSITDefinitions dmnXml) {

        final List<JSITDRGElement> importedDRGElements = dmnMarshallerImportsHelper.getImportedDRGElements(importDefinitions);

        // Update IDs with the alias used in this file for the respective imports
        final Map<String, String> indexByUri = NameSpaceUtils.extractNamespacesKeyedByUri(dmnXml);
        updateIDsWithAlias(indexByUri, importedDRGElements);

        return dmnShapes
                .stream()
                .map(shape -> {
                    final String dmnElementRef = getDmnElementRef(shape);
                    final Optional<JSITDRGElement> ref = getReference(importedDRGElements, dmnElementRef);
                    return ref.orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Optional<JSITDRGElement> getReference(final List<JSITDRGElement> importedDRGElements,
                                                  final String dmnElementRef) {
        for (JSITDRGElement importedDRGElement : importedDRGElements) {
            final String importedDRGElementId = importedDRGElement.getId();
            if (Objects.equals(importedDRGElementId, dmnElementRef)) {
                return Optional.of(importedDRGElement);
            }
        }
        return Optional.empty();
    }

    private String getDmnElementRef(final JSIDMNShape dmnShape) {
        final QName elementRef = dmnShape.getDmnElementRef();
        if (Objects.nonNull(elementRef)) {
            return elementRef.getLocalPart();
        }
        return "";
    }

    private List<JSIDMNShape> getUniqueDMNShapes(final JSIDMNDiagram dmnDDDiagram) {
        final Map<String, JSIDMNShape> jsidmnShapes = new HashMap<>();
        final JsArrayLike<JSIDiagramElement> unwrapped = JSIDMNDiagram.getDMNDiagramElement(dmnDDDiagram);
        for (int i = 0; i < unwrapped.getLength(); i++) {
            final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(unwrapped.getAt(i));
            if (JSIDMNShape.instanceOf(jsiDiagramElement)) {
                final JSIDMNShape jsidmnShape = Js.uncheckedCast(jsiDiagramElement);
                if (!jsidmnShapes.containsKey(jsidmnShape.getId())) {
                    jsidmnShapes.put(jsidmnShape.getId(), jsidmnShape);
                }
            }
        }
        return new ArrayList<>(jsidmnShapes.values());
    }

    /**
     * Stunner's factoryManager is only used to create Nodes that are considered part of a "Definition Set" (a collection of nodes visible to the User e.g. BPMN2 StartNode, EndNode and DMN's DecisionNode etc).
     * Relationships are not created with the factory.
     * This method specializes to connect with an Edge containing a Child relationship the target Node.
     */
    private static void connectDSChildEdge(Node dsNode, Node requiredNode) {
        final String uuid = dsNode.getUUID() + "er" + requiredNode.getUUID();
        final Edge<Child, Node> myEdge = new EdgeImpl<>(uuid);
        myEdge.setContent(new Child());
        connectEdge(myEdge,
                    dsNode,
                    requiredNode);
    }

    private static String idOfDMNorWBUUID(JSITDMNElement dmn) {
        return dmn.getId() != null ? dmn.getId() : UUID.uuid();
    }

    public static Node<?, ?> findDMNDiagramRoot(final Graph<?, Node<View, ?>> graph) {
        return StreamSupport.stream(graph.nodes().spliterator(),
                                    false).filter(n -> n.getContent().getDefinition() instanceof DMNDiagram).findFirst().orElseThrow(() -> new UnsupportedOperationException("TODO"));
    }

    private String getId(final JSITDMNElementReference er) {
        String href = er.getHref();
        return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
    }

    private Node dmnToStunner(final JSITDRGElement dmn,
                              final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer,
                              final List<JSITDRGElement> importedDrgElements) {

        final Node node = createNode(dmn,
                                     hasComponentWidthsConsumer);
        return setAllowOnlyVisualChange(importedDrgElements, node);
    }

    private Node createNode(final JSITDRGElement dmn,
                            final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (JSITInputData.instanceOf(dmn)) {
            return inputDataConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                  hasComponentWidthsConsumer);
        } else if (JSITDecision.instanceOf(dmn)) {
            return decisionConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                 hasComponentWidthsConsumer);
        } else if (JSITBusinessKnowledgeModel.instanceOf(dmn)) {
            return bkmConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                            hasComponentWidthsConsumer);
        } else if (JSITKnowledgeSource.instanceOf(dmn)) {
            return knowledgeSourceConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                        hasComponentWidthsConsumer);
        } else if (JSITDecisionService.instanceOf(dmn)) {
            return decisionServiceConverter.nodeFromDMN(Js.uncheckedCast(dmn),
                                                        hasComponentWidthsConsumer);
        } else {
            throw new UnsupportedOperationException("TODO"); // TODO
        }
    }

    Node setAllowOnlyVisualChange(final List<JSITDRGElement> importedDrgElements,
                                  final Node node) {

        getDRGElement(node).ifPresent(drgElement -> {
            if (isImportedDRGElement(importedDrgElements, drgElement)) {
                drgElement.setAllowOnlyVisualChange(true);
            } else {
                drgElement.setAllowOnlyVisualChange(false);
            }
        });

        return node;
    }

    Optional<DRGElement> getDRGElement(final Node node) {

        final Object objectDefinition = DefinitionUtils.getElementDefinition(node);

        if (objectDefinition instanceof DRGElement) {
            return Optional.of((DRGElement) objectDefinition);
        }

        return Optional.empty();
    }

    boolean isImportedDRGElement(final List<JSITDRGElement> importedDrgElements,
                                 final JSITDRGElement drgElement) {
        return isImportedIdNode(importedDrgElements, drgElement.getId());
    }

    boolean isImportedDRGElement(final List<JSITDRGElement> importedDrgElements,
                                 final DRGElement drgElement) {
        return isImportedIdNode(importedDrgElements, drgElement.getId().getValue());
    }

    private boolean isImportedIdNode(final List<JSITDRGElement> importedDrgElements,
                                     final String id) {
        return importedDrgElements
                .stream()
                .anyMatch(drgElement -> Objects.equals(drgElement.getId(), id));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void connectRootWithChild(final Node dmnDiagramRoot,
                                            final Node child) {
        final String uuid = UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge, dmnDiagramRoot, child);
        final Definitions definitions = ((DMNDiagram) ((View) dmnDiagramRoot.getContent()).getDefinition()).getDefinitions();
        final DMNModelInstrumentedBase childDRG = (DMNModelInstrumentedBase) ((View) child.getContent()).getDefinition();
        childDRG.setParent(definitions);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void connectEdge(final Edge edge,
                                   final Node source,
                                   final Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }

    @SuppressWarnings("unchecked")
    private void setConnectionMagnets(final Edge edge,
                                      final String dmnEdgeElementRef,
                                      final JSITDefinitions dmnXml) {
        final ViewConnector connectionContent = (ViewConnector) edge.getContent();
        final Optional<JSIDMNDiagram> dmnDiagram = findDMNDiagram(dmnXml);

        Optional<JSIDMNEdge> dmnEdge = Optional.empty();
        if (dmnDiagram.isPresent()) {
            final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDiagram.get());
            final JsArrayLike<JSIDiagramElement> jsiDiagramElements = JSIDMNDiagram.getDMNDiagramElement(jsiDiagram);
            for (int i = 0; i < jsiDiagramElements.getLength(); i++) {
                final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(jsiDiagramElements.getAt(i));
                if (JSIDMNEdge.instanceOf(jsiDiagramElement)) {
                    final JSIDMNEdge jsiEdge = Js.uncheckedCast(jsiDiagramElement);
                    if (Objects.equals(jsiEdge.getDmnElementRef().getLocalPart(), dmnEdgeElementRef)) {
                        dmnEdge = Optional.of(jsiEdge);
                        break;
                    }
                }
            }
        }
        if (dmnEdge.isPresent()) {
            final JSIDMNEdge e = Js.uncheckedCast(dmnEdge.get());
            final JSIPoint source = Js.uncheckedCast(e.getWaypoint().getAt(0));
            final Node<View<?>, Edge> sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                setConnectionMagnet(sourceNode,
                                    source,
                                    connectionContent::setSourceConnection);
            }
            final JSIPoint target = Js.uncheckedCast(e.getWaypoint().getAt(e.getWaypoint().getLength() - 1));
            final Node<View<?>, Edge> targetNode = edge.getTargetNode();
            if (null != targetNode) {
                setConnectionMagnet(targetNode,
                                    target,
                                    connectionContent::setTargetConnection);
            }
            if (e.getWaypoint().getLength() > 2) {
                connectionContent.setControlPoints(Arrays.asList(e.getWaypoint().asArray())
                                                           .subList(1, e.getWaypoint().getLength() - 1)
                                                           .stream()
                                                           .map(p -> ControlPoint.build(PointUtils.dmndiPointToPoint2D(p)))
                                                           .toArray(ControlPoint[]::new));
            }
        } else {
            // Set the source connection, if any.
            final Node sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                connectionContent.setSourceConnection(MagnetConnection.Builder.atCenter(sourceNode));
            }
            // Set the target connection, if any.
            final Node targetNode = edge.getTargetNode();
            if (null != targetNode) {
                connectionContent.setTargetConnection(MagnetConnection.Builder.atCenter(targetNode));
            }
        }
    }

    private void setConnectionMagnet(final Node<View<?>, Edge> node,
                                     final JSIPoint magnetPoint,
                                     final Consumer<Connection> connectionConsumer) {
        final View<?> view = node.getContent();
        final double viewX = xOfBound(upperLeftBound(view));
        final double viewY = yOfBound(upperLeftBound(view));
        final double magnetRelativeX = magnetPoint.getX() - viewX;
        final double magnetRelativeY = magnetPoint.getY() - viewY;
        final double viewWidth = view.getBounds().getWidth();
        final double viewHeight = view.getBounds().getHeight();
        if (isCentre(magnetRelativeX,
                     magnetRelativeY,
                     viewWidth,
                     viewHeight)) {
            connectionConsumer.accept(MagnetConnection.Builder.atCenter(node));
        } else {
            connectionConsumer.accept(MagnetConnection.Builder.at(magnetRelativeX, magnetRelativeY).setAuto(true));
        }
    }

    private boolean isCentre(final double magnetRelativeX,
                             final double magnetRelativeY,
                             final double viewWidth,
                             final double viewHeight) {
        return Math.abs((viewWidth / 2) - magnetRelativeX) < CENTRE_TOLERANCE &&
                Math.abs((viewHeight / 2) - magnetRelativeY) < CENTRE_TOLERANCE;
    }

    private Optional<JSITComponentsWidthsExtension> findComponentsWidthsExtension(final Optional<JSIDMNDiagram> dmnDDDiagram) {
        if (!dmnDDDiagram.isPresent()) {
            return Optional.empty();
        }
        final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagram.get());
        final JSIDiagramElement.JSIExtension dmnDDExtensions = Js.uncheckedCast(jsiDiagram.getExtension());

        if (Objects.isNull(dmnDDExtensions)) {
            return Optional.empty();
        }
        if (Objects.isNull(dmnDDExtensions.getAny())) {
            return Optional.empty();
        }
        final JsArrayLike<Object> extensions = dmnDDExtensions.getAny();
        if (!Objects.isNull(extensions)) {
            for (int i = 0; i < extensions.getLength(); i++) {
                final Object wrapped = extensions.getAt(i);
                final Object extension = JsUtils.getUnwrappedElement(wrapped);
                if (JSITComponentsWidthsExtension.instanceOf(extension)) {
                    final JSITComponentsWidthsExtension jsiExtension = Js.uncheckedCast(extension);
                    return Optional.of(jsiExtension);
                }
            }
        }
        return Optional.empty();
    }

    // ==================================
    // MARSHALL
    // ==================================

    @SuppressWarnings("unchecked")
   /* public JSITDefinitions marshall(final Graph<?, Node<View, ?>> graph) {
        final Map<String, JSITDRGElement> nodes = new HashMap<>();
        final Map<String, JSITTextAnnotation> textAnnotations = new HashMap<>();
        final Node<View<DMNDiagram>, ?> dmnDiagramRoot = (Node<View<DMNDiagram>, ?>) findDMNDiagramRoot(graph);
        final Definitions definitionsStunnerPojo = dmnDiagramRoot.getContent().getDefinition().getDefinitions();
        final List<JSIDMNEdge> dmnEdges = new ArrayList<>();

        cleanImportedItemDefinitions(definitionsStunnerPojo);

        final JSITDefinitions definitions = DefinitionsConverter.dmnFromWB(definitionsStunnerPojo);
        if (Objects.isNull(definitions.getExtensionElements())) {
            definitions.setExtensionElements(new JSITDMNElement.JSIExtensionElements());
        }

        if (Objects.isNull(definitions.getDMNDI())) {
            definitions.setDMNDI(new JSIDMNDI());
        }
        final JSIDMNDiagram dmnDDDMNDiagram = new JSIDMNDiagram();
        if (Objects.isNull(definitions.getDMNDI().getDMNDiagram())) {
            final JsArrayLike<JSIDMNDiagram> diagrams = JavaScriptObject.createArray().cast();
            definitions.getDMNDI().setDMNDiagram(diagrams);
        }
        JsUtils.add(definitions.getDMNDI().getDMNDiagram(), dmnDDDMNDiagram);

        //Convert relative positioning to absolute
        for (Node<?, ?> node : graph.nodes()) {
            PointUtils.convertToAbsoluteBounds(node);
        }

        //Setup callback for marshalling ComponentWidths
        if (Objects.isNull(dmnDDDMNDiagram.getExtension())) {
            dmnDDDMNDiagram.setExtension(new JSIDiagramElement.JSIExtension());
        }
        final JSITComponentsWidthsExtension componentsWidthsExtension = new JSITComponentsWidthsExtension();
        final JSIDiagramElement.JSIExtension extension = dmnDDDMNDiagram.getExtension();
        //TODO {manstis} Need to setup ComponentWidthsExtension if it is not already present
        //if (Objects.isNull(extension.getAny())) {
        //    extension.setAny(new Object[]{});
        //}
        //extension.setAny(ArrayUtils.add(extension.getAny().asArray(), componentsWidthsExtension));

        final Consumer<JSITComponentWidths> componentWidthsConsumer = (cw) -> {
            ArrayUtils.add(componentsWidthsExtension.getComponentWidths().asArray(), cw);
        };

        //Iterate Graph processing nodes..
        for (Node<?, ?> node : graph.nodes()) {
            if (node.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) node.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement n = (DRGElement) view.getDefinition();
                    if (view.getDefinition() instanceof DynamicReadOnly) {
                        final DynamicReadOnly def = (DynamicReadOnly) view.getDefinition();
                        if (!def.isAllowOnlyVisualChange()) {
                            nodes.put(n.getId().getValue(),
                                      stunnerToDMN(node,
                                                   componentWidthsConsumer));
                        }
                    } else {
                        nodes.put(n.getId().getValue(),
                                  stunnerToDMN(node,
                                               componentWidthsConsumer));
                    }
                    if (Objects.isNull(dmnDDDMNDiagram.getDMNDiagramElement())) {
                        final JsArrayLike<JSIDiagramElement> elements = JavaScriptObject.createArray().cast();
                        dmnDDDMNDiagram.setDMNDiagramElement(elements);
                    }
                    JsUtils.add(dmnDDDMNDiagram.getDMNDiagramElement(),
                                stunnerToDDExt((View<? extends DMNElement>) view));
                } else if (view.getDefinition() instanceof TextAnnotation) {
                    final TextAnnotation textAnnotation = (TextAnnotation) view.getDefinition();
                    textAnnotations.put(textAnnotation.getId().getValue(),
                                        textAnnotationConverter.dmnFromNode((Node<View<TextAnnotation>, ?>) node,
                                                                            componentWidthsConsumer));
                    if (Objects.isNull(dmnDDDMNDiagram.getDMNDiagramElement())) {
                        final JsArrayLike<JSIDiagramElement> elements = JavaScriptObject.createArray().cast();
                        dmnDDDMNDiagram.setDMNDiagramElement(elements);
                    }
                    JsUtils.add(dmnDDDMNDiagram.getDMNDiagramElement(),
                                stunnerToDDExt((View<? extends DMNElement>) view));

                    final List<JSITAssociation> associations = AssociationConverter.dmnFromWB((Node<View<TextAnnotation>, ?>) node);
                    final JSITAssociation[] aAssociations = new JSITAssociation[]{};
                    associations.toArray(aAssociations);
                    JsUtils.addAll(definitions.getArtifact(), aAssociations);
                }

                // DMNDI Edge management.
                final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
                for (Edge<?, ?> e : inEdges) {
                    if (e.getContent() instanceof ViewConnector) {
                        final ViewConnector connectionContent = (ViewConnector) e.getContent();
                        if (connectionContent.getSourceConnection().isPresent() && connectionContent.getTargetConnection().isPresent()) {
                            Point2D sourcePoint = ((Connection) connectionContent.getSourceConnection().get()).getLocation();
                            Point2D targetPoint = ((Connection) connectionContent.getTargetConnection().get()).getLocation();
                            if (sourcePoint == null) { // If the "connection source/target location is null" assume it's the centre of the shape.
                                final Node<?, ?> sourceNode = e.getSourceNode();
                                final View<?> sourceView = (View<?>) sourceNode.getContent();
                                double xSource = xOfBound(upperLeftBound(sourceView));
                                double ySource = yOfBound(upperLeftBound(sourceView));
                                if (sourceView.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) sourceView.getDefinition();
                                    xSource += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    ySource += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                sourcePoint = Point2D.create(xSource, ySource);
                            } else { // If it is non-null it is relative to the source/target shape location.
                                final Node<?, ?> sourceNode = e.getSourceNode();
                                final View<?> sourceView = (View<?>) sourceNode.getContent();
                                double xSource = xOfBound(upperLeftBound(sourceView));
                                double ySource = yOfBound(upperLeftBound(sourceView));
                                sourcePoint = Point2D.create(xSource + sourcePoint.getX(), ySource + sourcePoint.getY());
                            }
                            if (targetPoint == null) { // If the "connection source/target location is null" assume it's the centre of the shape.
                                double xTarget = xOfBound(upperLeftBound(view));
                                double yTarget = yOfBound(upperLeftBound(view));
                                if (view.getDefinition() instanceof DMNViewDefinition) {
                                    DMNViewDefinition dmnViewDefinition = (DMNViewDefinition) view.getDefinition();
                                    xTarget += dmnViewDefinition.getDimensionsSet().getWidth().getValue() / 2;
                                    yTarget += dmnViewDefinition.getDimensionsSet().getHeight().getValue() / 2;
                                }
                                targetPoint = Point2D.create(xTarget, yTarget);
                            } else { // If it is non-null it is relative to the source/target shape location.
                                final double xTarget = xOfBound(upperLeftBound(view));
                                final double yTarget = yOfBound(upperLeftBound(view));
                                targetPoint = Point2D.create(xTarget + targetPoint.getX(), yTarget + targetPoint.getY());
                            }

                            final JSIDMNEdge dmnEdge = new JSIDMNEdge();
                            // DMNDI edge elementRef is uuid of Stunner edge,
                            // with the only exception when edge contains as content a DMN Association (Association is an edge)
                            String uuid = e.getUUID();
                            if (e.getContent() instanceof View<?>) {
                                final View<?> edgeView = (View<?>) e.getContent();
                                if (edgeView.getDefinition() instanceof Association) {
                                    uuid = ((Association) edgeView.getDefinition()).getId().getValue();
                                }
                            }
                            dmnEdge.setId("dmnedge-" + uuid);
                            dmnEdge.setDmnElementRef(new QName(XMLConstants.NULL_NS_URI,
                                                               uuid,
                                                               XMLConstants.DEFAULT_NS_PREFIX));

                            JsUtils.add(dmnEdge.getWaypoint(), PointUtils.point2dToDMNDIPoint(sourcePoint));
                            for (ControlPoint cp : connectionContent.getControlPoints()) {
                                JsUtils.add(dmnEdge.getWaypoint(), PointUtils.point2dToDMNDIPoint(cp.getLocation()));
                            }
                            JsUtils.add(dmnEdge.getWaypoint(), PointUtils.point2dToDMNDIPoint(targetPoint));
                            dmnEdges.add(dmnEdge);
                        }
                    }
                }
            }
        }

        if (Objects.isNull(definitions.getDrgElement())) {
            final JsArrayLike<JSITDRGElement> elements = JavaScriptObject.createArray().cast();
            definitions.setDrgElement(elements);
        }
        nodes.values().forEach(n -> JsUtils.add(definitions.getDrgElement(), n));
        if (Objects.isNull(definitions.getArtifact())) {
            final JsArrayLike<JSITArtifact> artifacts = JavaScriptObject.createArray().cast();
            definitions.setArtifact(artifacts);
        }
        textAnnotations.values().forEach(text -> JsUtils.add(definitions.getArtifact(), text));

        // add DMNEdge last.
        final JSIDMNEdge[] aDMNEdges = new JSIDMNEdge[]{};
        dmnEdges.toArray(aDMNEdges);
        JsUtils.addAll(dmnDDDMNDiagram.getDMNDiagramElement(), aDMNEdges);

        return definitions;
    } */

    void loadImportedItemDefinitions(final Definitions definitions,
                                     final Map<JSITImport, JSITDefinitions> importDefinitions) {
        definitions.getItemDefinition().addAll(getWbImportedItemDefinitions(importDefinitions));
    }

    List<ItemDefinition> getWbImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        return dmnMarshallerImportsHelper
                .getImportedItemDefinitions(importDefinitions)
                .stream()
                .map(ItemDefinitionPropertyConverter::wbFromDMN)
                .peek(itemDefinition -> itemDefinition.setAllowOnlyVisualChange(true))
                .collect(toList());
    }

    private void ddExtAugmentStunner(final Optional<JSIDMNDiagram> dmnDDDiagram,
                                     final Node currentNode) {
        if (!dmnDDDiagram.isPresent()) {
            return;
        }

        final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagram.get());
        final JsArrayLike<JSIDiagramElement> jsiDiagramElements = JSIDMNDiagram.getDMNDiagramElement(jsiDiagram);

        final List<JSIDMNShape> drgShapes = new ArrayList<>();
        for (int i = 0; i < jsiDiagramElements.getLength(); i++) {
            final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(jsiDiagramElements.getAt(i));
            if (JSIDMNShape.instanceOf(jsiDiagramElement)) {
                drgShapes.add(Js.uncheckedCast(jsiDiagramElement));
            }
        }
        final View content = (View) currentNode.getContent();
        final Bound ulBound = upperLeftBound(content);
        final Bound lrBound = lowerRightBound(content);
        if (content.getDefinition() instanceof Decision) {
            final Decision d = (Decision) content.getDefinition();
            internalAugment(drgShapes, d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof InputData) {
            final InputData d = (InputData) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof KnowledgeSource) {
            final KnowledgeSource d = (KnowledgeSource) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof TextAnnotation) {
            final TextAnnotation d = (TextAnnotation) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet);
        } else if (content.getDefinition() instanceof DecisionService) {
            final DecisionService d = (DecisionService) content.getDefinition();
            internalAugment(drgShapes,
                            d.getId(),
                            ulBound,
                            d.getDimensionsSet(),
                            lrBound,
                            d.getBackgroundSet(),
                            d::setFontSet,
                            (dividerLineY) -> d.setDividerLineY(new DecisionServiceDividerLineY(dividerLineY - ulBound.getY())));
        }
    }

    @SuppressWarnings("unchecked")
    private void internalAugment(final List<JSIDMNShape> drgShapeStream,
                                 final Id id,
                                 final Bound ulBound,
                                 final RectangleDimensionsSet dimensionsSet,
                                 final Bound lrBound,
                                 final BackgroundSet bgset,
                                 final Consumer<FontSet> fontSetSetter) {
        internalAugment(drgShapeStream,
                        id,
                        ulBound,
                        dimensionsSet,
                        lrBound,
                        bgset,
                        fontSetSetter,
                        (line) -> {/*NOP*/});
    }

    @SuppressWarnings("unchecked")
    private void internalAugment(final List<JSIDMNShape> drgShapes,
                                 final Id id,
                                 final Bound ulBound,
                                 final RectangleDimensionsSet dimensionsSet,
                                 final Bound lrBound,
                                 final BackgroundSet bgset,
                                 final Consumer<FontSet> fontSetSetter,
                                 final Consumer<Double> decisionServiceDividerLineYSetter) {
        //Lookup JSIDMNShape corresponding to DRGElement...
        Optional<JSIDMNShape> drgShapeOpt = Optional.empty();
        for (int i = 0; i < drgShapes.size(); i++) {
            final JSIDMNShape jsiShape = Js.uncheckedCast(drgShapes.get(i));
            if (Objects.equals(id.getValue(), jsiShape.getDmnElementRef().getLocalPart())) {
                drgShapeOpt = Optional.of(jsiShape);
            }
        }
        if (!drgShapeOpt.isPresent()) {
            return;
        }

        //Augment Stunner Node with Shape data
        final JSIDMNShape drgShape = Js.uncheckedCast(drgShapeOpt.get());

        if (Objects.nonNull(ulBound)) {
            ulBound.setX(xOfShape(drgShape));
            ulBound.setY(yOfShape(drgShape));
        }
        dimensionsSet.setWidth(new Width(widthOfShape(drgShape)));
        dimensionsSet.setHeight(new Height(heightOfShape(drgShape)));
        if (Objects.nonNull(lrBound)) {
            lrBound.setX(xOfShape(drgShape) + widthOfShape(drgShape));
            lrBound.setY(yOfShape(drgShape) + heightOfShape(drgShape));
        }

        final JSIStyle drgStyle = Js.uncheckedCast(JsUtils.getUnwrappedElement(drgShape.getStyle()));
        final JSIDMNStyle dmnStyleOfDrgShape = JSIDMNStyle.instanceOf(drgStyle) ? Js.uncheckedCast(drgStyle) : null;
        if (Objects.nonNull(dmnStyleOfDrgShape)) {
            if (Objects.nonNull(dmnStyleOfDrgShape.getFillColor())) {
                bgset.setBgColour(new BgColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getFillColor())));
            }
            if (Objects.nonNull(dmnStyleOfDrgShape.getStrokeColor())) {
                bgset.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getStrokeColor())));
            }
        }

        final FontSet fontSet = new FontSet();
        if (Objects.nonNull(dmnStyleOfDrgShape)) {
            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(dmnStyleOfDrgShape));
        }
        if (Objects.nonNull(drgShape.getDMNLabel())) {
            final JSIDMNShape jsiLabel = Js.uncheckedCast(drgShape.getDMNLabel());
            final JSIDiagramElement jsiLabelStyle = Js.uncheckedCast(jsiLabel.getStyle());
            final JSIDiagramElement jsiLabelSharedStyle = Js.uncheckedCast(jsiLabel.getSharedStyle());
            if (Objects.nonNull(jsiLabelSharedStyle) && JSIDMNStyle.instanceOf(jsiLabelSharedStyle)) {
                mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((Js.uncheckedCast(jsiLabelSharedStyle))));
            }
            if (Objects.nonNull(jsiLabelStyle) && JSIDMNStyle.instanceOf(jsiLabelStyle)) {
                mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(Js.uncheckedCast(jsiLabelStyle)));
            }
        }
        fontSetSetter.accept(fontSet);

        if (Objects.nonNull(drgShape.getDMNDecisionServiceDividerLine())) {
            final JSIDMNDecisionServiceDividerLine divider = Js.uncheckedCast(drgShape.getDMNDecisionServiceDividerLine());
            final JsArrayLike<JSIPoint> dividerPoints = JSIEdge.getWaypoint(divider);
            final JSIPoint dividerY = Js.uncheckedCast(dividerPoints.getAt(0));
            decisionServiceDividerLineYSetter.accept(dividerY.getY());
        }
    }

    private static void mergeFontSet(FontSet fontSet, FontSet additional) {
        if (additional.getFontFamily() != null) {
            fontSet.setFontFamily(additional.getFontFamily());
        }
        if (additional.getFontSize() != null) {
            fontSet.setFontSize(additional.getFontSize());
        }
        if (additional.getFontColour() != null) {
            fontSet.setFontColour(additional.getFontColour());
        }
    }


}
