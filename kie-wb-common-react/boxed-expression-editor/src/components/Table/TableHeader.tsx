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
import { ColumnInstance, DataRecord, HeaderGroup, TableInstance } from "react-table";
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
}

export const TableHeader: React.FunctionComponent<TableHeaderProps> = ({
  tableInstance,
  tableColumns,
  setTableColumns,
  setTableRows,
  editColumnLabel,
  isHeadless = false,
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
      <Th {...column.getHeaderProps()} className="fixed-column no-clickable-cell" key={columnIndex}>
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          {column.label}
        </div>
      </Th>
    ),
    []
  );

  const renderResizableHeaderCell = useCallback(
    (column, columnIndex) => (
      <Th
        {...column.getHeaderProps()}
        {...tableInstance.getThProps(column, columnIndex)}
        className={`resizable-column ${!column.dataType ? "no-clickable-cell" : null}`}
        key={columnIndex}
      >
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          <div>
            <p className="pf-u-text-truncate">{column.label}</p>
            {column.dataType ? <p className="pf-u-text-truncate data-type">({column.dataType})</p> : null}
          </div>
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
    [tableInstance]
  );

  const renderResizableColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) =>
      column.dataType ? (
        <EditExpressionMenu
          title={editColumnLabel}
          selectedExpressionName={column.label}
          selectedDataType={column.dataType}
          onExpressionUpdate={onColumnNameOrDataTypeUpdate(columnIndex)}
          key={columnIndex}
        >
          {renderResizableHeaderCell(column, columnIndex)}
        </EditExpressionMenu>
      ) : (
        renderResizableHeaderCell(column, columnIndex)
      ),
    [editColumnLabel, onColumnNameOrDataTypeUpdate, renderResizableHeaderCell]
  );

  const renderColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) =>
      column.isCountColumn ? renderCountColumn(column, columnIndex) : renderResizableColumn(column, columnIndex),
    [renderCountColumn, renderResizableColumn]
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
