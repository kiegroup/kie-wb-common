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

import { DataType, LogicType } from "../../../api";
import { render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import { ContextEntry } from "../../../components/ContextExpression";
import * as _ from "lodash";
import * as React from "react";

describe("ContextEntry tests", () => {
  const name = "Expression Name";
  const dataType = DataType.Boolean;
  const emptyExpression = { name, dataType };

  test("should show a context entry element with logic type not selected and empty resizable entry info, when rendering it with an empty expression", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntry
          expression={emptyExpression}
          onUpdatingRecursiveExpression={_.identity}
          onUpdatingWidth={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".context-entry")).toBeTruthy();
    expect(container.querySelector(".context-entry .entry-info")!.children).toHaveLength(1);
    expect(container.querySelector(".context-entry .entry-info")!.children[0]).toHaveClass("react-resizable");
    expect(container.querySelector(".context-entry .logic-type-selector")).toHaveClass("logic-type-not-present");
  });

  test("should show a context entry element with selected logic type and empty resizable entry info, when rendering it with an expression", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntry
          expression={{ ...emptyExpression, logicType: LogicType.LiteralExpression }}
          onUpdatingRecursiveExpression={_.identity}
          onUpdatingWidth={_.identity}
        />
      ).wrapper
    );

    expect(container.querySelector(".context-entry")).toBeTruthy();
    expect(container.querySelector(".context-entry .entry-info")!.children).toHaveLength(1);
    expect(container.querySelector(".context-entry .entry-info")!.children[0]).toHaveClass("react-resizable");
    expect(container.querySelector(".context-entry .logic-type-selector")).toHaveClass("logic-type-selected");
  });

  test("should show a context entry element with logic type not selected and passed entry info, when rendering it with an empty expression", () => {
    const content = <div id="content">content</div>;
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <ContextEntry
          expression={emptyExpression}
          onUpdatingRecursiveExpression={_.identity}
          onUpdatingWidth={_.identity}
        >
          {content}
        </ContextEntry>
      ).wrapper
    );

    expect(container.querySelector(".context-entry")).toBeTruthy();
    expect(container.querySelector(".context-entry .entry-info")).toContainElement(container.querySelector("#content"));
    expect(container.querySelector(".context-entry .logic-type-selector")).toHaveClass("logic-type-not-present");
  });
});
