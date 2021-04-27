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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { DataType, ExpressionProps, LiteralExpressionProps, LogicType } from "../../api";
import { TextArea } from "@patternfly/react-core";
import { EditExpressionMenu, EXPRESSION_NAME } from "../EditExpressionMenu";
import { Resizer } from "../Resizer";

export const LiteralExpression: React.FunctionComponent<LiteralExpressionProps> = ({
  uid,
  content = "",
  dataType = DataType.Undefined,
  name = EXPRESSION_NAME,
  onUpdatingNameAndDataType,
  isHeadless = false,
  onUpdatingRecursiveExpression,
  width,
}: LiteralExpressionProps) => {
  const HEADER_WIDTH = 250;
  const HEADER_HEIGHT = 40;

  const [expressionName, setExpressionName] = useState<string>(name);
  const [expressionDataType, setExpressionDataType] = useState<DataType>(dataType);
  const literalExpressionContent = useRef<string>(content);
  const literalExpressionWidth = useRef<number>(width || HEADER_WIDTH);

  const spreadLiteralExpressionDefinition = useCallback(() => {
    const expressionDefinition: LiteralExpressionProps = {
      uid,
      name: expressionName,
      dataType: expressionDataType,
      logicType: LogicType.LiteralExpression,
      content: literalExpressionContent.current,
      ...(!isHeadless && literalExpressionWidth.current !== HEADER_WIDTH
        ? { width: literalExpressionWidth.current }
        : {}),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(expressionDefinition)
      : window.beeApi?.broadcastLiteralExpressionDefinition?.(expressionDefinition);
  }, [expressionDataType, expressionName, isHeadless, onUpdatingRecursiveExpression, uid]);

  const onExpressionUpdate = useCallback(
    ({ dataType = DataType.Undefined, name = EXPRESSION_NAME }: ExpressionProps) => {
      setExpressionName(name);
      setExpressionDataType(dataType);
      onUpdatingNameAndDataType?.(name, dataType);
      spreadLiteralExpressionDefinition();
    },
    [onUpdatingNameAndDataType, spreadLiteralExpressionDefinition]
  );

  const onContentChange = useCallback(
    (event) => {
      literalExpressionContent.current = event.target.value;
      spreadLiteralExpressionDefinition();
    },
    [spreadLiteralExpressionDefinition]
  );

  const onHorizontalResizeStop = useCallback(
    (width) => {
      literalExpressionWidth.current = width;
      spreadLiteralExpressionDefinition();
    },
    [spreadLiteralExpressionDefinition]
  );

  const renderElementWithResizeHandler = useCallback(
    (element) => (
      <Resizer
        width={literalExpressionWidth.current}
        height={HEADER_HEIGHT}
        minWidth={HEADER_WIDTH}
        minHeight={HEADER_HEIGHT}
        onHorizontalResizeStop={onHorizontalResizeStop}
      >
        {element}
      </Resizer>
    ),
    [onHorizontalResizeStop]
  );

  const renderLiteralExpressionHeader = useMemo(() => {
    return (
      <div className="literal-expression-header">
        {renderElementWithResizeHandler(
          <EditExpressionMenu
            selectedExpressionName={expressionName}
            selectedDataType={expressionDataType}
            onExpressionUpdate={onExpressionUpdate}
          >
            <div className="expression-info">
              <p className="expression-name pf-u-text-truncate">{expressionName}</p>
              <p className="expression-data-type pf-u-text-truncate">({expressionDataType})</p>
            </div>
          </EditExpressionMenu>
        )}
      </div>
    );
  }, [expressionDataType, expressionName, onExpressionUpdate, renderElementWithResizeHandler]);

  const getBodyContent = useMemo(
    () => (
      <TextArea
        defaultValue={literalExpressionContent.current}
        onBlur={onContentChange}
        aria-label="literal-expression-content"
      />
    ),
    [onContentChange]
  );

  useEffect(() => {
    /** Function executed only the first time the component is loaded */
    spreadLiteralExpressionDefinition();
  }, [spreadLiteralExpressionDefinition]);

  return (
    <div className="literal-expression">
      {!isHeadless ? renderLiteralExpressionHeader : null}
      <div className="literal-expression-body">{getBodyContent}</div>
    </div>
  );
};
