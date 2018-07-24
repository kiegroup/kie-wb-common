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

package org.kie.workbench.common.forms.cms.components.client.ui.wizard.ui;

import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.uberfire.client.mvp.UberElement;

public interface WizardFormView extends UberElement<WizardFormView.Presenter> {

    void renderStep(int index,
                    String title,
                    DynamicFormRenderer renderer);

    void clear();

    interface Presenter {

        boolean isFirst();

        boolean isLast();

        void previousStep();

        void nextStep();

        void cancel();

        void finish();
    }
}
