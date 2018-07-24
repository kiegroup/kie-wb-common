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

package org.kie.workbench.common.forms.cms.components.client.ui.report.entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

@Templated
public class ReportEntryViewImpl implements IsElement,
                                            ReportEntryView {

    @Inject
    @DataField
    private Div content;

    @Inject
    @DataField
    private Button preview;

    private DynamicFormRenderer formRenderer;

    private Presenter presenter;

    @Inject
    public ReportEntryViewImpl(DynamicFormRenderer formRenderer) {
        this.formRenderer = formRenderer;
    }

    @PostConstruct
    public void initialize() {
        DOMUtil.appendWidgetToElement(content, formRenderer);
    }

    @Override
    public void display(FormRenderingContext context) {
        formRenderer.render(context);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler
    public void onPreview(@ForEvent("click") Event event) {
        presenter.onPreview();
    }
}
