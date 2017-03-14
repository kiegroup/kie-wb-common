/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.ala;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.source.Source;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

@ApplicationScoped
public class LocalSourceConfigExecutor
        implements FunctionConfigExecutor< LocalSourceConfig, Source > {

    @Inject
    public LocalSourceConfigExecutor( ) {
    }

    @Override
    public Optional< Source > apply( LocalSourceConfig localSourceConfig ) {
        Path path = PathFactory.newPath( "pom.xml", localSourceConfig.getRootPath( ) );
        return Optional.of( new LocalSource( Paths.convert( path ) ) );
    }

    @Override
    public Class< ? extends Config > executeFor( ) {
        return LocalSourceConfig.class;
    }

    @Override
    public String outputId( ) {
        return "local-source";
    }
}