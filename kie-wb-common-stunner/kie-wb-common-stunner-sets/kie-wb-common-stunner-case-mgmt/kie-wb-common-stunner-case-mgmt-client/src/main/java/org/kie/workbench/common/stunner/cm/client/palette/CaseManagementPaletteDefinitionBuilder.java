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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.BS3IconTypeGlyph;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.ExpandedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

import static org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.getId;
import static org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.isType;

@ApplicationScoped
@CaseManagementEditor
public class CaseManagementPaletteDefinitionBuilder
        implements PaletteDefinitionBuilder<AbstractCanvasHandler, DefaultPaletteDefinition> {

    public static final String STAGES = "Stages";
    public static final String ACTIVITIES = "Activities";

    private static final Map<String, String> CAT_TITLES = new HashMap<String, String>(2) {{
        put(STAGES,
            STAGES);
        put(ACTIVITIES,
            ACTIVITIES);
    }};

    private static final Map<String, Class<?>> CAT_DEFAULTS = new HashMap<String, Class<?>>(5) {{
        put(STAGES,
            AdHocSubprocess.class);
        put(ACTIVITIES,
            BusinessRuleTask.class);
    }};

    @SuppressWarnings("unchecked")
    private final static Map<String, Glyph> CATEGORY_GLYPHS = new HashMap<String, Glyph>(2) {{
        put(STAGES,
            BS3IconTypeGlyph.create(IconType.STAR));
        put(ACTIVITIES,
            BS3IconTypeGlyph.create(IconType.TASKS));
    }};

    private final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder;

    // CDI proxy.
    protected CaseManagementPaletteDefinitionBuilder() {
        this(null);
    }

    @Inject
    public CaseManagementPaletteDefinitionBuilder(final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder) {
        this.paletteDefinitionBuilder = paletteDefinitionBuilder;
    }

    @PostConstruct
    public void init() {
        paletteDefinitionBuilder
                .itemFilter(isDefinitionAllowed())
                .categoryFilter(isCategoryAllowed())
                .categoryDefinitionIdProvider(category -> getId(CAT_DEFAULTS.get(category)))
                .categoryGlyphProvider(CATEGORY_GLYPHS::get)
                .categoryMessages(new ExpandedPaletteDefinitionBuilder.ItemMessageProvider() {
                    @Override
                    public String getTitle(String id) {
                        return CAT_TITLES.get(id);
                    }

                    @Override
                    public String getDescription(String id) {
                        return CAT_TITLES.get(id);
                    }
                });
    }

    private Predicate<String> isCategoryAllowed() {
        return CAT_TITLES::containsKey;
    }

    private Predicate<String> isDefinitionAllowed() {
        return isType(CaseManagementDiagram.class)
                .or(isType(NoneTask.class))
                .or(isType(Lane.class))
                .or(isType(StartNoneEvent.class))
                .or(isType(EndNoneEvent.class))
                .or(isType(ParallelGateway.class))
                .or(isType(ExclusiveGateway.class))
                .or(isType(SequenceFlow.class))
                .negate();
    }

    @Override
    public void build(final AbstractCanvasHandler canvasHandler,
                      final Consumer<DefaultPaletteDefinition> paletteDefinition) {
        paletteDefinitionBuilder.build(canvasHandler,
                                       paletteDefinition);
    }
}
