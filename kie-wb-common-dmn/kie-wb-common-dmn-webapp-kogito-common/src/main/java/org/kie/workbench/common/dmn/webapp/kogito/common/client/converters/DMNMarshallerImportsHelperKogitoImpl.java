/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.ImportedItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.util.FileUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.ImportedItemDefinitionPropertyConverter.withNamespace;

@ApplicationScoped
@Dependent
public class DMNMarshallerImportsHelperKogitoImpl implements DMNMarshallerImportsHelperKogito {

    private final KogitoResourceContentService contentService;
    private final DMNClientDiagramServiceImpl diagramService;
    private final Promises promises;
    private final DMNDiagramUtils diagramUtils;
    private final DMNIncludedNodeFactory includedModelFactory;

    private static final Logger LOGGER = Logger.getLogger(DMNMarshallerImportsHelperKogitoImpl.class.getName());
    private static final String DMN_FILES_PATTERN = "*.dmn";

    @Inject
    public DMNMarshallerImportsHelperKogitoImpl(final KogitoResourceContentService contentService,
                                                final DMNClientDiagramServiceImpl diagramService,
                                                final Promises promises,
                                                final DMNDiagramUtils diagramUtils,
                                                final DMNIncludedNodeFactory includedModelFactory) {
        this.contentService = contentService;
        this.diagramService = diagramService;
        this.promises = promises;
        this.diagramUtils = diagramUtils;
        this.includedModelFactory = includedModelFactory;
    }

    @Override
    public Promise<Map<JSITImport, JSITDefinitions>> getImportDefinitionsAsync(final Metadata metadata,
                                                                               final List<JSITImport> imports) {
        if (!imports.isEmpty()) {
            return loadDMNDefinitions().then(otherDefinitions -> {
                final Map<JSITImport, JSITDefinitions> importDefinitions = new HashMap<>();
                for (int i = 0; i < otherDefinitions.size(); i++) {
                    final JSITDefinitions def = Js.uncheckedCast(otherDefinitions.get(i));
                    findImportByDefinitions(def, imports).ifPresent(anImport -> {
                        final JSITImport foundImported = Js.uncheckedCast(anImport);
                        importDefinitions.put(foundImported, def);
                    });
                }
                return promises.resolve(importDefinitions);
            });
        }
        return promises.resolve(new HashMap<>());
    }

    Promise<Map<String, JSITDefinitions>> loadDMNDefinitions() {
        final Map<String, JSITDefinitions> otherDefinitions = new HashMap<>();
        return contentService.getFilteredItems(DMN_FILES_PATTERN, ResourceListOptions.assetFolder()).then(list -> {
            final List<Promise<JSITDefinitions>> fileLoadPromises = new ArrayList<>();
            for (final String file : list) {
                final Promise<JSITDefinitions> promise = loadDefinitionFromFile(file, otherDefinitions);
                fileLoadPromises.add(promise);
            }
            return promises.all(fileLoadPromises.toArray(new Promise[]{})).then(v -> promises.resolve(otherDefinitions));
        });
    }

