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

import { render } from "@testing-library/react";
import { usingTestingBoxedExpressionI18nContext } from "../test-utils";
import * as React from "react";
import { EditableCell } from "../../../components/Table";
import * as _ from "lodash";

describe("EditableCell tests", () => {
  test("should render the initial value", async () => {
    const initialValue = "INITIAL_VALUE";

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <EditableCell value={initialValue} row={{ index: 0 }} column={{ id: "col1" }} onCellUpdate={_.identity} />
      ).wrapper
    );

    expect(container.querySelector("textarea")).toBeTruthy();
    expect((container.querySelector("textarea") as HTMLTextAreaElement).value).toBe(initialValue);
  });

  test("should trigger onCellUpdate function when the user changes its value", () => {
    const value = "value";
    const rowIndex = 0;
    const columnId = "col1";
    const onCellUpdate = (rowIndex: number, columnId: string, value: string) => {
      _.identity({ rowIndex, columnId, value });
    };
    const mockedOnCellUpdate = jest.fn(onCellUpdate);

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <EditableCell
          value={value}
          row={{ index: rowIndex }}
          column={{ id: columnId }}
          onCellUpdate={mockedOnCellUpdate}
        />
      ).wrapper
    );

    (container.querySelector("textarea") as HTMLTextAreaElement)!.value = "changed";
    (container.querySelector("textarea") as HTMLTextAreaElement)!.dispatchEvent(new Event("change"));
    (container.querySelector("textarea") as HTMLTextAreaElement)!.dispatchEvent(new Event("blur"));

    expect(mockedOnCellUpdate).toHaveBeenCalled();
    expect(mockedOnCellUpdate).toHaveBeenCalledWith(rowIndex, columnId, value);
  });
});
