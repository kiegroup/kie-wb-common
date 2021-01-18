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

import { BoxedExpressionEditorI18n } from "..";
import { en as en_common } from "@kogito-tooling/i18n-common-dictionary";

export const en: BoxedExpressionEditorI18n = {
  ...en_common,
  choose: "Choose...",
  clear: "Clear",
  columnOperations: {
    delete: "Delete",
    insertLeft: "Insert left",
    insertRight: "Insert right",
  },
  columns: "COLUMNS",
  context: "Context",
  dataType: "Data Type",
  decisionTable: "Decision Table",
  editExpression: "Edit Expression",
  editRelation: "Edit Relation",
  function: "Function",
  invocation: "Invocation",
  list: "List",
  literalExpression: "Literal expression",
  name: "Name",
  relation: "Relation",
  rowOperations: {
    delete: "Delete",
    insertAbove: "Insert above",
    insertBelow: "Insert below",
  },
  rows: "ROWS",
  selectExpression: "Select expression",
  selectLogicType: "Select logic type",
};
