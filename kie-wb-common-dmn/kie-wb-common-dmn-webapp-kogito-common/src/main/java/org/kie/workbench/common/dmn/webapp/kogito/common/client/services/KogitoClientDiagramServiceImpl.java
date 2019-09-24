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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNMarshallerKogitoMarshaller;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNMarshallerKogitoUnmarshaller;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.kogito.api.editor.DiagramType;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.kie.workbench.common.stunner.kogito.api.service.KogitoDiagramService;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.commons.uuid.UUID;

import static elemental2.core.Global.JSON;

@ApplicationScoped
public class KogitoClientDiagramServiceImpl implements KogitoClientDiagramService {

    private static final String DIAGRAMS_PATH = "diagrams";

    //This path is needed by DiagramsNavigatorImpl's use of AbstractClientDiagramService.lookup(..) to retrieve a list of diagrams
    private static final String ROOT = "default://master@system/stunner/" + DIAGRAMS_PATH;

    private DMNMarshallerKogitoUnmarshaller dmnMarshallerKogitoUnmarshaller;
    private DMNMarshallerKogitoMarshaller dmnMarshallerKogitoMarshaller;
    private Caller<KogitoDiagramService> kogitoDiagramServiceCaller;
    private FactoryManager factoryManager;
    private DefinitionManager definitionManager;
    private DMNDiagramFactory dmnDiagramFactory;
    private Promises promises;
    // TODO {gcardosi} only for developing/debugging purpose - to be removed
    private JSITDefinitions unmarshalledDefinitions;

    public KogitoClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public KogitoClientDiagramServiceImpl(final DMNMarshallerKogitoUnmarshaller dmnMarshallerKogitoUnmarshaller,
                                          final DMNMarshallerKogitoMarshaller dmnMarshallerKogitoMarshaller,
                                          final Caller<KogitoDiagramService> kogitoDiagramServiceCaller,
                                          final FactoryManager factoryManager,
                                          final DefinitionManager definitionManager,
                                          final DMNDiagramFactory dmnDiagramFactory,
                                          final Promises promises) {
        this.dmnMarshallerKogitoUnmarshaller = dmnMarshallerKogitoUnmarshaller;
        this.dmnMarshallerKogitoMarshaller = dmnMarshallerKogitoMarshaller;
        this.kogitoDiagramServiceCaller = kogitoDiagramServiceCaller;
        this.factoryManager = factoryManager;
        this.definitionManager = definitionManager;
        this.dmnDiagramFactory = dmnDiagramFactory;
        this.promises = promises;
    }

    //Kogito requirements

