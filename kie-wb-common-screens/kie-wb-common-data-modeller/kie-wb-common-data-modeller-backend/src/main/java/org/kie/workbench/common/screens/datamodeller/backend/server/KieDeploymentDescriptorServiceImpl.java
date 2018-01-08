/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datamodeller.model.kiedeployment.KieDeploymentDescriptorContent;
import org.kie.workbench.common.screens.datamodeller.service.KieDeploymentDescriptorService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class KieDeploymentDescriptorServiceImpl
        extends KieService<KieDeploymentDescriptorContent>
        implements KieDeploymentDescriptorService {

    private final IOService ioService;
    private final CommentedOptionFactory optionsFactory;

    @Inject
    public KieDeploymentDescriptorServiceImpl(final CommentedOptionFactory optionsFactory,
                                              final @Named("ioStrategy") IOService ioService) {

        this.optionsFactory = optionsFactory;
        this.ioService = ioService;
    }

    @Override
    public KieDeploymentDescriptorContent load(final Path path) {
        if (ioService.notExists(Paths.convert(path))) {
            final KieDeploymentDescriptorContent kieDeploymentDescriptor = new KieDeploymentDescriptorContent();

            kieDeploymentDescriptor.setMarshallingStrategies(new ArrayList<>());
            kieDeploymentDescriptor.setEventListeners(new ArrayList<>());
            kieDeploymentDescriptor.setGlobals(new ArrayList<>());
            kieDeploymentDescriptor.setRequiredRoles(new ArrayList<>());

            kieDeploymentDescriptor.setRuntimeStrategy("Runtime strategy test");
            kieDeploymentDescriptor.setPersistenceUnitName("Persistence unit name test");
            kieDeploymentDescriptor.setPersistenceMode("Persistence mode test");
            kieDeploymentDescriptor.setAuditPersistenceUnitName("Audit persistence unit name test");
            kieDeploymentDescriptor.setAuditMode("Audit mode test");

            return kieDeploymentDescriptor;
        } else {
            return null;
        }
    }

    @Override
    protected KieDeploymentDescriptorContent constructContent(final Path path,
                                                              final Overview overview) {

        return load(path);
    }

    @Override
    public Path save(final Path path,
                     final KieDeploymentDescriptorContent content2,
                     final Metadata metadata,
                     final String comment) {

        if (content2 != null) {
            try {
                org.uberfire.java.nio.file.Path result = null;
                String content = ""; //FIXME: write toXML
                if (metadata != null) {
                    result = ioService.write(Paths.convert(path),
                                             content,
                                             metadataService.setUpAttributes(path, metadata),
                                             optionsFactory.makeCommentedOption(comment));
                } else if (comment != null) {
                    result = ioService.write(Paths.convert(path),
                                             content,
                                             optionsFactory.makeCommentedOption(comment));
                } else {
                    result = ioService.write(Paths.convert(path), content);
                }
                return Paths.convert(result);
            } catch (final Exception e) {
                throw ExceptionUtilities.handleException(e);
            }
        }
        return path;
    }

    @Override
    public List<ValidationMessage> validate(final Path path,
                                            final KieDeploymentDescriptorContent content) {
        return null;
    }
}
