/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.widgets.codecompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionDefinition {

    private final String name;
    private final Type type;
    private final Set<FunctionOverrideVariation> variations;

    public FunctionDefinition(final String name,
                              final Type type,
                              final FunctionOverrideVariation... variations) {
        this.name = name;
        this.type = type;
        this.variations = new HashSet<>(Arrays.asList(variations));
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Set<FunctionOverrideVariation> getVariations() {
        return Collections.unmodifiableSet(variations);
    }

    public List<FunctionDefinitionStrings> toHumanReadableStrings() {
        final ArrayList<FunctionDefinitionStrings> result = new ArrayList<>();

        int i = 0;
        for (FunctionOverrideVariation variation : variations) {

            final StringBuilder humanReadableBuilder = new StringBuilder();
            final StringBuilder templateBuilder = new StringBuilder();

            humanReadableBuilder.append(name);
            templateBuilder.append(name);
            humanReadableBuilder.append("(");
            templateBuilder.append("(");
            humanReadableBuilder.append(variation.toHumanReadableString());
            templateBuilder.append("$");
            templateBuilder.append(i);
            humanReadableBuilder.append(")");
            templateBuilder.append(")");

            result.add(new FunctionDefinitionStrings(humanReadableBuilder.toString(),
                                                     templateBuilder.toString()));
        }

        return result;
    }
}
