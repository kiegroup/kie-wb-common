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

package org.kie.workbench.common.stunner.core.definition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.slf4j.Logger;

@ApplicationScoped
public class DynamicDefinitions {
    private final Map<Class<?>, String> definitionIds;
    private final Map<Class<?>, DynamicDefinition> definitions;
    private final Map<Class<?>, Class<?>> baseTypes;
    private final Map<Class<?>, String> categoryFieldNames;
    private final Map<Class<?>, String> titleFieldNames;
    private final Map<Class<?>, String> descriptionFieldNames;
    private final Map<Class<?>, String> labelsFieldNames;
    private final Map<Class<?>, Class> graphFactoryFieldNames;
    private final Map<Class<?>, Set<String>> propertySetsFieldNames;
    private final Map<Class<?>, Set<String>> propertiesFieldNames;
    private final Map<Class<?>, Set<Class<?>>> classAddonGroups;
    private final Map<org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes, Set<Class<?>>>
            metaPropertyTypesAddonGroups;
    private final Map<org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes, Class>
            metaPropertyTypes;
    private final Collection<DynamicDefinitionListener> listeners;

    @Inject
    private Logger logger;

    @Inject
    public DynamicDefinitions() {
        definitionIds = new HashMap<>();
        definitions = new HashMap<>();
        baseTypes = new HashMap<>();
        categoryFieldNames = new HashMap<>();
        titleFieldNames = new HashMap<>();
        descriptionFieldNames = new HashMap<>();
        labelsFieldNames = new HashMap<>();
        graphFactoryFieldNames = new HashMap<>();
        propertySetsFieldNames = new HashMap<>();
        propertiesFieldNames = new HashMap<>();
        classAddonGroups = new HashMap<>();
        metaPropertyTypesAddonGroups = new HashMap<>();
        metaPropertyTypes = new HashMap<>();
        listeners = new HashSet<>();
    }

    @Inject
    private void initDefinitions(@Any Instance<DynamicDefinition> defs) {
        logger.info("Registering classes...");
        for (DynamicDefinition def: defs) {
            logger.info("Registering class " + def.getType().getName());
            addDefinition(def);
        }
        logger.info("Finished registering classes.");
    }

