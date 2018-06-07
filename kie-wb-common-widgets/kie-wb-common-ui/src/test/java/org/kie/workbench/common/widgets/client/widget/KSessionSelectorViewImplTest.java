/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class KSessionSelectorViewImplTest {

    @Mock
    private Document document;

    @Mock
    private Select kBaseSelect;

    @Mock
    private Select kSessionSelect;

    @Mock
    private Label warningLabel;

    @Mock
    private KSessionSelector presenter;

    @Mock
    private Option option;

    private KSessionSelectorViewImpl kSessionSelectorView;

    @Before
    public void setUp() throws Exception {
        kSessionSelectorView = spy(new KSessionSelectorViewImpl(document,
                                                                kBaseSelect,
                                                                kSessionSelect,
                                                                warningLabel));
        kSessionSelectorView.setPresenter(presenter);
    }

    @Test
    public void testSetSelected() throws Exception {
        kSessionSelectorView.setSelected("kbaseName",
                                         "ksessionName");

        verify(kSessionSelectorView).onSelectionChange();
    }

    @Test
    public void testName() throws Exception {
        kSessionSelectorView.onKSessionSelected(null);

        verify(kSessionSelectorView).onSelectionChange();
    }

    @Test
    public void testSetKSessions() {
        final String sessionName = "defaultSession";
        final List<String> sessions = Collections.singletonList(sessionName);
        doReturn(option).when(document).createElement("option");

        kSessionSelectorView.setKSessions(sessions);

        verify(kSessionSelect).add(option);
        verify(option).setText(sessionName);
        verify(option).setValue(sessionName);
    }
}