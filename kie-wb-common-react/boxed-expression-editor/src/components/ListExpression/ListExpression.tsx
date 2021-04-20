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
import { useCallback, useEffect, useRef, useState } from "react";
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
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataRecord, Row } from "react-table";
import * as _ from "lodash";
import { Resizer } from "../Resizer";

const LIST_EXPRESSION_MIN_WIDTH = 430;

export const ListExpression: React.FunctionComponent<ListProps> = ({
  isHeadless,
  items,
  onUpdatingRecursiveExpression,
  uid,
  width = LIST_EXPRESSION_MIN_WIDTH,
}: ListProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const handlerConfiguration: TableHandlerConfiguration = [
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
        { name: i18n.rowOperations.clear, type: TableOperation.RowClear },
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

  const listExpressionWidth = useRef(width);

  const listTableGetRowKey = useCallback((row: Row) => (row.original as ContextEntryRecord).entryExpression.uid!, []);

  const onRowAdding = useCallback(
    () => ({
      entryExpression: generateLiteralExpression(),
    }),
    []
  );

  const spreadListExpressionDefinition = useCallback(() => {
    const updatedDefinition: ListProps = {
      uid,
      ...(listExpressionWidth.current !== LIST_EXPRESSION_MIN_WIDTH ? { width: listExpressionWidth.current } : {}),
      logicType: LogicType.List,
      items: _.map(listItems, (listItem: DataRecord) => listItem.entryExpression as ExpressionProps),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(updatedDefinition)
      : window.beeApi?.broadcastListExpressionDefinition?.(updatedDefinition);
  }, [isHeadless, listItems, onUpdatingRecursiveExpression, uid]);

  const onRowsUpdate = useCallback((rows) => {
    setListItems(rows);
  }, []);

  const resetRowCustomFunction = useCallback((row: DataRecord) => {
    return { entryExpression: { uid: (row.entryExpression as ExpressionProps).uid } };
  }, []);

  const onHorizontalResizeStop = useCallback(
    (width) => {
      listExpressionWidth.current = width;
      spreadListExpressionDefinition();
    },
    [spreadListExpressionDefinition]
  );

  useEffect(() => {
    /** Everytime the list of items changes, we need to spread expression's updated definition */
    spreadListExpressionDefinition();
  }, [listItems, spreadListExpressionDefinition]);

  return (
    <div className="list-expression">
      <Resizer
        width={listExpressionWidth.current}
        height="100%"
        minWidth={LIST_EXPRESSION_MIN_WIDTH}
        onHorizontalResizeStop={onHorizontalResizeStop}
      >
        <Table
          tableId={uid}
          headerVisibility={TableHeaderVisibility.None}
          defaultCell={{ list: ContextEntryExpressionCell }}
          columns={[{ accessor: "list", width: "100%" }]}
          rows={listItems as DataRecord[]}
          onRowsUpdate={onRowsUpdate}
          onRowAdding={onRowAdding}
          handlerConfiguration={handlerConfiguration}
          getRowKey={listTableGetRowKey}
          resetRowCustomFunction={resetRowCustomFunction}
        />
      </Resizer>
    </div>
  );
};
