package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects.ExternalDataObjectsPresenter.ImportsListPresenter;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.AddImportPopup;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalDataObjectsItemPresenterTest {

    private ExternalDataObjectsItemPresenter externalDataObjectsItemPresenter;

    @Mock
    private ExternalDataObjectsItemPresenter.View view;

    @GwtMock
    private AddImportPopup importPopup;

    @Before
    public void before() {
        externalDataObjectsItemPresenter = spy(new ExternalDataObjectsItemPresenter(view,
                                                                                    importPopup));
    }

    @Test
    public void testSetup() {
        final Import import_ = new Import("type");
        externalDataObjectsItemPresenter.setup(import_, mock(ExternalDataObjectsPresenter.class));

        verify(view).init(eq(externalDataObjectsItemPresenter));
        verify(view).setTypeName(eq("type"));
        assertEquals(import_, externalDataObjectsItemPresenter.getObject());
    }

    @Test
    public void testRemove() {
        final ExternalDataObjectsPresenter parentPresenter = mock(ExternalDataObjectsPresenter.class);
        final ImportsListPresenter listPresenter = mock(ImportsListPresenter.class);

        externalDataObjectsItemPresenter.parentPresenter = parentPresenter;
        externalDataObjectsItemPresenter.setListPresenter(listPresenter);

        externalDataObjectsItemPresenter.remove();

        verify(listPresenter).remove(eq(externalDataObjectsItemPresenter));
        verify(parentPresenter).fireChangeEvent();
    }

    @Test
    public void testOpenEditModal(){
        final ExternalDataObjectsPresenter parentPresenter = mock(ExternalDataObjectsPresenter.class);
        final ImportsListPresenter listPresenter = mock(ImportsListPresenter.class);
        final Import import_ = new Import("test_class");

        externalDataObjectsItemPresenter.setup(import_, parentPresenter);
        externalDataObjectsItemPresenter.setListPresenter(listPresenter);

        externalDataObjectsItemPresenter.openEditModal();
        verify(importPopup).show("test_class");
        verify(importPopup).setCommand(any());
    }
}