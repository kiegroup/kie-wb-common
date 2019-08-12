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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.uberfire.client.mvp.UberElement;

public class SLASettingsFieldEditorPresenter
        extends FieldEditorPresenter<String> {

    public interface View extends UberElement<SLASettingsFieldEditorPresenter> {

        void setDurationTimerChecked(boolean value);

        void showDurationTimerParams(boolean show);

        void setTimeDuration(String timeDuration);

        String getTimeDuration();

        void clear();

        Date parseFromISO(final String value) throws IllegalArgumentException;

        String formatToISO(final Date value);

        void setReadOnly(final boolean readOnly);
    }

    private final View view;

    @Inject
    public SLASettingsFieldEditorPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        setDisplayMode(true);
    }

    public View getView() {
        return view;
    }

    public void setValue(String value) {
        super.setValue(value);
        view.clear();
        setDisplayMode(true);
        if (value != null) {
                setDisplayMode(true);
                view.setTimeDuration(value);
        }
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        view.setReadOnly(readOnly);
    }

    protected void onTimerDurationChange() {
        String oldValue = value;
        value = copy(oldValue);
        value = view.getTimeDuration();
        notifyChange(oldValue,
                     value);
    }


    private void setDisplayMode(boolean setRadioChecked) {
        view.showDurationTimerParams(false);
                view.showDurationTimerParams(true);
                if (setRadioChecked) {
                    view.setDurationTimerChecked(true);
                }
    }

    protected void onDurationTimerSelected() {
        setDisplayMode(false);
        onTimerDurationChange();
    }

    private String copy(String source) {
        if (source == null) {
            return new String();
        }
        String copy = source + "";
        return copy;
    }
}