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

import { DataType } from "../../../api";
import { render } from "@testing-library/react";
import { EDIT_EXPRESSION_NAME, updateElementViaPopover, usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextEntryCell } from "../../../components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";
import { DataRecord } from "react-table";

jest.useFakeTimers();

describe("ContextEntryCell tests", () => {
  const contextExpressionId = "id-0";
  const name = "Expression Name";
  const dataType = DataType.Boolean;
  const emptyExpression = { name, dataType };
  const entryName = "entry name";
  const entryDataType = DataType.Date;

  const value = "value";
  const newValue = "changed";
  const rowIndex = 0;
  const columnId = "col1";
  const onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void = (rowIndex, updatedRow) =>
    _.identity({ rowIndex, updatedRow });

  test("should show a context entry cell with logic type not selected", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryCell
          data={[{ name: entryName, dataType: entryDataType, expression: emptyExpression, contextExpressionId }]}
          row={{ index: 0 }}
          column={{ id: "col1" }}
          onRowUpdate={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".context-entry-cell")).toBeTruthy();
    expect(container.querySelector(".context-entry .entry-info")).not.toBeEmptyDOMElement();
    expect(container.querySelector(".context-entry .entry-info .entry-definition")).not.toBeEmptyDOMElement();
    expect(container.querySelector(".context-entry .entry-info .entry-definition .entry-name")).toContainHTML(
      entryName
    );
    expect(container.querySelector(".context-entry .entry-info .entry-definition .entry-data-type")).toContainHTML(
      entryDataType
    );
    expect(container.querySelector(".context-entry .entry-expression .logic-type-selector")).toHaveClass(
      "logic-type-not-present"
    );
  });

  test("should trigger onRowUpdate function when something in the context entry changes", async () => {
    const mockedOnRowUpdate = jest.fn(onRowUpdate);

    const { container, baseElement } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntryCell
          data={[{ name: value, dataType: entryDataType, expression: emptyExpression, contextExpressionId }]}
          row={{ index: rowIndex }}
          column={{ id: columnId }}
          onRowUpdate={mockedOnRowUpdate}
        />
      ).wrapper
    );

    await updateElementViaPopover(
      container.querySelector(".entry-definition") as HTMLTableHeaderCellElement,
      baseElement,
      EDIT_EXPRESSION_NAME,
      newValue
    );

    expect(mockedOnRowUpdate).toHaveBeenCalled();
    expect(mockedOnRowUpdate).toHaveBeenCalledWith(rowIndex, {
      contextExpressionId,
      name: newValue,
      dataType: entryDataType,
      expression: emptyExpression,
    });
  });
});
