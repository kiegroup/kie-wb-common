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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors;

import java.util.List;

public abstract class ImportsEditor<T> implements ImportsEditorView.Presenter<T> {

    ImportsEditorView<T> view;

    @Override
    public void setView(ImportsEditorView<T> view) {
        this.view = view;
    }

    @Override
    public void addImport() {
        List<T> imports = view.getImports();
        T imp = createImport();
        imports.add(imp);
    }

    @Override
    public void removeImport(final T imp) {
        List<T> imports = view.getImports();
        imports.remove(imp);
    }
}
