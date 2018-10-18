/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.test;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.workbench.client.resources.i18n.WorkbenchConstants;

import static org.dashbuilder.dataset.group.AggregateFunctionType.COUNT;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;

@Templated
public class TestRunnerReportingViewImpl
        implements TestRunnerReportingView {

    private Presenter presenter;

    @Inject
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @DataField
    private HTMLDivElement resultPanel;

    @DataField
    private HTMLDivElement testResultIcon;

    @DataField
    private HTMLDivElement testResultText;

    @DataField
    private HTMLDivElement scenariosRun;

    @DataField
    private HTMLDivElement completedAt;

    @DataField
    private HTMLDivElement duration;

    @DataField
    private HTMLAnchorElement viewAlerts;

    List<SystemMessage> systemMessages = new ArrayList<>();

    private TranslationService translationService;

    @Inject
    DisplayerLocator displayerLocator;

    @EventHandler("viewAlerts")
    public void onClickEvent(ClickEvent event) {
        PublishBatchMessagesEvent messagesEvent = new PublishBatchMessagesEvent();
        messagesEvent.setCleanExisting(true);
        messagesEvent.setMessagesToPublish(systemMessages);
        publishBatchMessagesEvent.fire(messagesEvent);
    }

    @Override
    public void reset() {
        testResultIcon.className = "";
        testResultText.textContent = "";
        this.duration.textContent = "";
        this.completedAt.textContent = "";
        this.scenariosRun.textContent = "";
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSystemMessages(List<SystemMessage> systemMessages){
        this.systemMessages = systemMessages;
    }

    @Override
    public void showSuccess() {
        testResultIcon.className = "pficon pficon-ok";
        testResultText.textContent = translationService.format(WorkbenchConstants.PASSED);
    }

    @Override
    public void showFailure() {
        testResultIcon.className = "pficon pficon-error-circle-o";
        testResultText.textContent = translationService.format(WorkbenchConstants.FAILED);
    }

    @Override
    public void setRunStatus(String completedAt,
                             String scenariosRun,
                             String duration) {
        this.completedAt.textContent = completedAt;
        this.scenariosRun.textContent = scenariosRun;
        this.duration.textContent = duration;
    }

    @Inject
    Elemental2DomUtil elemental2DomUtil;
    @DataField
    private HTMLDivElement donutDiv;

    @Inject
    public TestRunnerReportingViewImpl(HTMLDivElement resultPanel,
                                       HTMLDivElement testResultIcon,
                                       HTMLDivElement testResultText,
                                       HTMLDivElement scenariosRun,
                                       HTMLDivElement completedAt,
                                       HTMLDivElement duration,
                                       HTMLAnchorElement viewAlerts,
                                       TranslationService translationService) {
        this.resultPanel = resultPanel;
        this.testResultIcon = testResultIcon;
        this.testResultText = testResultText;
        this.scenariosRun = scenariosRun;
        this.completedAt = completedAt;
        this.duration = duration;
        this.viewAlerts = viewAlerts;
        this.translationService = translationService;
        gg();
    }

    public void gg() {

        DisplayerSettings displayerSettings =
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset("GIT_CONTRIB")
                .filter(createFilter(null))
                        .group("COLUMN_PROJECT")
                        .column("COLUMN_PROJECT",
                                "translationService.getTranslation(LibraryConstants.Project)")
                        .column(COUNT,
                                "#commits").format("translationService.getTranslation(LibraryConstants.NumberOfCommits)",
                                                   "#,##0")
                        .sort("#commits",
                              DESCENDING)
                        .subtype(DisplayerSubType.DONUT).multiple(true)
                        .titleVisible(false)
                        .margins(0,
                                 0,
                                 10,
                                 0)
                        .width(200)
                        .filterOn(false,
                                  true,
                                  true)
                        .buildSettings();
        Displayer displayer = displayerLocator.lookupDisplayer(displayerSettings);

        elemental2DomUtil.appendWidgetToElement(donutDiv,
                                                displayer.asWidget());

//        HTMLElement htmlElement = new Elemental2DomUtil().asHTMLElement(donutDiv);

//        donutDiv.appendChild(displayer);
//
//        Js.cast(TemplateWidgetMapper.get(displayer).getElement());

//        ElementWrapperWidget.getWidget(htmlElement);
//        DOMUtil.removeAllChildren(htmlElement);
//        DOMUtil.appendWidgetToElement(htmlElement,
//                                      displayer);
    }

    private String createFilter(Object o) {
        return null;
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(resultPanel);
    }
}
