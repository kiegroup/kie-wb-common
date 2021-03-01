/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback, useMemo } from "react";
import { Th, Thead, Tr } from "@patternfly/react-table";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord, HeaderGroup, TableInstance } from "react-table";
import { EditExpressionMenu } from "../EditExpressionMenu";
import { DataType } from "../../api";

export interface TableHeaderProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** Columns instance */
  tableColumns: ColumnInstance[];
  /** Function for setting table columns */
  setTableColumns: React.Dispatch<React.SetStateAction<ColumnInstance[]>>;
  /** Function for setting table rows */
  setTableRows: React.Dispatch<React.SetStateAction<DataRecord[]>>;
  /** Optional label to be used for the edit popover that appears when clicking on column header */
  editColumnLabel?: string;
  /** True to have only last level of header shown */
  isHeadless?: boolean;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
}

export const TableHeader: React.FunctionComponent<TableHeaderProps> = ({
  tableInstance,
  tableColumns,
  setTableColumns,
  setTableRows,
  editColumnLabel,
  isHeadless = false,
  getColumnKey,
}) => {
  const updateColumnNameInRows = useCallback(
    (prevColumnName: string, newColumnName: string) =>
      setTableRows((prevTableCells) => {
        return _.map(prevTableCells, (tableCells) => {
          const assignedCellValue = tableCells[prevColumnName]!;
          delete tableCells[prevColumnName];
          tableCells[newColumnName] = assignedCellValue;
          return tableCells;
        });
      }),
    [setTableRows]
  );

  const onColumnNameOrDataTypeUpdate = useCallback(
    (columnIndex: number) => {
      return ({ name = "", dataType = DataType.Undefined }) => {
        const prevColumnName = (tableColumns[columnIndex] as ColumnInstance).accessor as string;
        setTableColumns((prevTableColumns: ColumnInstance[]) => {
          const updatedTableColumns = [...prevTableColumns];
          updatedTableColumns[columnIndex].label = name;
          updatedTableColumns[columnIndex].accessor = name;
          updatedTableColumns[columnIndex].dataType = dataType;
          return updatedTableColumns;
        });
        if (name !== prevColumnName) {
          updateColumnNameInRows(prevColumnName, name);
        }
      };
    },
    [setTableColumns, tableColumns, updateColumnNameInRows]
  );

  const renderCountColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) => (
      <Th
        {...column.getHeaderProps()}
        className="fixed-column no-clickable-cell"
        key={`${getColumnKey(column)}-${columnIndex}`}
      >
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          {column.label}
        </div>
      </Th>
    ),
    [getColumnKey]
  );

  const renderHeaderCellInfo = useCallback(
    (column) => (
      <div className="header-cell-info" data-ouia-component-type="expression-column-header-cell-info">
        <p className="pf-u-text-truncate">{column.label}</p>
        {column.dataType ? <p className="pf-u-text-truncate data-type">({column.dataType})</p> : null}
      </div>
    ),
    []
  );

  const renderResizableHeaderCell = useCallback(
    (column, columnIndex) => (
      <Th
        {...column.getHeaderProps()}
        {...tableInstance.getThProps(column, columnIndex)}
        className={`resizable-column ${!column.dataType ? "no-clickable-cell" : null}`}
        key={`${getColumnKey(column)}-${columnIndex}`}
      >
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          {column.dataType ? (
            <EditExpressionMenu
              title={editColumnLabel}
              selectedExpressionName={column.label}
              selectedDataType={column.dataType}
              onExpressionUpdate={onColumnNameOrDataTypeUpdate(columnIndex)}
              key={`${getColumnKey(column)}-${columnIndex}`}
            >
              {renderHeaderCellInfo(column)}
            </EditExpressionMenu>
          ) : (
            renderHeaderCellInfo(column)
          )}
          <div
            className={`pf-c-drawer ${!column.canResize ? "resizer-disabled" : ""}`}
            {...(column.canResize ? column.getResizerProps() : {})}
          >
            <div className="pf-c-drawer__splitter pf-m-vertical">
              <div className="pf-c-drawer__splitter-handle" />
            </div>
          </div>
        </div>
      </Th>
    ),
    [editColumnLabel, getColumnKey, onColumnNameOrDataTypeUpdate, renderHeaderCellInfo, tableInstance]
  );

  const renderColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) =>
      column.isCountColumn ? renderCountColumn(column, columnIndex) : renderResizableHeaderCell(column, columnIndex),
    [renderCountColumn, renderResizableHeaderCell]
  );

  const renderHeaderGroups = useMemo(
    () =>
      tableInstance.headerGroups.map((headerGroup: HeaderGroup) => (
        <Tr key={headerGroup.accessor} {...headerGroup.getHeaderGroupProps()}>
          {headerGroup.headers.map((column: ColumnInstance, columnIndex: number) => renderColumn(column, columnIndex))}
        </Tr>
      )),
    [renderColumn, tableInstance.headerGroups]
  );

  const renderLastLevelInHeaderGroups = useMemo(
    () => (
      <Tr>
        {_.last(
          tableInstance.headerGroups as HeaderGroup[]
        )!.headers.map((column: ColumnInstance, columnIndex: number) => renderColumn(column, columnIndex))}
      </Tr>
    ),
    [renderColumn, tableInstance.headerGroups]
  );

  return <Thead noWrap>{isHeadless ? renderLastLevelInHeaderGroups : renderHeaderGroups}</Thead>;
};
