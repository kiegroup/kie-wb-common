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
import { useCallback, useEffect, useState } from "react";
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

  const [tableCells, setTableCells] = useState(rows);

  const [showTableHandler, setShowTableHandler] = useState(false);
  const [tableHandlerTarget, setTableHandlerTarget] = useState(document.body);
  const [tableHandlerAllowedOperations, setTableHandlerAllowedOperations] = useState(
    _.values(TableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumnIndex, setLastSelectedColumnIndex] = useState(-1);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  useEffect(() => {
    onColumnsUpdate(tableColumns.slice(1)); //Removing "# of rows" column
  }, [onColumnsUpdate, tableColumns]);

  useEffect(() => {
    onRowsUpdate(tableCells);
  }, [onRowsUpdate, tableCells]);

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
          setTableCells((prevTableCells) => {
            return _.map(prevTableCells, (tableCells) => {
              const assignedCellValue = tableCells[prevColumnName]!;
              delete tableCells[prevColumnName];
              tableCells[name] = assignedCellValue;
              return tableCells;
            });
          });
        }
      };
    },
    [tableColumns]
  );

  const onCellUpdate = useCallback((rowIndex: number, columnId: string, value: string) => {
    setTableCells((prevTableCells) => {
      const updatedTableCells = [...prevTableCells];
      updatedTableCells[rowIndex][columnId] = value;
      return updatedTableCells;
    });
  }, []);

  const generateNextAvailableColumnName: (lastIndex: number) => string = useCallback(
    (lastIndex) => {
      const candidateName = `${columnPrefix}${lastIndex}`;
      const columnWithCandidateName = _.find(tableColumns, (column: Column) =>
        _.isEqual(candidateName, column.accessor)
      );
      return columnWithCandidateName ? generateNextAvailableColumnName(lastIndex + 1) : candidateName;
    },
    [columnPrefix, tableColumns]
  );

  const onHandlerOperation = useCallback(
    (tableOperation: TableOperation) => {
      switch (tableOperation) {
        case TableOperation.ColumnInsertLeft:
          setTableColumns((prevTableColumns) => [
            ...prevTableColumns.slice(0, lastSelectedColumnIndex),
            {
              accessor: generateNextAvailableColumnName(prevTableColumns.length),
              label: generateNextAvailableColumnName(prevTableColumns.length),
              dataType: DataType.Undefined,
            } as Column,
            ...prevTableColumns.slice(lastSelectedColumnIndex),
          ]);
          break;
        case TableOperation.ColumnInsertRight:
          setTableColumns((prevTableColumns) => [
            ...prevTableColumns.slice(0, lastSelectedColumnIndex + 1),
            {
              accessor: generateNextAvailableColumnName(prevTableColumns.length),
              label: generateNextAvailableColumnName(prevTableColumns.length),
              dataType: DataType.Undefined,
            } as Column,
            ...prevTableColumns.slice(lastSelectedColumnIndex + 1),
          ]);
          break;
        case TableOperation.ColumnDelete:
          setTableColumns((prevTableColumns) => [
            ...prevTableColumns.slice(0, lastSelectedColumnIndex),
            ...prevTableColumns.slice(lastSelectedColumnIndex + 1),
          ]);
          break;
        case TableOperation.RowInsertAbove:
          setTableCells((prevTableCells) => [
            ...prevTableCells.slice(0, lastSelectedRowIndex),
            {},
            ...prevTableCells.slice(lastSelectedRowIndex),
          ]);
          break;
        case TableOperation.RowInsertBelow:
          setTableCells((prevTableCells) => [
            ...prevTableCells.slice(0, lastSelectedRowIndex + 1),
            {},
            ...prevTableCells.slice(lastSelectedRowIndex + 1),
          ]);
          break;
        case TableOperation.RowDelete:
          setTableCells((prevTableCells) => [
            ...prevTableCells.slice(0, lastSelectedRowIndex),
            ...prevTableCells.slice(lastSelectedRowIndex + 1),
          ]);
          break;
      }
      setShowTableHandler(false);
    },
    [generateNextAvailableColumnName, lastSelectedColumnIndex, lastSelectedRowIndex]
  );

  const defaultColumn = {
    minWidth: 38,
    width: 150,
    maxWidth: 400,
    Cell: useCallback((cellRef: EditableCellProps) => {
      const column = cellRef.column as ColumnInstance;
      return column.canResize ? EditableCell(cellRef) : cellRef.value;
    }, []),
  };

  const getThProps = (columnIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      e.preventDefault();
      const allowedOperations = [TableOperation.ColumnInsertLeft, TableOperation.ColumnInsertRight];
      if (tableColumns.length > 2) {
        allowedOperations.push(TableOperation.ColumnDelete);
      }
      setTableHandlerAllowedOperations(allowedOperations);
      setTableHandlerTarget(e.target as HTMLElement);
      setShowTableHandler(true);
      setLastSelectedColumnIndex(columnIndex);
    },
  });

  const getTdProps = (columnIndex: number, rowIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      e.preventDefault();
      let allowedOperations: TableOperation[] = [];
      if (columnIndex !== 0) {
        allowedOperations = [TableOperation.ColumnInsertLeft, TableOperation.ColumnInsertRight];
        if (tableColumns.length > 2) {
          allowedOperations.push(TableOperation.ColumnDelete);
        }
      }
      allowedOperations = [...allowedOperations, TableOperation.RowInsertAbove, TableOperation.RowInsertBelow];
      if (tableCells.length > 1) {
        allowedOperations.push(TableOperation.RowDelete);
      }
      setTableHandlerAllowedOperations(allowedOperations);
      setTableHandlerTarget(e.target as HTMLElement);
      setShowTableHandler(true);
      setLastSelectedColumnIndex(columnIndex);
      setLastSelectedRowIndex(rowIndex);
    },
  });

  const tableInstance = useTable(
    {
      columns: tableColumns,
      data: tableCells,
      defaultColumn,
      onCellUpdate,
      getThProps,
      getTdProps,
    },
    useBlockLayout,
    useResizeColumns
  );

  const buildTableHandler = useCallback(
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
            onOperation={onHandlerOperation}
          />
        }
      />
    ),
    [showTableHandler, handlerConfiguration, tableHandlerAllowedOperations, onHandlerOperation, tableHandlerTarget]
  );

  return (
    <div className="table-component">
      <TableComposable variant="compact" {...tableInstance.getTableProps()}>
        <Thead noWrap>
          <tr>
            {tableInstance.headers.map((column: ColumnInstance, columnIndex: number) =>
              column.canResize ? (
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
              ) : (
                <Th {...column.getHeaderProps()} key={columnIndex}>
                  <div className="header-cell">{column.label}</div>
                </Th>
              )
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
      {showTableHandler ? buildTableHandler() : null}
    </div>
  );
};
