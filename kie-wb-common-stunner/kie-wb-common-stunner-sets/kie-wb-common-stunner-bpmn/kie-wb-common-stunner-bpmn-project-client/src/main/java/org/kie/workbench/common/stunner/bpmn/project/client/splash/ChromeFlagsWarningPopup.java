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

package org.kie.workbench.common.stunner.bpmn.project.client.splash;

import java.util.Arrays;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.bpmn.project.client.editor.BPMNDiagramEditor;
import org.kie.workbench.common.stunner.client.widgets.popups.chrome.ChromeFlagsWarningPopupPresenter;
import org.uberfire.client.annotations.SplashBodyHeight;
import org.uberfire.client.annotations.SplashFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

@WorkbenchSplashScreen(identifier = ChromeFlagsWarningPopup.SPLASH_ID)
public class ChromeFlagsWarningPopup {

    public static final String SPLASH_ID = "ChromeFlagsWarningPopup.splash";

    @Inject
    private ChromeFlagsWarningPopupPresenter presenter;

    @WorkbenchPartTitle
    public String getTitle() {
        return presenter.getTitle();
    }

    @WorkbenchPartView
    public Widget getView() {
        return ElementWrapperWidget.getWidget(presenter.getView().getElement());
    }

    @SplashFilter
    public SplashScreenFilter getFilter() {
        if (presenter.isFullBuildMode() &&
                presenter.isGoogleChrome()) {
            return new SplashScreenFilterImpl("chromeFlags.splash",
                                              true,
                                              Arrays.asList(BPMNDiagramEditor.EDITOR_ID));
        } else {
            return null;
        }
    }

    @SplashBodyHeight
    public Integer getBodySize() {
        return 310;
    }
}
