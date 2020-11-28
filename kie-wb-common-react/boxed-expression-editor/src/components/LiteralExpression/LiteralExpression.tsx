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
import { useCallback, useState } from "react";
import { ExpressionProps, LiteralExpressionProps } from "../../api";
import { TextArea } from "@patternfly/react-core";
import { EditExpressionMenu } from "../EditExpressionMenu";

export const LiteralExpression: React.FunctionComponent<LiteralExpressionProps> = ({
  content,
  dataType,
  name,
  updateNameAndDataTypeCallback,
}: LiteralExpressionProps) => {
  const [expressionName, setExpressionName] = useState(name);
  const [expressionDataType, setExpressionDataType] = useState(dataType);
  const [literalExpressionContent, setLiteralExpressionContent] = useState(content);

  const onExpressionUpdate = useCallback(
    ({ dataType, name }: ExpressionProps) => {
      setExpressionName(name);
      setExpressionDataType(dataType);
      if (updateNameAndDataTypeCallback && dataType) {
        updateNameAndDataTypeCallback(name, dataType);
      }
    },
    [updateNameAndDataTypeCallback]
  );

  const onContentChange = useCallback((updatedContent) => {
    setLiteralExpressionContent(updatedContent);
  }, []);

  return (
    <div className="literal-expression">
      <div className="literal-expression-header">
        <p className="expression-name">{expressionName}</p>
        <p className="expression-data-type">({expressionDataType})</p>
      </div>
      <div className="literal-expression-body">
        <TextArea
          defaultValue={literalExpressionContent}
          onChange={onContentChange}
          aria-label="literal-expression-content"
        />
      </div>
      <EditExpressionMenu
        arrowPlacement={() => document.querySelector(".literal-expression-header")! as HTMLElement}
        selectedExpressionName={expressionName}
        selectedDataType={expressionDataType}
        onExpressionUpdate={onExpressionUpdate}
      />
    </div>
  );
};
