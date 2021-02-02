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

import "./ContextEntryCell.css";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { CellProps, ContextEntries, ExpressionProps } from "../../api";
import { EditExpressionMenu } from "../EditExpressionMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataRecord } from "react-table";
import { ContextEntry, DEFAULT_ENTRY_EXPRESSION_WIDTH, DEFAULT_ENTRY_INFO_WIDTH } from "./ContextEntry";

export interface ContextEntryCellProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
}

export const ContextEntryCell: React.FunctionComponent<ContextEntryCellProps> = ({
  data,
  row: { index },
  onRowUpdate,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const contextEntry = data[index];

  const [entryName, setEntryName] = useState(contextEntry.name);

  const [entryDataType, setEntryDataType] = useState(contextEntry.dataType);

  const [entryExpression, setEntryExpression] = useState(contextEntry.expression);

  const [entryInfoWidth, setEntryInfoWidth] = useState(contextEntry.infoWidth);

  const [entryExpressionWidth, setEntryExpressionWidth] = useState(contextEntry.expressionWidth);

  useEffect(() => {
    setEntryName(contextEntry.name);
  }, [contextEntry.name]);

  useEffect(() => {
    setEntryDataType(contextEntry.dataType);
  }, [contextEntry.dataType]);

  const expressionChangedExternally = contextEntry.expression.logicType === undefined;
  useEffect(() => {
    setEntryExpression(contextEntry.expression);
    // Every time, for an expression, its logic type is undefined, it means that corresponding entry has been just added
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expressionChangedExternally]);

  useEffect(() => {
    setEntryInfoWidth(contextEntry.infoWidth);
  }, [contextEntry.infoWidth]);

  useEffect(() => {
    onRowUpdate(index, { ...contextEntry, expression: entryExpression });
    // Purpose is to update the row every time the expression wrapped in the entry changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [entryExpression]);

  useEffect(() => {
    delete contextEntry.infoWidth;
    onRowUpdate(index, {
      ...contextEntry,
      ...(entryInfoWidth !== DEFAULT_ENTRY_INFO_WIDTH ? { infoWidth: entryInfoWidth } : {}),
    });
    // Purpose is to update the row every time the context info width changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [entryInfoWidth]);

  useEffect(() => {
    delete contextEntry.expressionWidth;
    onRowUpdate(index, {
      ...contextEntry,
      ...(entryExpressionWidth !== DEFAULT_ENTRY_EXPRESSION_WIDTH ? { expressionWidth: entryExpressionWidth } : {}),
    });
    // Purpose is to update the row every time the context expression width changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [entryExpressionWidth]);

  const onEntryNameOrDataTypeUpdate = useCallback(
    ({ name, dataType }) => {
      setEntryName(name);
      setEntryDataType(dataType);
      onRowUpdate(index, { ...contextEntry, name, dataType });
    },
    [contextEntry, index, onRowUpdate]
  );

  const onUpdatingRecursiveExpression = useCallback((expression: ExpressionProps) => {
    setEntryExpression(expression);
  }, []);

  return (
    <div className="context-entry-cell">
      <ContextEntry
        expression={entryExpression}
        onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
        infoWidth={entryInfoWidth}
        expressionWidth={entryExpressionWidth}
        onUpdatingInfoWidth={setEntryInfoWidth}
        onUpdatingExpressionWidth={setEntryExpressionWidth}
      >
        <EditExpressionMenu
          title={i18n.editContextEntry}
          selectedExpressionName={entryName}
          selectedDataType={entryDataType}
          onExpressionUpdate={onEntryNameOrDataTypeUpdate}
        >
          <div className="entry-definition">
            <p className="entry-name pf-u-text-truncate" title={entryName}>
              {entryName}
            </p>
            <p className="entry-data-type pf-u-text-truncate" title={entryDataType}>
              ({entryDataType})
            </p>
          </div>
        </EditExpressionMenu>
      </ContextEntry>
    </div>
  );
};
