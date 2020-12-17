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
import { useState } from "react";
import "@patternfly/patternfly/utilities/Text/text.css";
import { DataType, RelationProps } from "../../api";
import { Column, useBlockLayout, useResizeColumns, useTable } from "react-table";
import { TableComposable, Tbody, Td, Th, Thead, Tr } from "@patternfly/react-table";
import * as _ from "lodash";
import { EditExpressionMenu } from "../EditExpressionMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";

export const RelationExpression: React.FunctionComponent<RelationProps> = ({
  columns = [{ name: "column-1", dataType: DataType.Undefined }],
  rows = [[""]],
}: RelationProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [tableColumns, setTableColumns] = useState([
    { Header: "#", accessor: "#", disableResizing: true, width: 60 },
    ..._.map(
      columns,
      (column) =>
        ({
          Header: column.name,
          accessor: column.name,
          dataType: column.dataType,
        } as Column)
    ),
  ]);

  const [tableCells, setTableCells] = useState([
    ..._.map(rows, (row, rowIndex) =>
      _.reduce(
        row,
        (partialRow, cell, columnIndex) => {
          const columnName = tableColumns[columnIndex + 1].accessor?.toString();
          if (columnName) {
            Object.defineProperty(partialRow, columnName, { value: cell });
          }
          return partialRow;
        },
        { "#": "" + (rowIndex + 1) }
      )
    ),
  ]);

  console.log(tableCells);

  const tableInstance = useTable(
    {
      columns: tableColumns,
      data: tableCells,
    },
    useBlockLayout,
    useResizeColumns
  );

  return (
    <div className="relation-expression">
      <TableComposable variant="compact" {...tableInstance.getTableProps()}>
        <Thead noWrap>
          <tr>
            {tableInstance.headers.map((column) => {
              return (
                <Th {...column.getHeaderProps()} key={column.id}>
                  {column.canResize ? (
                    <EditExpressionMenu
                      title={i18n.editRelation}
                      selectedExpressionName={column.id}
                      selectedDataType={column.dataType}
                      onExpressionUpdate={(expression) => console.log("updated", expression)}
                    >
                      <div className="header-cell">
                        <div>
                          <p className="pf-u-text-truncate">{column.render("Header")}</p>
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
                    <div className="header-cell">{column.render("Header")}</div>
                  )}
                </Th>
              );
            })}
          </tr>
        </Thead>

        <Tbody {...tableInstance.getTableBodyProps()}>
          {tableInstance.rows.map((row, i) => {
            tableInstance.prepareRow(row);
            return (
              <Tr {...row.getRowProps()} key={i}>
                {row.cells.map((cell, j) => {
                  return (
                    <Td {...cell.getCellProps()} key={j}>
                      {cell.render("Cell")}
                    </Td>
                  );
                })}
              </Tr>
            );
          })}
        </Tbody>
      </TableComposable>
    </div>
  );
};
