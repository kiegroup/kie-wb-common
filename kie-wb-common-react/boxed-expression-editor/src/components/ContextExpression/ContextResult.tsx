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

import "./ContextResult.css";
import { ExpressionProps, LogicType } from "../../api";
import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { LogicTypeSelector } from "../LogicTypeSelector";

export interface ContextResultProps {
  expression: ExpressionProps;
  onUpdatingExpression: (expression: ExpressionProps) => void;
}

export const ContextResult: React.FunctionComponent<ContextResultProps> = ({ expression }) => {
  const [resultExpression, setResultExpression] = useState(expression);

  const expressionChangedExternally = expression.logicType === undefined;
  useEffect(() => {
    setResultExpression(expression);
    // Every time, for an expression, its logic type is undefined, it means that corresponding entry has been just added
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expressionChangedExternally]);

  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const getLogicTypeSelectorRef = useCallback(() => {
    return expressionContainerRef.current!;
  }, []);

  const onUpdatingResultExpression = useCallback((expression: ExpressionProps) => {
    setResultExpression(expression);
  }, []);

  const onLogicTypeUpdating = useCallback((logicType) => {
    setResultExpression((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      logicType: logicType,
    }));
  }, []);

  const onLogicTypeResetting = useCallback(() => {
    setResultExpression((previousSelectedExpression: ExpressionProps) => {
      return {
        name: previousSelectedExpression.name,
        dataType: previousSelectedExpression.dataType,
        logicType: LogicType.Undefined,
      };
    });
  }, []);

  return (
    <div className="context-result">
      <div className="result-label">{`<result>`}</div>
      <div className="result-expression" ref={expressionContainerRef}>
        <LogicTypeSelector
          isHeadless={true}
          onUpdatingRecursiveExpression={onUpdatingResultExpression}
          selectedExpression={resultExpression}
          onLogicTypeUpdating={onLogicTypeUpdating}
          onLogicTypeResetting={onLogicTypeResetting}
          getPlacementRef={getLogicTypeSelectorRef}
        />
      </div>
    </div>
  );
};
