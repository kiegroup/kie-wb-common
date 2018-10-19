/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.offprocess.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.openhft.chronicle.map.ChronicleMap;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponseOffProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapProvider {

    public static final String EXTENSION = ".dat";
    private Logger logger = LoggerFactory.getLogger(MapProvider.class);
    private ChronicleMap<CharSequence, DefaultKieCompilationResponseOffProcess> compilationResponseMap;
    private File file;
    private String name;
    private long entries;

    public MapProvider(File file,
                       long entries) {
        this.file = file;
        this.name = file.getName();
        this.entries = entries;
        try {
            compilationResponseMap =
                    ChronicleMap.of(CharSequence.class,
                                    DefaultKieCompilationResponseOffProcess.class)
                            .name(name)
                            .averageKey(UUID.randomUUID().toString())
                            .averageKeySize(128)
                            .averageValue(new DefaultKieCompilationResponseOffProcess(new DefaultKieCompilationResponse(true,
                                                                                                                        UUID.randomUUID().toString())))
                            .entries(entries)
                            .createPersistedTo(file);
        } catch (IOException ex) {
            logger.error(ex.getMessage(),
                         ex);
        }
    }

    public void putResponse(CharSequence sequence,
                            DefaultKieCompilationResponseOffProcess res) {
        compilationResponseMap.put(sequence,
                                   res);
    }

    public CompilationResponse getResponse(CharSequence sequence) {
        DefaultKieCompilationResponseOffProcess res = compilationResponseMap.get(sequence);
        return new DefaultKieCompilationResponse(res);
    }

    public void removeResponse(CharSequence sequence) {
        compilationResponseMap.remove(sequence);
    }

    public void closeProvider() {
        compilationResponseMap.close();
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public long getEntries() {
        return entries;
    }
}
