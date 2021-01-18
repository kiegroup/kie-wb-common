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

import "./ContextExpression.css";
import * as React from "react";
import { useCallback, useState } from "react";
import { ContextProps, TableHandlerConfiguration, TableOperation } from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance } from "react-table";

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  name,
  dataType,
  onUpdatingNameAndDataType,
  width,
}) => {
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
    { label: name, accessor: name, dataType, width: width ?? 300, disableHandlerOnHeader: true },
  ]);

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
        columnPrefix="ContextEntry-"
        columns={columns}
        rows={[{}]}
        onColumnsUpdate={onUpdatingExpressionColumn}
        onRowsUpdate={(rows) => console.log("rows updated", rows)}
        handlerConfiguration={handlerConfiguration}
        customAllowedOperations={{
          onTh: [TableOperation.RowInsertBelow],
          onTd: [],
        }}
      />
    </div>
  );
};
