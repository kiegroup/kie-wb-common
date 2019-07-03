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
package org.kie.workbench.common.stunner.bpmn.project.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.categories.Process;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNClientConstants;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.BPMNProjectImageResources;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.diff.DiffMode;

@ApplicationScoped
public class BPMNDiagramResourceType extends BPMNDefinitionSetResourceType implements ClientResourceType {

    private final Image ICON = newIcon();

    private final TranslationService translationService;

    protected BPMNDiagramResourceType() {
        this(null, null);
    }

    @Inject
    public BPMNDiagramResourceType(final Process category,
                                   final TranslationService translationService) {
        super(category);
        this.translationService = translationService;
    }

    @Override
    public String getShortName() {
        return translationService.getTranslation(BPMNClientConstants.BPMNDiagramResourceTypeShortName);
    }

    @Override
    public String getDescription() {
        return translationService.getTranslation(BPMNClientConstants.BPMNDiagramResourceTypeDescription);
    }

    @Override
    public IsWidget getIcon() {
        return ICON;
    }

    /**
     * convenient method for facilitating tests. Icon is still created once since the class is ApplicationScoped.
     */
    Image newIcon() {
        return new Image(BPMNProjectImageResources.INSTANCE.bpmn2Icon());
    }

    @Override
    public DiffMode getDiffMode() {
        return DiffMode.VISUAL;
    }
}
