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

import { CellProps, ContextEntries } from "../../api";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { DataRecord } from "react-table";
import { ContextEntryInfo } from "./ContextEntryInfo";

export interface ContextEntryInfoCellProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
}

export const ContextEntryInfoCell: React.FunctionComponent<ContextEntryInfoCellProps> = ({
  data,
  row: { index },
  onRowUpdate,
}) => {
  const contextEntry = data[index];

  const [entryInfo, setEntryInfo] = useState(contextEntry.entryInfo);

  useEffect(() => {
    setEntryInfo(contextEntry.entryInfo);
  }, [contextEntry.entryInfo]);

  const onContextEntryUpdate = useCallback(
    (name, dataType) => {
      onRowUpdate(index, { ...contextEntry, entryInfo: { name, dataType } });
    },
    [contextEntry, index, onRowUpdate]
  );

  return (
    <div className="context-entry-info-cell">
      <ContextEntryInfo
        name={entryInfo.name}
        dataType={entryInfo.dataType}
        onContextEntryUpdate={onContextEntryUpdate}
        editInfoPopoverLabel={contextEntry.editInfoPopoverLabel}
      />
    </div>
  );
};
