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
  ContextEntryRecord,
  ContextProps,
  DataType,
  ExpressionProps,
  LogicType,
  TableHandlerConfiguration,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord, Row } from "react-table";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";
import { useDragEvents } from "../../hooks";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;
const DEFAULT_ENTRY_INFO_MIN_WIDTH = 150;
const DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH = 370;

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  uid,
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
  const { setResizerElement, dragItHorizontally } = useDragEvents();

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

  const onExpressionResetting = useCallback(() => {
    setExpressionWidth(DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH);
  }, []);

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
        onExpressionResetting,
      } as DataRecord,
    ]
  );

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
      onExpressionResetting,
    }),
    [generateNextAvailableEntryName, onExpressionResetting, rows.length]
  );

  const checkForOverflowingCell = useCallback(
    () =>
      Array.from(
        document.querySelectorAll(
          `.context-expression.${uid} > .table-component > table > tbody > tr > td:last-of-type .table-component:first-of-type`
        )
      ).reduce(
        (acc, td: HTMLElement) => {
          const { clientWidth, scrollWidth } = td;
          return {
            isOverflow: acc.isOverflow || scrollWidth > clientWidth,
            contentWidth: Math.max(acc.contentWidth, scrollWidth - clientWidth),
          };
        },
        { isOverflow: false, contentWidth: 0 }
      ),
    [uid]
  );

  const checkForSpareSpace = useCallback(() => {
    const tableWidth = (document.querySelector(
      `.context-expression.${uid} > .table-component > table`
    ) as HTMLTableElement).getBoundingClientRect().width;
    const tableHeaderWidth = (document.querySelector(
      `.context-expression.${uid} > .table-component > table > thead`
    ) as HTMLTableElement).getBoundingClientRect().width;
    const spareSpace = tableWidth - tableHeaderWidth;
    if (spareSpace > 0) {
      return {
        isSpareSpace: true,
        spareSpace,
      };
    }
    return {
      isSpareSpace: false,
      spareSpace: 0,
    };
  }, [uid]);

  const updateValueColumnWidth = useCallback(
    (shiftWidth: number) => {
      setResizerElement(
        document.querySelector(
          `.table-component.${uid} > table > thead > tr:last-of-type > th:last-of-type div.pf-c-drawer`
        )! as HTMLDivElement
      );
      dragItHorizontally(shiftWidth);
    },
    [dragItHorizontally, setResizerElement, uid]
  );

  const contextTableGetRowKey = useCallback((row: Row) => (row.original as ContextEntryRecord).entryInfo.name, []);

  const onSingleRowUpdate = useCallback(() => {
    const { isOverflow, contentWidth } = checkForOverflowingCell();
    const { isSpareSpace, spareSpace } = checkForSpareSpace();
    if (isOverflow) {
      const contentWidthPlusPadding = contentWidth + 7;
      updateValueColumnWidth(contentWidthPlusPadding);
    } else if (isSpareSpace) {
      updateValueColumnWidth(spareSpace);
    }
  }, [checkForOverflowingCell, checkForSpareSpace, updateValueColumnWidth]);

  useEffect(() => {
    onSingleRowUpdate();
  }, [onSingleRowUpdate]);

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

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.OnlyLastLevel : TableHeaderVisibility.Full;
  }, [isHeadless]);

  return (
    <div className={`context-expression ${uid}`}>
      <Table
        tableId={uid}
        headerHasMultipleLevels={true}
        headerVisibility={getHeaderVisibility()}
        defaultCell={{ entryInfo: ContextEntryInfoCell, entryExpression: ContextEntryExpressionCell }}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRows}
        onSingleRowUpdate={onSingleRowUpdate}
        handlerConfiguration={handlerConfiguration}
        getRowKey={contextTableGetRowKey}
      >
        <div className="context-result">{`<result>`}</div>
        <ContextEntryExpression
          expression={resultExpression}
          onUpdatingRecursiveExpression={setResultExpression}
          onExpressionResetting={onExpressionResetting}
        />
      </Table>
    </div>
  );
};
