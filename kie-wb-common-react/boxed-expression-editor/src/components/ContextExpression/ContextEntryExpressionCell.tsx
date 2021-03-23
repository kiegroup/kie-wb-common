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

import "./ContextEntryExpressionCell.css";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { CellProps, ContextEntries, ExpressionProps } from "../../api";
import { DataRecord } from "react-table";
import { ContextEntryExpression } from "./ContextEntryExpression";

export interface ContextEntryExpressionCellProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
}

export const ContextEntryExpressionCell: React.FunctionComponent<ContextEntryExpressionCellProps> = ({
  data,
  row: { index },
  onRowUpdate,
}) => {
  const contextEntry = data[index];

  const [entryExpression, setEntryExpression] = useState(contextEntry.entryExpression);

  const expressionChangedExternally = contextEntry.entryExpression.logicType === undefined;
  useEffect(() => {
    setEntryExpression(contextEntry.entryExpression);
    // Every time, for an expression, its logic type is undefined, it means that corresponding entry has been just added
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expressionChangedExternally]);

  useEffect(() => {
    onRowUpdate(index, { ...contextEntry, entryExpression });
    // Purpose is to update the row every time the expression wrapped in the entry changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [entryExpression]);

  const onUpdatingRecursiveExpression = useCallback((expression: ExpressionProps) => {
    setEntryExpression(expression);
  }, []);

  return (
    <div className="context-entry-expression-cell">
      <ContextEntryExpression
        expression={entryExpression}
        onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
        onExpressionResetting={contextEntry.onExpressionResetting}
      />
    </div>
  );
};
