package org.kie.workbench.common.screens.library.client.settings.persistence.persistabledataobjects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PersistableDataObjectsItemPresenterTest {

    private PersistableDataObjectsItemPresenter persistableDataObjectsItemPresenter;

    @Mock
    private PersistableDataObjectsItemPresenter.View view;

    @Before
    public void before() {
        persistableDataObjectsItemPresenter = spy(new PersistableDataObjectsItemPresenter(view));
    }

    @Test
    public void testSetup() {
        persistableDataObjectsItemPresenter.setup("ClassName", mock(PersistencePresenter.class));
        verify(view).init(eq(persistableDataObjectsItemPresenter));
        verify(view).setClassName(eq("ClassName"));
    }

    @Test
    public void testRemove() {
        final PersistencePresenter parentPresenter = mock(PersistencePresenter.class);
        persistableDataObjectsItemPresenter.setup("ClassName", parentPresenter);

        persistableDataObjectsItemPresenter.remove();

        verify(parentPresenter).fireChangeEvent();
    }
}