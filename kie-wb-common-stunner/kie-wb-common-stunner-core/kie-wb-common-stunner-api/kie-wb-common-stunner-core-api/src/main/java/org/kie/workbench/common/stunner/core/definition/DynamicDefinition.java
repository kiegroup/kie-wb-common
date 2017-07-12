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

import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.builder.Builder;

public interface DynamicDefinition {

    /**
     * Returns the class this definition is for.
     * @return The class this definition is for.
     */
    Class getType();

    /**
     * Returns the base definition class of this definition. If there isn't one, returns class of this definition
     * @return The base definition class this definition is based off of. Not Null.
     */
    Class getBaseType();

    /**
     * Returns a factory to build an instance of this definition.
     * @return Either NodeFactory or EdgeFactory, depending on the definition.
     */
    Class getFactory();

    /**
     * Returns a builder of the definition
     * @return A builder of the definition
     */
    Builder<?> getBuilder();

    /**
     * Return the names of all set properties in this definition.
     * @return The names of all set properties in this definition.
     */
    Set<String> getSetProperties();

    /**
     * Return the names of all properties in this definition.
     * @return The names of all properties in this definition.
     */
    Set<String> getProperties();

    /**
     * Return the addon groups this definition is in. The definition will be made
     * available to any DefinitionSet belonging to those groups.
     * @return The addon groups this definition is in.
     */
    Set<Class<?>> getAddonGroups();
}
