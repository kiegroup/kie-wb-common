/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import "./LiteralExpression.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { DataType, ExpressionProps, LiteralExpressionProps, LogicType } from "../../api";
import { TextArea } from "@patternfly/react-core";
import { EditExpressionMenu, EXPRESSION_NAME } from "../EditExpressionMenu";
import { Resizer } from "../Resizer";

export const LiteralExpression: React.FunctionComponent<LiteralExpressionProps> = ({
  content,
  dataType,
  name = EXPRESSION_NAME,
  onUpdatingNameAndDataType,
  isHeadless = false,
  onUpdatingRecursiveExpression,
  width,
}: LiteralExpressionProps) => {
  const HEADER_WIDTH = 250;
  const HEADER_HEIGHT = 40;

  const [expressionName, setExpressionName] = useState(name);
  const [expressionDataType, setExpressionDataType] = useState(dataType);
  const [literalExpressionContent, setLiteralExpressionContent] = useState(content);
  const [literalExpressionWidth, setLiteralExpressionWidth] = useState(width || HEADER_WIDTH);

  useEffect(() => {
    const expressionDefinition: LiteralExpressionProps = {
      name: expressionName,
      dataType: expressionDataType,
      logicType: LogicType.LiteralExpression,
      content: literalExpressionContent,
      ...(!isHeadless && literalExpressionWidth !== HEADER_WIDTH ? { width: literalExpressionWidth } : {}),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(expressionDefinition)
      : window.beeApi?.broadcastLiteralExpressionDefinition?.(expressionDefinition);
  }, [
    expressionName,
    expressionDataType,
    literalExpressionContent,
    isHeadless,
    onUpdatingRecursiveExpression,
    literalExpressionWidth,
  ]);

  const onExpressionUpdate = useCallback(
    ({ dataType = DataType.Undefined, name = EXPRESSION_NAME }: ExpressionProps) => {
      setExpressionName(name);
      setExpressionDataType(dataType);
      onUpdatingNameAndDataType?.(name, dataType);
    },
    [onUpdatingNameAndDataType]
  );

  const onContentChange = useCallback((event) => {
    const updatedContent = event.target.value;
    setLiteralExpressionContent(updatedContent);
  }, []);

  const getEditExpressionMenuArrowPlacement = useCallback(
    () => document.querySelector(".literal-expression-header")! as HTMLElement,
    []
  );

  const onHorizontalResizeStop = useCallback((width) => setLiteralExpressionWidth(width), []);

  const renderElementWithResizeHandler = useCallback(
    (element) => (
      <Resizer
        width={literalExpressionWidth}
        height={HEADER_HEIGHT}
        minWidth={HEADER_WIDTH}
        minHeight={HEADER_HEIGHT}
        onHorizontalResizeStop={onHorizontalResizeStop}
      >
        {element}
      </Resizer>
    ),
    [literalExpressionWidth, onHorizontalResizeStop]
  );

  const renderLiteralExpressionHeader = useMemo(() => {
    return (
      <div className="literal-expression-header">
        {renderElementWithResizeHandler(
          <div className="expression-info">
            <p className="expression-name pf-u-text-truncate">{expressionName}</p>
            <p className="expression-data-type pf-u-text-truncate">({expressionDataType})</p>
          </div>
        )}
      </div>
    );
  }, [expressionDataType, expressionName, renderElementWithResizeHandler]);

  const getBodyContent = useMemo(
    () => (
      <TextArea
        defaultValue={literalExpressionContent}
        onBlur={onContentChange}
        aria-label="literal-expression-content"
      />
    ),
    [literalExpressionContent, onContentChange]
  );

  return (
    <div className="literal-expression">
      {!isHeadless ? renderLiteralExpressionHeader : null}
      <div className="literal-expression-body">{getBodyContent}</div>
      <EditExpressionMenu
        arrowPlacement={getEditExpressionMenuArrowPlacement}
        selectedExpressionName={expressionName}
        selectedDataType={expressionDataType}
        onExpressionUpdate={onExpressionUpdate}
      />
    </div>
  );
};
