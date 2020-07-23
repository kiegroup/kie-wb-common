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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;

public interface NotificationEditorWidgetView extends IsWidget {

    interface Presenter {

        String getNameHeader();

        String getFromLabel();

        String getExpirationLabel(Expiration type);

        void createOrEdit(NotificationWidgetView parent, NotificationRow row);

        void ok(String emails);

        String clearEmails(String emailsString);
    }

    void init(final NotificationEditorWidgetView.Presenter presenter);

    void createOrEdit(NotificationWidgetView parent, NotificationRow row);

    void setReadOnly(boolean readOnly);

    void ok();

    void setValidationFailed(String incorrectValue);
}
