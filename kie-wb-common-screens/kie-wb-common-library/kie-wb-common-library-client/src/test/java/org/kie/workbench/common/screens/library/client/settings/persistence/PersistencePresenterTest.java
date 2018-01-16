package org.kie.workbench.common.screens.library.client.settings.persistence;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.SyncPromises;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class PersistencePresenterTest {

    private PersistencePresenter persistencePresenter;

    @Mock
    private PersistencePresenter.View view;

    @Mock
    private ProjectContext projectContext;

    @Mock
    private SettingsPresenter.MenuItem menuItem;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private Event<SettingsSectionChange> settingsSectionChangeEvent;

    @Mock
    private ManagedInstance<ObservablePath> observablePaths;

    @Mock
    private AddDoubleValueModal newPropertyModal;

    @Mock
    private AddSingleValueModal newPersistableDataObjectModal;

    @Mock
    private Caller<PersistenceDescriptorEditorService> editorService;

    @Mock
    private Caller<DataModelerService> dataModelerService;

    @Mock
    private PersistencePresenter.PropertiesListPresenter propertiesItemPresenters;

    @Mock
    private PersistencePresenter.PersistableDataObjectsListPresenter persistableDataObjectsItemPresenters;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {
        persistencePresenter = spy(new PersistencePresenter(view,
                                                            projectContext,
                                                            promises,
                                                            menuItem,
                                                            notificationEvent,
                                                            settingsSectionChangeEvent,
                                                            observablePaths,
                                                            newPropertyModal,
                                                            newPersistableDataObjectModal,
                                                            editorService,
                                                            dataModelerService,
                                                            propertiesItemPresenters,
                                                            persistableDataObjectsItemPresenters));
    }

    @Test
    public void testSetup() {

    }
}