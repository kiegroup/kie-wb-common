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

import org.guvnor.common.services.backend.cache.KieModuleMetaDataCache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUKieModuleMetaDataCache")
public class KieModuleMetaDataCacheLRU extends LRUCache<Path, KieModuleMetaData> implements KieModuleMetaDataCache {

    public synchronized Object getKieModuleMetaData(Path projectRootPath) {
        return getEntry(projectRootPath);
    }

    public synchronized void addKieModuleMetaData(Path projectRootPath,
                                                  Object metadata) {
        setEntry(projectRootPath, (KieModuleMetaData)metadata);
    }

    public synchronized void removeKieModuleMetaData(Path projectRootPath) {
        invalidateCache(projectRootPath);
    }

    @Override
    public synchronized void clearKieModuleMetaDataCache() {
        invalidateCache();
    }
}
