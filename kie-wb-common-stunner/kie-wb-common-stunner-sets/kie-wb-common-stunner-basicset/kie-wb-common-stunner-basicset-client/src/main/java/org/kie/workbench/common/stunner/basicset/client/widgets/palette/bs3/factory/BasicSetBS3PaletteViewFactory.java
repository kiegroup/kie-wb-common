/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.basicset.client.widgets.palette.bs3.factory;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.basicset.BasicSet;
import org.kie.workbench.common.stunner.basicset.definition.Categories;
import org.kie.workbench.common.stunner.client.widgets.palette.bs3.factory.BindableBS3PaletteGlyphViewFactory;
import org.kie.workbench.common.stunner.core.client.ShapeManager;

@ApplicationScoped
public class BasicSetBS3PaletteViewFactory extends BindableBS3PaletteGlyphViewFactory<Icon> {

    private final static Map<String, Icon> CATEGORY_VIEWS = new HashMap<String, Icon>() {{
        put( Categories.BASIC,
             getIcon( IconType.SQUARE ) );
        put( Categories.BASIC_WITH_ICONS,
             getIcon( IconType.PLUS_SQUARE_O ) );
        put( Categories.ICONS,
             getIcon( IconType.DASHBOARD ) );
        put( Categories.CONNECTORS,
             getIcon( IconType.LONG_ARROW_RIGHT ) );
    }};

    protected BasicSetBS3PaletteViewFactory() {
        this( null );
    }

    @Inject
    public BasicSetBS3PaletteViewFactory( final ShapeManager shapeManager ) {
        super( shapeManager );
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BasicSet.class;
    }

    @Override
    protected Map<Class<?>, Icon> getDefinitionViews() {
        return null;
    }

    @Override
    protected Map<String, Icon> getCategoryViews() {
        return CATEGORY_VIEWS;
    }

    @Override
    protected Icon resize( final Icon widget,
                           final int width,
                           final int height ) {
        widget.setSize( IconSize.LARGE );
        return widget;
    }

    private static Icon getIcon( final IconType iconType ) {
        return new Icon( iconType );
    }
}
