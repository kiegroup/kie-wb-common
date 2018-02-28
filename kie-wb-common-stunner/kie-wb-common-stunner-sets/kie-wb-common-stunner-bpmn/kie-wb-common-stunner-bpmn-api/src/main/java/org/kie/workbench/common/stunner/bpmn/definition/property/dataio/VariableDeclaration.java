/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.dataio;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class VariableDeclaration {

    private final String identifier;
    private final String type;

    public VariableDeclaration(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        if (type == null || type.isEmpty()) {
            return identifier;
        } else {
            return identifier + ":" + type;
        }
    }

    public static VariableDeclaration fromString(String encoded) {
        String[] split = encoded.split(":");
        String identifier = split[0];
        String type = (split.length == 2)? split[1] : null;
        return new VariableDeclaration(identifier, type);
    }
}

