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
  HeaderGroup,
  Row,
  useBlockLayout,
  useResizeColumns,
  useTable,
} from "react-table";
import { TableComposable, Tbody, Td, Th, Thead, Tr } from "@patternfly/react-table";
import { EditExpressionMenu } from "../EditExpressionMenu";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { EditableCell } from "./EditableCell";
import { CellProps, DataType, TableHandlerConfiguration, TableOperation } from "../../api";
import * as _ from "lodash";
import { Popover } from "@patternfly/react-core";
import { TableHandlerMenu } from "./TableHandlerMenu";

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
          setTableRows((prevTableRows) => insertBefore(prevTableRows, lastSelectedRowIndex, onRowAdding()));
          break;
        case TableOperation.RowInsertBelow:
          setTableRows((prevTableRows) => insertAfter(prevTableRows, lastSelectedRowIndex, onRowAdding()));
          break;
        case TableOperation.RowDelete:
          setTableRows((prevTableRows) => deleteAt(prevTableRows, lastSelectedRowIndex));
          break;
      }
      setShowTableHandler(false);
    },
    [generateNextAvailableColumn, lastSelectedColumnIndex, lastSelectedRowIndex, onRowAdding]
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
        setTableHandlerTarget(target);
        setShowTableHandler(true);
        setLastSelectedColumnIndex(columnIndex);
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
        setTableHandlerTarget(target);
        setShowTableHandler(true);
        setLastSelectedColumnIndex(columnIndex);
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
    setTableColumns((prevTableColumns) => {
      _.forEach(tableInstance.state.columnResizing.columnWidths, (updatedColumnWidth, accessor) =>
        resizeNestedColumns(prevTableColumns as ColumnInstance[], accessor, updatedColumnWidth)
      );
      return [...prevTableColumns];
    });
    // Need to consider a change only when resizing is finished (no other dependencies to consider for this useEffect)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [finishedResizing]);

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

  return (
    <div className={`table-component ${tableId}`}>
      <TableComposable variant="compact" {...tableInstance.getTableProps()} ref={tableRef}>
        <Thead noWrap>{isHeadless ? renderLastLevelInHeaderGroups : renderHeaderGroups}</Thead>

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
      {showTableHandler ? buildTableHandler : null}
    </div>
  );
};
