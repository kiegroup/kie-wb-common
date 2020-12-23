/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import "./RelationExpression.css";
import * as React from "react";
import { ChangeEvent, useCallback, useEffect, useState } from "react";
import "@patternfly/patternfly/utilities/Text/text.css";
import { DataType, RelationProps } from "../../api";
import { Cell, Column, ColumnInstance, Row, useBlockLayout, useResizeColumns, useTable } from "react-table";
import { TableComposable, Tbody, Td, Th, Thead, Tr } from "@patternfly/react-table";
import * as _ from "lodash";
import { EditExpressionMenu } from "../EditExpressionMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { Popover } from "@patternfly/react-core";

function EditableCell({
  value: initialValue,
  row: { index },
  column: { id },
  onCellUpdate,
}: {
  value: string;
  row: { index: number };
  column: { id: string };
  onCellUpdate: (rowIndex: number, columnId: string, value: string) => void;
}): JSX.Element | string {
  const [value, setValue] = React.useState(initialValue);

  const onChange = useCallback((e: ChangeEvent<HTMLTextAreaElement>) => {
    setValue(e.target.value);
  }, []);

  const onBlur = useCallback(() => {
    onCellUpdate(index, id, value);
  }, [id, index, value, onCellUpdate]);

  return <textarea value={value} onChange={onChange} onBlur={onBlur} />;
}

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const NUMBER_OF_ROWS_COLUMN = "#";
  const FIRST_COLUMN_NAME = "column-1";

  const generateFirstRow = () => {
    const firstRow: { [key: string]: string } = {};
    firstRow[FIRST_COLUMN_NAME] = "";
    return firstRow;
  };

  const columns =
    relationProps.columns === undefined
      ? [
          { name: FIRST_COLUMN_NAME, label: FIRST_COLUMN_NAME, dataType: DataType.Undefined },
          { name: "column-2", label: "column-2", dataType: DataType.Undefined },
        ]
      : relationProps.columns;
  const rows = relationProps.rows === undefined ? [generateFirstRow()] : relationProps.rows;
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
    ..._.map(rows, (row, rowIndex) => {
      row[NUMBER_OF_ROWS_COLUMN] = "" + (rowIndex + 1);
      return row;
    }),
  ]);

  const [showContextMenu, setShowContextMenu] = useState(false);

  const [contextMenuTarget, setContextMenuTarget] = useState(document.body);

  useEffect(() => {
    window.beeApi?.broadcastRelationExpressionDefinition?.({
      ...relationProps,
      columns: _.map(tableColumns, (columnInstance: ColumnInstance) => {
        return {
          name: columnInstance.accessor,
          label: columnInstance.label,
          dataType: columnInstance.dataType,
        };
      }),
      rows: tableCells,
    });
  }, [relationProps, tableColumns, tableCells]);

  const onCellUpdate = (rowIndex: number, columnId: string, value: string) => {
    setTableCells((prevTableCells) => {
      const updatedTableCells = [...prevTableCells];
      updatedTableCells[rowIndex][columnId] = value;
      return updatedTableCells;
    });
  };

  const onColumnNameOrDataTypeUpdate = useCallback((columnIndex: number) => {
    return ({ name = "", dataType = DataType.Undefined }) => {
      setTableColumns((prevTableColumns: ColumnInstance[]) => {
        const updatedTableColumns = [...prevTableColumns];
        updatedTableColumns[columnIndex].label = name;
        updatedTableColumns[columnIndex].dataType = dataType;
        return updatedTableColumns;
      });
    };
  }, []);

  const buildContextMenu = () => (
    <Popover
      aria-label="Popover with selector reference example"
      headerContent={<div>Popover Header</div>}
      bodyContent={
        <div>
          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam id feugiat augue, nec fringilla turpis.
        </div>
      }
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
    <div className="relation-expression">
      <TableComposable variant="compact" {...tableInstance.getTableProps()}>
        <Thead noWrap>
          <tr>
            {tableInstance.headers.map((column: ColumnInstance, columnIndex: number) => {
              return (
                <Th {...column.getHeaderProps()} {...tableInstance.getThProps(column)} key={columnIndex}>
                  {column.canResize ? (
                    <EditExpressionMenu
                      title={i18n.editRelation}
                      selectedExpressionName={column.label}
                      selectedDataType={column.dataType}
                      onExpressionUpdate={onColumnNameOrDataTypeUpdate(columnIndex)}
                    >
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
                    </EditExpressionMenu>
                  ) : (
                    <div className="header-cell">{column.label}</div>
                  )}
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