    @Override
    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            doNewDiagram(callback);
        } else {
            doTransformation(xml, callback);
        }
    }

    private void doNewDiagram(final ServiceCallback<Diagram> callback) {
        final String title = UUID.uuid();
        final Metadata metadata = buildMetadataInstance();
        metadata.setTitle(title);

        try {
            final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
            final Diagram diagram = factoryManager.newDiagram(title, defSetId, metadata);
            updateClientShapeSetId(diagram);

            callback.onSuccess(diagram);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Metadata buildMetadataInstance() {
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setRoot(PathFactory.newPath(".", ROOT))
                .setShapeSetId(shapeSetId)
                .build();
    }

    private void updateClientShapeSetId(final Diagram diagram) {
        if (Objects.nonNull(diagram)) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
                metadata.setShapeSetId(shapeSetId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doTransformation(final String xml,
                                  final ServiceCallback<Diagram> callback) {
        final Metadata metadata = buildMetadataInstance();

        try {

            final DMN12UnmarshallCallback jsCallback = dmn12 -> {
                final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
                unmarshalledDefinitions = definitions;
                final Graph graph = dmnMarshallerKogitoUnmarshaller.unmarshall(metadata, definitions);
                final Node<Definition<DMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) graph, DMNDiagram.class);
                final String title = diagramNode.getContent().getDefinition().getDefinitions().getName().getValue();
                metadata.setTitle(title);

                final Diagram diagram = dmnDiagramFactory.build(title, metadata, graph);
                updateClientShapeSetId(diagram);

                callback.onSuccess(diagram);
            };
            //TODO {gcardosi} retrieve correct "xmlns" namespace
            MainJs.unmarshall(xml, "UNKNOWN", jsCallback);
        } catch (Exception e) {
            GWT.log(e.getMessage());
            callback.onError(new ClientRuntimeError(new DiagramParsingException(metadata, xml)));
        }
    }

    @Override
    public Promise<String> transform(final KogitoDiagramResourceImpl resource) {
        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
            return promises.promisify(kogitoDiagramServiceCaller,
                                      s -> {
                                          resource.projectDiagram().ifPresent(diagram -> testClientSideMarshaller(diagram.getGraph()));
                                          return s.transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present")));
                                      });
        }
        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
    }

    @SuppressWarnings("unchecked")
    private void testClientSideMarshaller(final Graph graph) {
        if (Objects.isNull(graph)) {
            return;
        }

        final DMN12MarshallCallback jsCallback = xml -> {
            String breakpoint = xml;
            //TODO {gcardosi} marshalled xml does not start with "xml" tag: for the moment being manually add it
            if (!breakpoint.startsWith("<?xml version=\"1.0\" ?>")) {
                breakpoint = "<?xml version=\"1.0\" ?>\r\n" + breakpoint;
            }
            GWT.log(breakpoint);
        };
        try {
            final JSITDefinitions jsitDefinitions = dmnMarshallerKogitoMarshaller.marshall(graph);
            GWT.log("**************WARNING********************");
            GWT.log("Instantiating dmn12 because it is null");
            org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName jsiName = JSITDefinitions.getJSIName();
            jsiName.setPrefix("dmn");
            jsiName.setLocalPart("definitions");
            String key = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getLocalPart();
            jsiName.setKey(key);
            String string = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getPrefix() + ":" + jsiName.getLocalPart();
            jsiName.setString(string);
            if (!Objects.isNull(unmarshalledDefinitions)) {
                compareJSITDefinitions(unmarshalledDefinitions, jsitDefinitions);
            }
            final DMN12 dmn12 = Js.uncheckedCast(JsUtils.newWrappedInstance());
            JsUtils.setNameOnWrapped(dmn12, jsiName);
            JsUtils.setValueOnWrapped(dmn12, jsitDefinitions);
            MainJs.marshall(dmn12, jsitDefinitions.getNamespace(), jsCallback);
        } catch (Exception e) {
            GWT.log(e.getMessage(), e);
        }
    }

    private void compareJSITDefinitions(JSITDefinitions original, JSITDefinitions marshalled) {
        JSONValue originalJSONValue = getJSONObject(JSON.stringify(original));
        JSONValue marshalledJSONValue = getJSONObject(JSON.stringify(marshalled));
        if (checkNotNull(originalJSONValue, marshalledJSONValue)) {
            compareJSONValue(originalJSONValue, marshalledJSONValue);
        } else {
            GWT.log("**************WARNING********************");
            GWT.log(String.format("originalJSONValue is not null ? %1$b", originalJSONValue));
            GWT.log(String.format("marshalledJSONValue is not null ? %1$b", marshalledJSONValue));
        }
    }

    private void compareJSONValue(JSONValue original, JSONValue marshalled) {
        JSONObject originalJSONObject = original.isObject();
        JSONObject marshalledJSONObject = marshalled.isObject();
        JSONArray originalJSONArray = original.isArray();
        JSONArray marshalledJSONArray = marshalled.isArray();
        if (checkNotNull(originalJSONObject, originalJSONObject)) {
            compareJSONObject(originalJSONObject, marshalledJSONObject);
        } else if (checkNotNull(originalJSONArray, marshalledJSONArray)) {
            compareJSONArray(originalJSONArray, marshalledJSONArray);
        } else if (!original.equals(marshalled)) {
            GWT.log("**************WARNING********************");
            GWT.log(String.format("original expected %1$s", original));
            GWT.log(String.format("marshalled retrieved %1$s", marshalled));
        }
    }

    private void compareJSONObject(JSONObject original, JSONObject marshalled) {
        final Set<String> originalKeys = original.keySet();
        final Set<String> marshalledKeys = marshalled.keySet();
        if (originalKeys.size() != marshalledKeys.size()) {
            GWT.log("**************WARNING********************");
            GWT.log(String.format("original keys expected %1$d", originalKeys.size()));
            GWT.log(String.format("marshalled keys retrieved %1$d", marshalledKeys.size()));
        }
        for (String originalKey : originalKeys) {
            if (!marshalledKeys.contains(originalKey)) {
                GWT.log("**************WARNING********************");
                GWT.log(String.format("original key %1$s missing in marshalled %2$s", originalKey, marshalled));
            } else {
                compareJSONObjectKey(original, marshalled, originalKey);
            }
        }
        for (String marshalledKey : marshalledKeys) {
            if (!originalKeys.contains(marshalledKey)) {
                GWT.log("**************WARNING********************");
                GWT.log(String.format("marshalled key %1$s not expected in %2$s", marshalledKey, original));
            }
        }
    }

    private void compareJSONObjectKey(JSONObject original, JSONObject marshalled, String key) {
        final JSONValue originalJSONValue = original.get(key);
        final JSONValue marshalledJSONValue = marshalled.get(key);
        if (checkNotNull(originalJSONValue, marshalledJSONValue)) {
            compareJSONValue(originalJSONValue, marshalledJSONValue);
        } else {
            GWT.log("**************WARNING********************");
            GWT.log(String.format("original %1$s : %2$s is not null ? %3$b", original, key, originalJSONValue));
            GWT.log(String.format("marshalled %1$s : %2$s is not null ? %3$b", marshalled, key, marshalledJSONValue));
        }
    }

    private void compareJSONArray(JSONArray original, JSONArray marshalled) {
        if (original.size() != marshalled.size()) {
            GWT.log("**************WARNING********************");
            GWT.log(String.format("original size expected %1$d", original.size()));
            GWT.log(String.format("marshalled size retrieved %1$d", marshalled.size()));
        }
        int limit = Math.min(original.size(), marshalled.size());
        for (int i = 0; i < limit; i++) {
            compareJSONValue(original.get(i), marshalled.get(i));
        }
    }

    private boolean checkNotNull(JSONValue original, JSONValue marshalled) {
        return !Objects.isNull(original) && !Objects.isNull(marshalled);
    }

    private JSONValue getJSONValue(String jsonString) {
        try {
            return JSONParser.parseStrict(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    private JSONObject getJSONObject(String jsonString) {
        try {
            return getJSONValue(jsonString).isObject();
        } catch (Exception e) {
            return null;
        }
    }
}
