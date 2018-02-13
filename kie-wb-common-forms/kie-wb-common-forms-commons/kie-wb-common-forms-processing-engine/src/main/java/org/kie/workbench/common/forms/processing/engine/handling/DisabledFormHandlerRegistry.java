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

package org.kie.workbench.common.forms.processing.engine.handling;

/**
 * Registers a {@link FormHandler} and handles its clear
 */
public interface DisabledFormHandlerRegistry {

    /**
     * Starts a batch to clear all {@link FormHandler} present in the form handled by the given {@link FormHandler}
     * @param handler the handler for the current form
     */
    void startBatch(FormHandler handler);

    /**
     * Appends the given handler to the current batch. There might be a batch active prior calling this method.
     * @param handler
     */
    void addToActiveBatch(FormHandler handler);

    /**
     * Finishes the active batch and
     */
    void finishBatch();

    /**
     * Determinies if there's an active batch or not
     * @return True if there's an active batch or false if not.
     */
    boolean isBatchActive();
}
