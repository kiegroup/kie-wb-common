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

package org.kie.workbench.common.stunner.client.widgets.popups.chrome;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.ChromeFlagsWarningPopupConstants;

@Templated
public class ChromeFlagsWarningPopupView
        implements ChromeFlagsWarningPopupPresenter.View {

    @Inject
    @DataField("flag-1-link")
    private Anchor flag1link;

    @Inject
    @DataField("flag-2-link")
    private Anchor flag2link;

    @Inject
    @DataField("flag-3-link")
    private Anchor flag3link;

    private ChromeFlagsWarningPopupPresenter presenter;

    @Override
    public void init(ChromeFlagsWarningPopupPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("flag-1-link")
    private void onFlag1Click(final ClickEvent event) {
        presenter.copyUrlToClipboard(presenter.translate(ChromeFlagsWarningPopupConstants.Flag_1_URL));
    }

    @EventHandler("flag-2-link")
    private void onFlag2Click(final ClickEvent event) {
        presenter.copyUrlToClipboard(presenter.translate(ChromeFlagsWarningPopupConstants.Flag_2_URL));
    }

    @EventHandler("flag-3-link")
    private void onFlag3Click(final ClickEvent event) {
        presenter.copyUrlToClipboard(presenter.translate(ChromeFlagsWarningPopupConstants.Flag_3_URL));
    }
}