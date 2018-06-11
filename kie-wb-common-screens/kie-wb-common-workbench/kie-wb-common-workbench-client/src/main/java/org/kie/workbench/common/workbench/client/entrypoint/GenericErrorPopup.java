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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static elemental2.dom.DomGlobal.console;

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
    @DataField("ignore-button")
    private HTMLButtonElement ignoreButton;

    @Inject
    @DataField("copy-details-button")
    private HTMLButtonElement copyDetailsButton;

    @Inject
    @DataField("error-details")
    private HTMLTextAreaElement errorDetails;

    private final Clipboard clipboard;

    @Inject
    public GenericErrorPopup(final GenericErrorPopup view,
                             final Clipboard clipboard) {
        super(view);
        this.clipboard = clipboard;
    }

    @Override
    public void init(final GenericErrorPopup this_) {
    }

    public void setup(final String details) {
        this.errorDetails.textContent = details;
        super.setup();
    }

    @EventHandler("ignore-button")
    private void onIgnoreButtonClicked(final @ForEvent("click") Event e) {
        hide();
    }

    @EventHandler("copy-details-button")
    private void onCopyDetailsButtonClicked(final @ForEvent("click") Event e) {
        clipboard.copy(errorDetails);
        console.error(errorDetails.textContent);
        hide();
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
