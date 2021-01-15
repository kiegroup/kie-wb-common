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

import { LogicType } from "./LogicType";
import { DataType } from "./DataType";
import { Columns, Rows } from "./Table";

export interface ExpressionProps {
  /** Expression name (which, in DMN world, is equal to the Decision node's name) */
  name: string;
  /** Expression data type */
  dataType: DataType;
  /** Optional callback executed to update expression's name and data type */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DataType) => void;
  /** Logic type should not be defined at this stage */
  logicType?: LogicType;
}

export interface LiteralExpressionProps extends ExpressionProps {
  /** Logic type must be LiteralExpression */
  logicType: LogicType.LiteralExpression;
  /** Optional content to display for this literal expression */
  content?: string;
  /** True to have no header for this specific literal expression */
  isHeadless?: boolean;
}

export interface RelationProps extends ExpressionProps {
  /** Logic type must be Relation */
  logicType: LogicType.Relation;
  /** Each column has a name and a data type. Their order is from left to right */
  columns?: Columns;
  /** Rows order is from top to bottom. Each row has a collection of cells, one for each column */
  rows?: Rows;
}

export interface ContextProps extends ExpressionProps {
  /** Logic type must be Context */
  logicType: LogicType.Context;
  /** Collection of context entries */
  contextEntries?: {
    /** Entry name */
    name: string;
    /** Entry data type */
    dataType: DataType;
    /** Entry expression */
    expression: ExpressionProps;
  }[];
  /** Context result */
  result?: ExpressionProps;
}
