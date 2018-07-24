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

package org.kie.workbench.common.forms.cms.components.client.ui.report.preview;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.uberfire.mvp.Command;

@Dependent
public class ObjectPreview implements ObjectPreviewView.Presenter {

    private ObjectPreviewView view;

    private DynamicFormRenderer formRenderer;

    private TranslationService translationService;

    @Inject
    public ObjectPreview(ObjectPreviewView view,
                         DynamicFormRenderer formRenderer,
                         TranslationService translationService) {
        this.view = view;
        this.formRenderer = formRenderer;
        this.translationService = translationService;

        view.init(this);
    }

    public void init(FormRenderingContext context) {
        PortablePreconditions.checkNotNull("context",
                                           context);

        this.formRenderer.render(context);

        view.show();
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(CMSComponentsConstants.ObjectPreviewTitle);
    }

    @Override
    public DynamicFormRenderer getRenderer() {
        return formRenderer;
    }

    @Override
    public Command getAcceptCommand() {
        return view::hide;
    }
}
