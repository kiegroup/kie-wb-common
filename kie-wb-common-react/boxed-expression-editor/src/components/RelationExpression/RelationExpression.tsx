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

import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import "@patternfly/patternfly/utilities/Text/text.css";
import { DataType, RelationProps, TableOperation } from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord } from "react-table";

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const FIRST_COLUMN_NAME = "column-1";
  const { i18n } = useBoxedExpressionEditorI18n();

  const handlerConfiguration = [
    {
      group: i18n.columns,
      items: [
        { name: i18n.columnOperations.insertLeft, type: TableOperation.ColumnInsertLeft },
        { name: i18n.columnOperations.insertRight, type: TableOperation.ColumnInsertRight },
        { name: i18n.columnOperations.delete, type: TableOperation.ColumnDelete },
      ],
    },
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  const [tableColumns, setTableColumns] = useState(
    relationProps.columns === undefined
      ? [{ name: FIRST_COLUMN_NAME, dataType: DataType.Undefined }]
      : relationProps.columns
  );

  const [tableRows, setTableRows] = useState(relationProps.rows === undefined ? [[]] : relationProps.rows);

  useEffect(() => {
    window.beeApi?.broadcastRelationExpressionDefinition?.({
      ...relationProps,
      columns: tableColumns,
      rows: tableRows,
    });
  }, [relationProps, tableColumns, tableRows]);

  const convertColumnsForTheTable = useCallback(
    () =>
      _.map(
        tableColumns,
        (column) =>
          ({
            label: column.name,
            accessor: column.name,
            dataType: column.dataType,
            ...(column.width ? { width: column.width } : {}),
          } as Column)
      ),
    [tableColumns]
  );

  const onSavingColumns = useCallback(
    (columns) =>
      setTableColumns(
        _.map(columns, (columnInstance: ColumnInstance) => ({
          name: columnInstance.accessor,
          dataType: columnInstance.dataType,
          width: columnInstance.width,
        }))
      ),
    []
  );

  const convertRowsForTheTable = useCallback(
    () =>
      _.map(tableRows, (row) =>
        _.reduce(
          tableColumns,
          (tableRow: DataRecord, column, columnIndex) => {
            tableRow[column.name] = row[columnIndex] || "";
            return tableRow;
          },
          {}
        )
      ),
    [tableColumns, tableRows]
  );

  const onSavingRows = useCallback(
    (rows) =>
      setTableRows(
        _.map(rows, (tableRow: DataRecord) =>
          _.reduce(
            tableColumns,
            (row: string[], column) => {
              row.push(tableRow[column.name]! || "");
              return row;
            },
            []
          )
        )
      ),
    [tableColumns]
  );

  return (
    <div className="relation-expression">
      <Table
        columnPrefix="column-"
        columns={convertColumnsForTheTable()}
        rows={convertRowsForTheTable()}
        onColumnsUpdate={onSavingColumns}
        onRowsUpdate={onSavingRows}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};
