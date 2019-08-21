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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.TextInput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.DATA_CONTENT_ATTR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.DURATION_TIMER_HELP_HEADER;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.DURATION_TIMER_HELP_LINE_1;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.EXPRESSION_HELP_LINE;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.PLACEHOLDER_ATTR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.TIME_DURATION_PLACE_HOLDER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SLASettingsFieldEditorViewTest {

    private static final String SOME_VALUE = "SOME_VALUE";

    @Mock
    private Anchor slaDurationTimerHelp;

    @Mock
    private JQueryProducer.JQuery<Popover> slaDurationTimerHelpPopover;

    @Mock
    private Popover dateTimerHelpPopoverWrapped;

    @Mock
    private Div slaDurationTimerParamsContainer;

    @Mock
    private CSSStyleDeclaration slaDurationTimerParamsContainerCSS;

    @Mock
    private TextInput slaTimeDuration;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private SLASettingsFieldEditorPresenter presenter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private SLASettingsFieldEditorView view;

    @Before
    public void setUp() {
        view.init(presenter);
        when(slaDurationTimerParamsContainer.getStyle()).thenReturn(slaDurationTimerParamsContainerCSS);
        when(slaDurationTimerHelpPopover.wrap(slaDurationTimerHelp)).thenReturn(dateTimerHelpPopoverWrapped);
        when(translationService.getValue(TIME_DURATION_PLACE_HOLDER)).thenReturn(TIME_DURATION_PLACE_HOLDER);
        when(translationService.getValue(DURATION_TIMER_HELP_HEADER)).thenReturn(DURATION_TIMER_HELP_HEADER);
        when(translationService.getValue(DURATION_TIMER_HELP_LINE_1)).thenReturn(DURATION_TIMER_HELP_LINE_1);
        when(translationService.getValue(EXPRESSION_HELP_LINE)).thenReturn(EXPRESSION_HELP_LINE);
        when(translationService.getValue(EXPRESSION_HELP_LINE)).thenReturn(EXPRESSION_HELP_LINE);
        when(translationService.getValue(EXPRESSION_HELP_LINE)).thenReturn(EXPRESSION_HELP_LINE);
    }

    @Test
    public void testInit() {
        view.init();
        verify(slaTimeDuration).setAttribute(PLACEHOLDER_ATTR, TIME_DURATION_PLACE_HOLDER);

        String expectedDurationHelp = DURATION_TIMER_HELP_HEADER + ":" + "<UL>" +
                "<LI>" + DURATION_TIMER_HELP_LINE_1 + "</LI>" +
                "<LI>" + EXPRESSION_HELP_LINE + "</LI>" +
                "</UL>";
        verify(slaDurationTimerHelp).setAttribute(DATA_CONTENT_ATTR, expectedDurationHelp);
    }

    @Test
    public void testSetTimeDuration() {
        view.setTimeDuration(SOME_VALUE);
        verify(slaTimeDuration).setValue(SOME_VALUE);
    }

    @Test
    public void testGetTimeDuration() {
        when(slaTimeDuration.getValue()).thenReturn(SOME_VALUE);
        assertEquals(SOME_VALUE, view.getTimeDuration());
    }

    @Test
    public void testSetReadOnly() {
        final boolean arbitraryValue = false;
        view.setReadOnly(arbitraryValue);
        verify(slaTimeDuration).setDisabled(arbitraryValue);
    }

    @Test
    public void testOnTimeDurationChange() {
        view.onTimeDurationChange(mock(Event.class));
        verify(presenter).onTimerDurationChange();
    }
}