    @Override
    public void loadNodesFromModels(final List<DMNIncludedModel> includedModels,
                                    final ServiceCallback<List<DMNIncludedNode>> callback) {

        final List<DMNIncludedNode> result = new Vector<>();
        final List<Promise> fileLoadPromises = new ArrayList<>();

        loadDMNDefinitions().then(existingDefinitions -> {
            for (final DMNIncludedModel model : includedModels) {
                String filePath = "";
                for (final Map.Entry<String, JSITDefinitions> entry : existingDefinitions.entrySet()) {
                    filePath = entry.getKey();
                    final JSITDefinitions definitions = Js.uncheckedCast(entry.getValue());
                    if (model.getNamespace().equals(definitions.getNamespace())) {
                        break;
                    }
                }
                final String path = filePath;
                final Promise promise = contentService.loadFile(path).then(content -> {
                                                                               diagramService.transform(content, new ServiceCallback<Diagram>() {
                                                                                   @Override
                                                                                   public void onSuccess(final Diagram item) {
                                                                                       final Optional<DMNIncludedModel> diagramImport = getDiagramImport(item, includedModels);
                                                                                       final List<DMNIncludedNode> nodes = diagramImport
                                                                                               .map(dmn -> diagramUtils
                                                                                                       .getDRGElements(item)
                                                                                                       .stream()
                                                                                                       .map(node -> includedModelFactory.makeDMNIncludeNode(path, dmn, node))
                                                                                                       .collect(Collectors.toList()))
                                                                                               .orElse(new ArrayList<>());

                                                                                       result.addAll(nodes);
                                                                                   }

                                                                                   @Override
                                                                                   public void onError(final ClientRuntimeError error) {
                                                                                       LOGGER.log(Level.SEVERE, error.getMessage());
                                                                                   }
                                                                               });
                                                                               return promises.resolve();
                                                                           }
                );
                fileLoadPromises.add(promise);
            }

            return promises.all(fileLoadPromises.toArray(new Promise[]{})).then(p -> {
                callback.onSuccess(result);
                return promises.resolve();
            });
        });
    }

    @Override
    public void loadModels(final ServiceCallback<List<IncludedModel>> callback) {

        final List<IncludedModel> models = new Vector<>();
        contentService.getFilteredItems(DMN_FILES_PATTERN, ResourceListOptions.assetFolder()).then(items -> {
            final List<Promise> fileLoadPromises = new ArrayList<>();

            for (final String file : items) {
                final Promise<Object> fileLoad = contentService.loadFile(file).then(fileContent -> {
                    diagramService.transform(fileContent, new ServiceCallback<Diagram>() {
                        @Override
                        public void onSuccess(final Diagram item) {
                            final String modelPackage = "";
                            final Diagram<Graph, Metadata> diagram = (Diagram<Graph, Metadata>) item;
                            final String namespace = diagramUtils.getNamespace(diagram);
                            final String importType = DMNImportTypes.DMN.getDefaultNamespace();
                            final int drgElementCount = diagramUtils.getDRGElements(diagram).size();
                            final int itemDefinitionCount = diagramUtils.getDefinitions(diagram).getItemDefinition().size();
                            // We get files only from the same folder, so doesn't need relative or full path.
                            final String filename = FileUtils.getFileName(file);
                            models.add(new DMNIncludedModel(filename,
                                                            modelPackage,
                                                            filename,
                                                            namespace,
                                                            importType,
                                                            drgElementCount,
                                                            itemDefinitionCount));
                        }

                        @Override
                        public void onError(final ClientRuntimeError error) {
                            LOGGER.log(Level.SEVERE, error.getMessage());
                        }
                    });
                    return promises.resolve();
                });

                fileLoadPromises.add(fileLoad);
            }

            return promises.all(fileLoadPromises.toArray(new Promise[]{})).then(v -> promises.resolve());
        }).then(v -> {
            callback.onSuccess(models);
            return promises.resolve();
        });
    }

    private Optional<DMNIncludedModel> getDiagramImport(final Diagram<Graph, Metadata> diagram,
                                                        final List<DMNIncludedModel> includedModels) {

        final String pathNamespace = diagramUtils.getNamespace(diagram);
        return includedModels
                .stream()
                .filter(i -> Objects.equals(pathNamespace, i.getNamespace()))
                .findAny();
    }

    Promise loadDefinitionFromFile(final String file,
                                   final Map<String, JSITDefinitions> otherDefinitions) {
        return contentService.loadFile(file).then((IThenable.ThenOnFulfilledCallbackFn<String, Void>) xml -> {
            final ServiceCallback<Object> callback = getCallback(file, otherDefinitions);
            diagramService.getDefinitions(xml, callback);
            return promises.resolve();
        });
    }

