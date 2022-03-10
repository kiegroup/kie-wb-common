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

package org.kie.workbench.common.dmn.backend.validation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.builder.Message;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.validation.DMNDomainValidator;
import org.kie.workbench.common.dmn.backend.DMNMarshallerStandalone;
import org.kie.workbench.common.dmn.backend.common.DMNIOHelper;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelperStandalone;
import org.kie.workbench.common.dmn.backend.definition.v1_1.ImportConverter;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.Paths;

@Service
@ApplicationScoped
public class DMNDomainValidatorImpl implements DMNDomainValidator {

    static final String DEFAULT_UUID = "uuid";

    private DMNMarshallerStandalone dmnMarshaller;
    private DMNDiagramUtils dmnDiagramUtils;
    private DMNMarshallerImportsHelperStandalone importsHelper;
    private final DMNIOHelper dmnIOHelper;
    private final WorkspaceProjectService workspaceProjectService;
    private final BuildHelper buildHelper;

    @Inject
    public DMNDomainValidatorImpl(final DMNMarshallerStandalone dmnMarshaller,
                                  final DMNDiagramUtils dmnDiagramUtils,
                                  final DMNMarshallerImportsHelperStandalone importsHelper,
                                  final DMNIOHelper dmnIOHelper,
                                  final WorkspaceProjectService workspaceProjectService,
                                  final BuildHelper buildHelper) {
        this.dmnMarshaller = dmnMarshaller;
        this.dmnDiagramUtils = dmnDiagramUtils;
        this.importsHelper = importsHelper;
        this.dmnIOHelper = dmnIOHelper;
        this.workspaceProjectService = workspaceProjectService;
        this.buildHelper = buildHelper;
    }

    @Override
    public String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(final Diagram diagram,
                         final Consumer<Collection<DomainViolation>> resultConsumer) {

        // The Definitions contained within the diagram do not contain DRGElements therefore marshall
        // the diagram to XML that then builds a fully enriched representation of the DMN model.
        final String diagramXml = dmnMarshaller.marshall(diagram);
        final Collection<DomainViolation> messages = validate(diagram, diagramXml);

        resultConsumer.accept(messages);
    }

    @Override
    public Collection<DomainViolation> validate(final Diagram diagram,
                                                final String diagramXml) {
        final List<Reader> dmnXMLReaders = new ArrayList<>();
        try {
            dmnXMLReaders.add(getStringReader(diagramXml));

            // Load Readers for all other imported DMN models.
            final Definitions uiDefinitions = dmnDiagramUtils.getDefinitions(diagram);
            final List<Import> uiImports = uiDefinitions.getImport();
            final List<org.kie.dmn.model.api.Import> dmnImports = uiImports.stream().map(ImportConverter::dmnFromWb).collect(Collectors.toList());
            final Metadata metadata = diagram.getMetadata();

            final Map<org.kie.dmn.model.api.Import, String> importedDiagramsXML = importsHelper.getImportXML(metadata, dmnImports);
            importedDiagramsXML.values().forEach(importedDiagramXML -> dmnXMLReaders.add(getStringReader(importedDiagramXML)));

            final Reader[] aDMNXMLReaders = new Reader[]{};

            final ClassLoader classLoader = getClassLoader(diagram);

            final DMNValidator dmnValidator = getDmnValidator(classLoader);

            final List<DMNMessage> messages = dmnValidator
                    .validateUsing(DMNValidator.Validation.VALIDATE_MODEL,
                                   DMNValidator.Validation.VALIDATE_COMPILATION,
                                   DMNValidator.Validation.ANALYZE_DECISION_TABLE)
                    .usingImports(getValidatorImportReaderResolver(metadata))
                    .theseModels(dmnXMLReaders.toArray(aDMNXMLReaders));

            return convert(messages);
        } finally {
            dmnXMLReaders.forEach(reader -> {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    //Swallow. The Reader is already closed.
                }
            });
        }
    }

    DMNValidator getDmnValidator(final ClassLoader classLoader) {
        return DMNValidatorFactory.newValidator(classLoader, Collections.emptyList());
    }

    ClassLoader getClassLoader(final Diagram diagram) {
        final Path path = diagram.getMetadata().getPath();
        final WorkspaceProject project = workspaceProjectService.resolveProject(path);
        final Module module = project.getMainModule();
        final BuildHelper.BuildResult result = buildHelper.build(module);
        final ClassLoader classLoader = ((InternalKieModule) result.getBuilder().getKieModuleIgnoringErrors()).getModuleClassLoader();
        return classLoader;
    }

    DMNValidator.ValidatorBuilder.ValidatorImportReaderResolver getValidatorImportReaderResolver(final Metadata metadata) {

        return (modelNamespace, modelName, locationURI) -> {

            final Path modelPath = importsHelper.getDMNModelPath(metadata, modelNamespace, modelName);
            final URI pmmlURI = getPMMLURI(modelPath, locationURI);
            final String pmmlXML = importsHelper.loadPath(getPath(pmmlURI)).map(dmnIOHelper::isAsString).orElse("");

            return getStringReader(pmmlXML);
        };
    }

    URI getPMMLURI(final Path modelPath,
                   final String pmmlRelativeURI) {
        return URI
                .create(modelPath.toURI())
                .resolve(pmmlRelativeURI);
    }

    StringReader getStringReader(final String xml) {
        return new StringReader(xml);
    }

    private Collection<DomainViolation> convert(final List<DMNMessage> messages) {
        return messages.stream().map(this::convert).collect(Collectors.toList());
    }

    private DomainViolation convert(final DMNMessage message) {
        return new MarshallingMessage.MarshallingMessageBuilder()
                .elementUUID(getDMNElementUUID(message.getSourceReference()))
                .type(convert(message.getLevel()))
                .message(message.getMessage())
                .build();
    }

    private String getDMNElementUUID(final Object source) {
        if (source instanceof DMNElement) {
            final DMNElement element = ((DMNElement) source);
            if (Objects.isNull(element.getId())) {
                return getDMNElementUUID(element.getParent());
            } else {
                return element.getId();
            }
        }
        return DEFAULT_UUID;
    }

    private Violation.Type convert(final Message.Level level) {
        switch (level) {
            case ERROR:
                return Violation.Type.ERROR;
            case WARNING:
                return Violation.Type.WARNING;
            default:
                return Violation.Type.INFO;
        }
    }

    Path getPath(final URI uri) {
        return convertPath(Paths.get(uri));
    }

    Path convertPath(final org.uberfire.java.nio.file.Path path) {
        return org.uberfire.backend.server.util.Paths.convert(path);
    }
}
