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

import "./LogicTypeSelector.css";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { ContextProps, DataType, ExpressionProps, LiteralExpressionProps, LogicType, RelationProps } from "../../api";
import { LiteralExpression } from "../LiteralExpression";
import { RelationExpression } from "../RelationExpression";
import { ContextExpression } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import { Button, ButtonVariant, SimpleList, SimpleListItem, SimpleListItemProps } from "@patternfly/react-core";
import * as _ from "lodash";
import { useContextMenuHandler } from "../../hooks";

export interface LogicTypeSelectorProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
  /** Function to be invoked when logic type changes */
  onLogicTypeUpdating: (logicType: LogicType) => void;
  /** Function to be invoked when logic type is reset */
  onLogicTypeResetting: () => void;
  /** Function to be invoked to update expression's name and datatype */
  onNameAndDataTypeUpdating: (updatedName: string, updatedDataType: DataType) => void;
}

export const LogicTypeSelector: React.FunctionComponent<LogicTypeSelectorProps> = ({
  selectedExpression,
  onLogicTypeUpdating,
  onLogicTypeResetting,
  onNameAndDataTypeUpdating,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [logicTypeSelected, setLogicTypeSelected] = useState(
    !_.isEmpty(selectedExpression.logicType) || selectedExpression.logicType === LogicType.Undefined
  );

  const {
    contextMenuRef,
    contextMenuXPos,
    contextMenuYPos,
    contextMenuVisibility,
    setContextMenuVisibility,
  } = useContextMenuHandler();

  const renderSelectedExpression = useMemo(() => {
    switch (selectedExpression.logicType) {
      case LogicType.LiteralExpression:
        return (
          <LiteralExpression
            onUpdatingNameAndDataType={onNameAndDataTypeUpdating}
            {...(selectedExpression as LiteralExpressionProps)}
          />
        );
      case LogicType.Relation:
        return <RelationExpression {...(selectedExpression as RelationProps)} />;
      case LogicType.Context:
        return (
          <ContextExpression
            onUpdatingNameAndDataType={onNameAndDataTypeUpdating}
            {...(selectedExpression as ContextProps)}
          />
        );
      case LogicType.DecisionTable:
      case LogicType.Function:
      case LogicType.Invocation:
      case LogicType.List:
      default:
        return selectedExpression.logicType;
    }
  }, [selectedExpression, onNameAndDataTypeUpdating]);

  const getLogicTypesWithoutUndefined = useCallback(
    () => Object.values(LogicType).filter((logicType) => logicType !== LogicType.Undefined),
    []
  );

  const renderLogicTypeItems = useCallback(
    () => _.map(getLogicTypesWithoutUndefined(), (key) => <SimpleListItem key={key}>{key}</SimpleListItem>),
    [getLogicTypesWithoutUndefined]
  );

  const getLogicSelectionArrowPlacement = useCallback(
    () => document.querySelector(".expression-container-box")! as HTMLElement,
    []
  );

  const onLogicTypeSelect = useCallback(
    (currentItem: React.RefObject<HTMLButtonElement>, currentItemProps: SimpleListItemProps) => {
      setLogicTypeSelected(true);
      const selectedLogicType = currentItemProps.children as LogicType;
      onLogicTypeUpdating(selectedLogicType);
    },
    [onLogicTypeUpdating]
  );

  const buildLogicSelectorMenu = useCallback(
    () => (
      <PopoverMenu
        title={i18n.selectLogicType}
        arrowPlacement={getLogicSelectionArrowPlacement}
        body={<SimpleList onSelect={onLogicTypeSelect}>{renderLogicTypeItems()}</SimpleList>}
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
        className="context-menu-container"
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
      className={`logic-type-selector ${logicTypeSelected ? "logic-type-selected" : "logic-type-not-present"}`}
      ref={contextMenuRef}
    >
      {logicTypeSelected ? renderSelectedExpression : i18n.selectExpression}
      {!logicTypeSelected ? buildLogicSelectorMenu() : null}
      {contextMenuVisibility ? buildContextMenu() : null}
    </div>
  );
};
