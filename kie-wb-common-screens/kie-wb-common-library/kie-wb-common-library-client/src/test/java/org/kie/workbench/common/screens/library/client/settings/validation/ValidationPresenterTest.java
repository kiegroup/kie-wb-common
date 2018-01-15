package org.kie.workbench.common.screens.library.client.settings.validation;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class ValidationPresenterTest {

    private ValidationPresenter validationPresenter;

    @Before
    public void before() {
        validationPresenter = spy(new ValidationPresenter(null, null, null, null, null));
    }

    @Test
    public void testSetup() {
        validationPresenter.setup(mock(ProjectScreenModel.class));
    }
}