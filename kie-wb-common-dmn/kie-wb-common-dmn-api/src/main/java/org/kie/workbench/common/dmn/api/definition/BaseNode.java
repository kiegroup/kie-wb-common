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
package org.kie.workbench.common.dmn.api.definition;

import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.dmn.api.definition.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.definition.property.font.FontSet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;

public abstract class BaseNode implements DMNDefinition {

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected BackgroundSet backgroundSet;

    @FormField(
            afterElement = "backgroundSet"
    )
    @PropertySet
    protected FontSet fontSet;

    @PropertySet
    protected RectangleDimensionsSet dimensionsSet;

    @Labels
    protected final Set<String> labels = new HashSet<String>() {{
        add("dmn_node");
    }};

    @NonPortable
    static abstract class BaseNodeBuilder<T extends BaseNode> implements Builder<T> {

        public static final String COLOR = "#f9fad2";
        public static final Double WIDTH = 136d;
        public static final Double HEIGHT = 48d;
        public static final Double BORDER_SIZE = 0d;
        public static final String BORDER_COLOR = "#000000";
    }

    protected BaseNode() {
    }

    public BaseNode(final BackgroundSet backgroundSet,
                    final FontSet fontSet,
                    final RectangleDimensionsSet dimensionsSet) {
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }

    public abstract String getCategory();

    public abstract String getTitle();

    public abstract String getDescription();

    public Set<String> getLabels() {
        return labels;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }
}
