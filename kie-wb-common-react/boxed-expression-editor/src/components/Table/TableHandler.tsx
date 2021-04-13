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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { DataType, TableHandlerConfiguration, TableOperation } from "../../api";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord } from "react-table";
import { Popover } from "@patternfly/react-core";
import { TableHandlerMenu } from "./TableHandlerMenu";
import { BoxedExpressionGlobalContext } from "../../context";

export interface TableHandlerProps {
  /** The prefix to be used for the column name */
  columnPrefix: string;
  /** Columns instance */
  tableColumns: React.MutableRefObject<Column[]>;
  /** Last selected column index */
  lastSelectedColumnIndex: number;
  /** Last selected row index */
  lastSelectedRowIndex: number;
  /** Rows instance */
  tableRows: React.MutableRefObject<DataRecord[]>;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate: (rows: DataRecord[]) => void;
  /** Function to be executed when adding a new row to the table */
  onRowAdding: () => DataRecord;
  /** Show/hide table handler */
  showTableHandler: boolean;
  /** Function to programmatically show/hide table handler */
  setShowTableHandler: React.Dispatch<React.SetStateAction<boolean>>;
  /** Target for showing the table handler  */
  tableHandlerTarget: HTMLElement;
  /** Custom configuration for the table handler */
  handlerConfiguration: TableHandlerConfiguration;
  /** Table handler allowed operations */
  tableHandlerAllowedOperations: TableOperation[];
  /** Custom function called for manually resetting a row */
  resetRowCustomFunction?: (row: DataRecord) => DataRecord;
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[]) => void;
}

export const TableHandler: React.FunctionComponent<TableHandlerProps> = ({
  columnPrefix,
  tableColumns,
  lastSelectedColumnIndex,
  lastSelectedRowIndex,
  tableRows,
  onRowsUpdate,
  onRowAdding,
  showTableHandler,
  setShowTableHandler,
  tableHandlerTarget,
  handlerConfiguration,
  tableHandlerAllowedOperations,
  resetRowCustomFunction = () => ({}),
  onColumnsUpdate,
}) => {
  const globalContext = useContext(BoxedExpressionGlobalContext);

  const [selectedColumnIndex, setSelectedColumnIndex] = useState(lastSelectedColumnIndex);
  const [selectedRowIndex, setSelectedRowIndex] = useState(lastSelectedRowIndex);

  useEffect(() => {
    setSelectedColumnIndex(lastSelectedColumnIndex);
  }, [lastSelectedColumnIndex]);

  useEffect(() => {
    setSelectedRowIndex(lastSelectedRowIndex);
  }, [lastSelectedRowIndex]);

  const insertBefore = <T extends unknown>(elements: T[], index: number, element: T) => {
    return [...elements.slice(0, index), element, ...elements.slice(index)];
  };

  const insertAfter = <T extends unknown>(elements: T[], index: number, element: T) => {
    return [...elements.slice(0, index + 1), element, ...elements.slice(index + 1)];
  };

  const deleteAt = <T extends unknown>(elements: T[], index: number) => {
    return [...elements.slice(0, index), ...elements.slice(index + 1)];
  };

  const clearAt = <T extends unknown>(elements: T[], index: number) => {
    return [
      ...elements.slice(0, index),
      resetRowCustomFunction(elements[index] as DataRecord),
      ...elements.slice(index + 1),
    ];
  };

  const generateNextAvailableColumnName: (lastIndex: number) => string = useCallback(
    (lastIndex) => {
      const candidateName = `${columnPrefix}${lastIndex}`;
      const columnWithCandidateName = _.find(tableColumns.current, { accessor: candidateName });
      return columnWithCandidateName ? generateNextAvailableColumnName(lastIndex + 1) : candidateName;
    },
    [columnPrefix, tableColumns]
  );

  const generateNextAvailableColumn = useCallback(
    (columns: Column[]) => {
      return {
        accessor: generateNextAvailableColumnName(columns.length),
        label: generateNextAvailableColumnName(columns.length),
        dataType: DataType.Undefined,
      } as ColumnInstance;
    },
    [generateNextAvailableColumnName]
  );

  /** These column operations have impact also on the collection of cells */
  const updateColumnsThenRows = useCallback(
    (columns) => {
      onColumnsUpdate(columns);
      onRowsUpdate(tableRows.current);
    },
    [onColumnsUpdate, onRowsUpdate, tableRows]
  );

  const handlingOperation = useCallback(
    (tableOperation: TableOperation) => {
      switch (tableOperation) {
        case TableOperation.ColumnInsertLeft:
          updateColumnsThenRows(
            insertBefore(tableColumns.current, selectedColumnIndex, generateNextAvailableColumn(tableColumns.current))
          );
          break;
        case TableOperation.ColumnInsertRight:
          updateColumnsThenRows(
            insertAfter(tableColumns.current, selectedColumnIndex, generateNextAvailableColumn(tableColumns.current))
          );
          break;
        case TableOperation.ColumnDelete:
          updateColumnsThenRows(deleteAt(tableColumns.current, selectedColumnIndex));
          break;
        case TableOperation.RowInsertAbove:
          onRowsUpdate(insertBefore(tableRows.current, selectedRowIndex, onRowAdding()));
          break;
        case TableOperation.RowInsertBelow:
          onRowsUpdate(insertAfter(tableRows.current, selectedRowIndex, onRowAdding()));
          break;
        case TableOperation.RowDelete:
          onRowsUpdate(deleteAt(tableRows.current, selectedRowIndex));
          break;
        case TableOperation.RowClear:
          onRowsUpdate(clearAt(tableRows.current, selectedRowIndex));
          break;
      }
      setShowTableHandler(false);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [
      generateNextAvailableColumn,
      updateColumnsThenRows,
      onRowAdding,
      onRowsUpdate,
      selectedColumnIndex,
      selectedRowIndex,
      setShowTableHandler,
      tableColumns,
      tableRows,
    ]
  );

  return useMemo(
    () => (
      <Popover
        className="table-handler"
        hasNoPadding
        showClose={false}
        distance={5}
        position={"right"}
        isVisible={showTableHandler}
        shouldClose={() => setShowTableHandler(false)}
        shouldOpen={(showFunction) => showFunction?.()}
        reference={() => tableHandlerTarget}
        appendTo={globalContext.boxedExpressionEditorRef?.current ?? undefined}
        bodyContent={
          <TableHandlerMenu
            handlerConfiguration={handlerConfiguration}
            allowedOperations={tableHandlerAllowedOperations}
            onOperation={handlingOperation}
          />
        }
      />
    ),
    [
      showTableHandler,
      globalContext.boxedExpressionEditorRef,
      handlerConfiguration,
      tableHandlerAllowedOperations,
      handlingOperation,
      setShowTableHandler,
      tableHandlerTarget,
    ]
  );
};
