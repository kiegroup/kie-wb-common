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
import { TableComposable, Tbody, Td, Tr } from "@patternfly/react-table";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { EditableCell } from "./EditableCell";
import { CellProps, TableHandlerConfiguration, TableOperation } from "../../api";
import * as _ from "lodash";
import { TableHeader } from "./TableHeader";
import { TableHandler } from "./TableHandler";
import { BoxedExpressionGlobalContext } from "../../context";

export interface TableProps {
  /** Table identifier, useful for nested structures */
  tableId?: string;
  /** Optional children element to be appended below the table content */
  children?: React.ReactElement[];
  /** The prefix to be used for the column name */
  columnPrefix?: string;
  /** Optional label to be used for the edit popover that appears when clicking on column header */
  editColumnLabel?: string;
  /** For each column there is a default component to be used to render the related cell */
  defaultCell?: {
    [columnName: string]: React.FunctionComponent<CellProps>;
  };
  /** Table's columns */
  columns: Column[];
  /** Table's cells */
  rows: DataRecord[];
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[]) => void;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate?: (rows: DataRecord[]) => void;
  /** Function to be executed when a single row gets modified */
  onSingleRowUpdate?: (rowIndex: number, row: DataRecord) => void;
  /** Function to be executed when adding a new row to the table */
  onRowAdding?: () => DataRecord;
  /** Custom configuration for the table handler */
  handlerConfiguration: TableHandlerConfiguration;
  /** True to have no header for this table */
  isHeadless?: boolean;
  /** True to support multiple levels in the header */
  headerHasMultipleLevels?: boolean;
}

export const NO_TABLE_CONTEXT_MENU_CLASS = "no-table-context-menu";

