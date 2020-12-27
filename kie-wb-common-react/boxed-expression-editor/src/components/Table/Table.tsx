/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { Cell, Column, ColumnInstance, Row, useBlockLayout, useResizeColumns, useTable } from "react-table";
import { TableComposable, Tbody, Td, Th, Thead, Tr } from "@patternfly/react-table";
import { EditExpressionMenu } from "../EditExpressionMenu";
import * as React from "react";
import { useCallback, useState } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { Popover } from "@patternfly/react-core";
import { EditableCell } from "./EditableCell";
import { Cells, Columns, DataType } from "../../api";
import * as _ from "lodash";

export interface TableProps {
  /** Table's columns */
  columns: Columns;
  /** Table's cells */
  cells: Cells;
  /** */
  onColumnsUpdate: (columns: Columns) => void;
  onCellsUpdate: (cells: Cells) => void;
}

export const Table: React.FunctionComponent<TableProps> = ({
  onCellsUpdate,
  onColumnsUpdate,
  cells,
  columns,
}: TableProps) => {
  const NUMBER_OF_ROWS_COLUMN = "#";
  const { i18n } = useBoxedExpressionEditorI18n();

  const [tableColumns, setTableColumns] = useState([
    { label: NUMBER_OF_ROWS_COLUMN, accessor: NUMBER_OF_ROWS_COLUMN, disableResizing: true, width: 60 },
    ..._.map(
      columns,
      (column) =>
        ({
          label: column.label,
          accessor: column.name,
          dataType: column.dataType,
        } as Column)
    ),
  ]);

  const [tableCells, setTableCells] = useState([
    ..._.map(cells, (row, rowIndex) => {
      row[NUMBER_OF_ROWS_COLUMN] = "" + (rowIndex + 1);
      return row;
    }),
  ]);

  const onCellUpdate = (rowIndex: number, columnId: string, value: string) => {
    setTableCells((prevTableCells) => {
      const updatedTableCells = [...prevTableCells];
      updatedTableCells[rowIndex][columnId] = value;
      onCellsUpdate(updatedTableCells);
      return updatedTableCells;
    });
  };

  const onColumnNameOrDataTypeUpdate = useCallback(
    (columnIndex: number) => {
      return ({ name = "", dataType = DataType.Undefined }) => {
        setTableColumns((prevTableColumns: ColumnInstance[]) => {
          const updatedTableColumns = [...prevTableColumns];
          updatedTableColumns[columnIndex].label = name;
          updatedTableColumns[columnIndex].dataType = dataType;
          onColumnsUpdate(
            _.map(updatedTableColumns, (columnInstance: ColumnInstance) => ({
              name: columnInstance.accessor,
              label: columnInstance.label,
              dataType: columnInstance.dataType,
            }))
          );
          return updatedTableColumns;
        });
      };
    },
    [onColumnsUpdate]
  );

  const [showContextMenu, setShowContextMenu] = useState(false);
  const [contextMenuTarget, setContextMenuTarget] = useState(document.body);
  const buildContextMenu = () => (
    <Popover
      aria-label="Popover with selector reference example"
      headerContent={<div>Popover Header</div>}
      bodyContent={<div>popover body</div>}
      footerContent="Popover Footer"
      isVisible={showContextMenu}
      shouldClose={() => setShowContextMenu(false)}
      shouldOpen={(showFunction) => showFunction?.()}
      reference={() => contextMenuTarget}
    />
  );

  const tableInstance = useTable(
    {
      columns: tableColumns,
      data: tableCells,
      defaultColumn: {
        Cell: (cellRef) => (cellRef.column.canResize ? EditableCell(cellRef) : cellRef.value),
      },
      onCellUpdate,
      getThProps: (column) => ({
        onContextMenu: (e) => {
          e.preventDefault();
          setContextMenuTarget(e.target);
          setShowContextMenu(true);
          console.log("contextMenu - header", e, column);
        },
      }),
      getTdProps: (cell, column, row) => ({
        onContextMenu: (e) => {
          e.preventDefault();
          setContextMenuTarget(e.target);
          setShowContextMenu(true);
          console.log("contextMenu - cell", cell, column, row);
        },
      }),
    },
    useBlockLayout,
    useResizeColumns
  );

  return (
    <div className="table-component">
      <TableComposable variant="compact" {...tableInstance.getTableProps()}>
        <Thead noWrap>
          <tr>
            {tableInstance.headers.map((column: ColumnInstance, columnIndex: number) => {
              return column.canResize ? (
                <EditExpressionMenu
                  title={i18n.editRelation}
                  selectedExpressionName={column.label}
                  selectedDataType={column.dataType}
                  onExpressionUpdate={onColumnNameOrDataTypeUpdate(columnIndex)}
                  key={columnIndex}
                >
                  <Th {...column.getHeaderProps()} {...tableInstance.getThProps(column)} key={columnIndex}>
                    <div className="header-cell">
                      <div>
                        <p className="pf-u-text-truncate">{column.label}</p>
                        <p className="pf-u-text-truncate data-type">({column.dataType})</p>
                      </div>
                      <div className="pf-c-drawer" {...column.getResizerProps()}>
                        <div className="pf-c-drawer__splitter pf-m-vertical">
                          <div className="pf-c-drawer__splitter-handle" />
                        </div>
                      </div>
                    </div>
                  </Th>
                </EditExpressionMenu>
              ) : (
                <Th {...column.getHeaderProps()} key={columnIndex}>
                  <div className="header-cell">{column.label}</div>
                </Th>
              );
            })}
          </tr>
        </Thead>

        <Tbody {...tableInstance.getTableBodyProps()}>
          {tableInstance.rows.map((row: Row, rowIndex: number) => {
            tableInstance.prepareRow(row);
            return (
              <Tr {...row.getRowProps()} key={rowIndex}>
                {row.cells.map((cell: Cell, cellIndex: number) => {
                  return (
                    <Td {...cell.getCellProps()} {...tableInstance.getTdProps(cell, cell.column, row)} key={cellIndex}>
                      {cell.render("Cell")}
                    </Td>
                  );
                })}
              </Tr>
            );
          })}
        </Tbody>
      </TableComposable>
      {showContextMenu ? buildContextMenu() : null}
    </div>
  );
};
