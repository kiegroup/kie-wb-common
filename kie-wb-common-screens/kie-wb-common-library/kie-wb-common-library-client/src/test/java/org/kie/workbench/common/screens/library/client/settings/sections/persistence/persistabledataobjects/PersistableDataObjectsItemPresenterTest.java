package org.kie.workbench.common.screens.library.client.settings.sections.persistence.persistabledataobjects;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.PersistencePresenter.PersistableDataObjectsListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PersistableDataObjectsItemPresenterTest {

    private PersistableDataObjectsItemPresenter persistableDataObjectsItemPresenter;

    @Mock
    private PersistableDataObjectsItemPresenter.View view;

    @Mock
    private AddSingleValueModal newPersistableDataObjectModal;

    @Before
    public void before() {
        persistableDataObjectsItemPresenter = spy(new PersistableDataObjectsItemPresenter(view,
                                                                                          newPersistableDataObjectModal));
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
        final PersistableDataObjectsListPresenter listPresenter = mock(PersistableDataObjectsListPresenter.class);

        persistableDataObjectsItemPresenter.parentPresenter = parentPresenter;
        persistableDataObjectsItemPresenter.setListPresenter(listPresenter);

        persistableDataObjectsItemPresenter.remove();

        verify(listPresenter).remove(eq(persistableDataObjectsItemPresenter));
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testOpenEditModal(){
        final PersistencePresenter parentPresenter = mock(PersistencePresenter.class);
        final PersistableDataObjectsListPresenter listPresenter = mock(PersistableDataObjectsListPresenter.class);

        persistableDataObjectsItemPresenter.setListPresenter(listPresenter);
        persistableDataObjectsItemPresenter.setup("TestClasssName", parentPresenter);

        persistableDataObjectsItemPresenter.openEditModal();

        ArgumentCaptor<Consumer> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(newPersistableDataObjectModal).show(captor.capture(), any());
        captor.getValue().accept("TestClasssName");

        verify(listPresenter).add("TestClasssName");
        verify(parentPresenter).fireChangeEvent();
        verify(newPersistableDataObjectModal).show(any(), any());
    }
}