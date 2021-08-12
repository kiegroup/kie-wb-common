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

package org.kie.workbench.common.services.backend.allowlist;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.services.backend.builder.core.NoBuilderFoundException;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

public class PackageNameAllowListLoader
        implements SupportsRead<AllowList> {

    private static final Logger logger = LoggerFactory.getLogger( PackageNameAllowListLoader.class );

    private PackageNameSearchProvider packageNameSearchProvider;
    private IOService                 ioService;

    public PackageNameAllowListLoader() {
    }

    @Inject
    public PackageNameAllowListLoader(final PackageNameSearchProvider packageNameSearchProvider,
                                      final @Named( "ioStrategy" ) IOService ioService ) {
        this.packageNameSearchProvider = packageNameSearchProvider;
        this.ioService = ioService;
    }

    @Override
    public AllowList load(final Path packageNamesAllowListPath ) {
        return new AllowList( parsePackages( loadContent( packageNamesAllowListPath ) ) );
    }

    protected String loadContent( final Path packageNamesAllowListPath ) {

        final org.uberfire.java.nio.file.Path path = Paths.convert( packageNamesAllowListPath );

        if ( Files.exists( path ) ) {
            return ioService.readAllString( path );
        } else {
            return "";
        }
    }

    private boolean isEmpty( final String content ) {
        return (content == null || content.trim().isEmpty());
    }

    //See https://bugzilla.redhat.com/show_bug.cgi?id=1205180. Use OS-independent line splitting.
    private List<String> parsePackages( final String content ) {
        if ( isEmpty( content ) ) {
            return Collections.emptyList();
        } else {
            try {
                return IOUtils.readLines( new StringReader( content ) );
            } catch ( IOException ioe ) {
                logger.warn( "Unable to parse package names from '" + content + "'. Falling back to empty list." );
                return Collections.emptyList();
            }
        }
    }

    public AllowList load(final POM pom ) {
        try {

            return new AllowList( packageNameSearchProvider.newTopLevelPackageNamesSearch( pom ).search() );

        } catch ( NoBuilderFoundException e ) {

            logger.info( "Could not create allow list for project: " + pom.getGav().toString() );

            return new AllowList();
        }
    }
}
