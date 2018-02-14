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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import com.google.gwt.core.client.Scheduler;

@Dependent
public class CloseableSchedulerProvider {

    private final Scheduler wrappedScheduler;


    public CloseableSchedulerProvider(Scheduler wrappedScheduler) {
        this.wrappedScheduler = wrappedScheduler;
    }


    public CloseableSchedulerProvider() {
        this(Scheduler.get());
    }

    @Produces
    public CloseableSchedulerWrapper getScheduler() {
        return new CloseableSchedulerWrapper(wrappedScheduler);
    }

    public void closeScheduler(@Disposes CloseableSchedulerWrapper schedulerWrapper) {
        schedulerWrapper.close();
    }

    public static class CloseableSchedulerWrapper extends Scheduler {

        private boolean open = true;
        private Scheduler scheduler;


        public CloseableSchedulerWrapper(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public void scheduleDeferred(ScheduledCommand cmd) {
            scheduler.scheduleDeferred(wrappedCommand(cmd));
        }

        @Override
        public void scheduleEntry(RepeatingCommand cmd) {
            scheduler.scheduleEntry(wrappedCommand(cmd));
        }

        @Override
        public void scheduleEntry(ScheduledCommand cmd) {
            scheduler.scheduleEntry(wrappedCommand(cmd));
        }

        @Override
        public void scheduleFinally(RepeatingCommand cmd) {
            scheduler.scheduleFinally(wrappedCommand(cmd));
        }

        @Override
        public void scheduleFinally(ScheduledCommand cmd) {
            scheduler.scheduleFinally(wrappedCommand(cmd));
        }

        @Override
        public void scheduleFixedDelay(RepeatingCommand cmd, int delayMs) {
            scheduler.scheduleFixedDelay(wrappedCommand(cmd), delayMs);
        }

        @Override
        public void scheduleFixedPeriod(RepeatingCommand cmd, int delayMs) {
            scheduler.scheduleFixedPeriod(wrappedCommand(cmd), delayMs);
        }

        @Override
        public void scheduleIncremental(RepeatingCommand cmd) {
            scheduler.scheduleIncremental(wrappedCommand(cmd));
        }

        private ScheduledCommand wrappedCommand(ScheduledCommand cmd) {
            return () -> {
                if (open) {
                    cmd.execute();
                }
            };
        }

        private RepeatingCommand wrappedCommand(RepeatingCommand cmd) {
            return () -> {
                return open && cmd.execute();
            };
        }

        /**
         * Closes scheduler such that no currently scheduled events will be executed.
         */
        public void close() {
            open = false;
        }

    }

}
