/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorViewImplTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems.DefaultImportListItemViewImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultImportsEditorViewImplTest extends ImportsEditorViewImplTest<DefaultImport, DefaultImportListItemViewImpl> {

    private DefaultImportsEditorViewImpl concreteTested;

    @Before
    public void setUp() {
        super.setUp();

        concreteTested = (DefaultImportsEditorViewImpl) tested;
        concreteTested.importsDataBinder = dataBinder;
        concreteTested.importComponents = listComponent;
        concreteTested.importsTable = tableElement;
        concreteTested.addImportButton = button;
        concreteTested.presenter = (DefaultImportsEditor) presenter;
    }

    @Override
    protected ImportsEditorViewImpl<DefaultImport, DefaultImportListItemViewImpl> spyEditor() {
        return spy(DefaultImportsEditorViewImpl.class);
    }

    @Override
    protected ImportsEditorView.Presenter<DefaultImport> mockPresenter() {
        return mock(DefaultImportsEditor.class);
    }
}