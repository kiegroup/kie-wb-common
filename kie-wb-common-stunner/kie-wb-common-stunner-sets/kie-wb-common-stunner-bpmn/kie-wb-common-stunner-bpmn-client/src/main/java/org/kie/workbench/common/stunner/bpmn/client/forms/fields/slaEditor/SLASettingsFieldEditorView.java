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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor.TimerSettingsFieldEditorView;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.ValidationState;

@Templated
public class SLASettingsFieldEditorView
        implements IsElement,
                   SLASettingsFieldEditorPresenter.View {

    static final String EMPTY_VALUE = "";

    static final String TIME_DURATION_PLACE_HOLDER = "SLASettingsFieldEditorView.TimeDuration_Placeholder";

    static final String DURATION_TIMER_HELP_HEADER = "SLASettingsFieldEditorView.DurationTimer_Help_Header";

    static final String DURATION_TIMER_HELP_LINE_1 = "SLASettingsFieldEditorView.DurationTimer_Help_Line_1";

    static final String EXPRESSION_HELP_LINE = "SLASettingsFieldEditorView.Expression_Help_Line";

    static final String PLACEHOLDER_ATTR = "placeholder";

    static final String DATA_CONTENT_ATTR = "data-content";

    @Inject
    @DataField("sla-duration-timer-help")
    private Anchor durationTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> durationTimerHelpPopover;

    @Inject
    @DataField("sla-duration-timer-params")
    private Div durationTimerParamsContainer;

    @Inject
    @DataField("sla-time-duration")
    private TextInput timeDuration;
    @Inject
    private ClientTranslationService translationService;

    @Inject
    @DataField
    private Div slaInputFormGroup;

    @Inject
    @DataField
    private Span slaInputHelpBlock;

    @Inject
    private Validator validator;

    private SLASettingsFieldEditorPresenter presenter;

    @Override
    public void init(SLASettingsFieldEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        timeDuration.setAttribute(PLACEHOLDER_ATTR,
                                  translationService.getValue(TIME_DURATION_PLACE_HOLDER));

        durationTimerHelp.setAttribute(DATA_CONTENT_ATTR,
                                       getDurationTimerHtmlHelpText());
        durationTimerHelpPopover.wrap(durationTimerHelp).popover();
    }

    @Override
    public void setTimeDuration(String timeDuration) {
        this.timeDuration.setValue(timeDuration);
    }

    @Override
    public String getTimeDuration() {
        return timeDuration.getValue();
    }

    @Override
    public void clear() {
        setTimeDuration(EMPTY_VALUE);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        timeDuration.setDisabled(readOnly);
    }

    @Override
    public boolean isValid() {
        Set<ConstraintViolation<SLADueDate>> violations = validator.validate(new SLADueDate(timeDuration.getValue()));
        if (violations.isEmpty()) {
            clearErrors();
            return true;
        } else {
            showError(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(",")));
            return false;
        }
    }

    private void showError(String errorMessage) {
        DOMUtil.addCSSClass(slaInputFormGroup, ValidationState.ERROR.getCssName());
        slaInputHelpBlock.setTextContent(errorMessage);
    }

    private void clearErrors() {
        DOMUtil.removeCSSClass(slaInputFormGroup, ValidationState.ERROR.getCssName());
        slaInputHelpBlock.setTextContent("");
    }

    private String getDurationTimerHtmlHelpText() {
        return TimerSettingsFieldEditorView.buildHtmlHelpText(translationService.getValue(DURATION_TIMER_HELP_HEADER),
                                                              translationService.getValue(DURATION_TIMER_HELP_LINE_1),
                                                              translationService.getValue(EXPRESSION_HELP_LINE));
    }

    @EventHandler("sla-time-duration")
    void onTimeDurationChange(@ForEvent("change") final Event event) {
        presenter.onTimerDurationChange();
    }
}
