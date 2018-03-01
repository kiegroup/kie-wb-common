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

package org.kie.workbench.common.stunner.bpmn.definition.property.dataio;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNProperty;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.AssignmentsType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Type;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@Portable
@Bindable
@Property
@FieldDefinition(i18nMode = I18nMode.OVERRIDE_I18N_KEY)
public class AssignmentsInfo implements BPMNProperty {

    @Type
    public static final PropertyType type = new AssignmentsType();

    @Value
    @FieldValue
    private String value;

    private DeclarationList inputs;
    private DeclarationList outputs;
    private AssociationList associations;
    private boolean alternativeEncoding;

    public AssignmentsInfo() {
        this("");
    }

    @Deprecated
    public AssignmentsInfo(final String value) {
        this.value = value;
        readIntoStringRepresentation(value);
    }

    public AssignmentsInfo(
            DeclarationList inputs,
            DeclarationList outputs,
            AssociationList associations,
            boolean alternativeEncoding) {

        this(encodeStringRepresentation(
                inputs,
                outputs,
                associations,
                alternativeEncoding));

        this.inputs = inputs;
        this.outputs = outputs;
        this.associations = associations;
        this.alternativeEncoding = alternativeEncoding;
    }

    public PropertyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
        readIntoStringRepresentation(value);
    }

    public DeclarationList getInputs() {
        return inputs;
    }

    public void setInputs(DeclarationList inputs) {
        this.inputs = inputs;
    }

    public DeclarationList getOutputs() {
        return outputs;
    }

    public void setOutputs(DeclarationList outputs) {
        this.outputs = outputs;
    }

    public AssociationList getAssociations() {
        return associations;
    }

    public void setAssociations(AssociationList associations) {
        this.associations = associations;
    }

    @Override
    public int hashCode() {
        return (null != value) ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AssignmentsInfo) {
            AssignmentsInfo other = (AssignmentsInfo) o;
            return (null != value) ? value.equals(other.value) : null == other.value;
        }
        return false;
    }

    private static String encodeStringRepresentation(
            DeclarationList inputs,
            DeclarationList outputs,
            AssociationList associations,
            boolean alternativeEncoding) {
        if (alternativeEncoding) {
            return nonCanonicalEncoding(inputs, outputs, associations);
        } else {
            return canonicalEncoding(inputs, outputs, associations);
        }
    }

    private static String canonicalEncoding(DeclarationList inputs, DeclarationList outputs, AssociationList associations) {
        return Stream.of(
                inputs.toString(),
                "",
                outputs.toString(),
                "",
                associations.toString())
                .collect(Collectors.joining("|"));
    }

    private static String nonCanonicalEncoding(DeclarationList inputs, DeclarationList outputs, AssociationList associations) {
        return Stream.of("",
                         inputs.toString(),
                         "",
                         outputs.toString(),
                         associations.toString())
                .collect(Collectors.joining("|"));
    }

    private void readIntoStringRepresentation(String encoded) {
        this.inputs = new DeclarationList();
        this.outputs = new DeclarationList();
        this.associations = new AssociationList();

        if (encoded.isEmpty()) {
            return;
        }

        String[] split = encoded.split("\\|");
        if (split.length == 0) {
            return;
        }

        if (!split[0].isEmpty()) {
            // then it is certainly canonicalEncoding (see canonicalEncoding())
            this.inputs = DeclarationList.fromString(split[0]);
            if (split.length < 3) {
                return;
            }
            this.outputs = DeclarationList.fromString(split[2]);
            if (split.length < 5) {
                return;
            }
            this.associations = AssociationList.fromString(split[4]);
        } else {
            // otherwise, let's try offsetting by one -- fixme: verify this assumption is good enough
            this.alternativeEncoding = true;
            this.inputs = DeclarationList.fromString(split[1]);
            if (split.length < 4) {
                return;
            }
            this.outputs = DeclarationList.fromString(split[3]);
            if (split.length < 5) {
                return;
            }
            this.associations = AssociationList.fromString(split[4]); // associations are always last
        }
    }
}