    private <T> Map<Class<?>, T> filterByAddonGroups(final Map<Class<?>, T> hashMap,
                                                            final Set<Class<?>> groups) {
        return hashMap
                .entrySet()
                .stream()
                .filter(e -> containsAny(groups, getAddonGroupsFor(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          Map.Entry::getValue));
    }

    private <T> Map<org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes, T> filterPropertyByAddonGroups(final Map<org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes, T> hashMap,
                                                                                                                                       final Set<Class<?>> groups) {
        return hashMap
                .entrySet()
                .stream()
                .filter(e -> containsAny(groups, getAddonGroupsFor(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          Map.Entry::getValue));
    }

    public static boolean containsAny(Collection<?> a, Collection<?> b) {
        return !Collections.disjoint(a, b);
    }

    public static boolean inAddonGroups(DynamicDefinition def, Set<Class<?>> groups) {
        return containsAny(groups, def.getAddonGroups());
    }

    public Collection<String> getDefinitionIds() {
        return Collections.unmodifiableCollection(definitionIds.values());
    }

    public Collection<String> getDefinitionIds(final Set<Class<?>> groups) {
        return Collections.unmodifiableCollection(filterByAddonGroups(definitionIds,
                                       groups).values());
    }

    public Collection<DynamicDefinition> getDynamicDefinitions() {
        return Collections.unmodifiableCollection(definitions.values());
    }

    public Collection<DynamicDefinition> getDynamicDefinitions(final Set<Class<?>> groups) {
        return Collections.unmodifiableCollection(filterByAddonGroups(definitions,
                                                                      groups).values());
    }

    public Collection<? extends Class<?>> getDomainMorphs(final Class<?> baseClass) {
        return getBaseTypes()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals(baseClass))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))
                .keySet();
    }

    public Collection<? extends Class<?>> getDomainMorphs(final Class<?> baseClass, final Set<Class<?>> groups) {
        return getBaseTypes(groups)
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals(baseClass))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue))
                .keySet();
    }

    public Set<Class<?>> getSupportedClasses() {
        return Collections.unmodifiableSet(definitions.keySet());
    }

    public Set<Class<?>> getSupportedClasses(final Set<Class<?>> groups) {
        return Collections.unmodifiableSet(filterByAddonGroups(definitions,
                                                               groups).keySet());
    }

    public Object getInstanceOf(final Class clazz) {
        return definitions.get(clazz).getBuilder().build();
    }

    public Set<Class<?>> getAddonGroupsFor(final Class clazz) {
        return Collections.unmodifiableSet(classAddonGroups.get(clazz));
    }

    public Set<Class<?>> getAddonGroupsFor(final org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes type) {
        return Collections.unmodifiableSet(metaPropertyTypesAddonGroups.get(type));
    }

    public Map<Class<?>, Class<?>> getBaseTypes() {
        return Collections.unmodifiableMap(baseTypes);
    }

    public Map<Class<?>, Class<?>> getBaseTypes(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getBaseTypes(),
                                                               groups));
    }

    public Map<Class<?>, String> getCategoryFieldNames() {
        return Collections.unmodifiableMap(categoryFieldNames);
    }

    public Map<Class<?>, String> getCategoryFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getCategoryFieldNames(),
                                                               groups));
    }

    public Map<Class<?>, String> getTitleFieldNames() {
        return Collections.unmodifiableMap(titleFieldNames);
    }

    public Map<Class<?>, String> getTitleFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getTitleFieldNames(),
                                                               groups));
    }

    public Map<Class<?>, String> getDescriptionFieldNames() {
        return Collections.unmodifiableMap(descriptionFieldNames);
    }

    public Map<Class<?>, String> getDescriptionFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getDescriptionFieldNames(),
                                                               groups));
    }

    public Map<Class<?>, String> getLabelsFieldNames() {
        return Collections.unmodifiableMap(labelsFieldNames);
    }

    public Map<Class<?>, String> getLabelsFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getLabelsFieldNames(),
                                                               groups));
    }

    public Map<Class<?>, Class> getGraphFactoryFieldNames() {
        return Collections.unmodifiableMap(graphFactoryFieldNames);
    }

    public Map<Class<?>, Class> getGraphFactoryFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getGraphFactoryFieldNames(),
                                                               groups));
    }

    public Map<Class<?>, Set<String>> getPropertySetsFieldNames() {
        return Collections.unmodifiableMap(propertySetsFieldNames);
    }

    public Map<Class<?>, Set<String>> getPropertySetsFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getPropertySetsFieldNames(),
                                                               groups));
    }

    public Map<Class<?>, Set<String>> getPropertiesFieldNames() {
        return Collections.unmodifiableMap(propertiesFieldNames);
    }

    public Map<Class<?>, Set<String>> getPropertiesFieldNames(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterByAddonGroups(getPropertiesFieldNames(),
                                                               groups));
    }

    public Map<org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes,
            Class> getMetaPropertyTypes() {
        return Collections.unmodifiableMap(metaPropertyTypes);
    }

    public Map<org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes,
            Class> getMetaPropertyTypes(final Set<Class<?>> groups) {
        return Collections.unmodifiableMap(filterPropertyByAddonGroups(getMetaPropertyTypes(),
                                                                       groups));
    }

    public void addDefinition(final DynamicDefinition definition) {
        if (definitions.values().contains(definition) || definitions.keySet().contains(definition.getType())) {
            return;
        }

        Class<?> clazz = definition.getType();
        classAddonGroups.put(clazz,
                             definition.getAddonGroups());

        definitions.put(clazz,
                        definition);

        definitionIds.put(clazz,
                          clazz.getName());

        baseTypes.put(clazz,
                              definition.getBaseType());
        categoryFieldNames.put(clazz,
                                       "category");
        titleFieldNames.put(clazz,
                                    "title");
        descriptionFieldNames.put(clazz,
                                          "description");
        labelsFieldNames.put(clazz,
                                     "labels");
        graphFactoryFieldNames.put(clazz,
                                           definition.getFactory());
        propertySetsFieldNames.put(clazz,
                                           definition.getSetProperties());
        propertiesFieldNames.put(clazz,
                                         definition.getProperties());

        for (DynamicDefinitionListener listener : listeners) {
            listener.onDynamicDefinitionAdded(definition);
        }
    }

    public void addMetaType(final PropertyMetaTypes type,
                            final Class value,
                            final Set<Class<?>> addonGroups) {
        metaPropertyTypesAddonGroups.put(type,
                                         addonGroups);
        metaPropertyTypes.put(type,
                                      value);
    }

    public void registerDynamicDefinitionListener(final DynamicDefinitionListener listener) {
        listeners.add(listener);
    }

    public void removeDynamicDefinitionListener(final DynamicDefinitionListener listener) {
        listeners.remove(listener);
    }

    public interface DynamicDefinitionListener {
        void onDynamicDefinitionAdded(DynamicDefinition def);
    }
}