export const Table: React.FunctionComponent<TableProps> = ({
  tableId,
  children,
  columnPrefix = "column-",
  editColumnLabel,
  onColumnsUpdate,
  onRowsUpdate,
  onSingleRowUpdate,
  onRowAdding = () => ({}),
  defaultCell,
  rows,
  columns,
  handlerConfiguration,
  isHeadless = false,
  headerHasMultipleLevels = false,
}: TableProps) => {
  const NUMBER_OF_ROWS_COLUMN = "#";
  const NUMBER_OF_ROWS_SUBCOLUMN = "0";

  const tableRef = useRef<HTMLTableElement>(null);

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const [tableColumns, setTableColumns] = useState([
    {
      label: NUMBER_OF_ROWS_COLUMN,
      accessor: NUMBER_OF_ROWS_COLUMN,
      width: 60,
      disableResizing: true,
      isCountColumn: true,
      hideFilter: true,
      ...(headerHasMultipleLevels
        ? {
            columns: [
              {
                label: NUMBER_OF_ROWS_SUBCOLUMN,
                accessor: NUMBER_OF_ROWS_SUBCOLUMN,
                width: 60,
                disableResizing: true,
                isCountColumn: true,
                hideFilter: true,
              },
            ],
          }
        : {}),
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

  const onCellUpdate = useCallback((rowIndex: number, columnId: string, value: string) => {
    setTableRows((prevTableCells) => {
      const updatedTableCells = [...prevTableCells];
      updatedTableCells[rowIndex][columnId] = value;
      return updatedTableCells;
    });
  }, []);

  const onRowUpdate = useCallback(
    (rowIndex: number, updatedRow: DataRecord) => {
      onSingleRowUpdate?.(rowIndex, updatedRow);
      setTableRows((prevTableCells) => {
        const updatedRows = [...prevTableCells];
        updatedRows[rowIndex] = updatedRow;
        return updatedRows;
      });
    },
    [onSingleRowUpdate]
  );

  const defaultColumn = {
    minWidth: 38,
    width: 150,
    Cell: useCallback((cellRef) => {
      const column = cellRef.column as ColumnInstance;
      if (column.isCountColumn) {
        return cellRef.value;
      } else {
        return defaultCell ? defaultCell[column.id](cellRef) : EditableCell(cellRef);
      }
      // Table performance optimization: no need to re-render cells, since nested component themselves will re-render
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []),
  };

  const contextMenuIsAvailable = (target: HTMLElement) => {
    const targetIsContainedInCurrentTable = target.closest("table") === tableRef.current;
    const contextMenuAvailableForTarget = !target.classList.contains(NO_TABLE_CONTEXT_MENU_CLASS);
    return targetIsContainedInCurrentTable && contextMenuAvailableForTarget;
  };

  const tableHandlerStateUpdate = (target: HTMLElement, columnIndex: number) => {
    setTableHandlerTarget(target);
    globalContext.currentlyOpenedHandlerCallback?.(false);
    setShowTableHandler(true);
    globalContext.setCurrentlyOpenedHandlerCallback?.(() => setShowTableHandler);
    setLastSelectedColumnIndex(columnIndex);
  };

  const getThProps = (column: ColumnInstance, columnIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      const target = e.target as HTMLElement;
      const handlerOnHeaderIsAvailable = !column.disableHandlerOnHeader;
      if (contextMenuIsAvailable(target) && handlerOnHeaderIsAvailable) {
        e.preventDefault();
        setTableHandlerAllowedOperations([
          TableOperation.ColumnInsertLeft,
          TableOperation.ColumnInsertRight,
          ...(tableColumns.length > 2 && columnIndex > 0 ? [TableOperation.ColumnDelete] : []),
        ]);
        tableHandlerStateUpdate(target, columnIndex);
      }
    },
  });

  const getTdProps = (columnIndex: number, rowIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      const target = e.target as HTMLElement;
      if (contextMenuIsAvailable(target)) {
        e.preventDefault();
        setTableHandlerAllowedOperations([
          TableOperation.ColumnInsertLeft,
          TableOperation.ColumnInsertRight,
          ...(tableColumns.length > 2 && columnIndex > 0 ? [TableOperation.ColumnDelete] : []),
          TableOperation.RowInsertAbove,
          TableOperation.RowInsertBelow,
          ...(tableRows.length > 1 ? [TableOperation.RowDelete] : []),
        ]);
        tableHandlerStateUpdate(target, columnIndex);
        setLastSelectedRowIndex(rowIndex);
      }
    },
  });

  const tableInstance = useTable(
    {
      columns: tableColumns,
      data: tableRows,
      defaultColumn,
      onCellUpdate,
      onRowUpdate,
      getThProps,
      getTdProps,
    },
    useBlockLayout,
    useResizeColumns
  );

  useEffect(() => {
    onColumnsUpdate(tableColumns.slice(1)); //Removing "# of rows" column
  }, [onColumnsUpdate, tableColumns]);

  useEffect(() => {
    onRowsUpdate?.(tableRows);
  }, [onRowsUpdate, tableRows]);

  const resizeNestedColumns = (columns: ColumnInstance[], accessor: string, updatedWidth: number) => {
    const columnIndex = _.findIndex(columns, { accessor });
    if (columnIndex >= 0) {
      const updatedColumn = { ...columns[columnIndex] };
      updatedColumn.width = updatedWidth;
      columns.splice(columnIndex, 1, updatedColumn);
    } else {
      _.forEach(columns, (column) => resizeNestedColumns(column.columns, accessor, updatedWidth));
    }
  };

  const finishedResizing =
    tableInstance.state.columnResizing.isResizingColumn === null &&
    !_.isEmpty(tableInstance.state.columnResizing.columnWidths);
  useEffect(() => {
    if (finishedResizing) {
      setTableColumns((prevTableColumns) => {
        _.forEach(tableInstance.state.columnResizing.columnWidths, (updatedColumnWidth, accessor) =>
          resizeNestedColumns(prevTableColumns as ColumnInstance[], accessor, updatedColumnWidth)
        );
        return [...prevTableColumns];
      });
    }
    // Need to consider a change only when resizing is finished (no other dependencies to consider for this useEffect)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [finishedResizing]);

  const renderAdditiveRow = useMemo(
    () => (
      <Tr className="table-row additive-row">
        <Td role="cell" className="empty-cell">
          <br />
        </Td>
        {children?.map((child, childIndex) => {
          return (
            <Td
              role="cell"
              key={childIndex}
              className="row-remainder-content"
              style={{
                width: tableInstance.allColumns[childIndex + 1].width,
                minWidth: tableInstance.allColumns[childIndex + 1].minWidth,
              }}
            >
              {child}
            </Td>
          );
        })}
      </Tr>
    ),
    [children, tableInstance.allColumns]
  );

  return (
    <div className={`table-component ${tableId}`}>
      <TableComposable variant="compact" {...tableInstance.getTableProps()} ref={tableRef}>
        <TableHeader
          tableInstance={tableInstance}
          editColumnLabel={editColumnLabel}
          isHeadless={isHeadless}
          tableColumns={tableColumns as ColumnInstance[]}
          setTableColumns={setTableColumns}
          setTableRows={setTableRows}
        />
        <Tbody {...tableInstance.getTableBodyProps()}>
          {tableInstance.rows.map((row: Row, rowIndex: number) => {
            tableInstance.prepareRow(row);
            return (
              <Tr className="table-row" {...row.getRowProps()} key={rowIndex} ouiaId={"expression-row-" + rowIndex}>
                {row.cells.map((cell: Cell, cellIndex: number) => (
                  <Td
                    {...(cellIndex === 0 ? {} : cell.getCellProps())}
                    {...tableInstance.getTdProps(cellIndex, rowIndex)}
                    key={cellIndex}
                    data-ouia-component-id={"expression-column-" + cellIndex}
                    className={cellIndex === 0 ? "counter-cell" : "data-cell"}
                  >
                    {cellIndex === 0 ? rowIndex + 1 : cell.render("Cell")}
                  </Td>
                ))}
              </Tr>
            );
          })}
          {children ? renderAdditiveRow : null}
        </Tbody>
      </TableComposable>
      {showTableHandler ? (
        <TableHandler
          tableColumns={tableColumns as ColumnInstance[]}
          setTableColumns={setTableColumns}
          setTableRows={setTableRows}
          columnPrefix={columnPrefix}
          handlerConfiguration={handlerConfiguration}
          lastSelectedColumnIndex={lastSelectedColumnIndex}
          lastSelectedRowIndex={lastSelectedRowIndex}
          onRowAdding={onRowAdding}
          showTableHandler={showTableHandler}
          setShowTableHandler={setShowTableHandler}
          tableHandlerAllowedOperations={tableHandlerAllowedOperations}
          tableHandlerTarget={tableHandlerTarget}
        />
      ) : null}
    </div>
  );
};
