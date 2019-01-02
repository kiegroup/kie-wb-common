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

package org.kie.workbench.common.stunner.bpmn.client.documentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.bpmn.client.components.palette.BPMNCategoryDefinitionProvider;
import org.kie.workbench.common.stunner.bpmn.client.documentation.template.BPMNDocumentationTemplateSource;
import org.kie.workbench.common.stunner.bpmn.client.shape.factory.BPMNShapeFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariableSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.documentation.BPMNDocumentationService;
import org.kie.workbench.common.stunner.bpmn.documentation.model.BPMNDocumentation;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.Element;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.ElementDetails;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.ElementTotal;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.DataTotal;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.General;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.ProcessOverview;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.template.mustache.ClientMustacheTemplateRenderer;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class ClientBPMNDocumentationService implements BPMNDocumentationService {

    private static final Map<String, Boolean> ignoredPropertiesIds = buildIgnoredPropertiesIds();
    public static final int ICON_HEIGHT = 20;
    public static final int ICON_WIDTH = 20;

    private final ClientMustacheTemplateRenderer mustacheTemplateRenderer;
    private final DefinitionManager definitionManager;
    private final DefinitionUtils definitionUtils;
    private final BPMNShapeFactory shapeFactory;
    private final CanvasFileExport canvasFileExport;
    private final SessionManager sessionManager;
    private final BPMNCategoryDefinitionProvider categoryDefinitionProvider;
    private final DOMGlyphRenderers glyphRenderer;
    private final ClientTranslationService translationService;

    @Inject
    public ClientBPMNDocumentationService(final ClientMustacheTemplateRenderer mustacheTemplateRenderer,
                                          final DefinitionManager definitionManager,
                                          final DefinitionUtils definitionUtils,
                                          final BPMNShapeFactory shapeFactory,
                                          final CanvasFileExport canvasFileExport,
                                          final SessionManager sessionManager,
                                          final BPMNCategoryDefinitionProvider categoryDefinitionProvider,
                                          final DOMGlyphRenderers glyphRenderer,
                                          final ClientTranslationService translationService) {
        this.mustacheTemplateRenderer = mustacheTemplateRenderer;
        this.definitionManager = definitionManager;
        this.definitionUtils = definitionUtils;
        this.shapeFactory = shapeFactory;
        this.canvasFileExport = canvasFileExport;
        this.sessionManager = sessionManager;
        this.categoryDefinitionProvider = categoryDefinitionProvider;
        this.glyphRenderer = glyphRenderer;
        this.translationService = translationService;
    }

    @Override
    public BPMNDocumentation processDocumentation(Diagram<Graph, Metadata> diagram) {
        final Graph<?, Node> graph = diagram.getGraph();

        final Optional<BPMNDiagramImpl> diagramModel = StreamSupport.stream(graph.nodes().spliterator(), false)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .filter(d -> d instanceof BPMNDiagramImpl)
                .map(d -> (BPMNDiagramImpl) d)
                .findFirst();

        return BPMNDocumentation.create(getProcessOverview(diagramModel),
                                        getElementsDetails(graph),
                                        getDiagramImage(Optional.ofNullable(sessionManager.getCurrentSession())
                                                                .map(s -> ((ClientSession) s).getCanvasHandler())
                                                                .filter(c -> c instanceof AbstractCanvasHandler)
                                                                .map(c -> (AbstractCanvasHandler) c)));
    }

    private ProcessOverview getProcessOverview(Optional<BPMNDiagramImpl> diagramModel) {
        return ProcessOverview.create(getGeneral(diagramModel), getDataTotal(diagramModel));
    }

    private General getGeneral(Optional<BPMNDiagramImpl> diagramModel) {
        final Optional<DiagramSet> diagramSet = diagramModel
                .map(BPMNDiagram::getDiagramSet);

        final String documentation = diagramSet
                .map(DiagramSet::getDocumentation)
                .map(Documentation::getValue)
                .orElse(null);

        final String version = diagramSet
                .map(DiagramSet::getVersion)
                .map(Version::getValue)
                .orElse(null);

        final String pkg = diagramSet
                .map(DiagramSet::getPackageProperty)
                .map(Package::getValue)
                .orElse(null);

        final String adhoc = diagramSet
                .map(DiagramSet::getAdHoc)
                .map(AdHoc::getValue)
                .map(String::valueOf)
                .orElse(null);

        final String executable = diagramSet
                .map(DiagramSet::getExecutable)
                .map(Executable::getValue)
                .map(String::valueOf)
                .orElse(null);

        final String id = diagramSet
                .map(DiagramSet::getId)
                .map(Id::getValue)
                .orElse(null);

        final String name = diagramSet
                .map(DiagramSet::getName)
                .map(Name::getValue)
                .orElse(null);

        return new General.Builder()
                .id(id)
                .name(name)
                .isAdhoc(adhoc)
                .isExecutable(executable)
                .documentation(documentation)
                .version(version)
                .pkg(pkg)
                .build();
    }

    private DataTotal getDataTotal(Optional<BPMNDiagramImpl> diagramModel) {
        final Optional<ProcessVariables> processVariables =
                diagramModel.map(BPMNDiagramImpl::getProcessData).map(ProcessData::getProcessVariables);
        final Map<String, String> variablesMap =
                processVariables.map(ProcessVariables::getValue).map(ProcessVariableSerializer::deserialize).orElse(Collections.emptyMap());
        return DataTotal.create(variablesMap.size(), variablesMap.size(), variablesMap);
    }

    private ElementDetails getElementsDetails(final Graph<?, Node> graph) {
        final List<ElementTotal> elementsTotals = StreamSupport.stream(graph.nodes().spliterator(), false)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .filter(e -> !(e instanceof BPMNDiagram))
                .map(def -> Element.create(getElementName(def),
                                           getElementCategory(def),
                                           getElementTitle(def),
                                           getDefinitionIcon(def),
                                           getElementProperties(def))
                )
                .collect(Collectors.groupingBy(Element::getType))
                .entrySet()
                .stream()
                .map(entry -> ElementTotal.create(entry.getValue(),
                                                  getCategoryName(entry.getKey()),
                                                  getCategoryIcon(entry.getKey())))
                .collect(Collectors.toList());

        return ElementDetails.create(elementsTotals);
    }

    private String getElementTitle(Object def) {
        return definitionUtils.getTitle(definitionManager.adapters().forDefinition().getId(def));
    }

    private String getElementName(Object def) {
        return definitionUtils.getName(def);
    }

    private String getElementCategory(Object def) {
        return definitionManager.adapters().forDefinition().getCategory(def);
    }

    private String getCategoryName(String category) {
        return translationService.getValue(BPMNCategories.class.getName() + "." + category);
    }

    @Override
    public String getDocumentationTemplate(Diagram<Graph, Metadata> diagram) {
        final BPMNDocumentationTemplateSource source = GWT.create(BPMNDocumentationTemplateSource.class);
        return source.documentationTemplate().getText();
    }

    @Override
    public String buildDocumentation(String template, BPMNDocumentation diagramDocumentation) {
        return mustacheTemplateRenderer.render(template, diagramDocumentation);
    }

    public String buildDocumentation(Diagram<Graph, Metadata> diagram) {

        //todo:tiago

        return "";
    }

    private String getCategoryIcon(String category) {
        return Optional.ofNullable(categoryDefinitionProvider.glyphProvider().apply(category))
                .map(glyph -> glyphRenderer.render(glyph, ICON_WIDTH, ICON_HEIGHT))
                .map(IsElement::getElement)
                .map(HTMLElement::getInnerHTML)
                .orElse("");
    }

    private String getDefinitionIcon(Object definition) {
        final String id = definitionManager.adapters().forDefinition().getId(definition);
        final Glyph glyph = shapeFactory.getGlyph(id);
        return Optional.ofNullable(glyph)
                .filter(glyphImg -> glyphImg instanceof ImageStripGlyph)
                .map(glyphImg -> (ImageStripGlyph) glyphImg)
                .map(glyphImg -> glyphRenderer.render(glyphImg, ICON_WIDTH, ICON_HEIGHT))
                .map(IsElement::getElement)
                .map(HTMLElement::getInnerHTML)
                .orElse("");
    }

    private Map<String, String> getElementProperties(Object definition) {
        final PropertyAdapter<Object, Object> propertyAdapter = definitionManager.adapters().forProperty();
        final Set<?> properties = definitionManager.adapters().forDefinition().getProperties(definition);
        return properties.stream()
                .filter(prop -> !ignoredPropertiesIds.containsKey(propertyAdapter.getId(prop)))
                .collect(Collectors.toMap(propertyAdapter::getCaption,
                                          prop -> String.valueOf(propertyAdapter.getValue(prop))));
    }

    private String getDiagramImage(Optional<AbstractCanvasHandler> canvasHandler) {
        return canvasHandler.map(canvasFileExport::exportToSvg).orElse(null);
    }

    private static Map<String, Boolean> buildIgnoredPropertiesIds() {
        return Stream.of(BindableAdapterUtils.getPropertyId(FontColor.class),
                         BindableAdapterUtils.getPropertyId(FontBorderColor.class),
                         BindableAdapterUtils.getPropertyId(FontBorderSize.class),
                         BindableAdapterUtils.getPropertyId(FontFamily.class),
                         BindableAdapterUtils.getPropertyId(FontSize.class),
                         BindableAdapterUtils.getPropertyId(BgColor.class),
                         BindableAdapterUtils.getPropertyId(BorderColor.class),
                         BindableAdapterUtils.getPropertyId(BorderSize.class),
                         BindableAdapterUtils.getPropertyId(Radius.class),
                         BindableAdapterUtils.getPropertyId(Height.class),
                         BindableAdapterUtils.getPropertyId(Width.class),
                         BindableAdapterUtils.getPropertyId(ProcessVariables.class),
                         BindableAdapterUtils.getPropertyId(AssignmentsInfo.class))
                .collect(Collectors.toMap(id -> id, id -> Boolean.TRUE));
    }
}
