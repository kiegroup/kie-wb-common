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

package org.kie.workbench.common.stunner.core.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class DynamicShapeDefinitions<W> {
    private final Map<Class<?>, DynamicShapeDefinition<? extends W>> definitions;
    private final Collection<DynamicShapeDefinitionListener<W>> listeners;

    @Inject
    public DynamicShapeDefinitions() {
        definitions = new HashMap<>();
        listeners = new HashSet<>();
    }

    @Inject
    private void initDefinitions(@Any Instance<DynamicShapeDefinition<? extends W>> defs) {
        for (DynamicShapeDefinition<? extends W> def: defs) {
            addDefinition(def);
        }
    }

    public DynamicShapeDefinition<? extends W> getShapeDefinition(Class<?> clazz) {
        return definitions.get(clazz);
    }

    public Collection<DynamicShapeDefinition<? extends W>> getShapeDefinitions() {
        return definitions.values();
    }

    public void addDefinition(final DynamicShapeDefinition<? extends W> definition) {
        if (definitions.values().contains(definition) || definitions.keySet().contains(definition.getType())) {
            return;
        }
        definitions.put(definition.getDefinitionClass(), definition);
        listeners.forEach(listener -> listener.onDynamicShapeDefinitionAdded(definition));
    }

    public void registerDynamicShapeDefinitionListener(DynamicShapeDefinitionListener<W> listener) {
        listeners.add(listener);
    }

    public void removeDynamicShapeDefinitionListener(DynamicShapeDefinitionListener<W> listener) {
        listeners.remove(listener);
    }

    public interface DynamicShapeDefinitionListener<T> {

        void onDynamicShapeDefinitionAdded(DynamicShapeDefinition<? extends T> dynamicShapeDefinition);
    }
}
