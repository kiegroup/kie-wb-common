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

package org.kie.workbench.common.dmn.client.shape.def;

import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.ConnectorGlyphDef;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public final class KnowledgeRequirementShapeDef
        implements ConnectorShapeDef<KnowledgeRequirement> {

    @Override
    public double getAlpha(final KnowledgeRequirement element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final KnowledgeRequirement element) {
        return "#000000";
    }

    @Override
    public double getBackgroundAlpha(final KnowledgeRequirement element) {
        return 1;
    }

    @Override
    public String getBorderColor(final KnowledgeRequirement element) {
        return "#000000";
    }

    @Override
    public double getBorderSize(final KnowledgeRequirement element) {
        return 1d;
    }

    @Override
    public double getBorderAlpha(final KnowledgeRequirement element) {
        return 1;
    }

    @Override
    public String getNamePropertyValue(final KnowledgeRequirement element) {
        return KnowledgeRequirement.stunnerTitle;
    }

    @Override
    public String getFontFamily(final KnowledgeRequirement element) {
        return null;
    }

    @Override
    public String getFontColor(final KnowledgeRequirement element) {
        return null;
    }

    @Override
    public String getFontBorderColor(final KnowledgeRequirement element) {
        return null;
    }

    @Override
    public double getFontSize(final KnowledgeRequirement element) {
        return 0;
    }

    @Override
    public double getFontBorderSize(final KnowledgeRequirement element) {
        return 0;
    }

    @Override
    public HasTitle.Position getFontPosition(final KnowledgeRequirement element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final KnowledgeRequirement element) {
        return 0;
    }

    @Override
    public GlyphDef<KnowledgeRequirement> getGlyphDef() {
        return new BasicConnectorGlyphDef();
    }

    private static class BasicConnectorGlyphDef extends ConnectorGlyphDef<KnowledgeRequirement> {

        @Override
        public String getColor() {
            return "#000000";
        }

        @Override
        public String getGlyphDescription(final KnowledgeRequirement element) {
            return KnowledgeRequirement.stunnerTitle;
        }
    }
}
