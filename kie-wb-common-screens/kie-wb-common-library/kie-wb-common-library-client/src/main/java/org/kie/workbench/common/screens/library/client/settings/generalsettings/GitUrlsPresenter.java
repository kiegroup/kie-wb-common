/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.generalsettings;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel.GitUrl;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

public class GitUrlsPresenter {

    private final View view;
    private final Event<NotificationEvent> notificationEventEvent;

    private Map<String, GitUrl> gitUrlsByProtocol;
    private String selectedProtocol;

    @Inject
    public GitUrlsPresenter(final View view,
                            final Event<NotificationEvent> notificationEventEvent) {

        this.view = view;
        this.notificationEventEvent = notificationEventEvent;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    public void setup(final List<GitUrl> gitUrls) {

        this.gitUrlsByProtocol = gitUrls.stream().collect(toMap(GitUrl::getProtocol, identity()));
        this.selectedProtocol = gitUrls.get(0).getProtocol();

        view.setProtocols(gitUrlsByProtocol.keySet().stream().sorted().collect(toList()));

        update();
    }

    public void setSelectedProtocol(final String selectedProtocol) {
        this.selectedProtocol = selectedProtocol;
        update();
    }

    private void update() {
        view.setUrl(gitUrlsByProtocol.get(selectedProtocol).getUrl());
    }

    public View getView() {
        return view;
    }

    public void copyToClipboard() {
        if (copy()) {
            notificationEventEvent.fire(new NotificationEvent(
                    "Git URL copied to clipboard!", SUCCESS));
        } else {
            notificationEventEvent.fire(new NotificationEvent(
                    "Git URL couldn't be copied to the clipboard because this browser does not support it", WARNING));
        }
    }

    private native boolean copy() /*-{
        return $doc.execCommand("Copy");
    }-*/;

    public interface View extends UberElemental<GitUrlsPresenter>,
                                  IsElement {

        void setProtocols(final List<String> protocols);

        void setUrl(final String url);
    }
}
