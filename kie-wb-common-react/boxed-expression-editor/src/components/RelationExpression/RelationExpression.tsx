/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./RelationExpression.css";
import * as React from "react";
import { useEffect, useState } from "react";
import "@patternfly/patternfly/utilities/Text/text.css";
import { DataType, RelationProps } from "../../api";
import { Table } from "../Table";

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const FIRST_COLUMN_NAME = "column-1";

  const generateFirstRow = () => {
    const firstRow: { [key: string]: string } = {};
    firstRow[FIRST_COLUMN_NAME] = "";
    return firstRow;
  };

  const [tableColumns, setTableColumns] = useState(
    relationProps.columns === undefined
      ? [
          { name: FIRST_COLUMN_NAME, label: FIRST_COLUMN_NAME, dataType: DataType.Undefined },
          { name: "column-2", label: "column-2", dataType: DataType.Undefined },
        ]
      : relationProps.columns
  );

  const [tableCells, setTableCells] = useState(
    relationProps.cells === undefined ? [generateFirstRow()] : relationProps.cells
  );

  useEffect(() => {
    window.beeApi?.broadcastRelationExpressionDefinition?.({
      ...relationProps,
      columns: tableColumns,
      cells: tableCells,
    });
  }, [relationProps, tableColumns, tableCells]);

  return (
    <div className="relation-expression">
      <Table
        columns={tableColumns}
        cells={tableCells}
        onColumnsUpdate={setTableColumns}
        onCellsUpdate={setTableCells}
      />
    </div>
  );
};
