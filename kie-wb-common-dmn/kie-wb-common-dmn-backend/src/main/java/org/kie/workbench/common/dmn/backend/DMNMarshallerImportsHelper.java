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

package org.kie.workbench.common.dmn.backend;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelper;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DMNMarshallerImportsHelper {

    private final DMNPathsHelper pathsHelper;

    private final WorkspaceProjectService projectService;

    private final IOService ioService;

    private DMNMarshaller marshaller;

    public DMNMarshallerImportsHelper() {
        this(null, null, null);
    }

    @Inject
    public DMNMarshallerImportsHelper(final DMNPathsHelper pathsHelper,
                                      final WorkspaceProjectService projectService,
                                      final @Named("ioStrategy") IOService ioService) {
        this.pathsHelper = pathsHelper;
        this.projectService = projectService;
        this.ioService = ioService;
    }

    public void init(final DMNMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public List<DRGElement> getImportedDRGElements(final Metadata metadata,
                                                   final List<Import> imports) {

        final List<DRGElement> importedNodes = new ArrayList<>();

        if (imports.size() > 0) {
            for (final Definitions definitions : getOtherDMNDiagramsDefinitions(metadata)) {

                final Optional<Import> anImport = findImportByDefinitions(definitions, imports);
                final boolean isDiagramAnImport = anImport.isPresent();

                if (isDiagramAnImport) {
                    importedNodes.addAll(getDrgElementsWithNamespace(definitions, anImport.get()));
                }
            }
        }

        return importedNodes;
    }

    List<DRGElement> getDrgElementsWithNamespace(final Definitions definitions,
                                                 final Import anImport) {
        return definitions
                .getDrgElement()
                .stream()
                .peek(drgElementWithNamespace(anImport))
                .collect(Collectors.toList());
    }

    private Consumer<DRGElement> drgElementWithNamespace(final Import anImport) {
        return drgElement -> {

            final String namespace = anImport.getName();

            drgElement.setId(namespace + ":" + drgElement.getId());
            drgElement.setName(namespace + "." + drgElement.getName());
        };
    }

    private Optional<Import> findImportByDefinitions(final Definitions definitions,
                                                     final List<Import> imports) {
        return imports
                .stream()
                .filter(anImport -> Objects.equals(anImport.getNamespace(), definitions.getNamespace()))
                .findAny();
    }

    List<Definitions> getOtherDMNDiagramsDefinitions(final Metadata metadata) {

        final List<Path> diagramPaths = pathsHelper.getDiagramsPaths(getProject(metadata));

        return diagramPaths
                .stream()
                .filter(path -> !Objects.equals(metadata.getPath(), path))
                .map(path -> marshaller.unmarshal(loadPath(path)))
                .collect(Collectors.toList());
    }

    InputStreamReader loadPath(final Path path) {
        final byte[] bytes = ioService.readAllBytes(Paths.convert(path));
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return new InputStreamReader(inputStream);
    }

    private WorkspaceProject getProject(final Metadata metadata) {
        try {
            return projectService.resolveProject(metadata.getPath());
        } catch (final NullPointerException e) {
            // There's not project when the webapp is running on standalone mode, thus NullPointerException is raised.
            return null;
        }
    }
}
