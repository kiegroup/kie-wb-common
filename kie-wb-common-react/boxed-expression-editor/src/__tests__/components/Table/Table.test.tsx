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
import { Table } from "../../../components/Table";
import * as _ from "lodash";
import * as React from "react";
import { DataType, TableHandlerConfiguration } from "../../../api";
import { ColumnInstance, DataRecord } from "react-table";

describe("Table tests", () => {
  const columnName = "column-1";
  const handlerConfiguration: TableHandlerConfiguration = [];

  test("should render a table element", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <Table
          columnPrefix="column-"
          columns={[]}
          rows={[]}
          onColumnsUpdate={_.identity}
          onRowsUpdate={_.identity}
          handlerConfiguration={handlerConfiguration}
        />
      ).wrapper
    );

    expect(container.querySelector(".table-component table")).toBeTruthy();
  });

  test("should render a table head with only one default column (#)", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <Table
          columnPrefix="column-"
          columns={[]}
          rows={[]}
          onColumnsUpdate={_.identity}
          onRowsUpdate={_.identity}
          handlerConfiguration={handlerConfiguration}
        />
      ).wrapper
    );

    expect(container.querySelector(".table-component table thead")).toBeTruthy();
    expect(container.querySelector(".table-component table thead tr")).toBeTruthy();
    expect(container.querySelectorAll(".table-component table thead tr th").length).toBe(1);
    expect(container.querySelectorAll(".table-component table thead tr th")[0].innerHTML).toContain("#");
  });

  test("should render a table head with one configured column", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <Table
          columnPrefix="column-"
          columns={[{ accessor: columnName, label: columnName, dataType: DataType.Undefined } as ColumnInstance]}
          rows={[]}
          onColumnsUpdate={_.identity}
          onRowsUpdate={_.identity}
          handlerConfiguration={handlerConfiguration}
        />
      ).wrapper
    );

    expect(container.querySelector(".table-component table thead")).toBeTruthy();
    expect(container.querySelector(".table-component table thead tr")).toBeTruthy();
    expect(container.querySelectorAll(".table-component table thead tr th").length).toBe(2);
    expect(container.querySelectorAll(".table-component table thead tr th")[1].innerHTML).toContain(columnName);
  });

  test("should render a table body with no rows", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <Table
          columnPrefix="column-"
          columns={[]}
          rows={[]}
          onColumnsUpdate={_.identity}
          onRowsUpdate={_.identity}
          handlerConfiguration={handlerConfiguration}
        />
      ).wrapper
    );

    expect(container.querySelector(".table-component table tbody")).toBeTruthy();
    expect(container.querySelector(".table-component table tbody tr")).toBeFalsy();
  });

  test("should render a table body with one configured row", () => {
    const row: DataRecord = {};
    const cellValue = "cell value";
    row[columnName] = cellValue;
    const rows: DataRecord[] = [row];

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        <Table
          columnPrefix="column-"
          columns={[{ accessor: columnName, dataType: DataType.Undefined } as ColumnInstance]}
          rows={rows}
          onColumnsUpdate={_.identity}
          onRowsUpdate={_.identity}
          handlerConfiguration={handlerConfiguration}
        />
      ).wrapper
    );

    expect(container.querySelector(".table-component table tbody")).toBeTruthy();
    expect(container.querySelector(".table-component table tbody tr")).toBeTruthy();
    expect(container.querySelectorAll(".table-component table tbody tr td").length).toBe(2);
    expect(container.querySelectorAll(".table-component table tbody tr td")[0].innerHTML).toContain("1");
    expect(container.querySelectorAll(".table-component table tbody tr td")[1].innerHTML).toContain(cellValue);
  });
});
