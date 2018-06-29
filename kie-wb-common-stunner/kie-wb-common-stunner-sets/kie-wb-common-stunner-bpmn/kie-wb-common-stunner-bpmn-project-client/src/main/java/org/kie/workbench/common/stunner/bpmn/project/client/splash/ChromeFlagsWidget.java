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

import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.project.client.resources.i18n.ChromeFlagsSplashScreenConstants;
import org.uberfire.workbench.events.NotificationEvent;

public class ChromeFlagsWidget extends Composite {

    interface ChromeFlagsUIBinder extends UiBinder<Widget, ChromeFlagsWidget> {

    }

    private static final ChromeFlagsUIBinder UI_BINDER = GWT.create(ChromeFlagsUIBinder.class);

    private Event<NotificationEvent> notification;

    public ChromeFlagsWidget(Event<NotificationEvent> notification) {
        initWidget(UI_BINDER.createAndBindUi(this));
        this.notification = notification;
    }

    @UiHandler("flag1CopyLink")
    void handleFlag1Click(ClickEvent e) {
        copyUrlToClipboard(ChromeFlagsSplashScreenConstants.INSTANCE.flag1URL());
    }

    @UiHandler("flag2CopyLink")
    void handleFlag2Click(ClickEvent e) {
        copyUrlToClipboard(ChromeFlagsSplashScreenConstants.INSTANCE.flag2URL());
    }

    @UiHandler("flag3CopyLink")
    void handleFlag3Click(ClickEvent e) {
        copyUrlToClipboard(ChromeFlagsSplashScreenConstants.INSTANCE.flag3URL());
    }

    private void copyUrlToClipboard(String url) {
        if (copyStringToClipboard(url)) {

            String message = ChromeFlagsSplashScreenConstants.INSTANCE.copySuccessStart() +
                    " " +
                    url +
                    " " +
                    ChromeFlagsSplashScreenConstants.INSTANCE.copySuccessEnd();
            NotificationEvent successEvent = new NotificationEvent(message,
                                                                   NotificationEvent.NotificationType.SUCCESS);
            notification.fire(successEvent);
        } else {

            String message = url + " " + ChromeFlagsSplashScreenConstants.INSTANCE.copySuccessEnd();
            NotificationEvent errorEvent = new NotificationEvent(message,
                                                                 NotificationEvent.NotificationType.ERROR);
            notification.fire(errorEvent);
        }
    }

    private static native boolean copyStringToClipboard(String string)/*-{
        if (window.clipboardData && window.clipboardData.setData) {
            return clipboardData.setData("Text", link);

        } else if (document.queryCommandSupported && document.queryCommandSupported("copy")) {
            var textarea = document.createElement("textarea");
            textarea.textContent = string;
            textarea.style.position = "fixed";
            document.body.appendChild(textarea);
            textarea.select();
            try {
                document.execCommand("copy");
                return true;
            } catch (ex) {
                console.warn("Copy to clipboard failed.", ex);
                return false;
            } finally {
                document.body.removeChild(textarea);
            }
        }
    }-*/;
}