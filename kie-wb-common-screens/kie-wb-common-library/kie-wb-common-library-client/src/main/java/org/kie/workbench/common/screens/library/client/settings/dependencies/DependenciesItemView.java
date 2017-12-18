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

package org.kie.workbench.common.screens.library.client.settings.dependencies;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DependenciesItemView implements DependenciesItemPresenter.View,
                                             IsElement {

    private DependenciesItemPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("selected")
    private HTMLInputElement selected;

    @Inject
    @DataField("group-id")
    private HTMLDivElement groupId;

    @Inject
    @DataField("artifact-id")
    private HTMLDivElement artifactId;

    @Inject
    @DataField("version")
    private HTMLDivElement version;

    @Inject
    @DataField("package-white-list")
    @Named("span")
    private HTMLElement packageWhiteList;

    @Inject
    @DataField("white-list-add-all")
    private HTMLInputElement whiteListAll;

    @Inject
    @DataField("white-list-add-none")
    private HTMLInputElement whiteListNone;

    @Inject
    @DataField("delete")
    private HTMLAnchorElement delete;

    @Override
    public void init(final DependenciesItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGroupId(final String groupId) {
        this.groupId.innerHTML = groupId;
    }

    @Override
    public void setArtifactId(final String artifactId) {
        this.artifactId.innerHTML = artifactId;
    }

    @Override
    public void setVersion(final String version) {
        this.version.innerHTML = version;
    }

    @Override
    public void setPackageWhiteList(final String packageWhiteListKey) {
        this.packageWhiteList.innerHTML = translationService.format(packageWhiteListKey);
    }

    @EventHandler("white-list-add-all")
    public void whiteListAddAll(final ClickEvent event) {
        presenter.whiteListAddAll();
    }

    @EventHandler("white-list-add-none")
    public void whiteListAddNone(final ClickEvent event) {
        presenter.whiteListAddNone();
    }

    @EventHandler("delete")
    public void delete(final ClickEvent event) {
        presenter.delete();
    }
}
