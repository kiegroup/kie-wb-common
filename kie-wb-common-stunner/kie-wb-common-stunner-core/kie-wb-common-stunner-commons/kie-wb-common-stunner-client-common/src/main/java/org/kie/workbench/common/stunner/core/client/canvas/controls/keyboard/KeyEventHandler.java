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

package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Timer;
import elemental2.dom.DomGlobal;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsApi;
import org.jboss.errai.bus.client.util.BusToolsCli;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyShortcutCallback;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyShortcutKeyDownThenUp;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControlImpl.SessionKeyShortcutCallback;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A helper class for component that listen to keyboard events.
 * It provides keyboard shortcuts support by listening for
 * multiple key events.
 */
@Dependent
public class KeyEventHandler {

    private final static int KEYS_TIMER_DELAY = 100;

    @Inject
    private KeyboardShortcutsApi keyboardShortcutsApi;

    private final Set<KeyboardEvent.Key> keys = new HashSet<>();
    private final List<KeyboardControl.KeyShortcutCallback> shortcutCallbacks = new ArrayList<>();
    private final List<Integer> registeredShortcutsIds = new ArrayList<>();

    private boolean enabled = true;
    private KeyboardEvent.Key[] _keys;
    private Timer timer;
    private int delay = KEYS_TIMER_DELAY;

    public KeyEventHandler addKeyShortcutCallback(final KeyboardControl.KeyShortcutCallback shortcutCallback) {

        final Optional<KogitoKeyShortcutCallback> possibleKogitoShortcutCallback = getAssociatedKogitoKeyShortcutCallback(shortcutCallback);
        if (!possibleKogitoShortcutCallback.isPresent() || possibleKogitoShortcutCallback.get().getKeyCombination().isEmpty()) {
            //This means we're registering a non-Kogito keyboard shortcut
            this.shortcutCallbacks.add(shortcutCallback);
            return this;
        }

        // This means that we're NOT in the Kogito environment
        if (BusToolsCli.isRemoteCommunicationEnabled()) {
            return this;
        }

        final KogitoKeyShortcutCallback kogitoShortcutCallback = possibleKogitoShortcutCallback.get();
        DomGlobal.console.debug("Registering: " + shortcutCallback.getClass().getCanonicalName() + " - " + kogitoShortcutCallback.getLabel());

        //Normal
        if (shortcutCallback instanceof KogitoKeyShortcutKeyDownThenUp) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyDownThenUp(
                    kogitoShortcutCallback.getKeyCombination(),
                    kogitoShortcutCallback.getLabel(),
                    () -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    () -> runIfEnabled(() -> shortcutCallback.onKeyUp(null)),
                    kogitoShortcutCallback.getOpts()));
        } else if (shortcutCallback instanceof KogitoKeyPress) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyPress(
                    kogitoShortcutCallback.getKeyCombination(),
                    kogitoShortcutCallback.getLabel(),
                    () -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    kogitoShortcutCallback.getOpts()));
        }

        //Session
        else if (shortcutCallback instanceof SessionKeyShortcutCallback && ((SessionKeyShortcutCallback) shortcutCallback).getDelegate() instanceof KogitoKeyShortcutKeyDownThenUp) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyDownThenUp(
                    kogitoShortcutCallback.getKeyCombination(),
                    kogitoShortcutCallback.getLabel(),
                    () -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    () -> runIfEnabled(() -> shortcutCallback.onKeyUp(null)),
                    kogitoShortcutCallback.getOpts()));
        } else if (shortcutCallback instanceof SessionKeyShortcutCallback && ((SessionKeyShortcutCallback) shortcutCallback).getDelegate() instanceof KogitoKeyPress) {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyPress(
                    kogitoShortcutCallback.getKeyCombination(),
                    kogitoShortcutCallback.getLabel(),
                    () -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    kogitoShortcutCallback.getOpts()));
        }

        //Default
        else {
            registeredShortcutsIds.add(keyboardShortcutsApi.registerKeyPress(
                    kogitoShortcutCallback.getKeyCombination(),
                    kogitoShortcutCallback.getLabel(),
                    () -> runIfEnabled(shortcutCallback::onKeyShortcut),
                    KeyboardShortcutsApi.Opts.DEFAULT));
        }

        return this;
    }

    private void runIfEnabled(final Runnable runnable) {
        if (this.enabled) {
            runnable.run();
        }
    }

    private Optional<KogitoKeyShortcutCallback> getAssociatedKogitoKeyShortcutCallback(final KeyboardControl.KeyShortcutCallback shortcutCallback) {
        if (shortcutCallback instanceof KogitoKeyShortcutCallback) {
            return Optional.of((KogitoKeyShortcutCallback) shortcutCallback);
        }

        if (shortcutCallback instanceof SessionKeyShortcutCallback && ((SessionKeyShortcutCallback) shortcutCallback).getDelegate() instanceof KogitoKeyShortcutCallback) {
            return Optional.of((KogitoKeyShortcutCallback) ((SessionKeyShortcutCallback) shortcutCallback).getDelegate());
        }

        return Optional.empty();
    }

    public KeyEventHandler setTimerDelay(final int millis) {
        this.delay = millis;
        return this;
    }

    @PreDestroy
    public void clear() {
        if (null != timer && timer.isRunning()) {
            timer.cancel();
        }
        shortcutCallbacks.clear();
        registeredShortcutsIds.forEach(keyboardShortcutsApi::deregister);
        reset();
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void onKeyUpEvent(final @Observes KeyUpEvent event) {
        checkNotNull("event",
                     event);
        onKeyUp(event.getKey());
    }

    void onKeyDownEvent(final @Observes KeyDownEvent event) {
        checkNotNull("event",
                     event);
        onKeyDown(event.getKey());
    }

    private void onKeyDown(final KeyboardEvent.Key key) {
        if (!enabled) {
            return;
        }
        if (!shortcutCallbacks.isEmpty()) {
            startKeysTimer(key);
        }
    }

    private void onKeyUp(final KeyboardEvent.Key key) {
        if (!enabled) {
            return;
        }
        keys.remove(key);
        shortcutCallbacks.stream().forEach(s -> s.onKeyUp(key));
    }

    private void startKeysTimer(final KeyboardEvent.Key key) {
        keys.add(key);
        this._keys = keys.toArray(new KeyboardEvent.Key[this.keys.size()]);
        if (null == timer) {
            timer = new Timer() {
                @Override
                public void run() {
                    KeyEventHandler.this.keysTimerTimeIsUp();
                }
            };
        }
        timer.schedule(delay);
    }

    void keysTimerTimeIsUp() {
        if (!shortcutCallbacks.isEmpty() && null != _keys) {
            shortcutCallbacks.stream().forEach(s -> s.onKeyShortcut(_keys));
        }
    }

    void reset() {
        setEnabled(false);
        _keys = null;
        keys.clear();
        timer = null;
    }
}
