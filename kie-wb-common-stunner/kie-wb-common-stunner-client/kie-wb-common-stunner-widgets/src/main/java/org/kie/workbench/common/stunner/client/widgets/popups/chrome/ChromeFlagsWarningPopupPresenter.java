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

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.ChromeFlagsWarningPopupConstants;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.workbench.events.NotificationEvent;

public class ChromeFlagsWarningPopupPresenter {

    public interface View extends IsElement,
                                  UberElement<ChromeFlagsWarningPopupPresenter> {

    }

    private final View view;
    private final Event<NotificationEvent> notification;
    private TranslationService translationService;

    @Inject
    public ChromeFlagsWarningPopupPresenter(final View view,
                                            final Event<NotificationEvent> notification,
                                            final TranslationService translationService) {
        this.view = view;
        this.notification = notification;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return translate(ChromeFlagsWarningPopupConstants.Flags_Title);
    }

    public void copyUrlToClipboard(String url) {
        if (copyStringToClipboard(url)) {

            String message = translate(ChromeFlagsWarningPopupConstants.Copy_Success_Start) +
                    " " +
                    url +
                    " " +
                    translate(ChromeFlagsWarningPopupConstants.Copy_Success_End);
            NotificationEvent successEvent = new NotificationEvent(message,
                                                                   NotificationEvent.NotificationType.SUCCESS);
            notification.fire(successEvent);
        } else {
            String message = translate(ChromeFlagsWarningPopupConstants.Copy_Error_Start) +
                    " " +
                    url +
                    " " +
                    translate(ChromeFlagsWarningPopupConstants.Copy_Error_End);
            NotificationEvent errorEvent = new NotificationEvent(message,
                                                                 NotificationEvent.NotificationType.ERROR);
            notification.fire(errorEvent);
        }
    }

    public static native boolean isGoogleChrome()/*-{
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

    public String translate(final String translationKey) {
        return translationService.getTranslation(translationKey);
    }
}
