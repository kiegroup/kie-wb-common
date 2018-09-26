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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.mvp.Command;

@Templated
@ApplicationScoped
public class HitPolicyPopoverViewImpl extends AbstractPopoverViewImpl implements HitPolicyPopoverView {

    @DataField("lstHitPolicies")
    private Select lstHitPolicies;

    @DataField("lstBuiltinAggregator")
    private Select lstBuiltinAggregator;

    @DataField("lstDecisionTableOrientation")
    private Select lstDecisionTableOrientation;

    @DataField("hitPolicyLabel")
    private Span hitPolicyLabel;

    @DataField("builtinAggregatorLabel")
    private Span builtinAggregatorLabel;

    @DataField("decisionTableOrientationLabel")
    private Span decisionTableOrientationLabel;

    private BuiltinAggregatorUtils builtinAggregatorUtils;

    private HitPolicyPopoverView.Presenter presenter;

    public HitPolicyPopoverViewImpl() {
        //CDI proxy
    }

    @Inject
    public HitPolicyPopoverViewImpl(final Select lstHitPolicies,
                                    final Select lstBuiltinAggregator,
                                    final Select lstDecisionTableOrientation,
                                    final BuiltinAggregatorUtils builtinAggregatorUtils,
                                    final Div popoverElement,
                                    final Div popoverContentElement,
                                    final Span hitPolicyLabel,
                                    final Span builtinAggregatorLabel,
                                    final Span decisionTableOrientationLabel,
                                    final JQueryProducer.JQuery<Popover> jQueryPopover,
                                    final TranslationService translationService) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);

        this.lstHitPolicies = lstHitPolicies;
        this.lstBuiltinAggregator = lstBuiltinAggregator;
        this.lstDecisionTableOrientation = lstDecisionTableOrientation;
        this.builtinAggregatorUtils = builtinAggregatorUtils;

        this.hitPolicyLabel = hitPolicyLabel;
        this.builtinAggregatorLabel = builtinAggregatorLabel;
        this.decisionTableOrientationLabel = decisionTableOrientationLabel;

        this.hitPolicyLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_HitPolicyLabel));
        this.builtinAggregatorLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_BuiltinAggregatorLabel));
        this.decisionTableOrientationLabel.setTextContent(translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_DecisionTableOrientationLabel));

        setupHitPolicyEventHandler();
        setupBuiltinAggregatorEventHandler();
        setupDecisionTableOrientationEventHandler();
    }

    private void setupHitPolicyEventHandler() {
        setupChangeEventHandler(lstHitPolicies,
                                () -> {
                                    final HitPolicy hp = HitPolicy.fromValue(lstHitPolicies.getValue());
                                    presenter.setHitPolicy(hp);
                                });
    }

    private void setupBuiltinAggregatorEventHandler() {
        setupChangeEventHandler(lstBuiltinAggregator,
                                () -> {
                                    final BuiltinAggregator aggregator = builtinAggregatorUtils.toEnum(lstBuiltinAggregator.getValue());
                                    presenter.setBuiltinAggregator(aggregator);
                                });
    }

    private void setupDecisionTableOrientationEventHandler() {
        setupChangeEventHandler(lstDecisionTableOrientation,
                                () -> {
                                    final DecisionTableOrientation orientation = DecisionTableOrientation.fromValue(lstDecisionTableOrientation.getValue());
                                    presenter.setDecisionTableOrientation(orientation);
                                });
    }

    private void setupChangeEventHandler(final Select select,
                                         final Command command) {
        // org.uberfire.client.views.pfly.widgets.Select does not work with @EventHandler
        select.getElement().addEventListener("change",
                                             (event) -> command.execute(),
                                             false);
    }

    @Override
    public void init(final HitPolicyPopoverView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initHitPolicies(final List<HitPolicy> hitPolicies) {
        hitPolicies.forEach(hp -> lstHitPolicies.addOption(hp.value()));
    }

    @Override
    public void initBuiltinAggregators(final List<BuiltinAggregator> aggregators) {
        aggregators.forEach(a -> lstBuiltinAggregator.addOption(builtinAggregatorUtils.toString(a)));
    }

    @Override
    public void initDecisionTableOrientations(final List<DecisionTableOrientation> orientations) {
        orientations.forEach(o -> lstDecisionTableOrientation.addOption(o.value()));
    }

    @Override
    public void initSelectedHitPolicy(final HitPolicy hitPolicy) {
        initSelect(lstHitPolicies,
                   hitPolicy.value());
    }

    @Override
    public void initSelectedBuiltinAggregator(final BuiltinAggregator aggregator) {
        initSelect(lstBuiltinAggregator,
                   builtinAggregatorUtils.toString(aggregator));
    }

    @Override
    public void initSelectedDecisionTableOrientation(final DecisionTableOrientation orientation) {
        initSelect(lstDecisionTableOrientation,
                   orientation.value());
    }

    private void initSelect(final Select select,
                            final String value) {
        // Setting value directly throws a JavaScript error, probably because the Element is
        // not attached to the DOM at this point. Deferring setting the value works around
        Scheduler.get().scheduleDeferred(() -> select.refresh(s -> s.setValue(value)));
    }

    private void enableSelect(final Select select, final boolean enabled) {
        if (enabled) {
            select.enable();
        } else {
            select.disable();
        }
    }

    @Override
    public void enableHitPolicies(final boolean enabled) {
        enableSelect(lstHitPolicies, enabled);
    }

    @Override
    public void enableBuiltinAggregators(final boolean enabled) {
        enableSelect(lstBuiltinAggregator, enabled);
    }

    @Override
    public void enableDecisionTableOrientation(final boolean enabled) {
        enableSelect(lstDecisionTableOrientation, enabled);
    }
}
