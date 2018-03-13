package org.kie.workbench.common.widgets.client.callbacks;

import java.util.List;

import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

public class AssetValidatedCallback implements RemoteCallback<List<ValidationMessage>> {

    private final Command validationFinishedCommand;

    private final Event<NotificationEvent> notificationEvent;

    public AssetValidatedCallback(final Command validationFinishedCommand,
                                  final Event<NotificationEvent> notificationEvent) {
        this.validationFinishedCommand = validationFinishedCommand;
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void callback(final List<ValidationMessage> validationMessages) {
        if (validationMessages == null || validationMessages.isEmpty()) {
            notifyValidationSuccess();
        } else {
            ValidationPopup.showMessages(validationMessages);
        }
        if (validationFinishedCommand != null) {
            validationFinishedCommand.execute();
        }
    }

    private void notifyValidationSuccess() {
        // the null check is due to tests that are not able to inject Event instance
        if (notificationEvent != null) {
            notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                                         NotificationEvent.NotificationType.SUCCESS));
        }
    }
}
