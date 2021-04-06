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

import { DataType } from "./DataType";

/** Possible status for the visibility of the Table's Header */
export enum TableHeaderVisibility {
  Full,
  OnlyLastLevel,
  None,
}

/** Table allowed operations */
export enum TableOperation {
  ColumnInsertLeft,
  ColumnInsertRight,
  ColumnDelete,
  RowInsertAbove,
  RowInsertBelow,
  RowDelete,
  RowClear,
}

export interface GroupOperations {
  /** Name of the group (localized) */
  group: string;
  /** Collection of operations belonging to this group */
  items: {
    /** Name of the operation (localized) */
    name: string;
    /** Type of the operation */
    type: TableOperation;
  }[];
}

export type TableHandlerConfiguration = GroupOperations[];

export type AllowedOperations = TableOperation[];

export type Row = string[];

export type Rows = Row[];

export interface Column {
  /** Column name */
  name: string;
  /** Column data type */
  dataType: DataType;
  /** Column width */
  width?: string | number;
}

export type Columns = Column[];

export interface CellProps {
  /** Cell's row properties */
  row: { index: number };
  /** Cell's column properties */
  column: { id: string };
}
