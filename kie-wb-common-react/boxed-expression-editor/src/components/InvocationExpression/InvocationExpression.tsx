/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import "./InvocationExpression.css";
import * as React from "react";
import { ChangeEvent, useCallback, useEffect, useRef, useState } from "react";
import {
  ContextEntries,
  ContextEntryRecord,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  EntryInfo,
  generateNextAvailableEntryName,
  getEntryKey,
  getHandlerConfiguration,
  InvocationProps,
  resetEntry,
  TableHeaderVisibility,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell, ContextEntryInfoCell } from "../ContextExpression";
import * as _ from "lodash";

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DataType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationProps> = ({
  bindingEntries,
  dataType = DEFAULT_PARAMETER_DATA_TYPE,
  entryInfoWidth = DEFAULT_ENTRY_INFO_MIN_WIDTH,
  entryExpressionWidth = DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  invokedFunction = "",
  isHeadless,
  logicType,
  name = DEFAULT_PARAMETER_NAME,
  onUpdatingNameAndDataType,
  onUpdatingRecursiveExpression,
  uid,
}: InvocationProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [rows, setRows] = useState(
    bindingEntries || [
      {
        entryInfo: {
          name: DEFAULT_PARAMETER_NAME,
          dataType: DEFAULT_PARAMETER_DATA_TYPE,
        },
        entryExpression: {},
        editInfoPopoverLabel: i18n.editParameter,
      } as DataRecord,
    ]
  );

  const functionDefinition = useRef<string>(invokedFunction);

  const infoWidth = useRef<number>(entryInfoWidth);

  const expressionWidth = useRef<number>(entryExpressionWidth);

  const spreadInvocationExpressionDefinition = useCallback(() => {
    const [expressionColumn] = columns.current;

    const updatedDefinition: InvocationProps = {
      uid,
      logicType,
      name: expressionColumn.accessor,
      dataType: expressionColumn.dataType,
      bindingEntries: rows as ContextEntries,
      invokedFunction: functionDefinition.current,
      ...(infoWidth.current > DEFAULT_ENTRY_INFO_MIN_WIDTH ? { entryInfoWidth: infoWidth.current } : {}),
      ...(expressionWidth.current > DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH
        ? { entryExpressionWidth: expressionWidth.current }
        : {}),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]))
      : window.beeApi?.broadcastInvocationExpressionDefinition?.(updatedDefinition);
  }, [functionDefinition, isHeadless, logicType, onUpdatingRecursiveExpression, rows, uid]);

  const onFunctionDefinitionChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    functionDefinition.current = e.target.value;
  }, []);

  const onFunctionDefinitionBlur = useCallback(() => {
    spreadInvocationExpressionDefinition();
  }, [spreadInvocationExpressionDefinition]);

  const headerCellElement = (
    <div className="function-definition-container">
      <input
        className="function-definition pf-u-text-truncate"
        type="text"
        placeholder={i18n.enterFunction}
        onChange={onFunctionDefinitionChange}
        onBlur={onFunctionDefinitionBlur}
        defaultValue={functionDefinition.current}
      />
    </div>
  );

  const columns = useRef<ColumnInstance[]>([
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      columns: [
        {
          headerCellElement,
          accessor: "functionDefinition",
          disableHandlerOnHeader: true,
          columns: [
            {
              accessor: "entryInfo",
              disableHandlerOnHeader: true,
              canResizeOnCell: true,
              width: infoWidth.current,
              minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
            },
            {
              accessor: "entryExpression",
              disableHandlerOnHeader: true,
              canResizeOnCell: true,
              width: expressionWidth.current,
              minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            },
          ],
        },
      ],
    },
  ] as ColumnInstance[]);

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      onUpdatingNameAndDataType?.(expressionColumn.label as string, expressionColumn.dataType);
      infoWidth.current = _.find(expressionColumn.columns, { accessor: "entryInfo" })?.width as number;
      expressionWidth.current = _.find(expressionColumn.columns, { accessor: "entryExpression" })?.width as number;
      const [updatedExpressionColumn] = columns.current;
      updatedExpressionColumn.label = expressionColumn.label;
      updatedExpressionColumn.accessor = expressionColumn.accessor;
      updatedExpressionColumn.dataType = expressionColumn.dataType;
      spreadInvocationExpressionDefinition();
    },
    [onUpdatingNameAndDataType, spreadInvocationExpressionDefinition]
  );

  const onRowAdding = useCallback(
    () => ({
      entryInfo: {
        name: generateNextAvailableEntryName(
          _.map(rows, (row: ContextEntryRecord) => row.entryInfo) as EntryInfo[],
          "p"
        ),
        dataType: DEFAULT_PARAMETER_DATA_TYPE,
      },
      entryExpression: {},
      editInfoPopoverLabel: i18n.editParameter,
    }),
    [i18n.editParameter, rows]
  );

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.SecondToLastLevel : TableHeaderVisibility.Full;
  }, [isHeadless]);

  useEffect(() => {
    /** Everytime the list of items or the function definition change, we need to spread expression's updated definition */
    spreadInvocationExpressionDefinition();
  }, [rows, spreadInvocationExpressionDefinition]);

  return (
    <div className={`invocation-expression ${uid}`}>
      <Table
        tableId={uid}
        headerLevels={2}
        headerVisibility={getHeaderVisibility()}
        skipLastHeaderGroup
        defaultCell={{ entryInfo: ContextEntryInfoCell, entryExpression: ContextEntryExpressionCell }}
        columns={columns.current}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRows}
        handlerConfiguration={getHandlerConfiguration(i18n, i18n.parameters)}
        getRowKey={useCallback(getEntryKey, [])}
        resetRowCustomFunction={useCallback(resetEntry, [])}
      />
    </div>
  );
};
