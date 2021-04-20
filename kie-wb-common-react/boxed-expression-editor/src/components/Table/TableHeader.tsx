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
import { DataType, TableHeaderVisibility } from "../../api";
import { DRAWER_SPLITTER_ELEMENT } from "../Resizer";

export interface TableHeaderProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** Rows instance */
  tableRows: React.MutableRefObject<DataRecord[]>;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate: (rows: DataRecord[]) => void;
  /** Optional label to be used for the edit popover that appears when clicking on column header */
  editColumnLabel?: string;
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
  /** Columns instance */
  tableColumns: React.MutableRefObject<Column[]>;
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[]) => void;
}

export const TableHeader: React.FunctionComponent<TableHeaderProps> = ({
  tableInstance,
  tableRows,
  onRowsUpdate,
  editColumnLabel,
  headerVisibility = TableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getColumnKey,
  tableColumns,
  onColumnsUpdate,
}) => {
  const updateColumnNameInRows = useCallback(
    (prevColumnName: string, newColumnName: string) =>
      onRowsUpdate(
        _.map(tableRows.current, (tableCells) => {
          const assignedCellValue = tableCells[prevColumnName]!;
          delete tableCells[prevColumnName];
          tableCells[newColumnName] = assignedCellValue;
          return tableCells;
        })
      ),
    [onRowsUpdate, tableRows]
  );

  const onColumnNameOrDataTypeUpdate = useCallback(
    (columnIndex: number) => {
      return ({ name = "", dataType = DataType.Undefined }) => {
        const prevColumnName = (tableColumns.current[columnIndex] as ColumnInstance).accessor as string;
        const updatedTableColumns = [...tableColumns.current] as ColumnInstance[];
        updatedTableColumns[columnIndex].label = name;
        updatedTableColumns[columnIndex].accessor = name;
        updatedTableColumns[columnIndex].dataType = dataType;
        onColumnsUpdate(updatedTableColumns);
        if (name !== prevColumnName) {
          updateColumnNameInRows(prevColumnName, name);
        }
      };
    },
    [onColumnsUpdate, tableColumns, updateColumnNameInRows]
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
        {column.headerCellElement ? column.headerCellElement : <p className="pf-u-text-truncate">{column.label}</p>}
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
            {DRAWER_SPLITTER_ELEMENT}
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

  const getHeaderGroups = useCallback(
    (tableInstance) => {
      return skipLastHeaderGroup ? _.dropRight(tableInstance.headerGroups) : tableInstance.headerGroups;
    },
    [skipLastHeaderGroup]
  );

  const renderHeaderGroups = useMemo(
    () =>
      getHeaderGroups(tableInstance).map((headerGroup: HeaderGroup) => (
        <Tr key={headerGroup.getHeaderGroupProps().key} {...headerGroup.getHeaderGroupProps()}>
          {headerGroup.headers.map((column: ColumnInstance, columnIndex: number) => renderColumn(column, columnIndex))}
        </Tr>
      )),
    [getHeaderGroups, renderColumn, tableInstance]
  );

  const renderAtLevelInHeaderGroups = useCallback(
    (level: number) => (
      <Tr>
        {_.nth(
          tableInstance.headerGroups as HeaderGroup[],
          level
        )!.headers.map((column: ColumnInstance, columnIndex: number) => renderColumn(column, columnIndex))}
      </Tr>
    ),
    [renderColumn, tableInstance.headerGroups]
  );

  switch (headerVisibility) {
    case TableHeaderVisibility.Full:
      return <Thead noWrap>{renderHeaderGroups}</Thead>;
    case TableHeaderVisibility.LastLevel:
      return <Thead noWrap>{renderAtLevelInHeaderGroups(-1)}</Thead>;
    case TableHeaderVisibility.SecondToLastLevel:
      return <Thead noWrap>{renderAtLevelInHeaderGroups(-2)}</Thead>;
    default:
      return null;
  }
};
