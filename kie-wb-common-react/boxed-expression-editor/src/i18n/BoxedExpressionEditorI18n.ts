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

import { ReferenceDictionary } from "@kogito-tooling/i18n/dist/core";
import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";

interface BoxedExpressionEditorDictionary extends ReferenceDictionary<BoxedExpressionEditorDictionary> {
  addParameter: string;
  choose: string;
  columns: string;
  columnOperations: {
    delete: string;
    insertLeft: string;
    insertRight: string;
  };
  class: string;
  clear: string;
  context: string;
  contextEntry: string;
  dataType: string;
  decisionTable: string;
  editContextEntry: string;
  editExpression: string;
  editParameter: string;
  editParameters: string;
  editRelation: string;
  enterFunction: string;
  delete: string;
  function: string;
  invocation: string;
  list: string;
  literalExpression: string;
  methodSignature: string;
  name: string;
  parameters: string;
  relation: string;
  rows: string;
  rowOperations: {
    clear: string;
    delete: string;
    insertAbove: string;
    insertBelow: string;
  };
  selectExpression: string;
  selectFunctionKind: string;
  selectLogicType: string;
}

export interface BoxedExpressionEditorI18n extends BoxedExpressionEditorDictionary, CommonI18n {}
