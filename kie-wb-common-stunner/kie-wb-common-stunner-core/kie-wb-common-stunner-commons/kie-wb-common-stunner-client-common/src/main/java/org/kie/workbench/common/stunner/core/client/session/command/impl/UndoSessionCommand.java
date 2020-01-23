/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.stateControl.KogitoStateControlInitializer;
import org.appformer.kogito.bridge.client.stateControl.registry.CommandRegistry;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

@Dependent
@Default
public class UndoSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final KogitoStateControlInitializer stateControlInitializer;

    @Inject
    public UndoSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final KogitoStateControlInitializer stateControlInitializer) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
        this.stateControlInitializer = stateControlInitializer;
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);

        //If running in Kogito we should initialize the Kogito StateControl undo/redo commands. Otherwise we should keep the key binding.
        if(stateControlInitializer.isKogitoEnabled()) {
            stateControlInitializer.setUndoCommand(this::execute);
        } else {
            session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        }
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (isEnabled()) {
            handleCtrlZ(keys);
        }
    }

    private void handleCtrlZ(final KeyboardEvent.Key[] keys) {
        if (doKeysMatch(keys,
                        KeyboardEvent.Key.CONTROL,
                        KeyboardEvent.Key.Z)) {
            this.execute();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {

        checkNotNull("callback",
                     callback);
        final CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> registry = getSession().getCommandRegistry();
        if (!registry.isEmpty()) {
            final CommandResult<CanvasViolation> result = sessionCommandManager.undo(getSession().getCanvasHandler());
            checkState();
            if (CommandUtils.isError(result)) {
                callback.onError((V) result);
                // Clear the actual command registry otherwise the undo will continuously fail as it's same command.
                getSession().getCommandRegistry().clear();
            } else {
                callback.onSuccess();
            }
            getSession().getSelectionControl().clearSelection();
        }
    }

    void onCommandAdded(final @Observes RegisterChangedEvent registerChangedEvent) {
        checkNotNull("registerChangedEvent",
                     registerChangedEvent);
        if (registerChangedEvent.getCanvasHandler().equals(getCanvasHandler())) {
            checkState();
        }
    }

    private void checkState() {
        if (getSession() != null) {
            setEnabled(!getSession().getCommandRegistry().getCommandHistory().isEmpty());
            fire();
        }
    }
}
