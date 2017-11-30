/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.share;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import java.lang.Object;

import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUBuilderCache")
public class BuilderCacheLRU extends LRUCache<String, KieAFBuilder> implements BuilderCache {

    /**
     * KIE AFBUILDER
     */

    public synchronized void addKieAFBuilder(String uri, Object builder) {
        setEntry(uri, (KieAFBuilder) builder);
    }

    public synchronized Object getKieAFBuilder(String uri) {
        return getEntry(uri);
    }

    public synchronized void removeBuilder(String uri) {
        invalidateCache(uri);
    }

    public synchronized boolean containsBuilder(String uri) {
        return getKeys().contains(uri);
    }

    public synchronized void clearBuilderCache() {
        invalidateCache();
    }

    @Override
    public void cleanInternalCache(String uri) {
        getEntry(uri).cleanInternalCache();
    }

    @Override
    public Path getProjectRoot(String uri) {
        KieAFBuilder builder = (KieAFBuilder) getKieAFBuilder(uri);
        if (builder != null) {
            Path prjRoot = ((DefaultKieAFBuilder) builder).getInfo().getPrjPath();
            return prjRoot;
        } else {
            return org.uberfire.java.nio.file.Paths.get(URI.create(uri));
        }
    }
}
