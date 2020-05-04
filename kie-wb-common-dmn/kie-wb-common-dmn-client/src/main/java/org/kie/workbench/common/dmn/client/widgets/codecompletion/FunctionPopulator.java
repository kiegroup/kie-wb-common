/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.widgets.codecompletion;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.types.FunctionSymbol;

public class FunctionPopulator
        extends Populator {

    private Scope builtInScope;
    private Map<String, FunctionDefinition> functionDefinitions = new HashMap<>();

    public FunctionPopulator(final Scope builtInScope) {
        this.builtInScope = builtInScope;
    }

    private void addFunctionDefinition(final FunctionDefinition functionDefinition) {
        functionDefinitions.put(functionDefinition.getName(),
                                functionDefinition);
    }

    private void makeFunctionDefinitions(final JSONArray suggestionTypes) {

//        addFunctionDefinition(new FunctionDefinition("abs",
//                                                     Type.NUMBER,
//                                                     new FunctionOverrideVariation(new Parameter("n", Type.DURATION)),
//                                                     new FunctionOverrideVariation(new Parameter("n", Type.PERIOD)),
//                                                     new FunctionOverrideVariation(new Parameter("n", Type.NUMBER)))
//        );
//        addFunctionDefinition(new FunctionDefinition("after",
//                                                     Type.BOOLEAN,
//                                                     new FunctionOverrideVariation(new Parameter("value1", Type.COMPARABLE),
//                                                                                   new Parameter("value2", Type.COMPARABLE)),
//                                                     new FunctionOverrideVariation(new Parameter("value", Type.COMPARABLE),
//                                                                                   new Parameter("range", Type.RANGE)),
//                                                     new FunctionOverrideVariation(new Parameter("range", Type.RANGE),
//                                                                                   new Parameter("value", Type.COMPARABLE)),
//                                                     new FunctionOverrideVariation(new Parameter("range1", Type.RANGE),
//                                                                                   new Parameter("range2", Type.RANGE))
//        ));

        push(suggestionTypes, getFunctionSuggestion("abs(duration)", "abs($1)"));
        push(suggestionTypes, getFunctionSuggestion("abs(number)", "abs($1)"));

        push(suggestionTypes, getFunctionSuggestion("after(range, value)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("after(range1, range2)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("after(value, range)", "after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("after(value1, value2)", "after($1, $2)"));

        push(suggestionTypes, getFunctionSuggestion("all(b)", "all($1)"));
        push(suggestionTypes, getFunctionSuggestion("all(list)", "all($1)"));
        push(suggestionTypes, getFunctionSuggestion("any(b)", "any($1)"));
        push(suggestionTypes, getFunctionSuggestion("any(list)", "any($1)"));
        push(suggestionTypes, getFunctionSuggestion("append(list, item)", "append($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(range, value)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(range1, range2)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(value, range)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("before(value1, value2)", "before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("ceiling(n)", "ceiling($1)"));
        push(suggestionTypes, getFunctionSuggestion("code(value)", "code($1)"));
        push(suggestionTypes, getFunctionSuggestion("coincides(range1, range2)", "coincides($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("coincides(value1, value2)", "coincides($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("concatenate(list)", "concatenate($1)"));
        push(suggestionTypes, getFunctionSuggestion("contains(string, match)", "contains($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("count(c)", "count($1)"));
        push(suggestionTypes, getFunctionSuggestion("count(list)", "count($1)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(date, time)", "date and time($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(from)", "date and time($1)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(year, month, day, hour, minute, second)", "date and time($1, $2, $3, $4, $5, $6)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(year, month, day, hour, minute, second, hour offset)", "date and time($1, $2, $3, $4, $5, $6, $7)"));
        push(suggestionTypes, getFunctionSuggestion("date and time(year, month, day, hour, minute, second, timezone)", "date and time($1, $2, $3, $4, $5, $6, $7)"));
        push(suggestionTypes, getFunctionSuggestion("date(from)", "date($1)"));
        push(suggestionTypes, getFunctionSuggestion("date(year, month, day)", "date($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("day of week(date)", "day of week($1)"));
        push(suggestionTypes, getFunctionSuggestion("day of year(date)", "day of year($1)"));
        push(suggestionTypes, getFunctionSuggestion("decimal(n, scale)", "decimal($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("decision table(ctx, outputs, input expression list, input values list, output values, rule list, hit policy, default output value)", "decision table($1, $2, $3, $4, $5, $6, $7, $8)"));
        push(suggestionTypes, getFunctionSuggestion("distinct values(list)", "distinct values($1)"));
        push(suggestionTypes, getFunctionSuggestion("duration(from)", "duration($1)"));
        push(suggestionTypes, getFunctionSuggestion("during(range1, range2)", "during($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("during(value, range)", "during($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("ends with(string, match)", "ends with($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("even(number)", "even($1)"));
        push(suggestionTypes, getFunctionSuggestion("exp(number)", "exp($1)"));
        push(suggestionTypes, getFunctionSuggestion("finished by(range, value)", "finished by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("finished by(range1, range2)", "finished by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("finishes(range1, range2)", "finishes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("finishes(value, range)", "finishes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("flatten(list)", "flatten($1)"));
        push(suggestionTypes, getFunctionSuggestion("floor(n)", "floor($1)"));
        push(suggestionTypes, getFunctionSuggestion("get entries(m)", "get entries($1)"));
        push(suggestionTypes, getFunctionSuggestion("get value(m, key)", "get value($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("includes(range, value)", "includes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("includes(range1, range2)", "includes($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("index of(list, match)", "index of($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("insert before(list, position, newItem)", "insert before($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("invoke(ctx, namespace, model name, decision name, parameters)", "invoke($1, $2, $3, $4, $5)"));
        push(suggestionTypes, getFunctionSuggestion("list contains(list, element)", "list contains($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("log(number)", "log($1)"));
        push(suggestionTypes, getFunctionSuggestion("lower case(string)", "lower case($1)"));
        push(suggestionTypes, getFunctionSuggestion("matches(input, pattern)", "matches($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("matches(input, pattern, flags)", "matches($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("max(c)", "max($1)"));
        push(suggestionTypes, getFunctionSuggestion("max(list)", "max($1)"));
        push(suggestionTypes, getFunctionSuggestion("mean(list)", "mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("mean(n)", "mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("median(list)", "median($1)"));
        push(suggestionTypes, getFunctionSuggestion("median(n)", "median($1)"));
        push(suggestionTypes, getFunctionSuggestion("meets(range1, range2)", "meets($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("met by(range1, range2)", "met by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("min(c)", "min($1)"));
        push(suggestionTypes, getFunctionSuggestion("min(list)", "min($1)"));
        push(suggestionTypes, getFunctionSuggestion("mode(list)", "mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("mode(n)", "mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("modulo(dividend, divisor)", "modulo($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("month of year(date)", "month of year($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn all(b)", "nn all($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn all(list)", "nn all($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn any(b)", "nn any($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn any(list)", "nn any($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn count(c)", "nn count($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn count(list)", "nn count($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn max(c)", "nn max($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn max(list)", "nn max($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mean(list)", "nn mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mean(n)", "nn mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn median(list)", "nn median($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn median(n)", "nn median($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn min(c)", "nn min($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn min(list)", "nn min($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mode(list)", "nn mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn mode(n)", "nn mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn stddev(list)", "nn stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn stddev(n)", "nn stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn sum(list)", "nn sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("nn sum(n)", "nn sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("not(negand)", "not($1)"));
        push(suggestionTypes, getFunctionSuggestion("now()", "now()"));
        push(suggestionTypes, getFunctionSuggestion("number(from, grouping separator, decimal separator)", "number($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("odd(number)", "odd($1)"));
        push(suggestionTypes, getFunctionSuggestion("overlapped after by(range1, range2)", "overlapped after by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlapped before by(range1, range2)", "overlapped before by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlapped by(range1, range2)", "overlapped by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlaps after(range1, range2)", "overlaps after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlaps before(range1, range2)", "overlaps before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("overlaps(range1, range2)", "overlaps($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("product(list)", "product($1)"));
        push(suggestionTypes, getFunctionSuggestion("product(n)", "product($1)"));
        push(suggestionTypes, getFunctionSuggestion("remove(list, position)", "remove($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("replace(input, pattern, replacement)", "replace($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("replace(input, pattern, replacement, flags)", "replace($1, $2, $3, $4)"));
        push(suggestionTypes, getFunctionSuggestion("reverse(list)", "reverse($1)"));
        push(suggestionTypes, getFunctionSuggestion("sort()", "sort()"));
        push(suggestionTypes, getFunctionSuggestion("sort(ctx, list, precedes)", "sort($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("sort(list)", "sort($1)"));
        push(suggestionTypes, getFunctionSuggestion("split(string, delimiter)", "split($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("split(string, delimiter, flags)", "split($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("sqrt(number)", "sqrt($1)"));
        push(suggestionTypes, getFunctionSuggestion("started by(range, value)", "started by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("started by(range1, range2)", "started by($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts with(string, match)", "starts with($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts(range1, range2)", "starts($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts(value, range)", "starts($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("stddev(list)", "stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("stddev(n)", "stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("string length(string)", "string length($1)"));
        push(suggestionTypes, getFunctionSuggestion("string(from)", "string($1)"));
        push(suggestionTypes, getFunctionSuggestion("string(mask, p)", "string($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("sublist(list, start position)", "sublist($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("sublist(list, start position, length)", "sublist($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("substring after(string, match)", "substring after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring before(string, match)", "substring before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring(string, start position)", "substring($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring(string, start position, length)", "substring($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("sum(list)", "sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("sum(n)", "sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("time(from)", "time($1)"));
        push(suggestionTypes, getFunctionSuggestion("time(hour, minute, second)", "time($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("time(hour, minute, second, offset)", "time($1, $2, $3, $4)"));
        push(suggestionTypes, getFunctionSuggestion("today()", "today()"));
        push(suggestionTypes, getFunctionSuggestion("union(list)", "union($1)"));
        push(suggestionTypes, getFunctionSuggestion("upper case(string)", "upper case($1)"));
        push(suggestionTypes, getFunctionSuggestion("week of year(date)", "week of year($1)"));
        push(suggestionTypes, getFunctionSuggestion("years and months duration(from, to)", "years and months duration($1, $2)"));
    }

    public void populate(final JSONArray suggestionTypes) {
        makeFunctionDefinitions(suggestionTypes);

        for (final Map.Entry<String, Symbol> entry : builtInScope.getSymbols().entrySet()) {
            if (entry.getValue() instanceof FunctionSymbol) {
                if (functionDefinitions.containsKey(entry.getKey())) {
                    for (final FunctionDefinitionStrings functionDefStrings : functionDefinitions.get(entry.getKey()).toHumanReadableStrings()) {

                        push(suggestionTypes,
                             getFunctionSuggestion(functionDefStrings.getHumanReadable(),
                                                   functionDefStrings.getTemplate()));

                        LOGGER.log(Level.SEVERE, entry.getKey());
                        System.out.println("# FUNCTION NAME #  " + entry.getKey());
                    }
                }
            }
        }
    }

    JSONValue getFunctionSuggestion(final String label,
                                    final String insertText) {

        final JSONObject suggestion = makeJSONObject();
        final int completionItemKindFunction = 1;
        final int completionItemInsertTextRuleInsertAsSnippet = 4;

        suggestion.put(KIND_KEY, makeJSONNumber(completionItemKindFunction));
        suggestion.put(INSERT_TEXT_RULES_KEY, makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet));
        suggestion.put(LABEL_KEY, makeJSONString(label));
        suggestion.put(INSERT_TEXT_KEY, makeJSONString(insertText));

        return suggestion;
    }
}
