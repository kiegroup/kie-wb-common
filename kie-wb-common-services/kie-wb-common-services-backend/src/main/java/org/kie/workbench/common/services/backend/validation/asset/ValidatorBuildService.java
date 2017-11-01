/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.validation.asset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.utils.BuilderUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter.convertIntoValidationMessage;
import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
public class ValidatorBuildService {

    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\nPlease check the necessary external dependencies for this project are configured correctly.";

    private IOService ioService;
    private KieProjectService projectService;
    private String ERROR_LEVEL = "ERROR";
    private BuilderUtils builderUtils;

    public ValidatorBuildService() {
        //CDI proxies
    }

    @Inject
    public ValidatorBuildService(final @Named("ioStrategy") IOService ioService,
                                 final KieProjectService projectService,
                                 final BuilderUtils builderUtils) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.builderUtils = builderUtils;
    }

    public List<ValidationMessage> validate(final Path resourcePath,
                                            final String content) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));
            final List<ValidationMessage> results = doValidation(resourcePath, inputStream);
            return results;
        } catch (NoClassDefFoundError e) {
            return error(MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                              e.getLocalizedMessage()));
        } catch (Throwable e) {
            return error(e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public List<ValidationMessage> validate(final Path resourcePath) {
        InputStream inputStream = null;
        try {
            inputStream = ioService.newInputStream(convert(resourcePath));
            final List<ValidationMessage> results = doValidation(resourcePath, inputStream);
            return results;
        } catch (NoClassDefFoundError e) {
            return error(MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                              e.getLocalizedMessage()));
        } catch (Throwable e) {
            return error(e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private List<ValidationMessage> doValidation(final Path _resourcePath,
                                                 final InputStream inputStream) throws NoProjectException {

        final Optional<KieProject> project = project(_resourcePath);
        if (!project.isPresent()) {
            return getExceptionMsgs("[ERROR] no project found");
        }

        final KieProject kieProject = project.get();
        final org.uberfire.java.nio.file.Path resourcePath = convert(_resourcePath);

        final KieAFBuilder builder = builderUtils.getBuilder(kieProject);
        final KieCompilationResponse res = builder.validate(resourcePath, inputStream);

        return convertIntoValidationMessage(res.getMavenOutput().get(),
                                            ERROR_LEVEL,
                                            ((DefaultKieAFBuilder) builder).getGITURI(),
                                            ((DefaultKieAFBuilder) builder).getInfo().getPrjPath().getParent().toString());
    }

    private List<ValidationMessage> getExceptionMsgs(String msg) {
        List<ValidationMessage> msgs = new ArrayList<>();
        ValidationMessage msgInternal = new ValidationMessage();
        msgInternal.setText(msg);
        msgs.add(msgInternal);
        return msgs;
    }

    private Optional<KieProject> project(final Path resourcePath) throws NoProjectException {
        final KieProject project = projectService.resolveProject(resourcePath);
        return Optional.ofNullable(project);
    }

    private ArrayList<ValidationMessage> error(final String errorMessage) {
        return new ArrayList<ValidationMessage>() {{
            add(new ValidationMessage(Level.ERROR, errorMessage));
        }};
    }
}