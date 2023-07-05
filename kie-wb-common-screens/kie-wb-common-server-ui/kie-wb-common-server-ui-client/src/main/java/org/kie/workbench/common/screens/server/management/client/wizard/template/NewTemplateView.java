/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.wizard.template;

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;

@Dependent
@Templated
public class NewTemplateView extends Composite
        implements NewTemplatePresenter.View {

    private NewTemplatePresenter presenter;

    private TranslationService translationService;

    @DataField("new-template-name-form")
    Element templateNameGroup = DOM.createDiv();

    @Inject
    @DataField("new-template-name-textbox")
    TextBox templateName;

    @DataField("new-template-name-help")
    Element templateNameHelp = DOM.createSpan();

    @DataField("capability-checkbox-form")
    Element capabilityGroup = DOM.createDiv();

    @Inject
    @DataField("new-rule-capability-checkbox")
    CheckBox ruleEnabled;

    @Inject
    @DataField("new-process-capability-checkbox")
    CheckBox processEnabled;

    private final ArrayList<ContentChangeHandler> changeHandlers = new ArrayList<ContentChangeHandler>();

    @Inject
    public NewTemplateView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final NewTemplatePresenter presenter ) {
        this.presenter = presenter;
        ruleEnabled.setText( getRuleCheckBoxText() );
        processEnabled.setText( getProcessCheckBoxText() );

        templateName.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                if ( presenter.isTemplateNameValid() ) {
                    noErrorOnTemplateName();
                } else {
                    errorOnTemplateName();
                }
                fireChangeHandlers();
            }
        } );

        ruleEnabled.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                fireChangeHandlers();
            }
        } );
        processEnabled.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                fireChangeHandlers();
            }
        } );
        processEnabled.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                fireChangeHandlers();
            }
        } );
    }

    private void fireChangeHandlers() {
        for ( final ContentChangeHandler changeHandler : changeHandlers ) {
            changeHandler.onContentChange();
        }
    }

    @Override
    public String getTitle() {
        return getTitleText();
    }

    @Override
    public void clear() {
        templateName.setText( "" );
        ruleEnabled.setValue( false );
        processEnabled.setValue( false );
        noErrors();
    }

    @Override
    public void addContentChangeHandler( final ContentChangeHandler contentChangeHandler ) {
        changeHandlers.add( contentChangeHandler );
    }

    @Override
    public boolean getProcessCapabilityCheck() {
        return processEnabled.getValue();
    }

    @Override
    public String getTemplateName() {
        return templateName.getText();
    }

    @Override
    public boolean isRuleCapabilityChecked() {
        return ruleEnabled.getValue();
    }

    @Override
    public boolean isProcessCapabilityChecked() {
        return processEnabled.getValue();
    }

    @Override
    public void errorOnTemplateName() {
        StyleHelper.addUniqueEnumStyleName( templateNameGroup, ValidationState.class, ValidationState.ERROR );
    }

    @Override
    public void errorOnTemplateName( final String message ) {
        errorOnTemplateName();
        templateNameHelp.getStyle().setVisibility( Style.Visibility.VISIBLE );
        templateNameHelp.setInnerText( message );
    }

    @Override
    public void noErrorOnTemplateName() {
        templateNameHelp.getStyle().setVisibility( Style.Visibility.HIDDEN );
        StyleHelper.addUniqueEnumStyleName( templateNameGroup, ValidationState.class, ValidationState.NONE );
    }

    @Override
    public void errorCapability() {
        StyleHelper.addUniqueEnumStyleName( capabilityGroup, ValidationState.class, ValidationState.ERROR );
    }

    @Override
    public void noErrorOnCapability() {
        StyleHelper.addUniqueEnumStyleName( capabilityGroup, ValidationState.class, ValidationState.NONE );
    }

    @Override
    public void noErrors() {
        noErrorOnTemplateName();
        noErrorOnCapability();
    }

    @Override
    public String getInvalidErrorMessage() {
        return translationService.format( Constants.NewTemplateView_InvalidErrorMessage );
    }

    @Override
    public String getNewServerTemplateWizardTitle() {
        return translationService.format( Constants.NewTemplateView_NewServerTemplateWizardTitle );
    }

    @Override
    public String getNewServerTemplateWizardSaveSuccess() {
        return translationService.format( Constants.NewTemplateView_NewServerTemplateWizardSaveSuccess );
    }

    @Override
    public String getNewServerTemplateWizardSaveError() {
        return translationService.format( Constants.NewTemplateView_NewServerTemplateWizardSaveError );
    }

    private String getRuleCheckBoxText() {
        return translationService.format( Constants.NewTemplateView_RuleCheckBoxText );
    }

    private String getProcessCheckBoxText() {
        return translationService.format( Constants.NewTemplateView_ProcessCheckBoxText );
    }

    private String getTitleText() {
        return translationService.format( Constants.NewTemplateView_TitleText );
    }
}
