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
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetBuiltinAggregatorCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetHitPolicyCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetOrientationCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HasHitPolicyControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.mvp.Command;

public class DecisionTableGrid extends BaseExpressionGrid<DecisionTable, DecisionTableUIModelMapper> implements HasListSelectorControl,
                                                                                                                HasHitPolicyControl {

    public static final BuiltinAggregator DEFAULT_AGGREGATOR = BuiltinAggregator.COUNT;

    public static final String DESCRIPTION_GROUP = "DecisionTable$Description";

    private final ListSelectorView.Presenter listSelector;
    private final HitPolicyEditorView.Presenter hitPolicyEditor;

    private final TextBoxSingletonDOMElementFactory textBoxFactory;
    private final TextAreaSingletonDOMElementFactory textAreaFactory;
    private final TextBoxSingletonDOMElementFactory headerTextBoxFactory;
    private final TextAreaSingletonDOMElementFactory headerTextAreaFactory;

    private class ListSelectorItemDefinition {

        private final String caption;
        private final boolean enabled;
        private final Command command;

        public ListSelectorItemDefinition(final String caption,
                                          final boolean enabled,
                                          final Command command) {
            this.caption = caption;
            this.enabled = enabled;
            this.command = command;
        }
    }

    public DecisionTableGrid(final GridCellTuple parent,
                             final HasExpression hasExpression,
                             final Optional<DecisionTable> expression,
                             final Optional<HasName> hasName,
                             final DMNGridPanel gridPanel,
                             final DMNGridLayer gridLayer,
                             final SessionManager sessionManager,
                             final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final Event<ExpressionEditorSelectedEvent> editorSelectedEvent,
                             final CellEditorControlsView.Presenter cellEditorControls,
                             final TranslationService translationService,
                             final ListSelectorView.Presenter listSelector,
                             final HitPolicyEditorView.Presenter hitPolicyEditor) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new DecisionTableGridData(new DMNGridData(),
                                        sessionManager,
                                        sessionCommandManager,
                                        expression,
                                        gridLayer::batch),
              new DecisionTableGridRenderer(),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent,
              cellEditorControls,
              translationService,
              false);
        this.listSelector = listSelector;
        this.hitPolicyEditor = hitPolicyEditor;

        this.textBoxFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                    gridLayer,
                                                                    this,
                                                                    sessionManager,
                                                                    sessionCommandManager,
                                                                    newCellHasNoValueCommand(),
                                                                    newCellHasValueCommand());
        this.textAreaFactory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                                      gridLayer,
                                                                      this,
                                                                      sessionManager,
                                                                      sessionCommandManager,
                                                                      newCellHasNoValueCommand(),
                                                                      newCellHasValueCommand());
        this.headerTextBoxFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                          gridLayer,
                                                                          this,
                                                                          sessionManager,
                                                                          sessionCommandManager,
                                                                          newHeaderHasNoValueCommand(),
                                                                          newHeaderHasValueCommand());
        this.headerTextAreaFactory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                                            gridLayer,
                                                                            this,
                                                                            sessionManager,
                                                                            sessionCommandManager,
                                                                            newHeaderHasNoValueCommand(),
                                                                            newHeaderHasValueCommand());

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // makeUiModelMapper needs expressionEditorDefinitionsSupplier to have been set
    }

    @Override
    public DecisionTableUIModelMapper makeUiModelMapper() {
        return new DecisionTableUIModelMapper(this::getModel,
                                              () -> expression,
                                              listSelector);
    }

    @Override
    public void initialiseUiColumns() {
        expression.ifPresent(e -> {
            model.appendColumn(new DecisionTableRowNumberColumn(e::getHitPolicy,
                                                                e::getAggregation,
                                                                cellEditorControls,
                                                                hitPolicyEditor,
                                                                this));
            e.getInput().forEach(ic -> model.appendColumn(makeInputClauseColumn(ic)));
            e.getOutput().forEach(oc -> model.appendColumn(makeOutputClauseColumn(oc)));
            model.appendColumn(new DescriptionColumn(new BaseHeaderMetaData(translationService.format(DMNEditorConstants.DecisionTableEditor_DescriptionColumnHeader),
                                                                            DESCRIPTION_GROUP),
                                                     textBoxFactory,
                                                     this));
        });

        getRenderer().setColumnRenderConstraint((isSelectionLayer, gridColumn) -> !isSelectionLayer);
    }

    private InputClauseColumn makeInputClauseColumn(final InputClause ic) {
        final LiteralExpression le = ic.getInputExpression();
        final InputClauseColumn column = new InputClauseColumn(new InputClauseColumnHeaderMetaData(le::getText,
                                                                                                   le::setText,
                                                                                                   headerTextAreaFactory),
                                                               textAreaFactory,
                                                               this);
        return column;
    }

    private OutputClauseColumn makeOutputClauseColumn(final OutputClause oc) {
        final OutputClauseColumn column = new OutputClauseColumn(outputClauseHeaderMetaData(oc),
                                                                 textAreaFactory,
                                                                 this);
        return column;
    }

    private Supplier<List<GridColumn.HeaderMetaData>> outputClauseHeaderMetaData(final OutputClause oc) {
        return () -> {
            final List<GridColumn.HeaderMetaData> metaData = new ArrayList<>();
            expression.ifPresent(dtable -> {
                if (hasName.isPresent()) {
                    final HasName name = hasName.get();
                    final Name n = name.getName();
                    metaData.add(new OutputClauseColumnExpressionNameHeaderMetaData(n::getValue,
                                                                                    n::setValue,
                                                                                    headerTextBoxFactory));
                } else {
                    metaData.add(new BaseHeaderMetaData(translationService.format(DMNEditorConstants.DecisionTableEditor_OutputClauseHeader)));
                }
                if (dtable.getOutput().size() > 1) {
                    metaData.add(new OutputClauseColumnNameHeaderMetaData(oc::getName,
                                                                          oc::setName,
                                                                          headerTextBoxFactory));
                }
            });
            return metaData;
        };
    }

    @Override
    public void initialiseUiModel() {
        expression.ifPresent(e -> {
            e.getRule().forEach(r -> {
                int columnIndex = 0;
                model.appendRow(new DMNGridRow());
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex++);
                for (int ici = 0; ici < e.getInput().size(); ici++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
                for (int oci = 0; oci < e.getOutput().size(); oci++) {
                    uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                               columnIndex++);
                }
                uiModelMapper.fromDMNModel(model.getRowCount() - 1,
                                           columnIndex);
            });
        });
    }

    @Override
    public Optional<IsElement> getEditorControls() {
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unused")
    public java.util.List<ListSelectorItem> getItems(final int uiRowIndex,
                                                     final int uiColumnIndex) {
        final java.util.List<ListSelectorItem> items = new ArrayList<>();
        getExpression().ifPresent(dtable -> {
            final DecisionTableUIModelMapperHelper.DecisionTableSection section = DecisionTableUIModelMapperHelper.getSection(dtable, uiColumnIndex);
            switch (section) {
                case INPUT_CLAUSES:
                    addItems(items,
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertInputClauseBefore),
                                                            true,
                                                            () -> addInputClause(uiColumnIndex)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertInputClauseAfter),
                                                            true,
                                                            () -> addInputClause(uiColumnIndex + 1)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteInputClause),
                                                            dtable.getInput().size() > 1,
                                                            () -> deleteInputClause(uiColumnIndex)));
                    items.add(new ListSelectorDividerItem());
                    addDecisionRuleItems(dtable,
                                         items,
                                         uiRowIndex);
                    break;

                case OUTPUT_CLAUSES:
                    addItems(items,
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertOutputClauseBefore),
                                                            true,
                                                            () -> addOutputClause(uiColumnIndex)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertOutputClauseAfter),
                                                            true,
                                                            () -> addOutputClause(uiColumnIndex + 1)),
                             new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteOutputClause),
                                                            dtable.getOutput().size() > 1,
                                                            () -> deleteOutputClause(uiColumnIndex)));
                    items.add(new ListSelectorDividerItem());
                    addDecisionRuleItems(dtable,
                                         items,
                                         uiRowIndex);
                    break;

                default:
                    addDecisionRuleItems(dtable,
                                         items,
                                         uiRowIndex);
            }
        });

        return items;
    }

    void addItems(final java.util.List<ListSelectorItem> items,
                  final ListSelectorItemDefinition onBefore,
                  final ListSelectorItemDefinition onAfter,
                  final ListSelectorItemDefinition onDelete) {
        items.add(ListSelectorTextItem.build(onBefore.caption,
                                             onBefore.enabled,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 onBefore.command.execute();
                                             }));
        items.add(ListSelectorTextItem.build(onAfter.caption,
                                             onAfter.enabled,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 onAfter.command.execute();
                                             }));
        items.add(ListSelectorTextItem.build(onDelete.caption,
                                             onDelete.enabled,
                                             () -> {
                                                 cellEditorControls.hide();
                                                 onDelete.command.execute();
                                             }));
    }

    void addDecisionRuleItems(final DecisionTable dtable,
                              final java.util.List<ListSelectorItem> items,
                              final int uiRowIndex) {
        addItems(items,
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleAbove),
                                                true,
                                                () -> addDecisionRule(uiRowIndex)),
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleBelow),
                                                true,
                                                () -> addDecisionRule(uiRowIndex + 1)),
                 new ListSelectorItemDefinition(translationService.format(DMNEditorConstants.DecisionTableEditor_DeleteDecisionRule),
                                                dtable.getRule().size() > 1,
                                                () -> deleteDecisionRule(uiRowIndex)));
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    void addInputClause(final int index) {
        expression.ifPresent(dtable -> {
            final InputClause clause = new InputClause();
            final LiteralExpression le = new LiteralExpression();
            le.setText("input");
            clause.setInputExpression(le);

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddInputClauseCommand(dtable,
                                                                    clause,
                                                                    model,
                                                                    makeInputClauseColumn(clause),
                                                                    index,
                                                                    uiModelMapper,
                                                                    this::synchroniseView));
        });
    }

    void deleteInputClause(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteInputClauseCommand(dtable,
                                                                       model,
                                                                       index,
                                                                       uiModelMapper,
                                                                       this::synchroniseView));
        });
    }

    void addOutputClause(final int index) {
        expression.ifPresent(dtable -> {
            final OutputClause clause = new OutputClause();
            clause.setName("output");

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddOutputClauseCommand(dtable,
                                                                     clause,
                                                                     model,
                                                                     makeOutputClauseColumn(clause),
                                                                     index,
                                                                     uiModelMapper,
                                                                     this::synchroniseView));
        });
    }

    void deleteOutputClause(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteOutputClauseCommand(dtable,
                                                                        model,
                                                                        index,
                                                                        uiModelMapper,
                                                                        this::synchroniseView));
        });
    }

    void addDecisionRule(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new AddDecisionRuleCommand(dtable,
                                                                     new DecisionRule(),
                                                                     model,
                                                                     new DMNGridRow(),
                                                                     index,
                                                                     uiModelMapper,
                                                                     this::synchroniseView));
        });
    }

    void deleteDecisionRule(final int index) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new DeleteDecisionRuleCommand(dtable,
                                                                        model,
                                                                        index,
                                                                        this::synchroniseView));
        });
    }

    @Override
    public HitPolicy getHitPolicy() {
        return expression.orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getHitPolicy();
    }

    @Override
    public BuiltinAggregator getBuiltinAggregator() {
        return expression.orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getAggregation();
    }

    @Override
    public DecisionTableOrientation getDecisionTableOrientation() {
        return expression.orElseThrow(() -> new IllegalArgumentException("DecisionTable has not been set.")).getPreferredOrientation();
    }

    @Override
    public void setHitPolicy(final HitPolicy hitPolicy,
                             final Command onSuccess) {
        expression.ifPresent(dtable -> {
            final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = new CompositeCommand.Builder<>();
            if (requiresDefaultAggregation(hitPolicy)) {
                commandBuilder.addCommand(new SetBuiltinAggregatorCommand(dtable,
                                                                          getDefaultAggregation(dtable, hitPolicy),
                                                                          gridLayer::batch));
            }

            commandBuilder.addCommand(new SetHitPolicyCommand(dtable,
                                                              hitPolicy,
                                                              () -> {
                                                                  gridLayer.batch();
                                                                  onSuccess.execute();
                                                              }));

            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          commandBuilder.build());
        });
    }

    boolean requiresDefaultAggregation(final HitPolicy hitPolicy) {
        return HitPolicy.COLLECT.equals(hitPolicy);
    }

    BuiltinAggregator getDefaultAggregation(final DecisionTable dtable,
                                            final HitPolicy hitPolicy) {
        if (requiresDefaultAggregation(hitPolicy)) {
            if (dtable.getAggregation() == null) {
                return DEFAULT_AGGREGATOR;
            }
            return dtable.getAggregation();
        }
        return null;
    }

    @Override
    public void setBuiltinAggregator(final BuiltinAggregator aggregator) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetBuiltinAggregatorCommand(dtable,
                                                                          aggregator,
                                                                          gridLayer::batch));
        });
    }

    @Override
    public void setDecisionTableOrientation(final DecisionTableOrientation orientation) {
        expression.ifPresent(dtable -> {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          new SetOrientationCommand(dtable,
                                                                    orientation,
                                                                    gridLayer::batch));
        });
    }
}
