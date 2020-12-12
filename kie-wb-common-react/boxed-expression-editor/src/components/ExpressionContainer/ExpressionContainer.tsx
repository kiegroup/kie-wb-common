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
import { Button, ButtonVariant, SimpleList, SimpleListItem, SimpleListItemProps } from "@patternfly/react-core";
import { PopoverMenu } from "../PopoverMenu";
import { ExpressionProps, LiteralExpressionProps, LogicType, RelationProps } from "../../api";
import { LiteralExpression } from "../LiteralExpression";
import { useContextMenuHandler } from "../../hooks";
import { RelationExpression } from "../RelationExpression";

export interface ExpressionContainerProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
}

export const ExpressionContainer: ({ selectedExpression }: ExpressionContainerProps) => JSX.Element = (
  props: ExpressionContainerProps
) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [logicTypeSelected, setLogicTypeSelected] = useState(
    !_.isEmpty(props.selectedExpression.logicType) || props.selectedExpression.logicType === LogicType.Undefined
  );
  const [selectedExpression, setSelectedExpression] = useState(props.selectedExpression);

  const {
    contextMenuRef,
    contextMenuXPos,
    contextMenuYPos,
    contextMenuVisibility,
    setContextMenuVisibility,
  } = useContextMenuHandler();

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
    setContextMenuVisibility(false);
    setSelectedExpression((previousSelectedExpression: ExpressionProps) => {
      const updatedExpression = {
        name: previousSelectedExpression.name,
        dataType: previousSelectedExpression.dataType,
        logicType: LogicType.Undefined,
      };
      window.beeApi?.resetExpressionDefinition?.(updatedExpression);
      return updatedExpression;
    });
  }, [setContextMenuVisibility]);

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
      case LogicType.Relation:
        return <RelationExpression {...(selectedExpression as RelationProps)} />;
      case LogicType.Context:
      case LogicType.DecisionTable:
      case LogicType.Function:
      case LogicType.Invocation:
      case LogicType.List:
      default:
        return selectedExpression.logicType;
    }
  }, [selectedExpression, updateNameAndDataType]);

  const buildContextMenu = useCallback(() => {
    return (
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
    );
  }, [logicTypeSelected, contextMenuXPos, contextMenuYPos, executeClearAction, i18n.clear]);

  return (
    <div className="expression-container">
      <span className="expression-title">{selectedExpression.name}</span>
      <span className="expression-type">({selectedExpression.logicType || LogicType.Undefined})</span>

      <div
        className={`expression-container-box ${logicTypeSelected ? "logic-type-selected" : "logic-type-not-present"}`}
        ref={contextMenuRef}
      >
        {selectedExpression.logicType ? renderSelectedExpression : i18n.selectExpression}
      </div>

      {!logicTypeSelected ? buildLogicSelectorMenu() : null}
      {contextMenuVisibility ? buildContextMenu() : null}
    </div>
  );
};
