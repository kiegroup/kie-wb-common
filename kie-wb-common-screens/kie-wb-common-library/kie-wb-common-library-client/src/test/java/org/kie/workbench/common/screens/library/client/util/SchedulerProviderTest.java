/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.library.client.util;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.CloseableSchedulerProvider.CloseableSchedulerWrapper;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerProviderTest {

    @Mock
    private Scheduler wrappedScheduler;

    @Mock
    private ScheduledCommand mockScheduledCmd;

    @Mock
    private RepeatingCommand mockRepeatCmd;

    @Captor
    private ArgumentCaptor<ScheduledCommand> scheduledCaptor;

    @Captor
    private ArgumentCaptor<RepeatingCommand> repeatCaptor;

    @InjectMocks
    private CloseableSchedulerProvider schedulerProvider;

    private CloseableSchedulerWrapper scheduler;

    @Before
    public void setup() {
        scheduler = schedulerProvider.getScheduler();
    }

    @Test
    public void fixedDelayCalledWhenOpen() throws Exception {
        scheduler.scheduleFixedDelay(mockRepeatCmd, 1000);
        verify(wrappedScheduler).scheduleFixedDelay(repeatCaptor.capture(), eq(1000));

        RepeatingCommand cmd = repeatCaptor.getValue();
        cmd.execute();

        verify(mockRepeatCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void fixedDelayNotCalledWhenClosed() throws Exception {
        scheduler.scheduleFixedDelay(mockRepeatCmd, 1000);
        verify(wrappedScheduler).scheduleFixedDelay(repeatCaptor.capture(), eq(1000));

        RepeatingCommand cmd = repeatCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockRepeatCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void fixedPeriodCalledWhenOpen() throws Exception {
        scheduler.scheduleFixedPeriod(mockRepeatCmd, 1000);
        verify(wrappedScheduler).scheduleFixedPeriod(repeatCaptor.capture(), eq(1000));

        RepeatingCommand cmd = repeatCaptor.getValue();
        cmd.execute();

        verify(mockRepeatCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void fixedPeriodNotCalledWhenClosed() throws Exception {
        scheduler.scheduleFixedPeriod(mockRepeatCmd, 1000);
        verify(wrappedScheduler).scheduleFixedPeriod(repeatCaptor.capture(), eq(1000));

        RepeatingCommand cmd = repeatCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockRepeatCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void deferredCalledWhenOpen() throws Exception {
        scheduler.scheduleDeferred(mockScheduledCmd);
        verify(wrappedScheduler).scheduleDeferred(scheduledCaptor.capture());

        ScheduledCommand cmd = scheduledCaptor.getValue();
        cmd.execute();

        verify(mockScheduledCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void deferredNotCalledWhenClosed() throws Exception {
        scheduler.scheduleDeferred(mockScheduledCmd);
        verify(wrappedScheduler).scheduleDeferred(scheduledCaptor.capture());

        ScheduledCommand cmd = scheduledCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockScheduledCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void entryWithScheduledCommandCalledWhenOpen() throws Exception {
        scheduler.scheduleEntry(mockScheduledCmd);
        verify(wrappedScheduler).scheduleEntry(scheduledCaptor.capture());

        ScheduledCommand cmd = scheduledCaptor.getValue();
        cmd.execute();

        verify(mockScheduledCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void entryWithScheduledCommandNotCalledWhenClosed() throws Exception {
        scheduler.scheduleEntry(mockScheduledCmd);
        verify(wrappedScheduler).scheduleEntry(scheduledCaptor.capture());

        ScheduledCommand cmd = scheduledCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockScheduledCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void entryWithRepeatingCommandCalledWhenOpen() throws Exception {
        scheduler.scheduleEntry(mockRepeatCmd);
        verify(wrappedScheduler).scheduleEntry(repeatCaptor.capture());

        RepeatingCommand cmd = repeatCaptor.getValue();
        cmd.execute();

        verify(mockRepeatCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void entryWithRepeatingCommandNotCalledWhenClosed() throws Exception {
        scheduler.scheduleEntry(mockRepeatCmd);
        verify(wrappedScheduler).scheduleEntry(repeatCaptor.capture());

        RepeatingCommand cmd = repeatCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockRepeatCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void finallyWithScheduledCommandCalledWhenOpen() throws Exception {
        scheduler.scheduleFinally(mockScheduledCmd);
        verify(wrappedScheduler).scheduleFinally(scheduledCaptor.capture());

        ScheduledCommand cmd = scheduledCaptor.getValue();
        cmd.execute();

        verify(mockScheduledCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void finallyWithScheduledCommandNotCalledWhenClosed() throws Exception {
        scheduler.scheduleFinally(mockScheduledCmd);
        verify(wrappedScheduler).scheduleFinally(scheduledCaptor.capture());

        ScheduledCommand cmd = scheduledCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockScheduledCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void finallyWithRepeatingCommandCalledWhenOpen() throws Exception {
        scheduler.scheduleFinally(mockRepeatCmd);
        verify(wrappedScheduler).scheduleFinally(repeatCaptor.capture());

        RepeatingCommand cmd = repeatCaptor.getValue();
        cmd.execute();

        verify(mockRepeatCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void finallyWithRepeatingCommandNotCalledWhenClosed() throws Exception {
        scheduler.scheduleFinally(mockRepeatCmd);
        verify(wrappedScheduler).scheduleFinally(repeatCaptor.capture());

        RepeatingCommand cmd = repeatCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockRepeatCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void incrementalCalledWhenOpen() throws Exception {
        scheduler.scheduleIncremental(mockRepeatCmd);
        verify(wrappedScheduler).scheduleIncremental(repeatCaptor.capture());

        RepeatingCommand cmd = repeatCaptor.getValue();
        cmd.execute();

        verify(mockRepeatCmd).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }

    @Test
    public void incrementalNotCalledWhenClosed() throws Exception {
        scheduler.scheduleIncremental(mockRepeatCmd);
        verify(wrappedScheduler).scheduleIncremental(repeatCaptor.capture());

        RepeatingCommand cmd = repeatCaptor.getValue();
        schedulerProvider.closeScheduler(scheduler);
        cmd.execute();

        verify(mockRepeatCmd, never()).execute();
        verifyNoMoreInteractions(wrappedScheduler, mockRepeatCmd);
    }
}
