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

import "./ListExpression.css";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import {
  ContextEntryRecord,
  ExpressionProps,
  ListProps,
  LiteralExpressionProps,
  LogicType,
  TableHandlerConfiguration,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { ContextEntryExpressionCell } from "../ContextExpression";
import nextId from "react-id-generator";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataRecord, Row } from "react-table";
import * as _ from "lodash";

export const ListExpression: React.FunctionComponent<ListProps> = ({
  isHeadless,
  items,
  onUpdatingRecursiveExpression,
  uid,
}: ListProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const handlerConfiguration: TableHandlerConfiguration = [
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  const generateLiteralExpression = () => ({ logicType: LogicType.LiteralExpression } as LiteralExpressionProps);

  const [listItems, setListItems] = useState(
    _.isEmpty(items)
      ? [
          {
            entryExpression: generateLiteralExpression(),
          } as DataRecord,
        ]
      : _.map(items, (item) => ({ entryExpression: item } as DataRecord))
  );

  const listTableGetRowKey = useCallback((row: Row) => (row.original as ContextEntryRecord).entryExpression.uid!, []);

  const onRowAdding = useCallback(
    () => ({
      entryExpression: generateLiteralExpression(),
    }),
    []
  );

  useEffect(() => {
    const updatedDefinition: ListProps = {
      logicType: LogicType.List,
      items: _.map(listItems, (listItem: DataRecord) => listItem.entryExpression as ExpressionProps),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(updatedDefinition)
      : window.beeApi?.broadcastListExpressionDefinition?.(updatedDefinition);
  }, [isHeadless, listItems, onUpdatingRecursiveExpression]);

  return (
    <div className="list-expression">
      <Table
        tableId={uid}
        headerVisibility={TableHeaderVisibility.None}
        defaultCell={{ list: ContextEntryExpressionCell }}
        columns={[{ accessor: "list", width: 370, minWidth: 370 }]}
        rows={listItems as DataRecord[]}
        onRowsUpdate={setListItems}
        onRowAdding={onRowAdding}
        handlerConfiguration={handlerConfiguration}
        getRowKey={listTableGetRowKey}
      />
    </div>
  );
};
