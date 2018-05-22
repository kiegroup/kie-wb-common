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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.project.client.editor.BPMNDiagramEditor;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.i18n.ChromeFlagsSplashScreenConstants;
import org.uberfire.client.annotations.SplashBodyHeight;
import org.uberfire.client.annotations.SplashFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

@WorkbenchSplashScreen(identifier = "chromeFlags.splash", isEnabled = false)
public class ChromeFlagsSplashScreen {

    @WorkbenchPartTitle
    public String getTitle() {
        return ChromeFlagsSplashScreenConstants.INSTANCE.FlagsTitle();
    }

    @WorkbenchPartView
    public Widget getView() {
        return new FlowPanel() {{
            String html = "<html>" +
                    "<p align=\"center\">" + ChromeFlagsSplashScreenConstants.INSTANCE.FlagsMessage1() + "<br/><br/>" +
                    ChromeFlagsSplashScreenConstants.INSTANCE.Flag1() + "</br>" +
                    ChromeFlagsSplashScreenConstants.INSTANCE.Flag2() + "</br>" +
                    ChromeFlagsSplashScreenConstants.INSTANCE.Flag3() + "</br></br>" +
                    ChromeFlagsSplashScreenConstants.INSTANCE.FlagsMessage2() + "</p></html>";
            add(new HTML(html));
        }};
    }

    @SplashFilter
    public SplashScreenFilter getFilter() {
        if (isDevelopmentMode() && isGoogleChrome()) {
            return new SplashScreenFilterImpl("chromeFlags.splash",
                                              true,
                                              Arrays.asList(BPMNDiagramEditor.EDITOR_ID));
        } else {
            return null;
        }
    }

    @SplashBodyHeight
    public Integer getBodySize() {
        return 160;
    }

    private static native boolean isGoogleChrome()/*-{
        var isChromium = window.chrome;
        var winNav = window.navigator;
        var vendorName = winNav.vendor;
        var isOpera = typeof window.opr !== "undefined";
        var isIEedge = winNav.userAgent.indexOf("Edge") > -1;
        var isIOSChrome = winNav.userAgent.match("CriOS");
        var isYandex = winNav.userAgent.match("YaBrowser");

        if (isIOSChrome) {
            return true;
        } else if (isChromium !== null &&
                    typeof isChromium !== "undefined" &&
                    vendorName === "Google Inc." &&
                    isOpera === false &&
                    isIEedge === false &&
                    isYandex === null) {
            return true;
        } else {
            return false;
        }
    }-*/;

    private boolean isDevelopmentMode() {
        return !GWT.isProdMode() && GWT.isClient();
    }
}

