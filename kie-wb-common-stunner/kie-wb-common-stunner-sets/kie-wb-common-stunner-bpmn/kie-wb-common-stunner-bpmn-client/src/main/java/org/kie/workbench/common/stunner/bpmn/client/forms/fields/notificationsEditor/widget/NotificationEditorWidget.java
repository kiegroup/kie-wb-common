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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Dependent
public class NotificationEditorWidget implements IsWidget,
                                                 NotificationEditorWidgetView.Presenter {

    private static final String EXPIRATION_PREFIX = "notification.expiration.";

    private static final String EXPIRATION_POSTFIX = ".label";

    private NotificationEditorWidgetView view;

    private ClientTranslationService translationService;

    @Inject
    public NotificationEditorWidget(NotificationEditorWidgetView view,
                                    ClientTranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getNameHeader() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_LABEL);
    }

    @Override
    public String getFromLabel() {
        return translationService.getValue(StunnerBPMNConstants.NOTIFICATION_FROM);
    }

    @Override
    public String getExpirationLabel(Expiration type) {
        switch (type) {
            case EXPRESSION:
                return translationService.getValue(EXPIRATION_PREFIX + "expression" + EXPIRATION_POSTFIX);
            case DATETIME:
                return translationService.getValue(EXPIRATION_PREFIX + "datetime" + EXPIRATION_POSTFIX);
            case TIME_PERIOD:
                return translationService.getValue(EXPIRATION_PREFIX + "time.period" + EXPIRATION_POSTFIX);
            default:
                return "";
        }
    }

    @Override
    public void createOrEdit(NotificationWidgetView parent, NotificationRow row) {
        view.createOrEdit(parent, row);
    }

    @Override
    public void ok(String emails) {
        String incorrectValue = getFirstInvalidEmail(emails);
        if (incorrectValue.isEmpty()) {
            view.ok();
        } else {
            view.setValidationFailed(incorrectValue);
        }
    }

    @Override
    public String clearEmails(String emails) {
        String result = emails.replaceAll("\\s", "");
        if (result.startsWith(",")) {
            result = result.substring(1);
        }

        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String getFirstInvalidEmail(String emailsString) {
        String[] emails = clearEmails(emailsString).split(",");

        for (String email : emails) {
            if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                return email;
            }
        }

        return "";
    }

    public void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
    }
}
