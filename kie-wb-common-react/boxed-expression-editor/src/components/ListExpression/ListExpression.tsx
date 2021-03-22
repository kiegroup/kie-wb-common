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

import "./ListExpression.css";
import * as React from "react";
import { useState } from "react";
import { ListProps, LiteralExpressionProps, LogicType } from "../../api";
import nextId from "react-id-generator";

export const ListExpression: React.FunctionComponent<ListProps> = ({
  dataType,
  isHeadless,
  items,
  onUpdatingNameAndDataType,
  onUpdatingRecursiveExpression,
  uid,
}: ListProps) => {
  const [listItems, setListItems] = useState(
    items || [{ uid: nextId(), logicType: LogicType.LiteralExpression } as LiteralExpressionProps]
  );
  return <div className="list-expression"/>;
};
