package org.kie.workbench.common.widgets.client.callbacks;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@WithClassesToStub(Text.class)
@RunWith(GwtMockitoTestRunner.class)
public class AssetValidatedCallbackTest {

    @Mock
    private Command validationFinishedCommand;

    private Event<NotificationEvent> notification;

    private AssetValidatedCallback testedCallback;

    @Before
    public void setUp() throws Exception {
        notification = mock(EventSourceMock.class);
        testedCallback = new AssetValidatedCallback(validationFinishedCommand,
                                                    notification);
    }

    @Test
    public void testNoMessagesPresent() throws Exception {
        testedCallback.callback(new ArrayList<ValidationMessage>());

        verify(notification).fire(any(NotificationEvent.class));
        verify(validationFinishedCommand).execute();
    }

    @Test
    public void testMessagesPresent() throws Exception {
        final ValidationMessage validationMessage = mock(ValidationMessage.class);
        final List<ValidationMessage> messages = new ArrayList<>();
        messages.add(validationMessage);
        testedCallback.callback(messages);

        verify(notification, never()).fire(any(NotificationEvent.class));
        verify(validationFinishedCommand).execute();
    }
}
