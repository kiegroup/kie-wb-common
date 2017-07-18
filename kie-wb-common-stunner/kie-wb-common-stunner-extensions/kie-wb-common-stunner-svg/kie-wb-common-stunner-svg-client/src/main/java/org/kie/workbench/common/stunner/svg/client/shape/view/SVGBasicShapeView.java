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

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.Collection;

import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;

/**
 * A Shape View type for SVG representations.
 * <p/>
 * This type provides basic support for displaying the
 * (generated) view that are built from SVG structures.
 * <p/>
 * The SVGShapeView instances are usually generated at compile by parsing
 * and translating SVG images into the concrete view's domain.
 * @param <T> The SVGShapeView type.
 * @See {@link SVGViewFactory}
 * @See {@link SVGSource}
 * <p/>
 * This view type can be composed by other SVG view instances
 * of same type, this way SVG images can be their-self
 * referenced along an HTML page and same way in the Canvas.
 * <p/>
 * Each SVG shape view instance must provide a unique name,
 * this way the different SVG view children that compose
 * the view can be referenced as well.
 */
public interface SVGBasicShapeView<T extends SVGBasicShapeView>
        extends
        ShapeView<T> {

    /**
     * Returns a unique SVGShapeView name.
     */
    String getName();

    /**
     * Return the main shape in the view.
     * As SVGView instances are usually generated from
     * SVG structures, they can contain several shapes
     * and other composite view children. The shape
     * instance returned by this method is the instance
     * used by Stunner to change the view's state.
     */
    Shape<?> getShape();

    /**
     * Returns the container instance for this view.
     * This method is used to provide the children
     * for other composite SVG views.
     */
    IContainer<?, IPrimitive<?>> getContainer();

    /**
     * Adds an SVG shape view instance as a child for this view.
     * @param parent The target parent element used for adding the child view's container.
     * @param child The SVGShapeView instance.
     */
    SVGBasicShapeView addSVGChild(final String parent,
                                  final SVGBasicShapeView child);

    /**
     * Returns the SVGShapeView's children for this view.
     */
    Collection getSVGChildren();
}
