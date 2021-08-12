package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.TransitiveEnhancedDependency;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptySet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesItemPresenterTest {

    private DependenciesItemPresenter dependenciesItemPresenter;

    @Mock
    private DependenciesItemPresenter.View view;

    @Before
    public void before() {
        dependenciesItemPresenter = spy(new DependenciesItemPresenter(view));
    }

    @Test
    public void testSetupNormal() {

        dependenciesItemPresenter.setup(
                new NormalEnhancedDependency(mock(Dependency.class), emptySet()),
                new AllowList(),
                mock(DependenciesPresenter.class));

        verify(view).init(any());
        verify(view).setGroupId(any());
        verify(view).setArtifactId(any());
        verify(view).setVersion(any());
        verify(view).setPackagesAllowListedState(any());
        verify(view).setTransitiveDependency(eq(false));
    }

    @Test
    public void testSetupTransitive() {

        dependenciesItemPresenter.setup(
                new TransitiveEnhancedDependency(mock(Dependency.class), emptySet()),
                new AllowList(),
                mock(DependenciesPresenter.class));

        verify(view).init(any());
        verify(view).setGroupId(any());
        verify(view).setArtifactId(any());
        verify(view).setVersion(any());
        verify(view).setPackagesAllowListedState(any());
        verify(view).setTransitiveDependency(eq(true));
    }

    @Test
    public void testAddAllPackagesToAllowList() {
        final DependenciesPresenter parentPresenter = mock(DependenciesPresenter.class);
        final Set<String> packages = new HashSet<>(Arrays.asList("foo", "bar"));
        final EnhancedDependency enhancedDependency = new NormalEnhancedDependency(mock(Dependency.class), packages);

        dependenciesItemPresenter.parentPresenter = parentPresenter;
        dependenciesItemPresenter.enhancedDependency = enhancedDependency;

        dependenciesItemPresenter.addAllPackagesToAllowList();

        verify(parentPresenter).addAllToAllowList(eq(packages));
    }

    @Test
    public void testRemoveAllPackagesToAllowList() {
        final DependenciesPresenter parentPresenter = mock(DependenciesPresenter.class);
        final Set<String> packages = new HashSet<>(Arrays.asList("foo", "bar"));
        final EnhancedDependency enhancedDependency = new NormalEnhancedDependency(mock(Dependency.class), packages);

        dependenciesItemPresenter.parentPresenter = parentPresenter;
        dependenciesItemPresenter.enhancedDependency = enhancedDependency;

        dependenciesItemPresenter.removeAllPackagesFromAllowList();

        verify(parentPresenter).removeAllFromAllowList(eq(packages));
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
}