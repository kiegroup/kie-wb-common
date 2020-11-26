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

import "./EditExpressionMenu.css";
import * as React from "react";
import { useCallback, useState } from "react";
import { PopoverMenu } from "../PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataType, Expression } from "../../api";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as _ from "lodash";

export interface EditExpressionMenuProps {
  /** The node where to append the popover content */
  appendTo?: HTMLElement | ((ref?: HTMLElement) => HTMLElement);
  /** A function which returns the HTMLElement where the popover's arrow should be placed */
  arrowPlacement: () => HTMLElement;
  /** The label for the field 'Name' */
  nameField?: string;
  /** The label for the field 'Data Type' */
  dataTypeField?: string;
  /** The title of the popover menu */
  title?: string;
  /** The pre-selected data type */
  selectedDataType?: DataType;
  /** The pre-selected expression name */
  selectedExpressionName: string;
  /** Function to be called when the expression gets updated, passing the most updated version of it */
  onExpressionUpdate: (expression: Expression) => void;
}

export const EditExpressionMenu: React.FunctionComponent<EditExpressionMenuProps> = ({
  appendTo,
  arrowPlacement,
  title,
  nameField,
  dataTypeField,
  selectedDataType = DataType.Undefined,
  selectedExpressionName,
  onExpressionUpdate,
}: EditExpressionMenuProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  title = title ?? i18n.editExpression;
  nameField = nameField ?? i18n.name;
  dataTypeField = dataTypeField ?? i18n.dataType;

  const [dataTypeSelectIsOpen, setDataTypeSelectOpen] = useState(false);
  const [chosenDataType, setDataType] = useState(selectedDataType);
  const [chosenExpressionName, setExpressionName] = useState(selectedExpressionName);

  const onExpressionNameChange = useCallback(
    (event) => {
      setExpressionName(event.target.value);
      if (event.type === "blur") {
        onExpressionUpdate({
          name: event.target.value,
          dataType: chosenDataType,
        });
      }
    },
    [chosenDataType, onExpressionUpdate]
  );

  const onDataTypeSelect = useCallback(
    (event, selection) => {
      setDataTypeSelectOpen(false);
      setDataType(selection);
      onExpressionUpdate({
        name: chosenExpressionName,
        dataType: selection,
      });
    },
    [chosenExpressionName, onExpressionUpdate]
  );

  const getDataTypes = useCallback(() => {
    return _.map(Object.values(DataType), (key) => (
      <SelectOption key={key} value={key}>
        {key}
      </SelectOption>
    ));
  }, []);

  const onDataTypeFilter = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      let input: RegExp;
      try {
        input = new RegExp(e.target.value, "i");
      } catch (exception) {
        return getDataTypes();
      }
      return e.target.value !== "" ? getDataTypes().filter((child) => input.test(child.props.value)) : getDataTypes();
    },
    [getDataTypes]
  );

  const onDataTypeSelectToggle = useCallback((isOpen) => setDataTypeSelectOpen(isOpen), []);

  return (
    <PopoverMenu
      title={title}
      arrowPlacement={arrowPlacement}
      appendTo={appendTo}
      body={
        <div className="edit-expression-menu">
          <div className="expression-name">
            <label>{nameField}</label>
            <input
              type="text"
              id="expression-name"
              value={chosenExpressionName}
              onChange={onExpressionNameChange}
              onBlur={onExpressionNameChange}
              className="form-control pf-c-form-control"
              placeholder="Expression Name"
            />
          </div>
          <div className="expression-data-type">
            <label>{dataTypeField}</label>
            <Select
              variant={SelectVariant.typeahead}
              typeAheadAriaLabel={i18n.choose}
              onToggle={onDataTypeSelectToggle}
              onSelect={onDataTypeSelect}
              onFilter={onDataTypeFilter}
              isOpen={dataTypeSelectIsOpen}
              selections={chosenDataType}
              hasInlineFilter
              inlineFilterPlaceholderText={i18n.choose}
            >
              {getDataTypes()}
            </Select>
          </div>
        </div>
      }
    />
  );
};
