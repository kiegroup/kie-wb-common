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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.definition.property.font.FontBorderSize;
import org.kie.workbench.common.dmn.api.definition.property.font.FontColor;
import org.kie.workbench.common.dmn.api.definition.property.font.FontFamily;
import org.kie.workbench.common.dmn.api.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = DMNDiagram.DMNDiagramBuilder.class)
@CanContain(roles = {"dmn_element"})
public class DMNDiagram extends BaseNode implements DMNDefinition {

    @Category
    public static final transient String category = Categories.NODES;

    @Title
    public static final transient String title = "DMN Diagram";

    @Description
    public static final transient String description = "DMN Diagram";

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add("dmn_diagram");
    }};

    @NonPortable
    public static class DMNDiagramBuilder extends BaseNodeBuilder<DMNDiagram> {

        @Override
        public DMNDiagram build() {
            return new DMNDiagram(new BackgroundSet("#0088ce",
                                                    "#0088ce",
                                                    0d),
                                  new FontSet(FontFamily.defaultValue,
                                              FontColor.defaultValue,
                                              16d,
                                              FontBorderSize.defaultValue),
                                  new RectangleDimensionsSet(500d,
                                                             400d)
            );
        }
    }

    public DMNDiagram() {

    }

    public DMNDiagram(final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                      final @MapsTo("fontSet") FontSet fontSet,
                      final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        super(backgroundSet,
              fontSet,
              dimensionsSet);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
