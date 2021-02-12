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
  name?: string;
  /** Expression data type */
  dataType?: DataType;
  /** Optional callback executed to update expression's name and data type */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DataType) => void;
  /** Logic type should not be defined at this stage */
  logicType?: LogicType;
  /** True to have no header for this specific expression component, used in a recursive expression */
  isHeadless?: boolean;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionProps) => void;
}

export interface LiteralExpressionProps extends ExpressionProps {
  /** Logic type must be LiteralExpression */
  logicType: LogicType.LiteralExpression;
  /** Optional content to display for this literal expression */
  content?: string;
  /** Optional width for this literal expression */
  width?: number;
}

export interface RelationProps extends ExpressionProps {
  /** Logic type must be Relation */
  logicType: LogicType.Relation;
  /** Each column has a name and a data type. Their order is from left to right */
  columns?: Columns;
  /** Rows order is from top to bottom. Each row has a collection of cells, one for each column */
  rows?: Rows;
}

export interface ContextEntryRecord {
  entryInfo: {
    /** Entry name */
    name: string;
    /** Entry data type */
    dataType: DataType;
  };
  /** Entry expression */
  entryExpression: ExpressionProps;
}

export type ContextEntries = ContextEntryRecord[];

export interface ContextProps extends ExpressionProps {
  /** Logic type must be Context */
  logicType: LogicType.Context;
  /** Collection of context entries */
  contextEntries?: ContextEntries;
  /** Context result */
  result?: ExpressionProps;
  /** Entry info width */
  entryInfoWidth?: number;
  /** Entry expression width */
  entryExpressionWidth?: number;
}
