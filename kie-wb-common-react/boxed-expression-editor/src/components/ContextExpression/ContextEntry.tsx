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

import "./ContextEntry.css";
import { ExpressionProps, LogicType } from "../../api";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { LogicTypeSelector } from "../LogicTypeSelector";
import { Resizer } from "../Resizer";
import { useRecoilState } from "recoil";
import {
  DEFAULT_ENTRY_EXPRESSION_WIDTH,
  DEFAULT_ENTRY_INFO_HEIGHT,
  DEFAULT_ENTRY_INFO_WIDTH,
  lastContextInfoWidthStateFamily,
} from "./ContextExpression";

export interface ContextEntryProps {
  /** Reference to the context expression */
  contextExpressionId: string;
  /** Children element to be used for entry info */
  children?: React.ReactElement;
  /** The expression wrapped by the entry */
  expression: ExpressionProps;
  /** Function invoked when updating expression */
  onUpdatingRecursiveExpression: (expression: ExpressionProps) => void;
  /** Width size for entry expression */
  expressionWidth?: number;
  /** Function invoked when updating entry info width */
  onUpdatingInfoWidth: (width: number) => void;
  /** Function invoked when updating entry expression width */
  onUpdatingExpressionWidth: (width: number) => void;
}

export const LOGIC_TYPES_WITH_RESIZER = [LogicType.LiteralExpression];

export const ContextEntry: React.FunctionComponent<ContextEntryProps> = ({
  contextExpressionId,
  children,
  expression,
  onUpdatingRecursiveExpression,
  expressionWidth,
  onUpdatingInfoWidth,
  onUpdatingExpressionWidth,
}) => {
  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const [entryExpression, setEntryExpression] = useState(expression);

  const [entryExpressionWidth, setEntryExpressionWidth] = useState(expressionWidth || DEFAULT_ENTRY_EXPRESSION_WIDTH);

  const [entryExpressionMinWidth, setEntryExpressionMinWidth] = useState(
    expressionWidth || DEFAULT_ENTRY_EXPRESSION_WIDTH
  );

  const [lastContextInfoWidth, setLastContextInfoWidth] = useRecoilState(
    lastContextInfoWidthStateFamily(contextExpressionId)
  );

  // On each rendering, we check if container width has changed. In that case updating entry expression width/minWidth
  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    const containerCurrentWidth = expressionContainerRef.current?.getBoundingClientRect().width || 0;
    if (containerCurrentWidth > entryExpressionWidth && containerCurrentWidth - entryExpressionWidth >= 1) {
      setEntryExpressionWidth(containerCurrentWidth);
      setEntryExpressionMinWidth(containerCurrentWidth);
    }
  });

  useEffect(() => {
    setEntryExpressionWidth(expressionWidth || DEFAULT_ENTRY_EXPRESSION_WIDTH);
  }, [expressionWidth]);

  const expressionChangedExternally = expression.logicType === undefined;
  useEffect(() => {
    setEntryExpression(expression);
    // Every time, for an expression, its logic type is undefined, it means that corresponding entry has been just added
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expressionChangedExternally]);

  useEffect(() => {
    onUpdatingRecursiveExpression(entryExpression);
  }, [onUpdatingRecursiveExpression, entryExpression]);

  const getLogicTypeSelectorRef = useCallback(() => {
    return expressionContainerRef.current!;
  }, []);

  const onLogicTypeUpdating = useCallback((logicType) => {
    setEntryExpression((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      logicType: logicType,
    }));
  }, []);

  const onLogicTypeResetting = useCallback(() => {
    setEntryExpressionWidth(DEFAULT_ENTRY_EXPRESSION_WIDTH);
    onUpdatingExpressionWidth(DEFAULT_ENTRY_EXPRESSION_WIDTH);
    setEntryExpression((previousSelectedExpression: ExpressionProps) => ({
      name: previousSelectedExpression.name,
      dataType: previousSelectedExpression.dataType,
      logicType: LogicType.Undefined,
    }));
  }, [onUpdatingExpressionWidth]);

  const onHorizontalEntryInfoResizeStop = useCallback(
    (width) => {
      onUpdatingInfoWidth(width);
      setLastContextInfoWidth(width);
    },
    [onUpdatingInfoWidth, setLastContextInfoWidth]
  );
  const onHorizontalEntryExpressionResizeStop = useCallback(
    (width) => {
      setEntryExpressionWidth(width);
      onUpdatingExpressionWidth(width);
    },
    [onUpdatingExpressionWidth]
  );

  const renderLogicType = useMemo(
    () => (
      <LogicTypeSelector
        isHeadless={true}
        onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
        selectedExpression={entryExpression}
        onLogicTypeUpdating={onLogicTypeUpdating}
        onLogicTypeResetting={onLogicTypeResetting}
        getPlacementRef={getLogicTypeSelectorRef}
      />
    ),
    [entryExpression, getLogicTypeSelectorRef, onLogicTypeResetting, onLogicTypeUpdating, onUpdatingRecursiveExpression]
  );

  const renderExpressionResizer = useCallback(
    (element: JSX.Element) => (
      <Resizer
        width={entryExpressionWidth}
        height={DEFAULT_ENTRY_INFO_HEIGHT}
        minWidth={entryExpressionMinWidth}
        minHeight={DEFAULT_ENTRY_INFO_HEIGHT}
        onHorizontalResizeStop={onHorizontalEntryExpressionResizeStop}
      >
        {element}
      </Resizer>
    ),
    [entryExpressionMinWidth, entryExpressionWidth, onHorizontalEntryExpressionResizeStop]
  );

  const renderEntryExpression = useMemo(
    () =>
      entryExpression.logicType && LOGIC_TYPES_WITH_RESIZER.includes(entryExpression.logicType)
        ? renderExpressionResizer(renderLogicType)
        : renderLogicType,
    [entryExpression.logicType, renderLogicType, renderExpressionResizer]
  );

  return (
    <div className="context-entry">
      <div className="entry-info">
        <Resizer
          width={lastContextInfoWidth}
          height={DEFAULT_ENTRY_INFO_HEIGHT}
          minWidth={DEFAULT_ENTRY_INFO_WIDTH}
          minHeight={DEFAULT_ENTRY_INFO_HEIGHT}
          onHorizontalResizeStop={onHorizontalEntryInfoResizeStop}
        >
          {children}
        </Resizer>
      </div>

      <div className="entry-expression" ref={expressionContainerRef}>
        {renderEntryExpression}
      </div>
    </div>
  );
};
