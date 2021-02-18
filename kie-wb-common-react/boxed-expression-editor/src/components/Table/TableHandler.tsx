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
import { useCallback, useEffect, useMemo, useState } from "react";
import { DataType, TableHandlerConfiguration, TableOperation } from "../../api";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord } from "react-table";
import { Popover } from "@patternfly/react-core";
import { TableHandlerMenu } from "./TableHandlerMenu";

export interface TableHandlerProps {
  /** The prefix to be used for the column name */
  columnPrefix: string;
  /** Columns instance */
  tableColumns: ColumnInstance[];
  /** Function for setting table columns */
  setTableColumns: React.Dispatch<React.SetStateAction<ColumnInstance[]>>;
  /** Function for setting table rows */
  setTableRows: React.Dispatch<React.SetStateAction<DataRecord[]>>;
  /** Last selected column index */
  lastSelectedColumnIndex: number;
  /** Last selected row index */
  lastSelectedRowIndex: number;
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
}

export const TableHandler: React.FunctionComponent<TableHandlerProps> = ({
  columnPrefix,
  tableColumns,
  setTableColumns,
  setTableRows,
  lastSelectedColumnIndex,
  lastSelectedRowIndex,
  onRowAdding,
  showTableHandler,
  setShowTableHandler,
  tableHandlerTarget,
  handlerConfiguration,
  tableHandlerAllowedOperations,
}) => {
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

  const generateNextAvailableColumnName: (lastIndex: number) => string = useCallback(
    (lastIndex) => {
      const candidateName = `${columnPrefix}${lastIndex}`;
      const columnWithCandidateName = _.find(tableColumns, { accessor: candidateName });
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

  const handlingOperation = useCallback(
    (tableOperation: TableOperation) => {
      switch (tableOperation) {
        case TableOperation.ColumnInsertLeft:
          setTableColumns((prevTableColumns) =>
            insertBefore(prevTableColumns, selectedColumnIndex, generateNextAvailableColumn(prevTableColumns))
          );
          break;
        case TableOperation.ColumnInsertRight:
          setTableColumns((prevTableColumns) =>
            insertAfter(prevTableColumns, selectedColumnIndex, generateNextAvailableColumn(prevTableColumns))
          );
          break;
        case TableOperation.ColumnDelete:
          setTableColumns((prevTableColumns) => deleteAt(prevTableColumns, selectedColumnIndex));
          break;
        case TableOperation.RowInsertAbove:
          setTableRows((prevTableRows) => insertBefore(prevTableRows, selectedRowIndex, onRowAdding()));
          break;
        case TableOperation.RowInsertBelow:
          setTableRows((prevTableRows) => insertAfter(prevTableRows, selectedRowIndex, onRowAdding()));
          break;
        case TableOperation.RowDelete:
          setTableRows((prevTableRows) => deleteAt(prevTableRows, selectedRowIndex));
          break;
      }
      setShowTableHandler(false);
    },
    [
      setShowTableHandler,
      setTableColumns,
      setTableRows,
      selectedColumnIndex,
      generateNextAvailableColumn,
      selectedRowIndex,
      onRowAdding,
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
      handlerConfiguration,
      tableHandlerAllowedOperations,
      handlingOperation,
      setShowTableHandler,
      tableHandlerTarget,
    ]
  );
};
