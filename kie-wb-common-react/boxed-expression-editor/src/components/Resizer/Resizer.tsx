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

import "./Resizer.css";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { ResizableBox } from "react-resizable";

export interface ResizerProps {
  width: number;
  height: number | "100%";
  minWidth: number;
  minHeight?: number;
  onHorizontalResizeStop: (width: number) => void;
  children?: React.ReactElement;
}

export const DRAWER_SPLITTER_ELEMENT = (
  <div className="pf-c-drawer__splitter pf-m-vertical">
    <div className="pf-c-drawer__splitter-handle" />
  </div>
);

export const Resizer: React.FunctionComponent<ResizerProps> = ({
  children,
  height,
  minHeight = 0,
  minWidth,
  onHorizontalResizeStop,
  width,
}) => {
  const targetHeight = height === "100%" ? 0 : height;

  const resizerHandler = useMemo(() => <div className="pf-c-drawer">{DRAWER_SPLITTER_ELEMENT}</div>, []);

  const onResizeStop = useCallback((e, data) => onHorizontalResizeStop(data.size.width), [onHorizontalResizeStop]);

  return (
    <ResizableBox
      className={`${height === "100%" ? "height-based-on-content" : ""}`}
      width={width}
      height={targetHeight}
      minConstraints={[minWidth, minHeight]}
      axis="x"
      onResizeStop={onResizeStop}
      handle={resizerHandler}
    >
      {children}
    </ResizableBox>
  );
};
