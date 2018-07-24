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

import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.cms.components.client.ui.report.preview.ObjectPreview;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;

@Dependent
public class ReportEntry implements IsElement,
                                    ReportEntryView.Presenter {

    private ObjectPreview preview;

    private ReportEntryView view;

    private Map<String, Object> instance;

    private FormRenderingContext tableContext;

    private FormRenderingContext previewContext;

    @Inject
    public ReportEntry(ReportEntryView view,
                       ObjectPreview preview) {
        this.view = view;
        this.preview = preview;
        view.init(this);
    }

    public void init(Map<String, Object> instance,
                     FormRenderingContext tableContext,
                     FormRenderingContext previewContext) {
        PortablePreconditions.checkNotNull("instance", instance);
        PortablePreconditions.checkNotNull("tableContext", tableContext);
        PortablePreconditions.checkNotNull("previewContext", previewContext);

        this.instance = instance;
        this.tableContext = tableContext;
        this.previewContext = previewContext;

        tableContext.setModel(instance);
        tableContext.setRenderMode(RenderMode.PRETTY_MODE);

        view.display(tableContext);
    }

    @Override
    public void onPreview() {
        preview.init(previewContext);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
