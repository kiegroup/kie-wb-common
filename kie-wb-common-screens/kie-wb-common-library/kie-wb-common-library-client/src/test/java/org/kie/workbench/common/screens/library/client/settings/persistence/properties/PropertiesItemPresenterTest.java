package org.kie.workbench.common.screens.library.client.settings.persistence.properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesItemPresenterTest {

    private PropertiesItemPresenter propertiesItemPresenter;

    @Mock
    private PropertiesItemPresenter.View view;

    @Before
    public void before() {
        propertiesItemPresenter = spy(new PropertiesItemPresenter(view));
    }

    @Test
    public void testSetup() {
        propertiesItemPresenter.setup(new Property("Name", "Value"), mock(PersistencePresenter.class));
        verify(view).init(eq(propertiesItemPresenter));
        verify(view).setName(eq("Name"));
        verify(view).setValue(eq("Value"));
    }

    @Test
    public void testRemove() {
        final PersistencePresenter parentPresenter = mock(PersistencePresenter.class);
        propertiesItemPresenter.setup(new Property("Name", "Value"), parentPresenter);

        propertiesItemPresenter.remove();

        verify(parentPresenter).fireChangeEvent();
    }
}