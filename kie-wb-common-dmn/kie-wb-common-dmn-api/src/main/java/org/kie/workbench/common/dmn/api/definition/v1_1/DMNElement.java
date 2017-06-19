/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Label;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

public abstract class DMNElement extends DMNModelInstrumentedBase {

    @Property
    @FormField
    protected Id id;

    @Property
    @FormField(afterElement = "id")
    protected Label label;

    @Property
    @FormField(afterElement = "label")
    protected Description description;

    //@PropertySet
    //@FormField
    //TODO {manstis} Should be <QName, String>
    //private Map<QName, String> otherAttributes = new HashMap<>();

    //@PropertySet
    //@FormField
    //TODO {manstis} GWT/Errai cannot handle marshalling Object.. what values do we have?!
    //private DMNElement.ExtensionElements extensionElements;

    public DMNElement() {
    }

    public DMNElement(final Id id,
                      final Label label,
                      final Description description) {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public Id getId() {
        return id;
    }

    public void setId(final Id id) {
        this.id = id;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(final Label label) {
        this.label = label;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(final Description description) {
        this.description = description;
    }
}
