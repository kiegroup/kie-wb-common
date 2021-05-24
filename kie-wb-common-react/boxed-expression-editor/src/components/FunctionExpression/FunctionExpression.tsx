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

import "./FunctionExpression.css";
import * as React from "react";
import { PropsWithChildren, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import {
  ContextProps,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  ExpressionProps,
  FeelFunctionProps,
  FunctionKind,
  FunctionProps,
  JavaFunctionProps,
  LiteralExpressionProps,
  LogicType,
  PmmlFunctionProps,
  PMMLLiteralExpressionProps,
  resetEntry,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { PopoverMenu } from "../PopoverMenu";
import * as _ from "lodash";
import { BoxedExpressionGlobalContext } from "../../context";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { EditParameters } from "./EditParameters";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export const FunctionExpression: React.FunctionComponent<FunctionProps> = (props: PropsWithChildren<FunctionProps>) => {
  const parametersWidth =
    props.parametersWidth === undefined ? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH : props.parametersWidth;
  const formalParameters = props.formalParameters === undefined ? [] : props.formalParameters;
  const functionKind = props.functionKind === undefined ? FunctionKind.Feel : props.functionKind;

  const { i18n } = useBoxedExpressionEditorI18n();

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const [parameters, setParameters] = useState(formalParameters);

  const name = useRef(props.name === undefined ? DEFAULT_FIRST_PARAM_NAME : props.name);
  const dataType = useRef(props.dataType === undefined ? DataType.Undefined : props.dataType);

  const document = useRef((props as PmmlFunctionProps).document);
  const model = useRef((props as PmmlFunctionProps).model);

  const editParametersPopoverAppendTo = useCallback(() => {
    return () => globalContext.boxedExpressionEditorRef.current!;
  }, [globalContext.boxedExpressionEditorRef]);

  const headerCellElement = useMemo(
    () => (
      <PopoverMenu
        title={i18n.editParameters}
        appendTo={editParametersPopoverAppendTo()}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<EditParameters parameters={parameters} setParameters={setParameters} />}
      >
        <div className={`parameters-list ${_.isEmpty(parameters) ? "empty-parameters" : ""}`}>
          <p className="pf-u-text-truncate">
            {_.isEmpty(parameters)
              ? i18n.editParameters
              : `(${_.join(
                  _.map(parameters, (parameter) => parameter.name),
                  ", "
                )})`}
          </p>
        </div>
      </PopoverMenu>
    ),
    [editParametersPopoverAppendTo, i18n.editParameters, parameters]
  );

  const evaluateColumns = useCallback(
    () =>
      [
        {
          label: name.current,
          accessor: name.current,
          dataType: dataType.current,
          disableHandlerOnHeader: true,
          columns: [
            {
              headerCellElement,
              accessor: "parameters",
              disableHandlerOnHeader: true,
              width: width.current,
              minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            },
          ],
        },
      ] as ColumnInstance[],
    [headerCellElement]
  );

  const extractContextEntriesFromJavaProps = useCallback(
    (javaProps: JavaFunctionProps & { children?: React.ReactNode }) => {
      return [
        {
          entryInfo: { name: i18n.class, dataType: DataType.String },
          entryExpression: {
            noClearAction: true,
            logicType: LogicType.LiteralExpression,
            content: javaProps.class,
          } as LiteralExpressionProps,
        },
        {
          entryInfo: { name: i18n.methodSignature, dataType: DataType.String },
          entryExpression: {
            noClearAction: true,
            logicType: LogicType.LiteralExpression,
            content: javaProps.method,
          } as LiteralExpressionProps,
        },
      ];
    },
    [i18n.class, i18n.methodSignature]
  );

  const extractContextEntriesFromPmmlProps = useCallback(
    (pmmlProps: PmmlFunctionProps & { children?: React.ReactNode }) => {
      return [
        {
          entryInfo: { name: i18n.document, dataType: DataType.String },
          entryExpression: {
            noClearAction: true,
            logicType: LogicType.PMMLLiteralExpression,
            noOptionsLabel: i18n.pmml.firstSelection,
            getOptions: () => _.map(pmmlProps.pmmlParams, "document"),
            selected: document.current,
          } as PMMLLiteralExpressionProps,
        },
        {
          entryInfo: { name: i18n.model, dataType: DataType.String },
          entryExpression: {
            noClearAction: true,
            logicType: LogicType.PMMLLiteralExpression,
            noOptionsLabel: i18n.pmml.secondSelection,
            getOptions: () =>
              _.map(
                _.find(pmmlProps.pmmlParams, (param) => param.document === document.current)?.modelsFromDocument,
                "model"
              ),
            selected: model.current,
          } as PMMLLiteralExpressionProps,
        },
      ];
    },
    [i18n.document, i18n.model, i18n.pmml.firstSelection, i18n.pmml.secondSelection]
  );

  const extractParametersFromPmmlProps = useCallback(
    (pmmlProps: PmmlFunctionProps & { children?: React.ReactNode }) => {
      return (
        _.find(_.find(pmmlProps.pmmlParams, { document: document.current })?.modelsFromDocument, {
          model: model.current,
        })?.parametersFromModel || []
      );
    },
    []
  );

  const evaluateRows = useCallback(
    (functionKind: FunctionKind) => {
      switch (functionKind) {
        case FunctionKind.Java: {
          const javaProps: PropsWithChildren<JavaFunctionProps> = props as PropsWithChildren<JavaFunctionProps>;
          return [
            {
              entryExpression: {
                logicType: LogicType.Context,
                noClearAction: true,
                renderResult: false,
                noHandlerMenu: true,
                contextEntries: extractContextEntriesFromJavaProps(javaProps),
              },
            } as DataRecord,
          ];
        }
        case FunctionKind.Pmml: {
          const pmmlProps: PropsWithChildren<PmmlFunctionProps> = props as PropsWithChildren<PmmlFunctionProps>;
          return [
            {
              entryExpression: {
                logicType: LogicType.Context,
                noClearAction: true,
                renderResult: false,
                noHandlerMenu: true,
                contextEntries: extractContextEntriesFromPmmlProps(pmmlProps),
              },
            } as DataRecord,
          ];
        }
        case FunctionKind.Feel:
        default: {
          const feelProps: PropsWithChildren<FeelFunctionProps> = props as PropsWithChildren<FeelFunctionProps>;
          return [
            { entryExpression: feelProps.expression || { logicType: LogicType.LiteralExpression } } as DataRecord,
          ];
        }
      }
    },
    [extractContextEntriesFromJavaProps, extractContextEntriesFromPmmlProps, props]
  );

  const width = useRef<number>(parametersWidth);
  const columns = useRef(evaluateColumns());
  const [selectedFunctionKind, setSelectedFunctionKind] = useState(functionKind);
  const [rows, setRows] = useState(evaluateRows(selectedFunctionKind));

  const extendDefinitionBasedOnFunctionKind = useCallback(
    (definition: FunctionProps, functionKind: FunctionKind) => {
      switch (functionKind) {
        case FunctionKind.Java: {
          const contextProps = _.first(rows)?.entryExpression as ContextProps;
          const className =
            (_.nth(contextProps.contextEntries, 0)?.entryExpression as LiteralExpressionProps)?.content || "";
          const methodName =
            (_.nth(contextProps.contextEntries, 1)?.entryExpression as LiteralExpressionProps)?.content || "";
          return _.extend(definition, { class: className, method: methodName });
        }
        case FunctionKind.Pmml: {
          const contextProps = _.first(rows)?.entryExpression as ContextProps;
          const documentValue =
            (_.nth(contextProps.contextEntries, 0)?.entryExpression as PMMLLiteralExpressionProps)?.selected || "";
          const modelValue =
            documentValue === document.current
              ? _.includes(
                  (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.getOptions(),
                  (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.selected
                )
                ? (_.nth(contextProps.contextEntries, 1)?.entryExpression as PMMLLiteralExpressionProps)?.selected
                : ""
              : "";
          const documentHasBeenChanged = documentValue !== document.current;
          const modelHasBeenChanged = modelValue !== model.current;
          document.current = documentValue;
          model.current = modelValue;
          if (documentHasBeenChanged) {
            setParameters([]);
          }
          if (modelHasBeenChanged) {
            const parametersFromPmmlProps = extractParametersFromPmmlProps(props as PmmlFunctionProps);
            if (!_.isEmpty(parametersFromPmmlProps)) {
              setParameters(parametersFromPmmlProps);
            }
          }
          return _.extend(definition, { document: documentValue, model: modelValue });
        }
        case FunctionKind.Feel:
        default: {
          return _.extend(definition, { expression: _.first(rows)?.entryExpression as ExpressionProps });
        }
      }
    },
    [extractParametersFromPmmlProps, props, rows]
  );

  const spreadFunctionExpressionDefinition = useCallback(() => {
    const [expressionColumn] = columns.current;

    const updatedDefinition: FunctionProps = extendDefinitionBasedOnFunctionKind(
      {
        uid: props.uid,
        logicType: props.logicType,
        name: expressionColumn.accessor,
        dataType: expressionColumn.dataType,
        functionKind: selectedFunctionKind,
        formalParameters: parameters,
        ...(width.current > DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH ? { parametersWidth: width.current } : {}),
      },
      selectedFunctionKind
    );
    props.isHeadless
      ? props.onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]))
      : window.beeApi?.broadcastFunctionExpressionDefinition?.(updatedDefinition);
  }, [extendDefinitionBasedOnFunctionKind, parameters, props, selectedFunctionKind]);

  const getHeaderVisibility = useCallback(() => {
    return props.isHeadless ? TableHeaderVisibility.LastLevel : TableHeaderVisibility.Full;
  }, [props.isHeadless]);

  const onFunctionKindSelect = useCallback(
    (itemId: string) => {
      const kind = itemId as FunctionKind;
      setSelectedFunctionKind(kind);
      // Resetting table content, every time function kind gets selected
      setRows([{ entryExpression: { logicType: LogicType.Undefined } }]);
      // Need to wait for the next rendering cycle before setting the correct table rows, based on function kind
      setTimeout(() => {
        setRows(evaluateRows(kind));
      }, 0);
    },
    [evaluateRows]
  );

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      const label = expressionColumn.label as string;
      const typeRef = expressionColumn.dataType;
      name.current = label;
      dataType.current = typeRef;
      props.onUpdatingNameAndDataType?.(label, typeRef);
      width.current = _.find(expressionColumn.columns, { accessor: "parameters" })?.width as number;
      const [updatedExpressionColumn] = columns.current;
      updatedExpressionColumn.label = label;
      updatedExpressionColumn.accessor = expressionColumn.accessor;
      updatedExpressionColumn.dataType = typeRef;
      spreadFunctionExpressionDefinition();
    },
    [columns, props, spreadFunctionExpressionDefinition]
  );

  useEffect(() => {
    /** Everytime the list of parameters or the function definition change, we need to spread expression's updated definition */
    spreadFunctionExpressionDefinition();
  }, [rows, spreadFunctionExpressionDefinition]);

  useEffect(() => {
    columns.current = evaluateColumns();
    // Watching for changes of the parameters, in order to update the columns passed to the table
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [parameters]);

  const resetRowCustomFunction = useCallback((row) => {
    setSelectedFunctionKind(FunctionKind.Feel);
    return resetEntry(row);
  }, []);

  return (
    <div className={`function-expression ${props.uid}`}>
      <Table
        handlerConfiguration={[
          {
            group: _.upperCase(i18n.function),
            items: [{ name: i18n.rowOperations.clear, type: TableOperation.RowClear }],
          },
        ]}
        columns={columns.current}
        onColumnsUpdate={onColumnsUpdate}
        rows={rows}
        onRowsUpdate={setRows}
        headerLevels={1}
        headerVisibility={getHeaderVisibility()}
        controllerCell={
          <FunctionKindSelector
            selectedFunctionKind={selectedFunctionKind}
            onFunctionKindSelect={onFunctionKindSelect}
          />
        }
        defaultCell={{ parameters: ContextEntryExpressionCell }}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
