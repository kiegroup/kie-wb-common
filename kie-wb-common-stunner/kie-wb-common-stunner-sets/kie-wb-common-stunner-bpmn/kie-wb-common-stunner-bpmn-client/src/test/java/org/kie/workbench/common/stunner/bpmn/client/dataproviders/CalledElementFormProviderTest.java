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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestProcessDataEvent;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class CalledElementFormProviderTest {

    @Mock
    private ProcessesDataProvider dataProvider;

    @Mock
    private EventSourceMock<RequestProcessDataEvent> event;

    private CalledElementFormProvider tested;

    @Before
    public void setup() {
        tested = spy(new CalledElementFormProvider());
        tested.dataProvider = dataProvider;
        tested.requestProcessDataEvent = event;
        doAnswer(i -> {
            ((com.google.gwt.user.client.Command) i.getArguments()[0]).execute();
            return null;
        }).when(tested).scheduleServiceCall(any());
    }

    @Test
    public void testGetProviderName() {
        assertEquals(tested.getClass().getSimpleName(), tested.getProviderName());
    }

    @Test
    public void testGetSelectorData() {
        List<String> names = Arrays.asList("p1", "p2", "p3");
        when(dataProvider.getProcessIds()).thenReturn(names);
        FormRenderingContext context = mock(FormRenderingContext.class);
        SelectorData data = tested.getSelectorData(context);
        Map values = data.getValues();
        assertNotNull(values);
        assertEquals(3, values.size());
        assertTrue(values.containsKey("p1"));
        assertTrue(values.containsKey("p2"));
        assertTrue(values.containsKey("p3"));
        verify(event, times(1)).fire(any(RequestProcessDataEvent.class));
    }
}
