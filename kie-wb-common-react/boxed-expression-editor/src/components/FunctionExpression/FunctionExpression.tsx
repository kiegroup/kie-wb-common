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

import "./FunctionExpression.css";
import * as React from "react";
import { useCallback, useContext, useMemo, useRef, useState } from "react";
import {
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  FunctionKind,
  FunctionProps,
  LogicType,
  resetEntry,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import { Menu, MenuItem, MenuList } from "@patternfly/react-core";
import * as _ from "lodash";
import { BoxedExpressionGlobalContext } from "../../context";

export const FunctionExpression: React.FunctionComponent<FunctionProps> = ({
  children,
  dataType,
  formalParameters,
  functionKind = FunctionKind.Feel,
  isHeadless,
  logicType,
  name = "p-1",
  onUpdatingNameAndDataType,
  onUpdatingRecursiveExpression,
  uid,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const columns = useRef<ColumnInstance[]>([
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      columns: [
        {
          headerCellElement: <div>Parameters list</div>, //TODO parameters retrieved from `formalParameters`
          accessor: "parameters",
          disableHandlerOnHeader: true,
          width: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH, //TODO width as external param
          minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
        },
      ],
    },
  ] as ColumnInstance[]);

  const [selectedFunctionKind, setSelectedFunctionKind] = useState(functionKind);
  const [rows, setRows] = useState([{ entryExpression: { logicType: LogicType.LiteralExpression } } as DataRecord]); //TODO compose default row based on selected function kind

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.LastLevel : TableHeaderVisibility.Full;
  }, [isHeadless]);

  const onFunctionKindSelect = useCallback((event: MouseEvent, itemId: string) => {
    setSelectedFunctionKind(itemId as FunctionKind);
    //TODO for first task, only FEEL (default) is available
  }, []);

  const functionKindSelectionCallback = useCallback(
    (hide: () => void) => (event: MouseEvent, itemId: string) => {
      onFunctionKindSelect(event, itemId);
      hide();
    },
    [onFunctionKindSelect]
  );

  const renderFunctionKindItems = useCallback(
    () =>
      _.map(Object.values(FunctionKind), (key) => (
        <MenuItem key={key} itemId={key}>
          {key}
        </MenuItem>
      )),
    []
  );

  const renderFunctionKindCell = useMemo(() => {
    return (
      <PopoverMenu
        title={i18n.selectFunctionKind}
        appendTo={globalContext.boxedExpressionEditorRef?.current ?? undefined}
        className="function-kind-popover"
        hasAutoWidth
        body={(hide: () => void) => (
          <Menu onSelect={functionKindSelectionCallback(hide)}>
            <MenuList>{renderFunctionKindItems()}</MenuList>
          </Menu>
        )}
      >
        <div className="selected-function-kind">{_.first(selectedFunctionKind)}</div>
      </PopoverMenu>
    );
  }, [
    functionKindSelectionCallback,
    globalContext.boxedExpressionEditorRef,
    i18n.selectFunctionKind,
    renderFunctionKindItems,
    selectedFunctionKind,
  ]);

  return (
    <div className={`function-expression ${uid}`}>
      <Table
        handlerConfiguration={[
          {
            group: _.upperCase(i18n.function),
            items: [{ name: i18n.rowOperations.clear, type: TableOperation.RowClear }],
          },
        ]}
        columns={columns.current}
        rows={rows}
        onRowsUpdate={setRows}
        headerLevels={1}
        headerVisibility={getHeaderVisibility()}
        controllerCell={renderFunctionKindCell}
        defaultCell={{ parameters: ContextEntryExpressionCell }}
        resetRowCustomFunction={useCallback(resetEntry, [])} //TODO check that when spreading expression definition, clear action gets immediately executed
      />
    </div>
  );
};
