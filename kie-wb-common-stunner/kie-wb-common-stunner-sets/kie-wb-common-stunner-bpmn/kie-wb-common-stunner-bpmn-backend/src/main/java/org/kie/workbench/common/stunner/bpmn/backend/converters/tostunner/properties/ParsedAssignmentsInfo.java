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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssociationList;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DeclarationList;

public class ParsedAssignmentsInfo {

    private final DeclarationList inputs;
    private final DeclarationList outputs;
    private final AssociationList associations;
    private final boolean alternativeEncoding;

    public ParsedAssignmentsInfo(DeclarationList inputs, DeclarationList outputs, AssociationList associations, boolean alternativeEncoding) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.associations = associations;
        this.alternativeEncoding = alternativeEncoding;
    }

    public static ParsedAssignmentsInfo of(AssignmentsInfo assignmentsInfo) {
        return fromString(assignmentsInfo.getValue());
    }

    public DeclarationList getInputs() {
        return inputs;
    }

    public DeclarationList getOutputs() {
        return outputs;
    }

    public AssociationList getAssociations() {
        return associations;
    }

    public boolean isAlternativeEncoding() {
        return alternativeEncoding;
    }

    public static ParsedAssignmentsInfo fromString(String encoded) {
        DeclarationList inputs = new DeclarationList();
        DeclarationList outputs = new DeclarationList();
        AssociationList associations = new AssociationList();
        boolean alternativeEncoding = false;

        if (encoded.isEmpty()) {
            return new ParsedAssignmentsInfo(
                    inputs,
                    outputs,
                    associations,
                    alternativeEncoding
            );
        }

        String[] split = encoded.split("\\|");
        if (split.length == 0) {
            return new ParsedAssignmentsInfo(
                    inputs,
                    outputs,
                    associations,
                    alternativeEncoding
            );
        }

        if (!split[0].isEmpty()) {
            // then it is certainly canonicalEncoding (see canonicalEncoding())
            inputs = DeclarationList.fromString(split[0]);
            if (split.length < 3) {
                return new ParsedAssignmentsInfo(
                        inputs,
                        outputs,
                        associations,
                        alternativeEncoding
                );
            }
            outputs = DeclarationList.fromString(split[2]);
            if (split.length < 5) {
                return new ParsedAssignmentsInfo(
                        inputs,
                        outputs,
                        associations,
                        alternativeEncoding
                );
            }
            associations = AssociationList.fromString(split[4]);
        } else {
            // otherwise, let's try offsetting by one -- fixme: verify this assumption is good enough
            alternativeEncoding = true;
            inputs = DeclarationList.fromString(split[1]);
            if (split.length < 4) {
                return new ParsedAssignmentsInfo(
                        inputs,
                        outputs,
                        associations,
                        alternativeEncoding
                );
            }
            outputs = DeclarationList.fromString(split[3]);
            if (split.length < 5) {
                return new ParsedAssignmentsInfo(
                        inputs,
                        outputs,
                        associations,
                        alternativeEncoding
                );
            }
            associations = AssociationList.fromString(split[4]); // associations are always last
        }

        return new ParsedAssignmentsInfo(inputs, outputs, associations, alternativeEncoding);
    }
}
