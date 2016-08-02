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

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.uberfire.backend.vfs.Path;

public class Validator {

    private final ProjectService projectService;

    private final BuildService buildService;

    public Validator( final ProjectService projectService,
                      final BuildService buildService ) {
        this.projectService = projectService;
        this.buildService = buildService;
    }

    public List<ValidationMessage> validate( final Path path,
                                             final String content ) {

        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        for ( ValidationMessage message : validatorBuildService().validate( path, content ) ) {
            if ( isValidatPath( path, message ) ) {
                messages.add( message );
            }
        }

        return messages;
    }

    private ValidatorBuildService validatorBuildService() {
        return new ValidatorBuildService( projectService, buildService );
    }

    protected boolean isValidatPath( final Path path,
                                     final ValidationMessage message ) {
        final String destinationPathURI = removeFileExtension( path.toURI() );
        final String messageURI = message.getPath() != null ? removeFileExtension( message.getPath().toURI() ) : "";

        return messageURI.isEmpty() || destinationPathURI.endsWith( messageURI );
    }

    private String removeFileExtension( final String pathURI ) {
        if ( pathURI != null && pathURI.contains( "." ) ) {
            return pathURI.substring( 0, pathURI.lastIndexOf( "." ) );
        }

        return pathURI;
    }
}
