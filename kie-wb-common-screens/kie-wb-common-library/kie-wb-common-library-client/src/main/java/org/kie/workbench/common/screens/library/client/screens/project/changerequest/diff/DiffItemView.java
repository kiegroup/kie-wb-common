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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.diff2html.Diff2Html;
import org.uberfire.ext.widgets.common.client.diff2html.DiffOutputFormat;

@Dependent
@Templated
public class DiffItemView implements DiffItemPresenter.View,
                                     IsElement {

    private static int nextId = 0;

    private DiffItemPresenter presenter;

    @Inject
    @DataField("conflict-label")
    @Named("span")
    private HTMLElement conflictLabel;

    @Inject
    @DataField("change-type")
    @Named("span")
    private HTMLElement changeType;

    @Inject
    @DataField("filename")
    @Named("span")
    private HTMLElement filename;

    @Inject
    @DataField("collapse-container")
    private HTMLDivElement collapseContainer;

    @Inject
    @DataField("collapse-link")
    private HTMLAnchorElement collapseLink;

    @Inject
    @DataField("textual-diff-container")
    private HTMLDivElement textualDiffContainer;

    @Inject
    @DataField("custom-diff-container")
    private HTMLDivElement customDiffContainer;

    @Inject
    @DataField("left-container")
    private HTMLDivElement leftContainer;

    @Inject
    @DataField("right-container")
    private HTMLDivElement rightContainer;

    private Diff2Html diff2Html;

    @Override
    public void init(DiffItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupTextual(final String filename,
                             final String changeType,
                             final String diffText,
                             final boolean isUnified,
                             final boolean conflict) {
        this.setup(filename, changeType, conflict);

        this.textualDiffContainer.hidden = false;

        this.diff2Html = new Diff2Html(this.textualDiffContainer.id,
                                       isUnified ? DiffOutputFormat.LINE_BY_LINE : DiffOutputFormat.SIDE_BY_SIDE,
                                       diffText,
                                       true);
    }

    @Override
    public void setupCustom(final String filename,
                            final String changeType,
                            final boolean conflict) {
        this.setup(filename, changeType, conflict);

        this.customDiffContainer.hidden = false;
    }

    @Override
    public HTMLElement getLeftContainer() {
        return leftContainer;
    }

    @Override
    public HTMLElement getRightContainer() {
        return rightContainer;
    }

    @Override
    public void drawTextual() {
        if (this.diff2Html != null) {
            this.diff2Html.draw();
        }
    }

    @Override
    public void expandCustomLeftContainer() {
        this.leftContainer.hidden = false;
        this.leftContainer.style.width = CSSProperties.WidthUnionType.of("100%");

        this.rightContainer.hidden = true;
    }

    @Override
    public void expandCustomRightContainer() {
        this.leftContainer.hidden = true;

        this.rightContainer.hidden = false;
        this.rightContainer.style.width = CSSProperties.WidthUnionType.of("100%");
    }

    @Override
    public void resetCustomContainers() {
        this.leftContainer.hidden = false;
        this.leftContainer.style.width = CSSProperties.WidthUnionType.of("50%");

        this.rightContainer.hidden = false;
        this.rightContainer.style.width = CSSProperties.WidthUnionType.of("50%");
    }

    private void setup(final String filename,
                       final String changeType,
                       final boolean conflict) {
        final String elementId = "_" + DiffItemView.class.getSimpleName() + "GWT" + nextId++;
        final String containerId = "_collapsible-container" + elementId;

        this.filename.textContent = filename;
        this.changeType.textContent = changeType + " - ";

        this.textualDiffContainer.id = "_textual-diff-container" + elementId;
        this.customDiffContainer.id = "_custom-diff-container" + elementId;

        this.textualDiffContainer.hidden = true;
        this.customDiffContainer.hidden = true;

        this.collapseContainer.id = containerId;
        this.collapseLink.href = "#" + containerId;

        this.conflictLabel.hidden = !conflict;
    }
}