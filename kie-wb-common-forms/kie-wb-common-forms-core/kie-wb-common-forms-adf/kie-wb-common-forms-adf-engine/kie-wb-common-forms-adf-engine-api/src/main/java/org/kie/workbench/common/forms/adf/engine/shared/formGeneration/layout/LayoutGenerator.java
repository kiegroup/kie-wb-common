/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutSettings;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Dependent
public class LayoutGenerator {

    public static final int MAX_SPAN = 12;

    private ColSpan[] structure;

    private List<Row> rows = new ArrayList<>();

    private Row currentRow;

    public void init(LayoutColumnDefinition[] structure) {

        currentRow = null;

        rows.clear();

        int autoCount = 0;

        for (LayoutColumnDefinition col : structure) {
            autoCount++;
        }

        int freeOffset = MAX_SPAN % autoCount;
        int freeAVGSpan = Math.floorDiv(MAX_SPAN,
                autoCount);

        List<ColSpan> spans = new ArrayList<>();

        for (LayoutColumnDefinition definition : structure) {
            int span = freeAVGSpan;

            spans.add(ColSpan.calculateSpan(span));
        }

        this.structure = spans.toArray(new ColSpan[spans.size()]);

        newRow();
    }

    public void addComponent(LayoutComponent component,
                             LayoutSettings settings) {
        if (currentRow.isFull()) {
            newRow();
        }

        currentRow.addComponent(component, settings);
    }

    public LayoutTemplate build() {
        LayoutTemplate template = new LayoutTemplate();

        rows.forEach(row -> {
            LayoutRow layoutRow = new LayoutRow();

            template.addRow(layoutRow);

            row.cells.forEach(cell -> {
                LayoutColumn layoutColumn = new LayoutColumn(String.valueOf(cell.horizontalSpan));
                layoutRow.add(layoutColumn);

                if (cell.getComponentsCount() == 0) {
                    return;
                } else {
                    layoutColumn.add(cell.components.get(0));
                }
            });
        });

        return template;
    }

    protected void newRow() {
        List<Cell> cells = new ArrayList<>();
        for (ColSpan span : structure) {
            cells.add(new Cell(span.getSpan()));
        }
        currentRow = new Row(cells);
        rows.add(currentRow);
    }

    private class Row {

        List<Cell> cells;

        int currentIndex = 0;

        public Row(List<Cell> cells) {
            this.cells = cells;
        }

        boolean isFull() {
            return currentIndex == cells.size() || (currentIndex > 0 && cells.get(currentIndex - 1).wrap);
        }

        public void addComponent(LayoutComponent component,
                                    LayoutSettings settings) {

            int horizontalSpan = settings.getHorizontalSpan();

            Cell currentCell = cells.get(currentIndex);
            currentCell.wrap = settings.isWrap();

            currentCell.addLayoutComponent(component);

            if (horizontalSpan > 1) {
                while (horizontalSpan > 1 && cells.size() > currentIndex + 1) {
                    Cell cell = cells.remove(currentIndex + 1);
                    currentCell.horizontalSpan += cell.horizontalSpan;
                }
            }

            currentIndex++;
        }
    }

    private class Cell {

        private int horizontalSpan = 1;
        private boolean wrap;

        private List<LayoutComponent> components = new ArrayList<>();

        public Cell(int horizontalSpan) {
            this.horizontalSpan = horizontalSpan;
        }

        private void addLayoutComponent(LayoutComponent component) {
            components.add(component);
        }

        private int getComponentsCount() {
            return components.size();
        }

    }
}
