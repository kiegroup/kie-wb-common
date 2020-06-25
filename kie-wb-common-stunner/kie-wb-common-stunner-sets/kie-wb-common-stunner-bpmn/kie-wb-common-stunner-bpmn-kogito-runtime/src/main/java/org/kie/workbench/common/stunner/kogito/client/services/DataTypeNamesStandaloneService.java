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

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes.DataTypeCache;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DataTypeNamesStandaloneService implements DataTypeNamesService {

    private static Consumer<String> LOGGER2 = GWT::log;

    Set<String> dataTypesSet = new HashSet<>();

    boolean cacheRead = false;
    @Inject
    DataTypeCache cache;

    @Override
    public Promise<List<String>> call(final Path path) {
        logMe("Calling Local Service cache is: " + cache);
        logMe("Calling Local Service, contents are: " + dataTypesSet);
        logMe("Calling Local Service, contents size: " + dataTypesSet.size());
        if (!cacheRead && cache != null) {
            logMe("Adding Cached Types::" + cache.getCachedDataTypes());
            dataTypesSet.addAll(cache.getCachedDataTypes());
            cacheRead = true;
        }

        return Promise.resolve(new ArrayList<>(dataTypesSet));
    }

    private static void logMe(String message) {
        LOGGER2.accept(message);
    }

    @Override
    public void add(String value, String oldValue) {
        logMe("Adding New: " + value + "oldValue");

        if (dataTypesSet.contains(oldValue)) {
            dataTypesSet.remove(oldValue);
        }
        dataTypesSet.add(value);
    }


    }
