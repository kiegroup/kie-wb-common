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

package org.kie.workbench.common.stunner.cm.shapes.def;

import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNPictures;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBaseSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

public final class CaseManagementSubprocessShapeDef
        extends AbstractShapeDef<CaseManagementBaseSubprocess>
        implements StageShapeDef<CaseManagementBaseSubprocess> {

    @Override
    public String getBackgroundColor(final CaseManagementBaseSubprocess element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final CaseManagementBaseSubprocess element) {
        return 1;
    }

    @Override
    public String getBorderColor(final CaseManagementBaseSubprocess element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final CaseManagementBaseSubprocess element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final CaseManagementBaseSubprocess element) {
        return 1;
    }

    @Override
    public String getFontFamily(final CaseManagementBaseSubprocess element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final CaseManagementBaseSubprocess element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final CaseManagementBaseSubprocess element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final CaseManagementBaseSubprocess element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final CaseManagementBaseSubprocess element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final CaseManagementBaseSubprocess element) {
        return 0;
    }

    @Override
    public double getWidth(final CaseManagementBaseSubprocess element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final CaseManagementBaseSubprocess element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getVOffset(final CaseManagementBaseSubprocess element) {
        return 20.0;
    }

    private static final PictureGlyphDef<CaseManagementBaseSubprocess, BPMNPictures> SUBPROCESS_GLYPH_DEF =
            new PictureGlyphDef<CaseManagementBaseSubprocess, BPMNPictures>() {

                @Override
                public String getGlyphDescription(final CaseManagementBaseSubprocess element) {
                    return element.getDescription();
                }

                @Override
                public BPMNPictures getSource(final Class<?> type) {
                    return BPMNPictures.SUB_PROCESS;
                }
            };

    @Override
    public GlyphDef<CaseManagementBaseSubprocess> getGlyphDef() {
        return SUBPROCESS_GLYPH_DEF;
    }
}
