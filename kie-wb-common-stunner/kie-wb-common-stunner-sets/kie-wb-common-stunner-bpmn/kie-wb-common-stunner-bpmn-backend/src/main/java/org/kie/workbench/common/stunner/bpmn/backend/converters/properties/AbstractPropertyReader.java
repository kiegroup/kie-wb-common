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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;

public class AbstractPropertyReader {

    protected final BaseElement element;

    public AbstractPropertyReader(BaseElement element) {
        this.element = element;
    }

    public String getDocumentation() {
        return element.getDocumentation().stream()
                .findFirst()
                .map(org.eclipse.bpmn2.Documentation::getText)
                .orElse("");
    }

    public String getDescription() {
        return metaData("customDescription");
    }

    public FontSet getFontSet() {
        return new FontSet(
                new FontFamily(),
                new FontColor(attribute("fontcolor", "color")),
                new FontSize(optionalAttribute("fontsize")
                                     .map(Double::parseDouble).orElse(null)),
                new FontBorderSize(),
                new FontBorderColor());
    }

    public BackgroundSet getBackgroundSet() {
        return new BackgroundSet(
                new BgColor(attribute("bgcolor", "background-color")),
                new BorderColor(attribute("border-color", "bordercolor")),
                new BorderSize()
        );
    }

    protected Optional<String> optionalAttribute(String... attributeIds) {
        List<String> attributes = Arrays.asList(attributeIds);
        return element.getAnyAttribute().stream()
                .filter(e -> attributes.contains(e.getEStructuralFeature().getName()))
                .map(e -> e.getValue().toString())
                .findFirst();
    }


    protected String attribute(String... attributeId) {
        return optionalAttribute(attributeId).orElse("");
    }

    protected String metaData(String name) {
        return Utils.getMetaDataValue(element.getExtensionValues(), name);
    }
}
