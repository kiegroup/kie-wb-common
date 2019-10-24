/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.entrypoint;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.util.Clipboard;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static elemental2.dom.DomGlobal.console;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Templated
@ApplicationScoped
public class GenericErrorPopup extends Elemental2Modal<GenericErrorPopup> implements Elemental2Modal.View<GenericErrorPopup> {

    @Inject
    @DataField("header")
    private HTMLDivElement header;

    @Inject
    @DataField("body")
    private HTMLDivElement body;

    @Inject
    @DataField("footer")
    private HTMLDivElement footer;

    @Inject
    @DataField("continue-button")
    private HTMLButtonElement continueButton;

    @Inject
    @DataField("error-details-section")
    private Div errorDetailsSection;

    @Inject
    @DataField("error-details")
    private HTMLTextAreaElement errorDetails;

    @Inject
    @DataField("chevron-right")
    private Anchor chevronRight;

    @Inject
    @DataField("chevron-down")
    private Anchor chevronDown;

    @Inject
    @DataField("copy-details")
    private Anchor copyDetails;

    @Inject
    private HTMLTextAreaElement clipboardText;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    private final Clipboard clipboard;

    private Command onClose = () -> {
    };

    private String errorText;
    @Inject
    public GenericErrorPopup(final GenericErrorPopup view,
                             final Clipboard clipboard) {
        super(view);
        this.clipboard = clipboard;
    }

    @PostConstruct
    public void init() {
        super.setup();
        this.getModal().addHiddenHandler(e -> {
            errorDetails.textContent = "";
            errorText = "";
        });
        setShowDetailshandler();
        setCopyhandler();
    }

    @Override
    public void init(final GenericErrorPopup this_) {
    }

    public void setup(final String details) {
        setup(details,
              () -> {});
    }

    public void setup(final String details,
                      final Command onClose) {
        if (isShowing()) {
            //If multiple errors occur, we want to know the details of each one of them. In order.
            errorDetails.textContent += " | " + details;
        } else {
            errorDetails.textContent = details;
        }
        errorText = errorDetails.textContent;
        this.onClose = onClose;
    }

    public void close() {
        hide();
        this.onClose.execute();
    }

    private void setShowDetailshandler() {

        chevronRight.setOnclick(event -> {
            chevronDown.setHidden(false);
            chevronRight.setHidden(true);
            errorDetailsSection.setHidden(false);
        });

        chevronDown.setOnclick(event -> {
            chevronRight.setHidden(false);
            chevronDown.setHidden(true);
            errorDetailsSection.setHidden(true);
        });
    }

    private void setCopyhandler() {
        copyDetails.setOnclick(event -> {
            clipboardText.textContent = errorText;
            final boolean copySucceeded = clipboard.copy(clipboardText);

            if (copySucceeded) {
                notificationEvent.fire(new NotificationEvent(DefaultWorkbenchConstants.INSTANCE.ErrorDetailsSuccessfullyCopiedToClipboard(), SUCCESS));
            } else {
                notificationEvent.fire(new NotificationEvent(DefaultWorkbenchConstants.INSTANCE.ErrorDetailsFailedToBeCopiedToClipboard(), WARNING));
            }

            console.error(errorText);
            hide();
        });
    }

    @EventHandler("continue-button")
    private void onContinueButtonClicked(final @ForEvent("click") elemental2.dom.Event e) {
        console.error(errorText);
        close();
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }
}
