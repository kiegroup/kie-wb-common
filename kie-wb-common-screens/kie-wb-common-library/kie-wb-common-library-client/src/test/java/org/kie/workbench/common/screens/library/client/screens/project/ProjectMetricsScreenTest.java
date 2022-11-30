/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.kie.workbench.common.screens.library.client.screens.project;

import java.util.ArrayList;
import java.util.Date;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.RawDataSet;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayer;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.kie.workbench.common.screens.contributors.service.ContributorsService;
import org.kie.workbench.common.screens.library.client.events.WorkbenchProjectMetricsEvent;
import org.kie.workbench.common.screens.library.client.util.ProjectMetricsFactory;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.dashbuilder.dataset.Assertions.assertDataSetValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_AUTHOR;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_BRANCH;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_DATE;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_MSG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_ORG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_PROJECT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_REPO;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectMetricsScreenTest extends AbstractDisplayerTest {

    public static class ContributorsData extends RawDataSet {

        public static final ContributorsData INSTANCE = new ContributorsData(
                new String[]{COLUMN_ORG, COLUMN_REPO, COLUMN_BRANCH, COLUMN_PROJECT, COLUMN_AUTHOR, COLUMN_DATE, COLUMN_MSG},
                new Class[]{String.class, String.class, String.class, String.class, String.class, Date.class, String.class},
                new String[][]{
                        {"org1", "repo", "main", "project1", "user1", "01/01/19 12:00", "Commit 1"},
                        {"org1", "repo", "main", "project1", "user1", "03/02/19 12:00", "Commit 2"},
                        {"org1", "repo", "main", "project1", "user2", "04/03/19 12:00", "Commit 3"},
                        {"org1", "repo", "main", "project1", "user2", "06/04/19 12:00", "Commit 4"},
                        {"org2", "repo", "main", "project2", "user3", "07/05/19 12:00", "Commit 5"},
                        {"org2", "repo", "main", "project2", "user3", "09/06/19 12:00", "Commit 6"},
                        {"org2", "repo", "main", "project2", "user4", "11/07/19 12:00", "Commit 7"},
                        {"org2", "repo", "main", "project2", "user4", "02/08/20 12:00", "Commit 8"},
                        {"emptyOrg", null, null, null, null, null, null}});

        public ContributorsData(String[] columnIds,
                                Class[] types,
                                String[][] data) {
            super(columnIds,
                  types,
                  data);
        }
    }

    @Mock
    ProjectMetricsScreen.View view;
    @Mock
    TranslationService i18n;
    @Mock
    DisplayerListener displayerListener;
    @Mock
    Repository repository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WorkspaceProject project;
    ProjectMetricsFactory metricsFactory;
    ProjectMetricsScreen presenter;
    DataSet contributorsDataSet;
    DisplayerCoordinator displayerCoordinator;

    @Mock
    ContributorsService contributorsService;

    @Before
    public void init() throws Exception {
        super.init();

        when(project.getRepository()).thenReturn(repository);
        when(repository.getAlias()).thenReturn("repo");
        ArrayList<Branch> branches = new ArrayList<>();
        branches.add(new Branch("main", mock(Path.class)));
        doReturn(branches).when(repository).getBranches();
        when(project.getName()).thenReturn("project1");
        doReturn(new Branch("main", mock(Path.class))).when(project).getBranch();
        when(project.getOrganizationalUnit().getName()).thenReturn("org1");

        contributorsDataSet = ContributorsData.INSTANCE.toDataSet();
        contributorsDataSet.setUUID(ContributorsDataSets.GIT_CONTRIB);
        clientDataSetManager.registerDataSet(contributorsDataSet);

        displayerCoordinator = new DisplayerCoordinator(rendererManager);
        displayerCoordinator.addListener(displayerListener);

        metricsFactory = new ProjectMetricsFactory(i18n,
                                                   displayerLocator);

        presenter = new ProjectMetricsScreen(view,
                                             i18n,
                                             metricsFactory,
                                             displayerCoordinator,
                                             new CallerMock<>(contributorsService));
        presenter.onStartup(new WorkbenchProjectMetricsEvent(project));
    }

    @Test
    public void testDrawAll() {
        verify(view).clear();
        verify(view).setTopContribSelectorDisplayer(presenter.getTopAuthorSelectorDisplayer());
        verify(view).setDateSelectorDisplayer(presenter.getDateSelectorDisplayer());
        verify(view).setCommitsOverTimeDisplayer(presenter.getCommitsOverTimeDisplayer());
        verify(view).setCommitsPerAuthorDisplayer(presenter.getCommitsPerAuthorDisplayer());
        verify(view).setCommitsByYearDisplayer(presenter.getCommitsByYearDisplayer());
        verify(view).setCommitsByQuarterDisplayer(presenter.getCommitsByQuarterDisplayer());
        verify(view).setCommitsByDayOfWeekDisplayer(presenter.getCommitsByDayOfWeekDisplayer());
        verify(view).setAllCommitsDisplayer(presenter.getAllCommitsDisplayer());

        verify(displayerListener).onDraw(presenter.getCommitsOverTimeDisplayer());
        verify(displayerListener).onDraw(presenter.getCommitsPerAuthorDisplayer());
        verify(displayerListener).onDraw(presenter.getTopAuthorSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getDateSelectorDisplayer());
        verify(displayerListener).onDraw(presenter.getCommitsByYearDisplayer());
        verify(displayerListener).onDraw(presenter.getCommitsByQuarterDisplayer());
        verify(displayerListener).onDraw(presenter.getCommitsByDayOfWeekDisplayer());
        verify(displayerListener).onDraw(presenter.getAllCommitsDisplayer());
    }

    @Test
    public void testCommitsPerAuthor() {
        Displayer displayer = presenter.getCommitsPerAuthorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"user1", "2.00", "2.00", "user1", "1.00"},
                                    {"user2", "2.00", "2.00", "user2", "1.00"}
                            },
                            0);
    }

    @Test
    public void testCommitsOverTime() {
        Displayer displayer = presenter.getCommitsOverTimeDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"2019-01", "1.00"},
                                    {"2019-02", "0.00"},
                                    {"2019-03", "1.00"},
                                    {"2019-04", "1.00"},
                                    {"2019-05", "0.00"},
                                    {"2019-06", "1.00"},
                            },
                            0);
    }

    @Test
    public void testTopAuthorSelector() {
        Displayer displayer = presenter.getTopAuthorSelectorDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"user1", "2.00"},
                                    {"user2", "2.00"}
                            },
                            0);
    }

    @Test
    public void testYearsSelector() {
        Displayer displayer = presenter.getCommitsByYearDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"2019", "4.00"}
                            },
                            0);
    }

    @Test
    public void testQuarterSelector() {
        Displayer displayer = presenter.getCommitsByQuarterDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"1", "2.00"},
                                    {"2", "2.00"}
                            },
                            0);
    }

    @Test
    public void testDayOfWeekSelector() {
        Displayer displayer = presenter.getCommitsByDayOfWeekDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"1", "0.00"},
                                    {"2", "0.00"},
                                    {"3", "2.00"},
                                    {"4", "1.00"},
                                    {"5", "0.00"},
                                    {"6", "0.00"},
                                    {"7", "1.00"}
                            },
                            0);
    }

    @Test
    public void testAllCommits() {
        Displayer displayer = presenter.getAllCommitsDisplayer();
        DataSet dataSet = displayer.getDataSetHandler().getLastDataSet();

        assertDataSetValues(dataSet,
                            new String[][]{
                                    {"user2", "06/04/19 12:00", "Commit 4"},
                                    {"user2", "04/03/19 12:00", "Commit 3"},
                                    {"user1", "03/02/19 12:00", "Commit 2"},
                                    {"user1", "01/01/19 12:00", "Commit 1"}
                            },
                            0);
    }

    @Test
    public void testSelectYear() throws Exception {
        AbstractDisplayer displayer = (AbstractDisplayer) presenter.getCommitsByYearDisplayer();
        displayer.filterUpdate(COLUMN_DATE,
                               0); // "2019" selected
        DataSet dataSet = presenter.getAllCommitsDisplayer().getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(),
                     4);
    }

    @Test
    public void testSelectWeekOfDay() throws Exception {
        AbstractDisplayer displayer = (AbstractDisplayer) presenter.getCommitsByDayOfWeekDisplayer();
        displayer.filterUpdate(COLUMN_DATE,
                               2); // "Tuesday" selected
        DataSet dataSet = presenter.getAllCommitsDisplayer().getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(),
                     2);
    }

    @Test
    public void testAlwaysShow7Days() throws Exception {
        AbstractDisplayer displayer = (AbstractDisplayer) presenter.getTopAuthorSelectorDisplayer();
        displayer.filterUpdate(COLUMN_AUTHOR,
                               1); // "user" selected
        DataSet dataSet = presenter.getAllCommitsDisplayer().getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(),
                     2);

        // Bar chart must always show 7 bars, one per day of week
        dataSet = presenter.getCommitsByDayOfWeekDisplayer().getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(),
                     7);
    }

    @Test
    public void testSelectAuthorAndWeekOfDay() throws Exception {
        AbstractDisplayer displayer = (AbstractDisplayer) presenter.getTopAuthorSelectorDisplayer();
        displayer.filterUpdate(COLUMN_AUTHOR,
                               1); // "user" selected
        DataSet dataSet = presenter.getAllCommitsDisplayer().getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(),
                     2);

        displayer = (AbstractDisplayer) presenter.getCommitsByDayOfWeekDisplayer();
        displayer.filterUpdate(COLUMN_DATE,
                               2); // "Tuesday" selected
        dataSet = presenter.getAllCommitsDisplayer().getDataSetHandler().getLastDataSet();
        assertEquals(dataSet.getRowCount(),
                     1);
    }

    @Test
    public void dateSelectorFormatTest() {
        DisplayerSettings settings = metricsFactory.buildDateSelectorSettings(project);
        assertEquals(settings.getColumnSettings(COLUMN_DATE).getValuePattern(),
                     "dd MMM, yyyy HH:mm");
    }

    @Test
    public void displayersListenOthersTest() {
        assertTrue(metricsFactory.buildAllCommitsSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildCommitsByDayOfWeekSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildCommitsByQuarterSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildCommitsByYearSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildCommitsOverTimeSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildDateSelectorSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildTopContributorSelectorSettings(project).isFilterListeningEnabled());
        assertTrue(metricsFactory.buildCommitsPerAuthorSettings(project).isFilterListeningEnabled());
    }
}