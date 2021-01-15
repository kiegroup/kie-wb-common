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

import "./Table.css";
import {
  Cell,
  Column,
  ColumnInstance,
  ContextMenuEvent,
  DataRecord,
  Row,
  useBlockLayout,
  useResizeColumns,
  useTable,
} from "react-table";
import { TableComposable, Tbody, Td, Th, Thead, Tr } from "@patternfly/react-table";
import { EditExpressionMenu } from "../EditExpressionMenu";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { EditableCell, EditableCellProps } from "./EditableCell";
import { DataType, TableHandlerConfiguration, TableOperation } from "../../api";
import * as _ from "lodash";
import { Popover } from "@patternfly/react-core";
import { TableHandlerMenu } from "./TableHandlerMenu";

export interface TableProps {
  /** The prefix to be used for the column name */
  columnPrefix: string;
  /** Table's columns */
  columns: Column[];
  /** Table's cells */
  rows: DataRecord[];
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[]) => void;
  /** Function to be executed when cells are modified */
  onRowsUpdate: (rows: DataRecord[]) => void;
  /** Custom configuration for the table handler */
  handlerConfiguration: TableHandlerConfiguration;
}

export const Table: React.FunctionComponent<TableProps> = ({
  columnPrefix,
  onRowsUpdate,
  onColumnsUpdate,
  rows,
  columns,
  handlerConfiguration,
}: TableProps) => {
  const NUMBER_OF_ROWS_COLUMN = "#";
  const { i18n } = useBoxedExpressionEditorI18n();

  const [tableColumns, setTableColumns] = useState([
    {
      label: NUMBER_OF_ROWS_COLUMN,
      accessor: NUMBER_OF_ROWS_COLUMN,
      width: 60,
      disableResizing: true,
      hideFilter: true,
    },
    ...columns,
  ]);
  const [tableRows, setTableRows] = useState(rows);
  const [showTableHandler, setShowTableHandler] = useState(false);
  const [tableHandlerTarget, setTableHandlerTarget] = useState(document.body);
  const [tableHandlerAllowedOperations, setTableHandlerAllowedOperations] = useState(
    _.values(TableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumnIndex, setLastSelectedColumnIndex] = useState(-1);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  const insertBefore = <T extends unknown>(elements: T[], index: number, element: T) => {
    return [...elements.slice(0, index), element, ...elements.slice(index)];
  };

  const insertAfter = <T extends unknown>(elements: T[], index: number, element: T) => {
    return [...elements.slice(0, index + 1), element, ...elements.slice(index + 1)];
  };

  const deleteAt = <T extends unknown>(elements: T[], index: number) => {
    return [...elements.slice(0, index), ...elements.slice(index + 1)];
  };

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
    []
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
    [tableColumns, updateColumnNameInRows]
  );

  const onCellUpdate = useCallback((rowIndex: number, columnId: string, value: string) => {
    setTableRows((prevTableCells) => {
      const updatedTableCells = [...prevTableCells];
      updatedTableCells[rowIndex][columnId] = value;
      return updatedTableCells;
    });
  }, []);

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
      };
    },
    [generateNextAvailableColumnName]
  );

  const handlingOperation = useCallback(
    (tableOperation: TableOperation) => {
      switch (tableOperation) {
        case TableOperation.ColumnInsertLeft:
          setTableColumns((prevTableColumns) =>
            insertBefore(prevTableColumns, lastSelectedColumnIndex, generateNextAvailableColumn(prevTableColumns))
          );
          break;
        case TableOperation.ColumnInsertRight:
          setTableColumns((prevTableColumns) =>
            insertAfter(prevTableColumns, lastSelectedColumnIndex, generateNextAvailableColumn(prevTableColumns))
          );
          break;
        case TableOperation.ColumnDelete:
          setTableColumns((prevTableColumns) => deleteAt(prevTableColumns, lastSelectedColumnIndex));
          break;
        case TableOperation.RowInsertAbove:
          setTableRows((prevTableRows) => insertBefore(prevTableRows, lastSelectedRowIndex, {}));
          break;
        case TableOperation.RowInsertBelow:
          setTableRows((prevTableRows) => insertAfter(prevTableRows, lastSelectedRowIndex, {}));
          break;
        case TableOperation.RowDelete:
          setTableRows((prevTableRows) => deleteAt(prevTableRows, lastSelectedRowIndex));
          break;
      }
      setShowTableHandler(false);
    },
    [generateNextAvailableColumn, lastSelectedColumnIndex, lastSelectedRowIndex]
  );

  const defaultColumn = {
    minWidth: 38,
    width: 150,
    maxWidth: 600,
    Cell: useCallback((cellRef: EditableCellProps) => {
      const column = cellRef.column as ColumnInstance;
      return column.canResize ? EditableCell(cellRef) : cellRef.value;
    }, []),
  };

  const getThProps = (columnIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      e.preventDefault();
      setTableHandlerAllowedOperations([
        TableOperation.ColumnInsertLeft,
        TableOperation.ColumnInsertRight,
        ...(tableColumns.length > 2 ? [TableOperation.ColumnDelete] : []),
      ]);
      setTableHandlerTarget(e.target as HTMLElement);
      setShowTableHandler(true);
      setLastSelectedColumnIndex(columnIndex);
    },
  });

  const getTdProps = (columnIndex: number, rowIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      e.preventDefault();
      setTableHandlerAllowedOperations([
        TableOperation.ColumnInsertLeft,
        TableOperation.ColumnInsertRight,
        ...(tableColumns.length > 2 ? [TableOperation.ColumnDelete] : []),
        TableOperation.RowInsertAbove,
        TableOperation.RowInsertBelow,
        ...(tableRows.length > 1 ? [TableOperation.RowDelete] : []),
      ]);
      setTableHandlerTarget(e.target as HTMLElement);
      setShowTableHandler(true);
      setLastSelectedColumnIndex(columnIndex);
      setLastSelectedRowIndex(rowIndex);
    },
  });

  const tableInstance = useTable(
    {
      columns: tableColumns,
      data: tableRows,
      defaultColumn,
      onCellUpdate,
      getThProps,
      getTdProps,
    },
    useBlockLayout,
    useResizeColumns
  );

  const buildTableHandler = useMemo(
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
    [showTableHandler, handlerConfiguration, tableHandlerAllowedOperations, handlingOperation, tableHandlerTarget]
  );

  useEffect(() => {
    onColumnsUpdate(tableColumns.slice(1)); //Removing "# of rows" column
  }, [onColumnsUpdate, tableColumns]);

  useEffect(() => {
    onRowsUpdate(tableRows);
  }, [onRowsUpdate, tableRows]);

  const finishedResizing =
    tableInstance.state.columnResizing.isResizingColumn === null &&
    !_.isEmpty(tableInstance.state.columnResizing.columnWidths);
  useEffect(() => {
    setTableColumns((prevTableColumns) => {
      _.forEach(tableInstance.state.columnResizing.columnWidths, (updatedColumnWidth, accessor) => {
        const columnIndex = _.findIndex(prevTableColumns, { accessor });
        const updatedColumn = { ...prevTableColumns[columnIndex] };
        updatedColumn.width = updatedColumnWidth;
        prevTableColumns.splice(columnIndex, 1, updatedColumn);
      });
      return [...prevTableColumns];
    });
    // Need to consider a change only when resizing is finished (no other dependencies to consider for this useEffect)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [finishedResizing]);

  const renderFixedColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) => (
      <Th {...column.getHeaderProps()} key={columnIndex}>
        <div className="header-cell">{column.label}</div>
      </Th>
    ),
    []
  );

  const renderResizableColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) => (
      <EditExpressionMenu
        title={i18n.editRelation}
        selectedExpressionName={column.label}
        selectedDataType={column.dataType}
        onExpressionUpdate={onColumnNameOrDataTypeUpdate(columnIndex)}
        key={columnIndex}
      >
        <Th {...column.getHeaderProps()} {...tableInstance.getThProps(columnIndex)} key={columnIndex}>
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
    ),
    [i18n.editRelation, onColumnNameOrDataTypeUpdate, tableInstance]
  );

  return (
    <div className="table-component">
      <TableComposable variant="compact" {...tableInstance.getTableProps()}>
        <Thead noWrap>
          <tr>
            {tableInstance.headers.map((column: ColumnInstance, columnIndex: number) =>
              column.canResize ? renderResizableColumn(column, columnIndex) : renderFixedColumn(column, columnIndex)
            )}
          </tr>
        </Thead>

        <Tbody {...tableInstance.getTableBodyProps()}>
          {tableInstance.rows.map((row: Row, rowIndex: number) => {
            tableInstance.prepareRow(row);
            return (
              <Tr className="table-row" {...row.getRowProps()} key={rowIndex}>
                {row.cells.map((cell: Cell, cellIndex: number) => (
                  <Td {...cell.getCellProps()} {...tableInstance.getTdProps(cellIndex, rowIndex)} key={cellIndex}>
                    {cellIndex === 0 ? rowIndex + 1 : cell.render("Cell")}
                  </Td>
                ))}
              </Tr>
            );
          })}
        </Tbody>
      </TableComposable>
      {showTableHandler ? buildTableHandler : null}
    </div>
  );
};
