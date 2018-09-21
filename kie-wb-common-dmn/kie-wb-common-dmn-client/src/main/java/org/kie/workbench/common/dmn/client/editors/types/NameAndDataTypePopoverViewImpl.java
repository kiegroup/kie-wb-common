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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

@Templated
@ApplicationScoped
public class NameAndDataTypePopoverViewImpl implements NameAndDataTypePopoverView {

    @DataField("nameEditor")
    private Input nameEditor;

    @DataField("typeRefSelector")
    private DataTypePickerWidget typeRefEditor;

    @DataField("popover")
    private Div popoverElement;

    @DataField("popover-content")
    private Div popoverContentElement;

    @DataField("nameLabel")
    private Span nameLabel;

    @DataField("dataTypeLabel")
    private Span dataTypeLabel;

    private JQueryProducer.JQuery<Popover> jQueryPopover;

    private TranslationService translationService;

    private Popover popover;

    private Presenter presenter;

    public NameAndDataTypePopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public NameAndDataTypePopoverViewImpl(final Input nameEditor,
                                          final DataTypePickerWidget typeRefEditor,
                                          final Div popoverElement,
                                          final Div popoverContentElement,
                                          final Span nameLabel,
                                          final Span dataTypeLabel,
                                          final JQueryProducer.JQuery<Popover> jQueryPopover,
                                          final TranslationService translationService) {
        this.nameEditor = nameEditor;
        this.typeRefEditor = typeRefEditor;
        this.popoverElement = popoverElement;
        this.popoverContentElement = popoverContentElement;
        this.nameLabel = nameLabel;
        this.dataTypeLabel = dataTypeLabel;
        this.jQueryPopover = jQueryPopover;
        this.translationService = translationService;

        this.nameLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_NameLabel));
        this.dataTypeLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.NameAndDataTypePopover_DataTypeLabel));
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        typeRefEditor.addValueChangeHandler(e -> presenter.setTypeRef(e.getValue()));
    }

    @Override
    public void setDMNModel(final DMNModelInstrumentedBase dmnModel) {
        typeRefEditor.setDMNModel(dmnModel);
    }

    @Override
    public void initName(final String name) {
        nameEditor.setValue(name);
    }

    @Override
    public void initSelectedTypeRef(final QName typeRef) {
        typeRefEditor.setValue(typeRef,
                               false);
    }

    @Override
    public void show(final Optional<String> editorTitle) {
        final PopoverOptions options = new PopoverOptions();
        options.setContent((element) -> popoverContentElement);
        options.setHtml(true);

        editorTitle.ifPresent(t -> popoverElement.setAttribute("title", t));
        popover = jQueryPopover.wrap(this.getElement());
        popover.popover(options);
        popover.show();
    }

    @Override
    public void hide() {
        if (Objects.nonNull(popover)) {
            popover.hide();
            popover.destroy();
        }
    }

    @EventHandler("nameEditor")
    @SuppressWarnings("unused")
    void onNameChange(final BlurEvent event) {
        presenter.setName(nameEditor.getValue());
    }
}
