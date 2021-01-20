/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import "./ContextEntry.css";
import * as React from "react";
import { useCallback, useRef, useState } from "react";
import { CellProps, ContextEntries } from "../../api";
import { LogicTypeSelector } from "../LogicTypeSelector";
import { EditExpressionMenu } from "../EditExpressionMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataRecord } from "react-table";

export interface ContextEntryProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
}

export const ContextEntry: React.FunctionComponent<ContextEntryProps> = ({ data, row: { index }, onRowUpdate }) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const contextEntry = data[index];

  const [entryName, setEntryName] = useState(contextEntry.name);

  const [entryDataType, setEntryDataType] = useState(contextEntry.dataType);

  const onEntryNameOrDataTypeUpdate = useCallback(
    ({ name, dataType }) => {
      setEntryName(name);
      setEntryDataType(dataType);
      onRowUpdate(index, { ...contextEntry, name, dataType });
    },
    [contextEntry, index, onRowUpdate]
  );

  const getLogicTypeSelectorRef = useCallback(() => {
    return expressionContainerRef.current!;
  }, []);

  return (
    <div className="context-entry">
      <EditExpressionMenu
        title={i18n.editContextEntry}
        selectedExpressionName={entryName}
        selectedDataType={entryDataType}
        onExpressionUpdate={onEntryNameOrDataTypeUpdate}
      >
        <div className="entry-definition">
          <p className="entry-name">{entryName}</p>
          <p className="entry-data-type">({entryDataType})</p>
        </div>
      </EditExpressionMenu>

      <div className="entry-expression" ref={expressionContainerRef}>
        <LogicTypeSelector
          selectedExpression={contextEntry.expression}
          onLogicTypeUpdating={(logicType) => console.log(logicType)}
          onLogicTypeResetting={() => console.log("logic type resetting")}
          onNameAndDataTypeUpdating={(name, dataType) => console.log(name, dataType)}
          getPlacementRef={getLogicTypeSelectorRef}
        />
      </div>
    </div>
  );
};
