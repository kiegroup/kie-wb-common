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

import { ExpressionContainer } from "../../../components/ExpressionContainer";
import { render } from "@testing-library/react";
import * as React from "react";
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { LogicType } from "../../../api/LogicType";

describe("ExpressionContainer tests", () => {
  test("should render ExpressionContainer component", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer decisionName="Test" />).wrapper
    );

    expect(container).toMatchSnapshot();
  });

  test("should render expression title, when name prop is passed", () => {
    const expressionTitle = "Test";
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer decisionName={expressionTitle} />).wrapper
    );
    expect(container.querySelector("#expression-title")).toBeTruthy();
    expect(container.querySelector("#expression-title")!.innerHTML).toBe(expressionTitle);
  });

  test("should render expression type, when type prop is passed", () => {
    const expression = { name: "Test", logicType: LogicType.Context };
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ExpressionContainer decisionName="Test" selectedExpression={expression} />
      ).wrapper
    );

    expect(container.querySelector("#expression-type")).toBeTruthy();
    expect(container.querySelector("#expression-type")!.innerHTML).toBe("(" + LogicType.Context + ")");
  });

  test("should render expression type as undefined, when type prop is not passed", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer decisionName="Test" />).wrapper
    );
    expect(container.querySelector("#expression-type")).toBeTruthy();
    expect(container.querySelector("#expression-type")!.innerHTML).toBe("(&lt;Undefined&gt;)");
  });

  describe("Expression Actions dropdown", () => {
    test("should have the clear action disabled on startup", () => {
      const { container } = render(
        usingTestingBoxedExpressionI18nContext(<ExpressionContainer decisionName="Test" />).wrapper
      );

      const actionsToggleElement = container.querySelector("#expression-actions-toggle")!;
      const actionsToggleButton = actionsToggleElement as HTMLButtonElement;
      actionsToggleButton.click();

      expect(container.querySelector(".pf-m-disabled.pf-c-dropdown__menu-item")).toBeTruthy();
      expect(container.querySelector(".pf-m-disabled.pf-c-dropdown__menu-item")!.innerHTML).toBe("Clear");
    });

    test("should have the clear action enabled, when logic type is selected", () => {
      const expression = { name: "Test", logicType: LogicType.LiteralExpression };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <ExpressionContainer decisionName="Test" selectedExpression={expression} />
        ).wrapper
      );

      const actionsToggleElement = container.querySelector("#expression-actions-toggle")!;
      const actionsToggleButton = actionsToggleElement as HTMLButtonElement;
      actionsToggleButton.click();

      expect(container.querySelector(".pf-m-disabled.pf-c-dropdown__menu-item")).toBeFalsy();
      expect(container.querySelector(".pf-c-dropdown__menu-item")).toBeTruthy();
      expect(container.querySelector(".pf-c-dropdown__menu-item")!.innerHTML).toBe("Clear");
    });
  });

  describe("Logic type selection", () => {
    test("should show the pre-selection, when logic type prop is passed", () => {
      const expression = { name: "Test", logicType: LogicType.Context };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <ExpressionContainer decisionName="Test" selectedExpression={expression} />
        ).wrapper
      );

      expect(container.querySelector("#expression-container-box")).toBeTruthy();
      expect(container.querySelector("#expression-container-box")!.innerHTML).toBe(expression.logicType);
    });

    test("should reset the selection, when logic type is selected and clear button gets clicked", () => {
      const expression = { name: "Test", logicType: LogicType.LiteralExpression };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(
          <ExpressionContainer decisionName="Test" selectedExpression={expression} />
        ).wrapper
      );

      const actionsToggleElement = container.querySelector("#expression-actions-toggle")!;
      const actionsToggleButton = actionsToggleElement as HTMLButtonElement;
      actionsToggleButton.click();

      const clearElement = container.querySelector(".pf-c-dropdown__menu-item");
      const clearAnchor = clearElement as HTMLAnchorElement;
      clearAnchor.click();

      expect(container.querySelector("#expression-container-box")).toBeTruthy();
      expect(container.querySelector("#expression-container-box")!.innerHTML).not.toBe(expression.logicType);
    });
  });
});
