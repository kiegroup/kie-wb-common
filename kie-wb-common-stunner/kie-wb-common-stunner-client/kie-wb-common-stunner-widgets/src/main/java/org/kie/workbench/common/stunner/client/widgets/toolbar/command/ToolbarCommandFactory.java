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

package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;

@Dependent
public class ToolbarCommandFactory {

    private final ManagedInstance<ClearStatesToolbarCommand> clearStatesCommand;
    private final ManagedInstance<VisitGraphToolbarCommand> visitGraphCommand;
    private final ManagedInstance<SwitchGridToolbarCommand> switchGridCommand;
    private final ManagedInstance<ClearToolbarCommand> clearCommand;
    private final ManagedInstance<DeleteSelectionToolbarCommand> deleteSelectionCommand;
    private final ManagedInstance<UndoToolbarCommand> undoCommand;
    private final ManagedInstance<RedoToolbarCommand> redoCommand;
    private final ManagedInstance<ValidateToolbarCommand> validateCommand;
    private final ManagedInstance<RefreshToolbarCommand> refreshCommand;
    private final ManagedInstance<ExportToPngToolbarCommand> exportToPngToolbarCommand;
    private final ManagedInstance<ExportToJpgToolbarCommand> exportToJpgToolbarCommand;
    private final ManagedInstance<ExportToPdfToolbarCommand> exportToPdfToolbarCommand;

    protected ToolbarCommandFactory() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ToolbarCommandFactory(final ManagedInstance<ClearStatesToolbarCommand> clearStatesCommand,
                                 final ManagedInstance<VisitGraphToolbarCommand> visitGraphCommand,
                                 final ManagedInstance<SwitchGridToolbarCommand> switchGridCommand,
                                 final ManagedInstance<ClearToolbarCommand> clearCommand,
                                 final ManagedInstance<DeleteSelectionToolbarCommand> deleteSelectionCommand,
                                 final ManagedInstance<UndoToolbarCommand> undoCommand,
                                 final ManagedInstance<RedoToolbarCommand> redoCommand,
                                 final ManagedInstance<ValidateToolbarCommand> validateCommand,
                                 final ManagedInstance<RefreshToolbarCommand> refreshCommand,
                                 final ManagedInstance<ExportToPngToolbarCommand> exportToPngToolbarCommand,
                                 final ManagedInstance<ExportToJpgToolbarCommand> exportToJpgToolbarCommand,
                                 final ManagedInstance<ExportToPdfToolbarCommand> exportToPdfToolbarCommand) {
        this.clearStatesCommand = clearStatesCommand;
        this.visitGraphCommand = visitGraphCommand;
        this.switchGridCommand = switchGridCommand;
        this.clearCommand = clearCommand;
        this.deleteSelectionCommand = deleteSelectionCommand;
        this.undoCommand = undoCommand;
        this.redoCommand = redoCommand;
        this.validateCommand = validateCommand;
        this.refreshCommand = refreshCommand;
        this.exportToPngToolbarCommand = exportToPngToolbarCommand;
        this.exportToJpgToolbarCommand = exportToJpgToolbarCommand;
        this.exportToPdfToolbarCommand = exportToPdfToolbarCommand;
    }

    public ClearStatesToolbarCommand newClearStatesCommand() {
        return clearStatesCommand.get();
    }

    public SwitchGridToolbarCommand newSwitchGridCommand() {
        return switchGridCommand.get();
    }

    public VisitGraphToolbarCommand newVisitGraphCommand() {
        return visitGraphCommand.get();
    }

    public ClearToolbarCommand newClearCommand() {
        return clearCommand.get();
    }

    public DeleteSelectionToolbarCommand newDeleteSelectedElementsCommand() {
        return deleteSelectionCommand.get();
    }

    public UndoToolbarCommand newUndoCommand() {
        return undoCommand.get();
    }

    public RedoToolbarCommand newRedoCommand() {
        return redoCommand.get();
    }

    public ValidateToolbarCommand newValidateCommand() {
        return validateCommand.get();
    }

    public RefreshToolbarCommand newRefreshCommand() {
        return refreshCommand.get();
    }

    public ExportToPngToolbarCommand newExportToPngToolbarCommand() {
        return exportToPngToolbarCommand.get();
    }

    public ExportToJpgToolbarCommand newExportToJpgToolbarCommand() {
        return exportToJpgToolbarCommand.get();
    }

    public ExportToPdfToolbarCommand newExportToPdfToolbarCommand() {
        return exportToPdfToolbarCommand.get();
    }
}
