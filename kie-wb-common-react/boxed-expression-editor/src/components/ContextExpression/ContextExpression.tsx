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
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;
const DEFAULT_ENTRY_INFO_MIN_WIDTH = 150;
const DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH = 370;

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  name = DEFAULT_CONTEXT_ENTRY_NAME,
  dataType = DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
  onUpdatingNameAndDataType,
  contextEntries,
  result = {} as ExpressionProps,
  entryInfoWidth = DEFAULT_ENTRY_INFO_MIN_WIDTH,
  entryExpressionWidth = DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
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

  const [resultExpression, setResultExpression] = useState(result);
  const [infoWidth, setInfoWidth] = useState(entryInfoWidth);
  const [expressionWidth, setExpressionWidth] = useState(entryExpressionWidth);

  const [columns, setColumns] = useState([
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      columns: [
        {
          label: "Name",
          accessor: "entryInfo",
          disableHandlerOnHeader: true,
          width: infoWidth,
          minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
        },
        {
          label: "Value",
          accessor: "entryExpression",
          disableHandlerOnHeader: true,
          width: expressionWidth,
          minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
        },
      ],
    },
  ]);

  const [rows, setRows] = useState(
    contextEntries || [
      {
        entryInfo: {
          name: DEFAULT_CONTEXT_ENTRY_NAME,
          dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        },
        entryExpression: {},
      } as DataRecord,
    ]
  );

  useEffect(() => {
    const [expressionColumn] = columns;
    const updatedDefinition: ContextProps = {
      logicType: LogicType.Context,
      name: expressionColumn.accessor,
      dataType: expressionColumn.dataType,
      contextEntries: rows as ContextEntries,
      result: _.omit(resultExpression, "isHeadless"),
      ...(infoWidth > DEFAULT_ENTRY_INFO_MIN_WIDTH ? { entryInfoWidth: infoWidth } : {}),
      ...(expressionWidth > DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH ? { entryExpressionWidth: expressionWidth } : {}),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]))
      : window.beeApi?.broadcastContextExpressionDefinition?.(updatedDefinition);
  }, [columns, isHeadless, onUpdatingRecursiveExpression, rows, resultExpression, infoWidth, expressionWidth]);

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      onUpdatingNameAndDataType?.(expressionColumn.label, expressionColumn.dataType);
      setExpressionWidth(_.find(expressionColumn.columns, { accessor: "entryExpression" })?.width as number);
      setInfoWidth(_.find(expressionColumn.columns, { accessor: "entryInfo" })?.width as number);
      setColumns(([prevExpressionColumn]) => [
        {
          ...prevExpressionColumn,
          label: expressionColumn.label,
          accessor: expressionColumn.accessor,
          dataType: expressionColumn.dataType,
        },
      ]);
    },
    [onUpdatingNameAndDataType]
  );

  const generateNextAvailableEntryName: (lastIndex: number) => string = useCallback(
    (lastIndex) => {
      const candidateName = `ContextEntry-${lastIndex}`;
      const entryWithCandidateName = _.find(rows, { entryInfo: { name: candidateName } });
      return entryWithCandidateName ? generateNextAvailableEntryName(lastIndex + 1) : candidateName;
    },
    [rows]
  );

  const onRowAdding = useCallback(
    () => ({
      entryInfo: {
        name: generateNextAvailableEntryName(rows.length),
        dataType: DataType.Undefined,
      },
      entryExpression: {},
    }),
    [generateNextAvailableEntryName, rows.length]
  );

  return (
    <div className="context-expression">
      <Table
        headerHasMultipleLevels={true}
        isHeadless={isHeadless}
        defaultCell={{ entryInfo: ContextEntryInfoCell, entryExpression: ContextEntryExpressionCell }}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRows}
        handlerConfiguration={handlerConfiguration}
      >
        <div className="context-result">{`<result>`}</div>
        <ContextEntryExpression expression={resultExpression} onUpdatingRecursiveExpression={setResultExpression} />
      </Table>
    </div>
  );
};
