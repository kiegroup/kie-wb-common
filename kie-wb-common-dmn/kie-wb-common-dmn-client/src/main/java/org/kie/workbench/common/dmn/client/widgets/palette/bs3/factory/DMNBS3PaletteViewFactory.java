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
package org.kie.workbench.common.dmn.client.widgets.palette.bs3.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.Categories;
import org.kie.workbench.common.dmn.client.resources.DMNImageResources;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BindableBS3PaletteGlyphViewFactory;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.svg.SVGIconRenderer;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;

public class DMNBS3PaletteViewFactory extends BindableBS3PaletteGlyphViewFactory {

    private final static Map<String, IconResource> CATEGORY_RENDERER_SETTINGS = new HashMap<String, IconResource>() {{
        put(Categories.NODES,
            new IconResource<>(DMNImageResources.INSTANCE.nodes()));
        put(Categories.CONNECTORS,
            new IconResource<>(DMNImageResources.INSTANCE.connectors()));
    }};

    protected DMNBS3PaletteViewFactory() {
        this(null);
    }

    @Inject
    public DMNBS3PaletteViewFactory(final ShapeManager shapeManager) {
        super(shapeManager);
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return DMNDefinitionSet.class;
    }

    @Override
    protected Class<? extends IconRenderer> getPaletteIconRendererType() {
        return SVGIconRenderer.class;
    }

    @Override
    protected Map<String, IconResource> getCategoryIconResources() {
        return CATEGORY_RENDERER_SETTINGS;
    }

    @Override
    protected Map<String, IconResource> getDefinitionIconResources() {
        return Collections.emptyMap();
    }

}
