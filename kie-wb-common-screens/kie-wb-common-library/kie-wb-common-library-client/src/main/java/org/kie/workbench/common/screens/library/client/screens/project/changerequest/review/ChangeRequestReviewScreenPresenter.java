/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

@WorkbenchScreen(identifier = LibraryPlaces.CHANGE_REQUEST_REVIEW,
        owningPerspective = LibraryPerspective.class)
public class ChangeRequestReviewScreenPresenter {

    public interface View extends UberElemental<ChangeRequestReviewScreenPresenter> {

        void setTitle(String title);
    }

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final ManagedInstance<DiffItemPresenter> diffItemPresenters;
    private final Caller<ChangeRequestService> changeRequestService;
    private final BusyIndicatorView busyIndicatorView;

    @Inject
    public ChangeRequestReviewScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final ManagedInstance<DiffItemPresenter> diffItemPresenters,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final BusyIndicatorView busyIndicatorView) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.diffItemPresenters = diffItemPresenters;
        this.changeRequestService = changeRequestService;
        this.busyIndicatorView = busyIndicatorView;
    }

    @PostConstruct
    public void postConstruct() {
        this.prepareView();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "CR summary here";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    private void prepareView() {
        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }
}
