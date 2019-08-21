/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorEditorWidgetBaseTest;

import static org.mockito.Mockito.mock;

public class SLASettingsFieldEditorWidgetTest
        extends FieldEditorEditorWidgetBaseTest<String, SLASettingsFieldEditorPresenter, SLASettingsFieldEditorWidget, SLASettingsFieldEditorPresenter.View> {

    @Override
    public SLASettingsFieldEditorPresenter.View mockEditorView() {
        return mock(SLASettingsFieldEditorPresenter.View.class);
    }

    @Override
    public SLASettingsFieldEditorPresenter mockEditorPresenter() {
        return mock(SLASettingsFieldEditorPresenter.class);
    }

    @Override
    public SLASettingsFieldEditorWidget newEditorWidget(SLASettingsFieldEditorPresenter editor) {
        return new SLASettingsFieldEditorWidget(editor) {
            @Override
            protected void initWidget(Widget widget) {
                //avoid GWT client processing for testing purposes.
            }

            @Override
            protected Widget getWrapperWidget(HTMLElement element) {
                //avoid GWT client processing for testing purposes.
                return wrapperWidget;
            }
        };
    }

    @Override
    public String mockValue() {
        return "P6D";
    }
}
