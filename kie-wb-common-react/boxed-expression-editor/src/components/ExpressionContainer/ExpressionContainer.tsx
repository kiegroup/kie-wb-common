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

import * as React from "react";
import "./ExpressionContainer.css"
import {useBoxedExpressionEditorI18n} from "../../i18n";

export interface ExpressionContainerProps {
  /** The name of the expression */
  name: string,
  /** The type of the expression */
  type?: string
}

const ExpressionContainer: (props: ExpressionContainerProps) => JSX.Element = (props: ExpressionContainerProps) => {
  const {i18n} = useBoxedExpressionEditorI18n();
  return (
    <div className="expression-container">
      <span id="expression-title">
        {props.name}
      </span>
      <span id="expression-type">
          ({props.type ?? '<Undefined>'})
      </span>

      <div className="container-box">
        <p>{i18n.selectExpression}</p>
      </div>
    </div>
  );
}

export {ExpressionContainer};
