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
import { useCallback, useEffect, useState } from "react";
import {
  ContextEntries,
  ContextProps,
  DataType,
  ExpressionProps,
  LogicType,
  TableHandlerConfiguration,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryCell } from "./ContextEntryCell";
import * as _ from "lodash";
import { ContextEntry } from "./ContextEntry";
import { atomFamily, useRecoilState } from "recoil";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;

export const DEFAULT_ENTRY_INFO_WIDTH = 120;
export const DEFAULT_ENTRY_INFO_HEIGHT = 70;
export const DEFAULT_ENTRY_EXPRESSION_WIDTH = 210;
export const lastContextInfoWidthStateFamily = atomFamily({
  key: "lastContextInfoWidthStateFamily",
  default: DEFAULT_ENTRY_INFO_WIDTH,
});

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  uid,
  name = DEFAULT_CONTEXT_ENTRY_NAME,
  dataType = DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
  onUpdatingNameAndDataType,
  contextEntries,
  result = {} as ExpressionProps,
  resultInfoWidth,
  resultExpressionWidth,
  isHeadless = false,
  onUpdatingRecursiveExpression,
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
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      disableResizing: true,
    },
  ]);

  const [rows, setRows] = useState(
    contextEntries || [
      {
        contextExpressionId: uid,
        name: DEFAULT_CONTEXT_ENTRY_NAME,
        dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        expression: {},
      } as DataRecord,
    ]
  );

  const [resultExpression, setResultExpression] = useState(result);

  const [resultEntryInfoWidth, setResultEntryInfoWidth] = useState(resultInfoWidth);
  const [resultEntryExpressionWidth, setResultEntryExpressionWidth] = useState(resultExpressionWidth);

  const [lastContextInfoWidth, setLastContextInfoWidth] = useRecoilState(lastContextInfoWidthStateFamily(uid));

  useEffect(() => {
    const expressionColumn = columns[0];
    const updatedDefinition: ContextProps = {
      uid,
      logicType: LogicType.Context,
      name: expressionColumn.accessor,
      dataType: expressionColumn.dataType,
      contextEntries: rows as ContextEntries,
      result: resultExpression,
      ...(lastContextInfoWidth && lastContextInfoWidth !== DEFAULT_ENTRY_INFO_WIDTH
        ? { resultInfoWidth: lastContextInfoWidth }
        : {}),
      ...(resultEntryExpressionWidth && resultEntryExpressionWidth !== DEFAULT_ENTRY_EXPRESSION_WIDTH
        ? { resultExpressionWidth: resultEntryExpressionWidth }
        : {}),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(updatedDefinition)
      : window.beeApi?.broadcastContextExpressionDefinition?.(updatedDefinition);
  }, [
    columns,
    isHeadless,
    onUpdatingRecursiveExpression,
    rows,
    resultExpression,
    resultEntryInfoWidth,
    resultEntryExpressionWidth,
    lastContextInfoWidth,
    uid,
  ]);

  /**
   * Every time the ContextExpression component gets re-rendered, automatically calculating table cells width to fit the entire content
   */
  useEffect(() => {
    document
      .querySelectorAll(".context-expression > .table-component > table > tbody > tr > td.data-cell")
      .forEach((td: HTMLElement) => (td.style.width = "100%"));
    document
      .querySelectorAll(".context-expression > .table-component > table > tbody > tr.table-row")
      .forEach((td: HTMLElement) => (td.style.width = "100%"));
    document
      .querySelectorAll(".context-expression > .table-component > table > thead > tr > th.resizable-column")
      .forEach((th: HTMLElement) => (th.style.width = "calc((100% - 60px)"));
  });

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

  const generateNextAvailableEntryName: (lastIndex: number) => string = useCallback(
    (lastIndex) => {
      const candidateName = `ContextEntry-${lastIndex}`;
      const entryWithCandidateName = _.find(rows, { name: candidateName });
      return entryWithCandidateName ? generateNextAvailableEntryName(lastIndex + 1) : candidateName;
    },
    [rows]
  );

  const onRowAdding = useCallback(
    () => ({
      contextExpressionId: uid,
      name: generateNextAvailableEntryName(rows.length),
      dataType: DataType.Undefined,
      expression: {},
    }),
    [generateNextAvailableEntryName, rows.length, uid]
  );

  return (
    <div className="context-expression">
      <Table
        isHeadless={isHeadless}
        defaultCell={ContextEntryCell}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onUpdatingExpressionColumn}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRows}
        handlerConfiguration={handlerConfiguration}
      >
        <ContextEntry
          contextExpressionId={uid}
          expression={resultExpression}
          onUpdatingRecursiveExpression={setResultExpression}
          infoWidth={lastContextInfoWidth}
          expressionWidth={resultEntryExpressionWidth}
          onUpdatingInfoWidth={(entryInfoWidth) => {
            setResultEntryInfoWidth(entryInfoWidth);
            setLastContextInfoWidth(entryInfoWidth);
          }}
          onUpdatingExpressionWidth={setResultEntryExpressionWidth}
        >
          <div className="context-result">{`<result>`}</div>
        </ContextEntry>
      </Table>
    </div>
  );
};
