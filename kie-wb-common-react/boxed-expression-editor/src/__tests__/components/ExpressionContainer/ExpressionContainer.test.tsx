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
import { DataType, LogicType } from "../../../api";
import { act } from "react-dom/test-utils";

jest.useFakeTimers();
const flushPromises = () => new Promise((resolve) => process.nextTick(resolve));

describe("ExpressionContainer tests", () => {
  test("should render ExpressionContainer component", () => {
    const expression = { name: "Test", dataType: DataType.Undefined };

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
    );

    expect(container).toMatchSnapshot();
  });

  test("should render expression title, when name prop is passed", () => {
    const expressionTitle = "Test";
    const expression = { name: expressionTitle, dataType: DataType.Undefined };

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
    );
    expect(container.querySelector(".expression-title")).toBeTruthy();
    expect(container.querySelector(".expression-title")!.innerHTML).toBe(expressionTitle);
  });

  test("should render expression type, when type prop is passed", () => {
    const expression = { name: "Test", logicType: LogicType.Context, dataType: DataType.Undefined };
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
    );

    expect(container.querySelector(".expression-type")).toBeTruthy();
    expect(container.querySelector(".expression-type")!.innerHTML).toBe("(" + LogicType.Context + ")");
  });

  test("should render expression type as undefined, when type prop is not passed", () => {
    const expression = { name: "Test", dataType: DataType.Undefined };

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
    );
    expect(container.querySelector(".expression-type")).toBeTruthy();
    expect(container.querySelector(".expression-type")!.innerHTML).toBe("(&lt;Undefined&gt;)");
  });

  describe("Expression Actions dropdown", () => {
    test("should have the clear action disabled on startup", async () => {
      const expression = { name: "Test", dataType: DataType.Undefined };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
      );

      await triggerContextMenu(container as HTMLElement, ".expression-container-box");

      expect(container.querySelector(".context-menu-container button.pf-m-disabled")).toBeTruthy();
      expect(container.querySelector(".context-menu-container button.pf-m-disabled")!.innerHTML).toBe("Clear");
    });

    test("should have the clear action enabled, when logic type is selected", async () => {
      const expression = { name: "Test", logicType: LogicType.LiteralExpression, dataType: DataType.Undefined };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
      );

      await triggerContextMenu(container as HTMLElement, ".expression-container-box");

      expect(container.querySelector(".context-menu-container button.pf-m-disabled")).toBeFalsy();
      expect(container.querySelector(".context-menu-container button")).toBeTruthy();
      expect(container.querySelector(".context-menu-container button")!.innerHTML).toBe("Clear");
    });
  });

  describe("Logic type selection", () => {
    test("should show the pre-selection, when logic type prop is passed", () => {
      const expression = { name: "Test", logicType: LogicType.Context, dataType: DataType.Undefined };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
      );

      expect(container.querySelector(".expression-container-box")).toBeTruthy();
      expect(container.querySelector(".expression-container-box")!.innerHTML).toBe(expression.logicType);
    });

    test("should reset the selection, when logic type is selected and clear button gets clicked", async () => {
      const expression = { name: "Test", logicType: LogicType.LiteralExpression, dataType: DataType.Undefined };

      const { container } = render(
        usingTestingBoxedExpressionI18nContext(<ExpressionContainer selectedExpression={expression} />).wrapper
      );

      await triggerContextMenu(container as HTMLElement, ".expression-container-box");

      act(() => {
        const clearButtonElement = container.querySelector(".context-menu-container button")!;
        const clearButton = clearButtonElement as HTMLButtonElement;
        clearButton.click();
      });

      expect(container.querySelector(".expression-container-box")).toBeTruthy();
      expect(container.querySelector(".expression-container-box")!.innerHTML).not.toBe(expression.logicType);
    });
  });
});

const triggerContextMenu = async (container: HTMLElement, selector: string) => {
  await act(async () => {
    const element = container.querySelector(selector)!;

    element.dispatchEvent(
      new MouseEvent("contextmenu", {
        bubbles: true,
        cancelable: false,
        view: window,
        button: 2,
        buttons: 0,
        clientX: element.getBoundingClientRect().x,
        clientY: element.getBoundingClientRect().y,
      })
    );

    await flushPromises();
    jest.runAllTimers();
  });
};
