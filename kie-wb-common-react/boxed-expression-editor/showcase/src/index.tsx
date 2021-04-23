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
import { useState } from "react";
import * as ReactDOM from "react-dom";
import "./index.css";
// noinspection ES6PreferShortImport
import {
  BoxedExpressionEditor,
  ContextProps,
  DataType,
  ExpressionContainerProps,
  ExpressionProps,
  FunctionProps,
  InvocationProps,
  ListProps,
  LiteralExpressionProps,
  RelationProps,
} from "./boxed_expression_editor";

export const App: React.FunctionComponent = () => {
  //This definition comes directly from the decision node
  const selectedExpression: ExpressionProps = {
    name: "Expression Name",
    dataType: DataType.Undefined,
  };

  const [updatedExpression, setUpdatedExpression] = useState(selectedExpression);

  const expressionDefinition: ExpressionContainerProps = { selectedExpression };

  //Defining global function that will be available in the Window namespace and used by the BoxedExpressionEditor component
  window.beeApi = {
    resetExpressionDefinition: (definition: ExpressionProps) => setUpdatedExpression(definition),
    broadcastLiteralExpressionDefinition: (definition: LiteralExpressionProps) => setUpdatedExpression(definition),
    broadcastRelationExpressionDefinition: (definition: RelationProps) => setUpdatedExpression(definition),
    broadcastContextExpressionDefinition: (definition: ContextProps) => setUpdatedExpression(definition),
    broadcastListExpressionDefinition: (definition: ListProps) => setUpdatedExpression(definition),
    broadcastInvocationExpressionDefinition: (definition: InvocationProps) => setUpdatedExpression(definition),
    broadcastFunctionExpressionDefinition: (definition: FunctionProps) => setUpdatedExpression(definition),
  };

  return (
    <div className="showcase">
      <div className="boxed-expression">
        <BoxedExpressionEditor expressionDefinition={expressionDefinition} />
      </div>
      <div className="updated-json">
        <p className="disclaimer">
          ⚠ Currently, JSON gets updated only for literal expression, relation, context, list, invocation and function
          logic types
        </p>
        <pre>{JSON.stringify(updatedExpression, null, 2)}</pre>
      </div>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));
