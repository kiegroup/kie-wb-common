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

package org.kie.workbench.common.stunner.bpmn.integration.client;

import java.util.function.Consumer;

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;

public interface IntegrationHandler {

    void migrateFromJBPMDesignerToStunner(Path path, PlaceRequest place, boolean isDirty, ParameterizedCommand<Consumer<Boolean>> saveCommand, Command migrationFinishedCommand, Command cancelCommand, Command errorCommand);

    default void migrateFromJBPMDesignerToStunner(Path path, PlaceRequest place, boolean isDirty, ParameterizedCommand<Consumer<Boolean>> saveCommand) {
        migrateFromJBPMDesignerToStunner(path, place, isDirty, saveCommand, null, null, null);
    }

    void migrateFromStunnerToJBPMDesigner(Path path, PlaceRequest place, boolean isDirty, ParameterizedCommand<Consumer<Boolean>> saveCommand);

}
