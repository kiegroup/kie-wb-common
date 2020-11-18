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
import {useState} from "react";
import * as _ from "lodash";
import "./ExpressionContainer.css"
import {useBoxedExpressionEditorI18n} from "../../i18n";
import {
  Dropdown,
  DropdownItem,
  KebabToggle,
  Popover,
  SimpleList,
  SimpleListItem,
  SimpleListItemProps
} from "@patternfly/react-core";

export interface ExpressionContainerProps {
  /** The name of the expression */
  name: string,
  /** The type of the expression */
  type?: string,
  /** Selected expression is already present */
  selectedExpression?: string
}

const ExpressionContainer: (props: ExpressionContainerProps) => JSX.Element = (props: ExpressionContainerProps) => {
  const {i18n} = useBoxedExpressionEditorI18n();

  const [logicTypeIsPresent, isLogicTypeSet] = useState(!_.isEmpty(props.selectedExpression));
  const [actionDropdownIsOpen, isActionDropdownOpen] = useState(false);

  const hideSelectorMenuPopover = () => {
    const elem = document.querySelector('.expression-selector-menu .pf-c-button');
    const button: HTMLButtonElement = elem as HTMLButtonElement;
    return button.click();
  };

  const onLogicTypeSelect = (currentItem: React.RefObject<HTMLButtonElement>, currentItemProps: SimpleListItemProps) => {
    isLogicTypeSet(true);
    document.getElementById("expression-container-box")!.innerHTML = currentItemProps.children as string;
    hideSelectorMenuPopover();
  };

  const executeClearAction = () => {
    isLogicTypeSet(false);
    document.getElementById("expression-container-box")!.innerHTML = i18n.selectExpression;
  };

  const renderExpressionActionsDropdown = () => {
    return <Dropdown
      onSelect={() => isActionDropdownOpen(!actionDropdownIsOpen)}
      toggle={<KebabToggle onToggle={isOpen => isActionDropdownOpen(isOpen)} id="expression-actions-toggle"/>}
      isOpen={actionDropdownIsOpen}
      isPlain
      dropdownItems={[
        <DropdownItem key="clear" onClick={executeClearAction} isDisabled={!logicTypeIsPresent}>
          {i18n.clear}
        </DropdownItem>
      ]}
    />;
  };

  const buildLogicSelectorMenu = () => {
    return <Popover
      className="expression-selector-menu"
      position="bottom"
      distance={0}
      reference={() => document.getElementById("expression-container-box")!}
      isVisible={false}
      shouldOpen={(showFunction = _.identity) => {
        if (!logicTypeIsPresent) {
          showFunction();
        }
      }}
      shouldClose={(tip, hideFunction = _.identity) => hideFunction()}
      headerContent={<div className="selector-menu-title">{i18n.selectLogicType}</div>}
      bodyContent={
        <SimpleList onSelect={onLogicTypeSelect}>
          {renderLogicTypeItems()}
        </SimpleList>
      }
    />;
  };

  const renderLogicTypeItems = () => {
    return _.map([
      i18n.literalExpression,
      i18n.context,
      i18n.decisionTable,
      i18n.relation,
      i18n.function,
      i18n.invocation,
      i18n.list,
    ], key => <SimpleListItem key={key}>{key}</SimpleListItem>)
  };

  return (
    <div className="expression-container">
      <span id="expression-title">
        {props.name || ''}
      </span>
      <span id="expression-type">
        ({props.type ?? '<Undefined>'})
      </span>
      <span id="expression-actions">
        {renderExpressionActionsDropdown()}
      </span>

      <div id="expression-container-box">
        {props.selectedExpression || i18n.selectExpression}
      </div>

      {buildLogicSelectorMenu()}
    </div>
  );
}

export {ExpressionContainer};
