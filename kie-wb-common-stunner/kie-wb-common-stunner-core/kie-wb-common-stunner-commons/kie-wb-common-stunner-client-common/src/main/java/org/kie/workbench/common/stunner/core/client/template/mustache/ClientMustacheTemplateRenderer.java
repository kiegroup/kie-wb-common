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

package org.kie.workbench.common.stunner.core.client.template.mustache;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;

@ApplicationScoped
public class ClientMustacheTemplateRenderer implements MustacheTemplateRenderer<Object> {

    @PostConstruct
    protected void init() {
        final MustacheSource source = GWT.create(MustacheSource.class);
        inject(source.mustache().getText());
    }

    private void inject(final String raw) {
        final ScriptInjector.FromString jsPdfScript = ScriptInjector.fromString(raw);
        jsPdfScript.setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
    }

    public String render(String template, Object data) {
        final String response = Mustache.to_html(template, data);
        return response;
    }
}
