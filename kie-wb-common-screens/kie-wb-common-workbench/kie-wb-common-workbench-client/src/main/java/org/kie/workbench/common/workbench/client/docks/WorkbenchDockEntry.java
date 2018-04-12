/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.workbench.client.docks;

import org.uberfire.client.workbench.docks.UberfireDock;

/**
 * Component that holds a dock and has a property that signals if the dock should be open when the it is
 * initialized.
 */
public class WorkbenchDockEntry {

    private UberfireDock dock;
    private boolean openOnInitialization;

    /**
     * @param dock The dock.
     * @param openOnInitialization The flag to signal if the dock should be open on initialization.
     */
    public WorkbenchDockEntry(UberfireDock dock, boolean openOnInitialization) {
        this.dock = dock;
        this.openOnInitialization = openOnInitialization;
    }

    /**
     * This is the dock that will be signaled.
     * @return the dock.
     */
    public UberfireDock getDock() {
        return dock;
    }

    /**
     * The flag that signals if the dock should be open on initialization.
     * If this is set to true when the dock is initialized it's tab will appear open.
     * @return true if the dock should be opened on initialization, otherwise false.
     */
    public boolean getOpenOnInitialization() {
        return openOnInitialization;
    }
}
