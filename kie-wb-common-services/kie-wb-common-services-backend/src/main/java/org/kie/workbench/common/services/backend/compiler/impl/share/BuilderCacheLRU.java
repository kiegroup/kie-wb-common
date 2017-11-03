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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;

@ApplicationScoped
@Named("LRUBuilderCache")
public class BuilderCacheLRU extends LRUCache<Project, KieAFBuilder> implements BuilderCache<Project, KieAFBuilder> {

    @Override
    public void invalidateCache(final Project uri) {
        final KieAFBuilder result = getEntry(uri);
        if (result != null) {
            result.cleanInternalCache();
        }
    }
}
