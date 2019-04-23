package org.kie.workbench.common.screens.library.client.screens.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.mockito.Mock;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Anchor;

import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.never;

@RunWith(GwtMockitoTestRunner.class)
public class AddProjectPopUpViewTest {

    @Mock
    private AddProjectPopUpPresenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private Div advancedOptions;

    @Mock
    private Anchor showHideAdvancedOptions;

    private AddProjectPopUpView view;

    @Before
    public void setup() {
        view = spy(new AddProjectPopUpView(presenter,
                                           translationService,
                                           advancedOptions,
                                           showHideAdvancedOptions));
    }

    @Test
    public void testShowAdvancedOptions() {
        ClickEvent clickEvent = mock(ClickEvent.class);

        doReturn(true).when(advancedOptions).getHidden();
        doReturn("hide").when(translationService).format(LibraryConstants.HideAdvancedOptions);

        view.showAdvancedOptions(clickEvent);

        verify(advancedOptions).setHidden(false);
        verify(translationService).format(LibraryConstants.HideAdvancedOptions);
        verify(showHideAdvancedOptions).setTextContent("hide");
        verify(presenter, never()).restoreDefaultAdvancedOptions();
    }

    @Test
    public void testHideAdvancedOptions() {
        ClickEvent clickEvent = mock(ClickEvent.class);

        doReturn(false).when(advancedOptions).getHidden();
        doReturn("show").when(translationService).format(LibraryConstants.ShowAdvancedOptions);

        view.showAdvancedOptions(clickEvent);

        verify(advancedOptions).setHidden(true);
        verify(translationService).format(LibraryConstants.ShowAdvancedOptions);
        verify(showHideAdvancedOptions).setTextContent("show");
        verify(presenter).restoreDefaultAdvancedOptions();
    }
}
