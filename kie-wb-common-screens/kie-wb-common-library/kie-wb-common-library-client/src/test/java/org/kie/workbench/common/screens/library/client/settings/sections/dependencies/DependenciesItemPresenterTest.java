package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.NewDependencyPopup;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.TransitiveEnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static java.util.Collections.emptySet;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesItemPresenterTest {

    private DependenciesItemPresenter dependenciesItemPresenter;

    @Mock
    private DependenciesItemPresenter.View view;

    @Mock
    private NewDependencyPopup newDependencyPopup;

    @Before
    public void before() {
        dependenciesItemPresenter = spy(new DependenciesItemPresenter(view,
                                                                      newDependencyPopup));
    }

    @Test
    public void testSetupNormal() {

        dependenciesItemPresenter.setup(
                new NormalEnhancedDependency(mock(Dependency.class), emptySet()),
                new WhiteList(),
                mock(DependenciesPresenter.class));

        verify(view).init(any());
        verify(view).setGroupId(any());
        verify(view).setArtifactId(any());
        verify(view).setVersion(any());
        verify(view).setPackagesWhiteListedState(any());
        verify(view).setTransitiveDependency(eq(false));
    }

    @Test
    public void testSetupTransitive() {

        dependenciesItemPresenter.setup(
                new TransitiveEnhancedDependency(mock(Dependency.class), emptySet()),
                new WhiteList(),
                mock(DependenciesPresenter.class));

        verify(view).init(any());
        verify(view).setGroupId(any());
        verify(view).setArtifactId(any());
        verify(view).setVersion(any());
        verify(view).setPackagesWhiteListedState(any());
        verify(view).setTransitiveDependency(eq(true));
    }

    @Test
    public void testAddAllPackagesToWhiteList() {
        final DependenciesPresenter parentPresenter = mock(DependenciesPresenter.class);
        final Set<String> packages = new HashSet<>(Arrays.asList("foo", "bar"));
        final EnhancedDependency enhancedDependency = new NormalEnhancedDependency(mock(Dependency.class), packages);

        dependenciesItemPresenter.parentPresenter = parentPresenter;
        dependenciesItemPresenter.enhancedDependency = enhancedDependency;

        dependenciesItemPresenter.addAllPackagesToWhiteList();

        verify(parentPresenter).addAllToWhiteList(eq(packages));
    }

    @Test
    public void testRemoveAllPackagesToWhiteList() {
        final DependenciesPresenter parentPresenter = mock(DependenciesPresenter.class);
        final Set<String> packages = new HashSet<>(Arrays.asList("foo", "bar"));
        final EnhancedDependency enhancedDependency = new NormalEnhancedDependency(mock(Dependency.class), packages);

        dependenciesItemPresenter.parentPresenter = parentPresenter;
        dependenciesItemPresenter.enhancedDependency = enhancedDependency;

        dependenciesItemPresenter.removeAllPackagesFromWhiteList();

        verify(parentPresenter).removeAllFromWhiteList(eq(packages));
    }

    @Test
    public void testRemove() {
        final DependenciesPresenter parentPresenter = mock(DependenciesPresenter.class);
        final EnhancedDependency enhancedDependency = new NormalEnhancedDependency(mock(Dependency.class), emptySet());

        dependenciesItemPresenter.parentPresenter = parentPresenter;
        dependenciesItemPresenter.enhancedDependency = enhancedDependency;

        dependenciesItemPresenter.remove();

        verify(parentPresenter).remove(eq(enhancedDependency));
    }

    @Test
    public void testShowEditDependencyPopup() {
        final DependenciesPresenter parentPresenter = mock(DependenciesPresenter.class);
        final Dependency src = mock(Dependency.class);
        final EnhancedDependency enhancedDependency = new NormalEnhancedDependency(src, emptySet());

        dependenciesItemPresenter.parentPresenter = parentPresenter;
        dependenciesItemPresenter.enhancedDependency = enhancedDependency;

        ArgumentCaptor<Callback> captor = ArgumentCaptor.forClass(Callback.class);
        dependenciesItemPresenter.showEditDependencyPopup();

        verify(newDependencyPopup).show(captor.capture(), any());
        Dependency dependency = new Dependency();
        dependency.setArtifactId("org.kie.workbench.screens.test");
        dependency.setGroupId("kie-wb-common-library");
        dependency.setVersion("0.0.1");
        captor.getValue().callback(dependency);

        verify(view).setArtifactId("org.kie.workbench.screens.test");
        verify(view).setGroupId("kie-wb-common-library");
        verify(view).setVersion("0.0.1");

        verify(src).setArtifactId("org.kie.workbench.screens.test");
        verify(src).setGroupId("kie-wb-common-library");
        verify(src).setVersion("0.0.1");
    }
}