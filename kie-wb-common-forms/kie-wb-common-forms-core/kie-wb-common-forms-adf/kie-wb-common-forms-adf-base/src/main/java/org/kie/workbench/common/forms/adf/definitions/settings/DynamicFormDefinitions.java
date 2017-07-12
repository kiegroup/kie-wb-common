/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.adf.definitions.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;

@Named
@ApplicationScoped
public class DynamicFormDefinitions {

    private final Map<String, FormDefinitionSettings> formDefinitions;
    private final Collection<DynamicFormDefinitionListener> listeners;

    @Inject
    public DynamicFormDefinitions() {
        formDefinitions = new HashMap<>();
        listeners = new HashSet<>();
    }

    @Inject
    private void initDefinitions(@Any Instance<DynamicFormDefinition> defs) {
        for (DynamicFormDefinition def: defs) {
            addFormDefinition(def);
        }
    }

    public Map<String, FormDefinitionSettings> getFormDefinitions() {
        return new HashMap<>(formDefinitions);
    }

    public void addFormDefinition(DynamicFormDefinition def) {
        formDefinitions.put(def.getClassName(),
                            def.getFormDefinition());
        listeners.forEach(listener -> listener.onDynamicFormDefinitionAdded(def));
    }

    public void registerDynamicFormDefinitionListener(DynamicFormDefinitionListener listener) {
        listeners.add(listener);
    }

    public void removeDynamicFormDefinitionListener(DynamicFormDefinitionListener listener) {
        listeners.remove(listener);
    }

    public interface DynamicFormDefinitionListener {

        void onDynamicFormDefinitionAdded(DynamicFormDefinition definition);
    }
}
