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

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import * as _ from "lodash";
import "./ExpressionContainer.css";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import {
  Dropdown,
  DropdownItem,
  KebabToggle,
  SimpleList,
  SimpleListItem,
  SimpleListItemProps,
} from "@patternfly/react-core";
import { PopoverMenu } from "../PopoverMenu";
import { ExpressionProps, LiteralExpressionProps, LogicType } from "../../api";
import { LiteralExpression } from "../LiteralExpression";

export interface ExpressionContainerProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
}

export const ExpressionContainer: ({ selectedExpression }: ExpressionContainerProps) => JSX.Element = (
  props: ExpressionContainerProps
) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [logicTypeIsPresent, setLogicTypeSelected] = useState(
    !_.isEmpty(props.selectedExpression.logicType) || props.selectedExpression.logicType === LogicType.Undefined
  );
  const [actionDropdownIsOpen, setActionDropDownOpen] = useState(false);
  const [selectedExpression, setSelectedExpression] = useState(props.selectedExpression);

  const onLogicTypeSelect = useCallback(
    (currentItem: React.RefObject<HTMLButtonElement>, currentItemProps: SimpleListItemProps) => {
      setLogicTypeSelected(true);
      const selectedLogicType = currentItemProps.children as LogicType;
      setSelectedExpression((previousSelectedExpression: ExpressionProps) => ({
        ...previousSelectedExpression,
        logicType: selectedLogicType,
      }));
    },
    []
  );

  const executeClearAction = useCallback(() => {
    setLogicTypeSelected(false);
    setSelectedExpression((previousSelectedExpression: ExpressionProps) => ({
      name: previousSelectedExpression.name,
      dataType: previousSelectedExpression.dataType,
      logicType: LogicType.Undefined,
    }));
  }, []);

  const onDropdownToggle = useCallback((isOpen) => {
    return setActionDropDownOpen(isOpen);
  }, []);

  const onExpressionActionDropdownSelect = useCallback(
    (actionDropdownIsOpen) => setActionDropDownOpen(!actionDropdownIsOpen),
    []
  );

  const renderExpressionActionsDropdown = useCallback(() => {
    return (
      <Dropdown
        onSelect={onExpressionActionDropdownSelect}
        toggle={<KebabToggle onToggle={onDropdownToggle} className="expression-actions-toggle" />}
        isOpen={actionDropdownIsOpen}
        isPlain
        dropdownItems={[
          <DropdownItem key="clear" onClick={executeClearAction} isDisabled={!logicTypeIsPresent}>
            {i18n.clear}
          </DropdownItem>,
        ]}
      />
    );
  }, [
    i18n.clear,
    onExpressionActionDropdownSelect,
    onDropdownToggle,
    actionDropdownIsOpen,
    logicTypeIsPresent,
    executeClearAction,
  ]);

  const getLogicTypesWithoutUndefined = useCallback(() => {
    return Object.values(LogicType).filter((logicType) => logicType !== LogicType.Undefined);
  }, []);

  const renderLogicTypeItems = useCallback(() => {
    return _.map(getLogicTypesWithoutUndefined(), (key) => <SimpleListItem key={key}>{key}</SimpleListItem>);
  }, [getLogicTypesWithoutUndefined]);

  const getLogicSelectionArrowPlacement = useCallback(
    () => document.querySelector(".expression-container-box")! as HTMLElement,
    []
  );

  const buildLogicSelectorMenu = useCallback(() => {
    return (
      <PopoverMenu
        title={i18n.selectLogicType}
        arrowPlacement={getLogicSelectionArrowPlacement}
        body={<SimpleList onSelect={onLogicTypeSelect}>{renderLogicTypeItems()}</SimpleList>}
      />
    );
  }, [i18n.selectLogicType, getLogicSelectionArrowPlacement, onLogicTypeSelect, renderLogicTypeItems]);

  const updateNameAndDataType = useCallback((updatedName, updatedDataType) => {
    setSelectedExpression((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      name: updatedName,
      dataType: updatedDataType,
    }));
  }, []);

  const renderSelectedExpression = useMemo(() => {
    switch (selectedExpression.logicType) {
      case LogicType.LiteralExpression:
        return (
          <LiteralExpression
            onUpdatingNameAndDataType={updateNameAndDataType}
            {...(selectedExpression as LiteralExpressionProps)}
          />
        );
      case LogicType.Context:
      case LogicType.DecisionTable:
      case LogicType.Relation:
      case LogicType.Function:
      case LogicType.Invocation:
      case LogicType.List:
      default:
        return selectedExpression.logicType;
    }
  }, [selectedExpression, updateNameAndDataType]);

  return (
    <div className="expression-container">
      <span className="expression-title">{selectedExpression.name}</span>
      <span className="expression-type">({selectedExpression.logicType || LogicType.Undefined})</span>
      <span className="expression-actions">{renderExpressionActionsDropdown()}</span>

      <div
        className={`expression-container-box ${logicTypeIsPresent ? "logic-type-selected" : "logic-type-not-present"}`}
      >
        {selectedExpression.logicType ? renderSelectedExpression : i18n.selectExpression}
      </div>

      {!logicTypeIsPresent ? buildLogicSelectorMenu() : null}
    </div>
  );
};
