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
import { useCallback, useEffect, useState } from "react";
import {
  ContextEntries,
  ContextProps,
  DataType,
  LogicType,
  TableHandlerConfiguration,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntry } from "./ContextEntry";

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  name,
  dataType,
  onUpdatingNameAndDataType,
  width,
  contextEntries = [{ name: "ContextEntry-1", dataType: DataType.Undefined, expression: {} } as DataRecord],
}) => {
  const DEFAULT_EXPRESSION_COLUMN_WIDTH = 300;
  const { i18n } = useBoxedExpressionEditorI18n();

  const handlerConfiguration: TableHandlerConfiguration = [
    {
      group: i18n.contextEntry,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  const [columns, setColumns] = useState([
    {
      label: name,
      accessor: name,
      dataType,
      minWidth: DEFAULT_EXPRESSION_COLUMN_WIDTH,
      width: width ?? DEFAULT_EXPRESSION_COLUMN_WIDTH,
      disableHandlerOnHeader: true,
    },
  ]);

  const [rows, setRows] = useState(contextEntries);

  useEffect(() => {
    const expressionColumn = columns[0];
    window.beeApi?.broadcastContextExpressionDefinition?.({
      logicType: LogicType.Context,
      name: expressionColumn.accessor,
      dataType: expressionColumn.dataType,
      ...(expressionColumn.width > DEFAULT_EXPRESSION_COLUMN_WIDTH ? { width: expressionColumn.width } : {}),
      contextEntries: rows as ContextEntries,
    });
  }, [columns, rows]);

  const onUpdatingExpressionColumn = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      onUpdatingNameAndDataType?.(expressionColumn.label, expressionColumn.dataType);
      setColumns(([prevExpressionColumn]) => [
        {
          ...prevExpressionColumn,
          label: expressionColumn.label,
          accessor: expressionColumn.accessor,
          dataType: expressionColumn.dataType,
          width: expressionColumn.width as number,
        },
      ]);
    },
    [onUpdatingNameAndDataType]
  );

  return (
    <div className="context-expression">
      <Table
        defaultCell={ContextEntry}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onUpdatingExpressionColumn}
        onRowsUpdate={setRows}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};
