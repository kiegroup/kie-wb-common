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

import { render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextExpression } from "../../../components/ContextExpression";
import * as React from "react";
import { DataType, LogicType } from "../../../api";

describe("ContextExpression tests", () => {
  const name = "contextName";
  const dataType = DataType.Boolean;
  test("should show a table with two rows: two context entries, where last is representing the result", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextExpression logicType={LogicType.Context} name={name} dataType={dataType} />
      ).wrapper
    );

    expect(container.querySelector(".context-expression")).toBeTruthy();
    expect(container.querySelector(".context-expression table")).toBeTruthy();
    expect(container.querySelectorAll(".context-expression table tbody tr")).toHaveLength(2);
    expect(container.querySelector(".context-expression table tbody tr:first-of-type")).toContainHTML("ContextEntry-1");
    expect(container.querySelector(".context-expression table tbody tr:last-of-type")).toContainHTML("result");
  });

  test("should show a table with one row for each passed entry, plus the passed entry result", () => {
    const firstEntry = "first entry";
    const firstDataType = DataType.Boolean;
    const firstExpression = { name: "expressionName", dataType: DataType.Any, logicType: LogicType.LiteralExpression };
    const secondEntry = "second entry";
    const secondDataType = DataType.Date;
    const secondExpression = { name: "anotherName", dataType: DataType.Undefined };
    const resultEntry = "result entry";
    const resultDataType = DataType.Undefined;

    const contextEntries = [
      {
        name: firstEntry,
        dataType: firstDataType,
        expression: firstExpression,
      },
      {
        name: secondEntry,
        dataType: secondDataType,
        expression: secondExpression,
      },
    ];

    const result = {
      name: resultEntry,
      dataType: resultDataType,
      expression: {},
    };

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextExpression
          logicType={LogicType.Context}
          name={name}
          dataType={dataType}
          contextEntries={contextEntries}
          result={result}
        />
      ).wrapper
    );

    expect(container.querySelector(".context-expression")).toBeTruthy();
    expect(container.querySelector(".context-expression table")).toBeTruthy();
    expect(container.querySelectorAll(".context-expression table tbody tr")).toHaveLength(3);
    checkFirstRow(container, firstEntry, firstDataType);
    checkSecondRow(container, secondEntry, secondDataType);
    checkResultRow(container, resultEntry, resultDataType);
  });

  const checkFirstRow = (container: Element, firstEntry: string, firstDataType: DataType.Boolean) => {
    expect(container.querySelector(".context-expression table tbody tr:first-of-type")).toContainHTML(firstEntry);
    expect(container.querySelector(".context-expression table tbody tr:first-of-type")).toContainHTML(firstDataType);
    expect(
      container.querySelector(".context-expression table tbody tr:first-of-type .entry-expression .react-resizable")!
        .firstChild
    ).toHaveClass("logic-type-selected");
    expect(
      container.querySelector(".context-expression table tbody tr:first-of-type .entry-expression .react-resizable")!
        .firstChild!.firstChild
    ).toHaveClass("literal-expression");
  };

  const checkSecondRow = (container: Element, secondEntry: string, secondDataType: DataType.Date) => {
    expect(container.querySelector(".context-expression table tbody tr:nth-of-type(2)")).toContainHTML(secondEntry);
    expect(container.querySelector(".context-expression table tbody tr:nth-of-type(2)")).toContainHTML(secondDataType);
    expect(
      container.querySelector(".context-expression table tbody tr:nth-of-type(2) .entry-expression")!.firstChild
    ).toHaveClass("logic-type-not-present");
  };

  const checkResultRow = (container: Element, resultEntry: string, resultDataType: DataType.Undefined) => {
    expect(container.querySelector(".context-expression table tbody tr:last-of-type")).not.toContainHTML(resultEntry);
    expect(container.querySelector(".context-expression table tbody tr:last-of-type")).not.toContainHTML(
      resultDataType
    );
    expect(container.querySelector(".context-expression table tbody tr:last-of-type")).toContainHTML("result");
    expect(
      container.querySelector(".context-expression table tbody tr:last-of-type .entry-expression")!.firstChild
    ).toHaveClass("logic-type-not-present");
  };
});
