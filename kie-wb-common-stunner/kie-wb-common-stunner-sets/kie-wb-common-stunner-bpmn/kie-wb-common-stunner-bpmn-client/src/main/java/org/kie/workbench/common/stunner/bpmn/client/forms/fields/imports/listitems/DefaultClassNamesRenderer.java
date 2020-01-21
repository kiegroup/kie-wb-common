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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems;

import java.io.IOException;

import com.google.gwt.text.shared.Renderer;

class DefaultClassNamesRenderer implements Renderer<String> {

    @Override
    public String render(final String object) {
        return object != null ? object : "";
    }

    @Override
    public void render(final String object, final Appendable appendable) throws IOException {
        String s = render(object);
        appendable.append(s);
    }
}
