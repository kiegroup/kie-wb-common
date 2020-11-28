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
import { useCallback, useState } from "react";
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
      setSelectedExpression({
        ...selectedExpression,
        logicType: selectedLogicType,
      });
    },
    [selectedExpression]
  );

  const executeClearAction = useCallback(() => {
    setLogicTypeSelected(false);
    setSelectedExpression({
      ...selectedExpression,
      logicType: LogicType.Undefined,
    });
  }, [selectedExpression]);

  const renderExpressionActionsDropdown = useCallback(() => {
    return (
      <Dropdown
        onSelect={() => setActionDropDownOpen(!actionDropdownIsOpen)}
        toggle={<KebabToggle onToggle={(isOpen) => setActionDropDownOpen(isOpen)} id="expression-actions-toggle" />}
        isOpen={actionDropdownIsOpen}
        isPlain
        dropdownItems={[
          <DropdownItem key="clear" onClick={executeClearAction} isDisabled={!logicTypeIsPresent}>
            {i18n.clear}
          </DropdownItem>,
        ]}
      />
    );
  }, [i18n.clear, actionDropdownIsOpen, logicTypeIsPresent, executeClearAction]);

  const getLogicTypesWithoutUndefined = useCallback(() => {
    return Object.values(LogicType).filter((logicType) => logicType !== LogicType.Undefined);
  }, []);

  const renderLogicTypeItems = useCallback(() => {
    return _.map(getLogicTypesWithoutUndefined(), (key) => <SimpleListItem key={key}>{key}</SimpleListItem>);
  }, [getLogicTypesWithoutUndefined]);

  const buildLogicSelectorMenu = useCallback(() => {
    return (
      <PopoverMenu
        title={i18n.selectLogicType}
        arrowPlacement={() => document.getElementById("expression-container-box")!}
        body={<SimpleList onSelect={onLogicTypeSelect}>{renderLogicTypeItems()}</SimpleList>}
      />
    );
  }, [i18n.selectLogicType, onLogicTypeSelect, renderLogicTypeItems]);

  const renderSelectedExpression = useCallback((selectedExpression: ExpressionProps) => {
    switch (selectedExpression.logicType) {
      case LogicType.LiteralExpression:
        return <LiteralExpression {...(selectedExpression as LiteralExpressionProps)} />;
      case LogicType.Context:
      case LogicType.DecisionTable:
      case LogicType.Relation:
      case LogicType.Function:
      case LogicType.Invocation:
      case LogicType.List:
      default:
        return selectedExpression.logicType;
    }
  }, []);

  return (
    <div className="expression-container">
      <span id="expression-title">{selectedExpression.name}</span>
      <span id="expression-type">({selectedExpression.logicType || LogicType.Undefined})</span>
      <span id="expression-actions">{renderExpressionActionsDropdown()}</span>

      <div
        id="expression-container-box"
        className={logicTypeIsPresent ? "logic-type-selected" : "logic-type-not-present"}
      >
        {selectedExpression.logicType ? renderSelectedExpression(selectedExpression) : i18n.selectExpression}
      </div>

      {!logicTypeIsPresent ? buildLogicSelectorMenu() : null}
    </div>
  );
};
