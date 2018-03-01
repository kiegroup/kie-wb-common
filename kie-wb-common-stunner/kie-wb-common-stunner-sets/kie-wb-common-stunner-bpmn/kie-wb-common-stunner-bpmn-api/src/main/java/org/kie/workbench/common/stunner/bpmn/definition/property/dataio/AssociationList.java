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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AssociationList {

    private final List<AssociationDeclaration> inputs;
    private final List<AssociationDeclaration> outputs;

    public AssociationList(List<AssociationDeclaration> inputs, List<AssociationDeclaration> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public AssociationList(List<AssociationDeclaration> all) {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        for (AssociationDeclaration associationDeclaration : all) {
            if (associationDeclaration.getDirection() == AssociationDeclaration.Direction.Input) {
                inputs.add(associationDeclaration);
            } else {
                outputs.add(associationDeclaration);
            }
        }
    }

    public AssociationList() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public List<AssociationDeclaration> getInputs() {
        return inputs;
    }

    public List<AssociationDeclaration> getOutputs() {
        return outputs;
    }

    @Override
    public String toString() {
        return Stream.concat(inputs.stream(), outputs.stream())
                .map(AssociationDeclaration::toString)
                .collect(Collectors.joining(","));
    }

    public static AssociationList fromString(String encoded) {
        return new AssociationList(
                Arrays.asList(encoded.split(",")).stream()
                        .map(AssociationDeclaration::fromString)
                        .collect(Collectors.toList()));
    }
}