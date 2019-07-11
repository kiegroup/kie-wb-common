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
package org.kie.workbench.common.dmn.showcase.client.menus;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.submarine.client.session.EditorSessionCommands;

@Dependent
@DMNEditor
public class DMNEditorSessionCommands extends EditorSessionCommands {

    @Inject
    public DMNEditorSessionCommands(final ManagedClientSessionCommands commands) {
        super(commands);
    }

    @Override
    protected void registerCommands() {
        super.registerCommands();
        getCommands().register(PerformAutomaticLayoutCommand.class);
    }

    public PerformAutomaticLayoutCommand getPerformAutomaticLayoutCommand() {
        return get(PerformAutomaticLayoutCommand.class);
    }
}
