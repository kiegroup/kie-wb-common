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

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.forms.processing.engine.handling.DisabledFormHandlerRegistry;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;

@Singleton
public class DisabledFormHandlerRegistryImpl implements DisabledFormHandlerRegistry {

    private static final Integer timeout = 3000;

    private FormHandlerBatch currentBatch = null;

    protected List<FormHandlerBatch> activeBatches = new ArrayList<>();

    @Override
    public void startBatch(FormHandler handler) {
        if(currentBatch != null) {
            finishBatch();
        }
        currentBatch = new FormHandlerBatch(handler);
        activeBatches.add(currentBatch);
    }

    @Override
    public void addToActiveBatch(FormHandler handler) {
        if(currentBatch == null) {
            throw new IllegalArgumentException("Cannot add handler: there's no active batch!");
        } else {
            currentBatch.addNestedHandler(handler);
        }
    }

    @Override
    public void finishBatch() {
        if(currentBatch == null) {
            throw new IllegalArgumentException("Cannot end batch: there's no active batch!");
        } else {
            setupTimer(currentBatch);
            currentBatch = null;
        }
    }

    private void setupTimer(final FormHandlerBatch batch) {
        Timer timer = new Timer() {
            public void run() {
                /*
                if activeBatches doesn't contains the given batch means that
                destroy method has been called.
                 */

                if (activeBatches.contains(batch)) {
                    batch.clear();
                    activeBatches.remove(batch);
                }
            }
        };
        timer.schedule(timeout);
    }

    @Override
    public boolean isBatchActive() {
        return currentBatch != null;
    }

    @PreDestroy
    public void destroy() {
        // Clear remaining batches
        Iterator<FormHandlerBatch> it = activeBatches.iterator();
        while(it.hasNext()) {
            FormHandlerBatch batch = it.next();
            batch.clear();
            it.remove();
        }
    }

    public class FormHandlerBatch {

        private FormHandler rootHandler;

        private List<FormHandler> nestedHandlers = new ArrayList<>();

        public FormHandlerBatch(FormHandler rootHandler) {
            this.rootHandler = rootHandler;
        }

        public void addNestedHandler(FormHandler handler) {
            nestedHandlers.add(handler);
        }

        public FormHandler getRootHandler() {
            return rootHandler;
        }

        public Collection<FormHandler> getNestedHandlers() {
            return nestedHandlers;
        }

        public void clear() {
            if (rootHandler != null) {
                nestedHandlers.forEach(FormHandler::clear);
                rootHandler.clear();
                nestedHandlers.clear();
                rootHandler = null;
            }
        }
    }
}