    private ServiceCallback<Object> getCallback(final String filePath,
                                                final Map<String, JSITDefinitions> otherDefinitions) {
        return new ServiceCallback<Object>() {
            @Override
            public void onSuccess(final Object item) {
                final JSITDefinitions def = Js.uncheckedCast(item);
                otherDefinitions.put(filePath, def);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                LOGGER.log(Level.SEVERE, error.getMessage());
            }
        };
    }

    private Optional<JSITImport> findImportByDefinitions(final JSITDefinitions definitions,
                                                         final List<JSITImport> imports) {
        for (int i = 0; i < imports.size(); i++) {
            final JSITImport anImport = Js.uncheckedCast(imports.get(i));
            if (Objects.equals(anImport.getNamespace(), definitions.getNamespace())) {
                return Optional.of(anImport);
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<JSITImport, JSITDefinitions> getImportDefinitions(final Metadata metadata,
                                                                 final List<JSITImport> jsitImports) {
        throw new UnsupportedOperationException("This implementation does not support sync calls. Please, use getImportDefinitionsAsync.");
    }

    @Override
    public Map<JSITImport, PMMLDocumentMetadata> getPMMLDocuments(final Metadata metadata,
                                                                  final List<JSITImport> imports) {
        return Collections.emptyMap();
    }

    @Override
    public Map<JSITImport, String> getImportXML(final Metadata metadata,
                                                final List<JSITImport> imports) {
        return Collections.emptyMap();
    }

    @Override
    public List<JSITDRGElement> getImportedDRGElements(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        final List<JSITDRGElement> importedNodes = new ArrayList<>();
        for (final Map.Entry<JSITImport, JSITDefinitions> entry : importDefinitions.entrySet()) {
            final JSITImport anImport = Js.uncheckedCast(entry.getKey());
            final JSITDefinitions definitions = Js.uncheckedCast(entry.getValue());
            importedNodes.addAll(getDrgElementsWithNamespace(definitions, anImport));
        }
        return importedNodes;
    }

    private List<JSITDRGElement> getDrgElementsWithNamespace(final JSITDefinitions definitions,
                                                             final JSITImport anImport) {
        final List<JSITDRGElement> result = new ArrayList<>();
        final List<JSITDRGElement> drgElements = definitions.getDrgElement();
        for (int i = 0; i < drgElements.size(); i++) {
            final JSITDRGElement drgElement = Js.uncheckedCast(drgElements.get(i));
            final JSITDRGElement element = Js.uncheckedCast(drgElementWithNamespace(drgElement, anImport));
            result.add(element);
        }
        return result;
    }

    private JSITDRGElement drgElementWithNamespace(final JSITDRGElement drgElement,
                                                   final JSITImport anImport) {
        final String namespace = anImport.getName();
        final QName qname = QName.valueOf("Namespace");
        final Map<QName, String> map = JSITDMNElement.getOtherAttributesMap(drgElement);
        map.put(qname, anImport.getNamespace());
        drgElement.setOtherAttributes(map);
        drgElement.setName(namespace + "." + drgElement.getName());
        updateInformationItem(namespace, drgElement);

        return drgElement;
    }

    private void updateInformationItem(final String namespace,
                                       final JSITDRGElement drgElement) {

        getInformationItem(drgElement).ifPresent(informationItem -> {

            final JSITInformationItem tInformationItem = new JSITInformationItem();
            final String typeRef = informationItem.getTypeRef();

            if (!StringUtils.isEmpty(typeRef) && !isBuiltInType(typeRef)) {
                tInformationItem.setTypeRef(namespace + "." + typeRef);
                setInformationItem(drgElement, tInformationItem);
            }
        });
    }

    private void setInformationItem(final JSITDRGElement drgElement,
                                    final JSITInformationItem informationItem) {
        if (JSITDecision.instanceOf(drgElement)) {
            final JSITDecision decision = Js.uncheckedCast(drgElement);
            decision.setVariable(informationItem);
        } else if (JSITInputData.instanceOf(drgElement)) {
            final JSITInputData inputData = Js.uncheckedCast(drgElement);
            inputData.setVariable(informationItem);
        } else if (JSITInvocable.instanceOf(drgElement)) {
            final JSITInvocable invocable = Js.uncheckedCast(drgElement);
            invocable.setVariable(informationItem);
        }
    }

    private Optional<JSITInformationItem> getInformationItem(final JSITDRGElement drgElement) {
        final JSITInformationItem variable;
        if (JSITDecision.instanceOf(drgElement)) {
            final JSITDecision decision = Js.uncheckedCast(drgElement);
            variable = Js.uncheckedCast(decision.getVariable());
        } else if (JSITInputData.instanceOf(drgElement)) {
            final JSITInputData inputData = Js.uncheckedCast(drgElement);
            variable = Js.uncheckedCast(inputData.getVariable());
        } else if (JSITInvocable.instanceOf(drgElement)) {
            final JSITInvocable invocable = Js.uncheckedCast(drgElement);
            variable = Js.uncheckedCast(invocable.getVariable());
        } else {
            return Optional.empty();
        }
        return Optional.of(variable);
    }

    @Override
    public List<JSITItemDefinition> getImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        final List<JSITItemDefinition> itemDefinitions = new ArrayList<>();
        for (final Map.Entry<JSITImport, JSITDefinitions> entry : importDefinitions.entrySet()) {
            final JSITImport anImport = Js.uncheckedCast(entry.getKey());
            final JSITDefinitions definitions = Js.uncheckedCast(entry.getValue());
            final List<JSITItemDefinition> items = getItemDefinitionsWithNamespace(definitions, anImport);
            itemDefinitions.addAll(items);
        }

        return itemDefinitions;
    }

    List<JSITItemDefinition> getItemDefinitionsWithNamespace(final JSITDefinitions definitions,
                                                             final JSITImport anImport) {

        final List<JSITItemDefinition> itemDefinitions = definitions.getItemDefinition();
        final String prefix = anImport.getName();
        final List<JSITItemDefinition> result = new ArrayList<>();

        for (int i = 0; i < itemDefinitions.size(); i++) {
            final JSITItemDefinition itemDefinition = Js.uncheckedCast(itemDefinitions.get(i));
            final JSITItemDefinition item = Js.uncheckedCast(withNamespace(itemDefinition, prefix));
            result.add(item);
        }
        return result;
    }

    @Override
    public void getImportedItemDefinitionsByNamespaceAsync(final String modelName,
                                                           final String namespace,
                                                           final ServiceCallback<List<ItemDefinition>> callback) {
        loadDMNDefinitions().then(definitions -> {
            final List<ItemDefinition> result = new ArrayList<>();
            for (final Map.Entry<String, JSITDefinitions> entry : definitions.entrySet()) {
                final JSITDefinitions definition = Js.uncheckedCast(entry.getValue());
                if (Objects.equals(definition.getNamespace(), namespace)) {
                    final List<JSITItemDefinition> items = definition.getItemDefinition();
                    for (int j = 0; j < items.size(); j++) {
                        final JSITItemDefinition jsitItemDefinition = Js.uncheckedCast(items.get(j));
                        final ItemDefinition converted = ImportedItemDefinitionPropertyConverter.wbFromDMN(jsitItemDefinition, modelName);
                        result.add(converted);
                    }
                }
            }

            callback.onSuccess(result);
            return promises.resolve(result);
        });
    }

    @Override
    public List<JSITItemDefinition> getImportedItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                                          final String modelName,
                                                                          final String namespace) {
        throw new UnsupportedOperationException("This implementation does not support sync calls. Please, use getImportedItemDefinitionsByNamespaceAsync.");
    }

    @Override
    public Path getDMNModelPath(final Metadata metadata,
                                final String modelNamespace,
                                final String modelName) {
        throw new UnsupportedOperationException("Imports are not supported in the kogito-based editors.");
    }

    @Override
    public Optional<InputStream> loadPath(final Path path) {
        return Optional.empty();
    }
}
