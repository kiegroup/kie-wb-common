/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.home.client.widgets.home;

import org.jboss.errai.ui.client.local.spi.TemplateProvider;
import org.jboss.errai.ui.client.local.spi.TemplateRenderingCallback;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated(stylesheet = "home-view-style.less", provider = HomeViewStyle.EmptyTemplateProvider.class)
public class HomeViewStyle {

    // FIXME: this class is here only to use errai-ui to process the .less style file that is used in the javascript code.
    public class EmptyTemplateProvider implements TemplateProvider {

        @Override
        public void provideTemplate(String location, TemplateRenderingCallback renderingCallback) {
            // does nothing since there's no template
        }
    }

}
