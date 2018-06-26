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

package org.kie.workbench.common.dmn.client.resources;

import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

public interface DMNSVGGlyphFactory {

    ImageDataUriGlyph DIAGRAM_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.diagram().getSafeUri());

    ImageDataUriGlyph BUSINESS_KNOWLEDGE_MODEL_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.businessKnowledgeModelToolbox().getSafeUri());

    ImageDataUriGlyph DECISION_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.decisionToolbox().getSafeUri());

    ImageDataUriGlyph INPUT_DATA_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.inputDataToolbox().getSafeUri());

    ImageDataUriGlyph KNOWLEDGE_SOURCE_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.knowledgeSourceToolbox().getSafeUri());

    ImageDataUriGlyph TEXT_ANNOTATION_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.textAnnotationToolbox().getSafeUri());

    ImageDataUriGlyph ASSOCIATION_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.associationToolbox().getSafeUri());

    ImageDataUriGlyph AUTHORITY_REQUIREMENT_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.authorityRequirementToolbox().getSafeUri());

    ImageDataUriGlyph INFORMATION_REQUIREMENT_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.informationRequirementToolbox().getSafeUri());

    ImageDataUriGlyph KNOWLEDGE_REQUIREMENT_GLYPH = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.knowledgeRequirementToolbox().getSafeUri());

    ImageDataUriGlyph BUSINESS_KNOWLEDGE_MODEL_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.businessKnowledgeModelGlyph().getSafeUri());

    ImageDataUriGlyph DECISION_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.decisionGlyph().getSafeUri());

    ImageDataUriGlyph INPUT_DATA_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.inputDataGlyph().getSafeUri());

    ImageDataUriGlyph KNOWLEDGE_SOURCE_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.knowledgeSourceGlyph().getSafeUri());

    ImageDataUriGlyph TEXT_ANNOTATION_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.textAnnotationGlyph().getSafeUri());
}
