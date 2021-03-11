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

import "./LogicTypeSelector.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ContextProps, DataType, ExpressionProps, LiteralExpressionProps, LogicType, RelationProps } from "../../api";
import { LiteralExpression } from "../LiteralExpression";
import { RelationExpression } from "../RelationExpression";
import { ContextExpression } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import { Button, ButtonVariant, Menu, MenuItem, MenuList } from "@patternfly/react-core";
import * as _ from "lodash";
import { useContextMenuHandler } from "../../hooks";
import { NO_TABLE_CONTEXT_MENU_CLASS } from "../Table";
import nextId from "react-id-generator";

export interface LogicTypeSelectorProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
  /** Function to be invoked when logic type changes */
  onLogicTypeUpdating: (logicType: LogicType) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeResetting: () => void;
  /** Function to be invoked to update expression's name and datatype */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DataType) => void;
  /** Function to be invoked to retrieve the DOM reference to be used for selector placement */
  getPlacementRef: () => HTMLDivElement;
  /** True to have no header for this specific expression component, used in a recursive expression */
  isHeadless?: boolean;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionProps) => void;
}

export const LogicTypeSelector: React.FunctionComponent<LogicTypeSelectorProps> = ({
  selectedExpression,
  onLogicTypeUpdating,
  onLogicTypeResetting,
  onUpdatingNameAndDataType,
  getPlacementRef,
  isHeadless = false,
  onUpdatingRecursiveExpression,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const expression = _.extend(selectedExpression, {
    isHeadless,
    onUpdatingNameAndDataType,
    onUpdatingRecursiveExpression,
  });

  const isLogicTypeSelected = (logicType?: LogicType) => !_.isEmpty(logicType) && logicType !== LogicType.Undefined;

  const [logicTypeSelected, setLogicTypeSelected] = useState(isLogicTypeSelected(expression.logicType));

  useEffect(() => {
    setLogicTypeSelected(isLogicTypeSelected(selectedExpression.logicType));
  }, [selectedExpression.logicType]);

  const {
    contextMenuRef,
    contextMenuXPos,
    contextMenuYPos,
    contextMenuVisibility,
    setContextMenuVisibility,
  } = useContextMenuHandler();

  const renderExpression = useMemo(() => {
    switch (expression.logicType) {
      case LogicType.LiteralExpression:
        return <LiteralExpression {...(expression as LiteralExpressionProps)} />;
      case LogicType.Relation:
        return <RelationExpression {...(expression as RelationProps)} />;
      case LogicType.Context:
        return <ContextExpression {...(expression as ContextProps)} uid={nextId()} />;
      case LogicType.DecisionTable:
      case LogicType.Function:
      case LogicType.Invocation:
      case LogicType.List:
      default:
        return expression.logicType;
    }
    // logicType is enough for deciding when to re-execute this function
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expression.logicType]);

  const getLogicTypesWithoutUndefined = useCallback(
    () => Object.values(LogicType).filter((logicType) => logicType !== LogicType.Undefined),
    []
  );

  const renderLogicTypeItems = useCallback(
    () =>
      _.map(getLogicTypesWithoutUndefined(), (key) => (
        <MenuItem key={key} itemId={key}>
          {key}
        </MenuItem>
      )),
    [getLogicTypesWithoutUndefined]
  );

  const getLogicSelectionArrowPlacement = useCallback(() => getPlacementRef() as HTMLElement, [getPlacementRef]);

  const onLogicTypeSelect = useCallback(
    (event: MouseEvent, itemId: string) => {
      setLogicTypeSelected(true);
      const selectedLogicType = itemId as LogicType;
      onLogicTypeUpdating(selectedLogicType);
    },
    [onLogicTypeUpdating]
  );

  const buildLogicSelectorMenu = useCallback(
    () => (
      <PopoverMenu
        title={i18n.selectLogicType}
        arrowPlacement={getLogicSelectionArrowPlacement}
        className="logic-type-popover"
        hasAutoWidth
        body={
          <Menu onSelect={onLogicTypeSelect}>
            <MenuList>{renderLogicTypeItems()}</MenuList>
          </Menu>
        }
      />
    ),
    [i18n.selectLogicType, getLogicSelectionArrowPlacement, onLogicTypeSelect, renderLogicTypeItems]
  );

  const executeClearAction = useCallback(() => {
    setLogicTypeSelected(false);
    setContextMenuVisibility(false);
    onLogicTypeResetting();
  }, [onLogicTypeResetting, setContextMenuVisibility]);

  const buildContextMenu = useCallback(
    () => (
      <div
        className="context-menu-container no-table-context-menu"
        style={{
          top: contextMenuYPos,
          left: contextMenuXPos,
        }}
      >
        <Button
          isDisabled={!logicTypeSelected}
          isSmall={true}
          variant={ButtonVariant.primary}
          onClick={executeClearAction}
        >
          {i18n.clear}
        </Button>
      </div>
    ),
    [logicTypeSelected, contextMenuXPos, contextMenuYPos, executeClearAction, i18n.clear]
  );

  return (
    <div
      className={`logic-type-selector ${NO_TABLE_CONTEXT_MENU_CLASS} ${
        logicTypeSelected ? "logic-type-selected" : "logic-type-not-present"
      }`}
      ref={contextMenuRef}
    >
      {logicTypeSelected ? renderExpression : i18n.selectExpression}
      {!logicTypeSelected ? buildLogicSelectorMenu() : null}
      {contextMenuVisibility ? buildContextMenu() : null}
    </div>
  );
};
