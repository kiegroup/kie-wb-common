package org.kie.workbench.common.screens.library.client.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter.Section;
import org.kie.workbench.common.screens.library.client.settings.SyncPromises.SyncPromise;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.editor.commons.client.file.Customizable;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.screens.library.client.settings.SyncPromises.Status.REJECTED;
import static org.kie.workbench.common.screens.library.client.settings.SyncPromises.Status.RESOLVED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    @Customizable
    private SettingsSections settingsSections;

    @Mock
    private ManagedInstance<ObservablePath> observablePaths;

    @Mock
    private ProjectContext projectContext;

    @Mock
    private ObservablePath observablePathMock;

    @Mock
    private SavePopUpPresenter savePopUpPresenter;

    @Mock
    private SettingsPresenter.View view;

    @Mock
    private ProjectScreenService projectScreenService;

    private final SyncPromises promises = new SyncPromises();

    private SettingsPresenter settingsPresenter;

    @Before
    public void before() {

        doReturn(observablePathMock).when(observablePathMock).wrap(any());
        doReturn(observablePathMock).when(observablePaths).get();

        doReturn(mock(Project.class)).when(projectContext).getActiveProject();

        settingsPresenter = spy(new SettingsPresenter(
                view,
                promises,
                notificationEvent,
                settingsSections,
                savePopUpPresenter,
                new CallerMock<>(projectScreenService),
                projectContext,
                mock(SettingsPresenter.MenuItemsListPresenter.class),
                observablePaths,
                conflictingRepositoriesPopup));
    }

    @Test
    public void testSetup() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        mockSections(section1, section2);

        settingsPresenter.setup();

        verify(settingsPresenter, times(2)).setupSection(any(), any());
        verify(settingsPresenter).goTo(section1);
    }

    @Test
    public void testSetupFailWithSectionSetupThrowingException() {

        final Section section = newMockedSection();
        final RuntimeException testException = new RuntimeException("Test exception");

        mockSections(section);
        doThrow(testException).when(section).setup(any());
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(testException);

        settingsPresenter.setup();

        verify(settingsPresenter).setupSection(any(), any());
        verify(settingsPresenter, never()).goTo(any());
        verify(notificationEvent, never()).fire(any());
        verify(settingsPresenter).defaultErrorResolution(testException);
    }

    @Test
    public void testSetupWithOneSectionSetupRejection() {

        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        mockSections(section1, section2);
        doReturn(promises.reject(section1)).when(section1).setup(any());

        settingsPresenter.setup();

        // All sections are setup regardless of exceptions/rejections
        verify(settingsPresenter).setupSection(any(), eq(section1));
        verify(settingsPresenter).setupSection(any(), eq(section2));
        verify(settingsPresenter, never()).goTo(any());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testSetupSection() {
        final Section section = newMockedSection();
        final SettingsPresenter.MenuItem menuItem = mock(SettingsPresenter.MenuItem.class);

        doReturn(menuItem).when(section).getMenuItem();
        doReturn(promises.resolve()).when(settingsPresenter).resetDirtyIndicator(section);

        settingsPresenter.setupSection(mock(ProjectScreenModel.class), section);

        verify(section).setup(any());
        verify(menuItem).setup(section, settingsPresenter);
        verify(settingsPresenter).resetDirtyIndicator(section);
    }

    @Test
    public void testSetupSectionRejected() {
        final Section section = newMockedSection();

        doReturn(promises.reject(section)).when(section).setup(any());

        final Promise<Object> setupResult =
                settingsPresenter.setupSection(mock(ProjectScreenModel.class), section);

        assertTrue(setupResult instanceof SyncPromise);
        assertEquals(((SyncPromise<?>) setupResult).status, REJECTED);
        assertEquals(((SyncPromise<?>) setupResult).value, section);

        verify(section).setup(any());
        verify(settingsPresenter, never()).resetDirtyIndicator(section);
    }

    @Test
    public void testShowSaveModal() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        mockSections(section1, section2);
        doReturn(promises.resolve()).when(section1).validate();
        doReturn(promises.resolve()).when(section2).validate();

        settingsPresenter.showSaveModal();

        verify(section1).validate();
        verify(section2).validate();
        verify(savePopUpPresenter).show(any());
        verify(settingsPresenter, never()).goTo(any());
    }

    @Test
    public void testShowSaveModalWithValidationError() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        mockSections(section1, section2);
        doReturn(promises.reject(section1)).when(section1).validate();
        doReturn(promises.resolve()).when(section2).validate();

        settingsPresenter.showSaveModal();

        verify(section1).validate();
        verify(section2, never()).validate();
        verify(view).hideBusyIndicator();
        verify(savePopUpPresenter, never()).show(any());
        verify(settingsPresenter).goTo(section1);
    }

    @Test
    public void testShowSaveModalWithValidationException() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();
        final RuntimeException testException = new RuntimeException("Test exception");

        mockSections(section1, section2);
        doThrow(testException).when(section1).validate();
        doReturn(promises.resolve()).when(section2).validate();
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(any());

        settingsPresenter.showSaveModal();

        verify(section1).validate();
        verify(section2, never()).validate();
        verify(view, never()).hideBusyIndicator();
        verify(savePopUpPresenter, never()).show(any());
        verify(settingsPresenter, never()).goTo(section1);
        verify(settingsPresenter).defaultErrorResolution(testException);
    }

    @Test
    public void testSave() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        doReturn(promises.resolve()).when(settingsPresenter).resetDirtyIndicator(eq(section1));
        doReturn(promises.resolve()).when(settingsPresenter).resetDirtyIndicator(eq(section2));

        mockSections(section1, section2);

        settingsPresenter.save("Test comment");

        verify(section1).save(eq("Test comment"), any());
        verify(section2).save(eq("Test comment"), any());
        verify(settingsPresenter).saveProjectScreenModel(eq("Test comment"), eq(DeploymentMode.VALIDATED), any());
        verify(settingsPresenter).resetDirtyIndicator(eq(section1));
        verify(settingsPresenter).resetDirtyIndicator(eq(section2));
        verify(settingsPresenter).displaySuccessMessage();
    }

    @Test
    public void testSaveWithFirstSectionRejection() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();

        doReturn(promises.reject(section1)).when(section1).save(any(), any());
        doReturn(promises.resolve()).when(section2).save(any(), any());

        mockSections(section1, section2);

        settingsPresenter.save("Test comment");

        verify(section1).save(eq("Test comment"), any());
        verify(section2, never()).save(any(), any());
        verify(settingsPresenter).goTo(eq(section1));
        verify(settingsPresenter, never()).saveProjectScreenModel(any(), any(), any());
        verify(settingsPresenter, never()).resetDirtyIndicator(any());
        verify(settingsPresenter, never()).displaySuccessMessage();
    }

    @Test
    public void testSaveWithFirstSectionException() {
        final Section section1 = newMockedSection();
        final Section section2 = newMockedSection();
        final RuntimeException testException = new RuntimeException("Test exception");

        doThrow(testException).when(section1).save(any(), any());
        doReturn(promises.resolve()).when(section2).save(any(), any());
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(testException);

        mockSections(section1, section2);

        settingsPresenter.save("Test comment");

        verify(section1).save(eq("Test comment"), any());
        verify(section2, never()).save(any(), any());
        verify(settingsPresenter).defaultErrorResolution(eq(testException));
        verify(settingsPresenter, never()).saveProjectScreenModel(any(), any(), any());
        verify(settingsPresenter, never()).resetDirtyIndicator(any());
        verify(settingsPresenter, never()).displaySuccessMessage();
    }

    @Test
    public void testDisplaySuccessMessage() {
        final Promise<Void> result = settingsPresenter.displaySuccessMessage();

        assertPromiseStatusEquals(result, RESOLVED);

        verify(view).hideBusyIndicator();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testResetDirtyIndicator() {
        final Map<Section, Integer> hashes = new HashMap<>();
        final Section section = newMockedSection();

        doReturn(42).when(section).currentHashCode();

        settingsPresenter.originalHashCodes = hashes;
        settingsPresenter.resetDirtyIndicator(section);

        assertEquals((Integer) 42, hashes.get(section));
        verify(settingsPresenter).updateDirtyIndicator(eq(section));
    }

    @Test
    public void testSaveProjectScreenModel() {

        final Promise<Void> result = settingsPresenter.saveProjectScreenModel("Test comment", DeploymentMode.VALIDATED, null);

        assertPromiseStatusEquals(result, RESOLVED);

        verify(projectScreenService).save(any(), any(), eq("Test comment"), eq(DeploymentMode.VALIDATED));
        verify(settingsPresenter, never()).handlePomConcurrentUpdate(any(), any());
        verify(settingsPresenter, never()).defaultErrorResolution(any());
        verify(settingsPresenter, never()).handleSaveProjectScreenModelError(any(), any(), any());
    }

    @Test
    public void testSaveProjectScreenModelWithLocallyDetectedConcurrentUpdate() {

        settingsPresenter.concurrentPomUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);
        doNothing().when(settingsPresenter).handlePomConcurrentUpdate(eq("Test comment"), any());

        final Promise<Void> result = settingsPresenter.saveProjectScreenModel("Test comment", DeploymentMode.VALIDATED, null);

        assertPromiseStatusEquals(result, REJECTED);

        verify(projectScreenService, never()).save(any(), any(), any(), any());
        verify(settingsPresenter).handlePomConcurrentUpdate(eq("Test comment"), any());
        verify(settingsPresenter, never()).defaultErrorResolution(any());
        verify(settingsPresenter, never()).handleSaveProjectScreenModelError(any(), any(), any());
    }

    @Test
    public void testSaveProjectScreenModelThrowingException() {

        final RuntimeException testException = mock(RuntimeException.class);
        doThrow(testException).when(projectScreenService).save(any(), any(), any(), any());
        doReturn(promises.resolve()).when(settingsPresenter).handleSaveProjectScreenModelError(any(), any(), any());

        final Promise<Void> result = settingsPresenter.saveProjectScreenModel("Test comment", DeploymentMode.VALIDATED, null);

        assertPromiseStatusEquals(result, RESOLVED);

        verify(projectScreenService).save(any(), any(), eq("Test comment"), eq(DeploymentMode.VALIDATED));
        verify(settingsPresenter, never()).handlePomConcurrentUpdate(any(), any());
        verify(settingsPresenter, never()).defaultErrorResolution(any());
        verify(settingsPresenter).handleSaveProjectScreenModelError(any(), any(), any());
    }

    @Test
    public void testHandleSaveProjectScreenModelAnyException() {
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(any());
        doReturn(promises.resolve()).when(settingsPresenter).handlePomConcurrentUpdate(any(), any(), any());

        final RuntimeException testException = new RuntimeException();
        settingsPresenter.handleSaveProjectScreenModelError("Test comment", null, testException);

        verify(settingsPresenter).defaultErrorResolution(eq(testException));
        verify(settingsPresenter, never()).handlePomConcurrentUpdate(any(), any(), any());
    }

    @Test
    public void testHandleSaveProjectScreenModelGavAlreadyExistsException() {
        doReturn(promises.resolve()).when(settingsPresenter).defaultErrorResolution(any());
        doReturn(promises.resolve()).when(settingsPresenter).handlePomConcurrentUpdate(any(), any(), any());

        final GAVAlreadyExistsException testException = new GAVAlreadyExistsException();
        settingsPresenter.handleSaveProjectScreenModelError("Test comment", null, testException);

        verify(settingsPresenter, never()).defaultErrorResolution(any());
        verify(settingsPresenter).handlePomConcurrentUpdate(eq("Test comment"), any(), eq(testException));
    }

    @Test
    public void testHandlePomConcurrentUpdate() {

        settingsPresenter.currentSection = newMockedSection();
        settingsPresenter.model = mock(ProjectScreenModel.class);
        doReturn(mock(POM.class)).when(settingsPresenter.model).getPOM();

        final Promise<Void> result = settingsPresenter.handlePomConcurrentUpdate("Test comment", null, new GAVAlreadyExistsException());
        assertPromiseStatusEquals(result, REJECTED);

        verify(view).hideBusyIndicator();
        verify(conflictingRepositoriesPopup).setContent(any(), any(), any());
        verify(conflictingRepositoriesPopup).show();
    }

    @Test
    public void testForceSave() {
        settingsPresenter.concurrentPomUpdateInfo = mock(ObservablePath.OnConcurrentUpdateEvent.class);

        settingsPresenter.forceSave("Test comment", null);

        assertEquals(null, settingsPresenter.concurrentPomUpdateInfo);
        verify(conflictingRepositoriesPopup).hide();
        verify(settingsPresenter).saveProjectScreenModel(eq("Test comment"), eq(DeploymentMode.FORCED), any());
    }

    @Test
    public void testOnSettingsSectionChanged() {
        final Section section = newMockedSection();
        settingsPresenter.originalHashCodes = new HashMap<>();

        settingsPresenter.onSettingsSectionChanged(new SettingsSectionChange(section));

        verify(settingsPresenter).updateDirtyIndicator(eq(section));
    }

    @Test
    public void testUpdateDirtyIndicatorNonexistentSection() {
        final Section section = newMockedSection();
        settingsPresenter.originalHashCodes = new HashMap<>();

        settingsPresenter.updateDirtyIndicator(section);

        verify(section).setDirty(false);
    }

    @Test
    public void testUpdateDirtyIndicatorExistentDirtySection() {
        final Section section = newMockedSection();
        doReturn(42).when(section).currentHashCode();
        mockSections(section);

        settingsPresenter.originalHashCodes = new HashMap<>();
        settingsPresenter.originalHashCodes.put(section, 32);

        settingsPresenter.updateDirtyIndicator(section);

        verify(section).setDirty(true);
    }

    @Test
    public void testUpdateDirtyIndicatorExistentNotDirtySection() {
        final Section section = newMockedSection();
        doReturn(42).when(section).currentHashCode();
        mockSections(section);

        settingsPresenter.originalHashCodes = new HashMap<>();
        settingsPresenter.originalHashCodes.put(section, 42);

        settingsPresenter.updateDirtyIndicator(section);

        verify(section).setDirty(false);
    }

    @Test
    public void testGoTo() {
        final Section section = newMockedSection();

        settingsPresenter.goTo(section);

        assertEquals(section, settingsPresenter.currentSection);
        verify(view).setSection(eq(section.getView()));
    }

    private static void assertPromiseStatusEquals(final Promise<Void> promise,
                                                  final SyncPromises.Status status) {

        assertTrue(promise instanceof SyncPromise);
        assertEquals(status, ((SyncPromise<?>) promise).status);
    }

    private void mockSections(final Section... sections) {
        doReturn(Arrays.asList(sections)).when(settingsSections).getList();
    }

    private Section newMockedSection() {
        final Section section = mock(Section.class);
        doReturn(mock(SettingsPresenter.MenuItem.class)).when(section).getMenuItem();
        doReturn(promises.resolve()).when(section).setup(any());
        doReturn(promises.resolve()).when(section).save(any(), any());
        return section;
    }
}