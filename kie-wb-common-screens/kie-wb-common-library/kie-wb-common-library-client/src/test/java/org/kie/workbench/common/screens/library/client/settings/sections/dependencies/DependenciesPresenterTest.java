package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Dependency;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencySelectorPopup;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesPresenterTest {

    private DependenciesPresenter dependenciesPresenter;

    @Mock
    private DependenciesPresenter.View view;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private DependencySelectorPopup dependencySelectorPopup;

    @Mock
    private Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private EnhancedDependenciesManager enhancedDependenciesManager;

    @Mock
    private ManagedInstance<DependenciesItemPresenter> presenters;

    private Promises promises = new SyncPromises();

    @Before
    public void before() {

        dependenciesPresenter = spy(new DependenciesPresenter(view,
                                                              promises,
                                                              menuItem,
                                                              dependencySelectorPopup,
                                                              settingsSectionChangeEvent,
                                                              enhancedDependenciesManager,
                                                              presenters));
    }

    @Test
    public void testSetup() {

        dependenciesPresenter.setup(mock(ProjectScreenModel.class));

        verify(view).init(eq(dependenciesPresenter));
        verify(dependencySelectorPopup).addSelectionHandler(any());
        verify(enhancedDependenciesManager).init(any(), any());
    }

    @Test
    public void testAdd() {
        dependenciesPresenter.addNewDependency();

        verify(enhancedDependenciesManager).addNew(new Dependency());
        verify(settingsSectionChangeEvent).fire(any());
    }

    @Test
    public void testAddDependency() {
        final Dependency dependency = mock(Dependency.class);
        dependenciesPresenter.add(dependency);
        verify(enhancedDependenciesManager).addNew(eq(dependency));
    }

    @Test
    public void testAddAllToAllowList() {
        dependenciesPresenter.model = mock(ProjectScreenModel.class);
        doReturn(new AllowList()).when(dependenciesPresenter.model).getAllowList();
        assertEquals(0, dependenciesPresenter.model.getAllowList().size());

        dependenciesPresenter.addAllToAllowList(new HashSet<>(Arrays.asList("foo", "bar")));

        assertEquals(2, dependenciesPresenter.model.getAllowList().size());
        verify(enhancedDependenciesManager).update();
    }

    @Test
    public void testRemoveAllFromAllowList() {
        final Set<String> packages = new HashSet<>(Arrays.asList("foo", "bar"));

        dependenciesPresenter.model = mock(ProjectScreenModel.class);
        doReturn(new AllowList(packages)).when(dependenciesPresenter.model).getAllowList();
        assertEquals(2, dependenciesPresenter.model.getAllowList().size());

        dependenciesPresenter.removeAllFromAllowList(packages);

        assertEquals(0, dependenciesPresenter.model.getAllowList().size());
        verify(enhancedDependenciesManager).update();
    }

    @Test
    public void testAddFromRepository() {
        dependenciesPresenter.addFromRepository();
        verify(dependencySelectorPopup).show();
    }

    @Test
    public void testRemove() {
        final EnhancedDependency enhancedDependency = mock(EnhancedDependency.class);

        dependenciesPresenter.remove(enhancedDependency);

        verify(enhancedDependenciesManager).delete(eq(enhancedDependency));
    }
}