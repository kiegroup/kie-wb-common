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
import { useCallback, useState } from "react";
import { DataType, InvocationProps, TableHandlerConfiguration, TableHeaderVisibility, TableOperation } from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell, ContextEntryInfoCell } from "../ContextExpression";

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DataType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationProps> = ({
  bindingEntries,
  dataType = DEFAULT_PARAMETER_DATA_TYPE,
  entryExpressionWidth,
  entryInfoWidth,
  invokedFunction,
  isHeadless,
  logicType,
  name = DEFAULT_PARAMETER_NAME,
  onUpdatingNameAndDataType,
  onUpdatingRecursiveExpression,
  uid,
}: InvocationProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const handlerConfiguration: TableHandlerConfiguration = [
    {
      group: "Parameters",
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
        { name: i18n.rowOperations.clear, type: TableOperation.RowClear },
      ],
    },
  ];

  const [rows] = useState(
    bindingEntries || [
      {
        entryInfo: {
          name: DEFAULT_PARAMETER_NAME,
          dataType: DEFAULT_PARAMETER_DATA_TYPE,
        },
        entryExpression: {},
      } as DataRecord,
    ]
  );

  const onRowAdding = useCallback(
    () => ({
      entryInfo: {
        name: "p-2",
        dataType: DEFAULT_PARAMETER_DATA_TYPE,
      },
      entryExpression: {},
    }),
    []
  );

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.SecondToLastLevel : TableHeaderVisibility.Full;
  }, [isHeadless]);

  return (
    <div className={`invocation-expression ${uid}`}>
      <Table
        tableId={uid}
        headerLevels={2}
        defaultCell={{ entryInfo: ContextEntryInfoCell, entryExpression: ContextEntryExpressionCell }}
        onRowAdding={onRowAdding}
        headerVisibility={getHeaderVisibility()}
        columns={
          [
            {
              label: name,
              accessor: name,
              dataType,
              disableHandlerOnHeader: true,
              columns: [
                {
                  headerCellElement: (
                    <div className="function-definition-container">
                      <input
                        className="function-definition pf-u-text-truncate"
                        type="text"
                        placeholder="Enter function"
                      />
                    </div>
                  ),
                  accessor: "functionDefinition",
                  disableHandlerOnHeader: true,
                  columns: [
                    {
                      accessor: "entryInfo",
                      disableHandlerOnHeader: true,
                      canResizeOnCell: true,
                      minWidth: 130,
                      width: 130,
                    },
                    {
                      accessor: "entryExpression",
                      disableHandlerOnHeader: true,
                      canResizeOnCell: true,
                      minWidth: 225,
                      width: 225,
                    },
                  ],
                },
              ] as ColumnInstance[],
            },
          ] as ColumnInstance[]
        }
        rows={rows as DataRecord[]}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};
