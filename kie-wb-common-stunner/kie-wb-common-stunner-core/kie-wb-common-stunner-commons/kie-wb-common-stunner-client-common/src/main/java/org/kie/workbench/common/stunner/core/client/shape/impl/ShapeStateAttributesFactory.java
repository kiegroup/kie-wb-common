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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttribute;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateAttributeHandler.ShapeStateAttributes;

public class ShapeStateAttributesFactory {

    private static final Map<ShapeState, Supplier<ShapeStateAttributes>> STATE_STROKE_ATTRIBUTES =
            new HashMap<ShapeState, Supplier<ShapeStateAttributes>>() {{
                put(ShapeState.NONE,
                    ShapeStateAttributesFactory::buildAttributes);
                put(ShapeState.SELECTED,
                    () -> buildStrokeAttributes((ShapeState.SELECTED)));
                put(ShapeState.INVALID,
                    () -> buildStrokeAttributes((ShapeState.INVALID)));
                put(ShapeState.HIGHLIGHT,
                    () -> buildStrokeAttributes((ShapeState.HIGHLIGHT)));
            }};

    private static final Map<ShapeState, Supplier<ShapeStateAttributes>> STATE_FILL_ATTRIBUTES =
            new HashMap<ShapeState, Supplier<ShapeStateAttributes>>() {{
                put(ShapeState.NONE,
                    ShapeStateAttributesFactory::buildAttributes);
                put(ShapeState.SELECTED,
                    () -> buildFillAttributes(ShapeState.SELECTED));
                put(ShapeState.INVALID,
                    () -> buildFillAttributes(ShapeState.INVALID));
                put(ShapeState.HIGHLIGHT,
                    () -> buildFillAttributes(ShapeState.HIGHLIGHT));
            }};

    public static final String COLOR_SELECTED = "#0000FF";
    public static final String COLOR_HIGHLIGHT = "#3366CC";
    public static final String COLOR_INVALID = "#FF0000";

    public final static Function<ShapeState, ShapeStateAttributes> STATE_STROKE_ATTRIBUTES_PROVIDER =
            state -> STATE_STROKE_ATTRIBUTES.get(state).get();

    public final static Function<ShapeState, ShapeStateAttributes> STATE_FILL_ATTRIBUTES_PROVIDER =
            state -> STATE_FILL_ATTRIBUTES.get(state).get();

    public static ShapeStateAttributes buildStrokeAttributes(final ShapeState state) {
        switch (state) {
            case SELECTED:
                return buildAttributes()
                        .set(ShapeStateAttribute.STROKE_ALPHA, 1d)
                        .set(ShapeStateAttribute.STROKE_WIDTH, 1d)
                        .set(ShapeStateAttribute.STROKE_COLOR, COLOR_SELECTED);
            case HIGHLIGHT:
                return buildAttributes()
                        .set(ShapeStateAttribute.STROKE_ALPHA, 1d)
                        .set(ShapeStateAttribute.STROKE_WIDTH, 1d)
                        .set(ShapeStateAttribute.STROKE_COLOR, COLOR_HIGHLIGHT);
            case INVALID:
                return buildAttributes()
                        .set(ShapeStateAttribute.STROKE_ALPHA, 1d)
                        .set(ShapeStateAttribute.STROKE_WIDTH, 1d)
                        .set(ShapeStateAttribute.STROKE_COLOR, COLOR_INVALID);
            default:
                return buildAttributes();
        }
    }

    public static ShapeStateAttributes buildFillAttributes(final ShapeState state) {
        switch (state) {
            case SELECTED:
                return buildAttributes()
                        .set(ShapeStateAttribute.FILL_COLOR, COLOR_SELECTED)
                        .set(ShapeStateAttribute.FILL_ALPHA, 1d);
            case HIGHLIGHT:
                return buildAttributes()
                        .set(ShapeStateAttribute.FILL_COLOR, COLOR_HIGHLIGHT)
                        .set(ShapeStateAttribute.FILL_ALPHA, 1d);
            case INVALID:
                return buildAttributes()
                        .set(ShapeStateAttribute.FILL_COLOR, COLOR_INVALID)
                        .set(ShapeStateAttribute.FILL_ALPHA, 1d);
            default:
                return buildAttributes();
        }
    }

    private static ShapeStateAttributes buildAttributes() {
        return new ShapeStateAttributes();
    }
}