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

import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

public interface KeyboardControl<C extends Canvas, S extends ClientSession> extends CanvasControl<C>,
                                                                                    CanvasControl.SessionAware<S> {

    KeyboardControl<C, S> addKeyShortcutCallback(final KeyShortcutCallback shortcutCallback);

    interface KeyShortcutCallback {

        void onKeyShortcut(final KeyboardEvent.Key... keys);

        default void onKeyUp(final KeyboardEvent.Key key) {
        }
    }

    //
    //
    //Kogito

    class KogitoOpts {

        public static enum Repeat {
            REPEAT,
            NO_REPEAT
        }

        public static final KogitoOpts DEFAULT = new KogitoOpts(Repeat.NO_REPEAT);

        private final Repeat repeat;

        public KogitoOpts(final Repeat repeat) {
            this.repeat = repeat;
        }

        public boolean getRepeat() {
            return Repeat.REPEAT.equals(repeat);
        }
    }

    interface KogitoKeyShortcutCallback extends KeyShortcutCallback {

        default KogitoOpts getOpts() {
            return KogitoOpts.DEFAULT;
        }

        String getKeyCombination();

        String getLabel();
    }

    public static class KogitoKeyPress implements KogitoKeyShortcutCallback {

        private String combination;
        private String label;
        private Runnable onKeyDown;
        private KogitoOpts opts;

        public KogitoKeyPress() {
        }

        public KogitoKeyPress(final String combination, final String label, final Runnable onKeyDown) {
            this(combination, label, onKeyDown, KogitoOpts.DEFAULT);
        }

        public KogitoKeyPress(final String combination, final String label, final Runnable onKeyDown, final KogitoOpts opts) {
            this.combination = combination;
            this.label = label;
            this.onKeyDown = onKeyDown;
            this.opts = opts;
        }

        @Override
        public final void onKeyShortcut(final KeyboardEvent.Key... keys) {
            onKeyDown();
        }

        @Override
        public final void onKeyUp(final KeyboardEvent.Key key) {
            throw new RuntimeException("Keyup shouldn't be called on KeyPress events");
        }

        @Override
        public String getKeyCombination() {
            return combination;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public void onKeyDown() {
            onKeyDown.run();
        }

        @Override
        public KogitoOpts getOpts() {
            return opts;
        }
    }

    public static class KogitoKeyShortcutKeyDownThenUp implements KogitoKeyShortcutCallback {

        private final String combination;
        private final String label;
        private final Runnable onKeyDown;
        private final Runnable onKeyUp;
        private final KogitoOpts opts;

        public KogitoKeyShortcutKeyDownThenUp(final String combination, final String label, final Runnable onKeyDown, final Runnable onKeyUp) {
            this(combination, label, onKeyDown, onKeyUp, KogitoOpts.DEFAULT);
        }

        public KogitoKeyShortcutKeyDownThenUp(final String combination, final String label, final Runnable onKeyDown, final Runnable onKeyUp, final KogitoOpts opts) {
            this.combination = combination;
            this.label = label;
            this.onKeyDown = onKeyDown;
            this.onKeyUp = onKeyUp;
            this.opts = opts;
        }

        @Override
        public final void onKeyShortcut(final KeyboardEvent.Key... keys) {
            onKeyDown();
        }

        @Override
        public final void onKeyUp(final KeyboardEvent.Key key) {
            onKeyUp();
        }

        @Override
        public String getKeyCombination() {
            return combination;
        }

        @Override
        public String getLabel() {
            return label;
        }

        public void onKeyDown() {
            onKeyDown.run();
        }

        public void onKeyUp() {
            onKeyUp.run();
        }

        @Override
        public KogitoOpts getOpts() {
            return opts;
        }
    }
}
