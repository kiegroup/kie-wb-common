/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.shape.def;

import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.CircleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

public final class StartSignalEventShapeDef
        extends AbstractShapeDef<StartSignalEvent>
        implements CircleShapeDef<StartSignalEvent> {

    @Override
    public double getRadius(final StartSignalEvent element) {
        return element.getDimensionsSet().getRadius().getValue();
    }

    @Override
    public String getBackgroundColor(final StartSignalEvent element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final StartSignalEvent element) {
        return 1;
    }

    @Override
    public String getBorderColor(final StartSignalEvent element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final StartSignalEvent element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final StartSignalEvent element) {
        return 1;
    }

    @Override
    public String getFontFamily(final StartSignalEvent element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final StartSignalEvent element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final StartSignalEvent element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final StartSignalEvent element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final StartSignalEvent element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final StartSignalEvent element) {
        return 0;
    }

    private static final PictureGlyphDef<StartSignalEvent, BPMNPictures> PICTURE_GLYPH_DEF =
            new PictureGlyphDef<StartSignalEvent, BPMNPictures>() {

                @Override
                public String getGlyphDescription(final StartSignalEvent element) {
                    return element.getDescription();
                }

                @Override
                public BPMNPictures getSource(final Class<?> type) {
                    return BPMNPictures.EVENT_START;
                }
            };

    @Override
    public GlyphDef<StartSignalEvent> getGlyphDef() {
        return PICTURE_GLYPH_DEF;
    }
}
