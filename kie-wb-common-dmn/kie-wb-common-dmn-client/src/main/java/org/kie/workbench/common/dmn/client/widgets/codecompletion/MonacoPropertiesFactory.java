/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.vmware.antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.uberfire.client.views.pfly.monaco.jsinterop.ITextModel;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoLanguages.ProvideCompletionItemsFunction;
import org.uberfire.client.views.pfly.monaco.jsinterop.Position;

import static org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer.FEEL_RESERVED_KEYWORDS;

public class MonacoPropertiesFactory
        extends Populator {

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.ieditorconstructionoptions.html
     */
    public JavaScriptObject getConstructionOptions() {

        final JSONObject options = makeJSONObject();
        final JSONObject scrollbar = makeJSONObject();
        final JSONObject miniMap = makeJSONObject();

        options.put("language", makeJSONString(FEEL_LANGUAGE_ID));
        options.put("theme", makeJSONString(FEEL_THEME_ID));

        options.put("renderLineHighlight", makeJSONString("none"));
        options.put("lineNumbers", makeJSONString("off"));

        options.put("fontSize", makeJSONNumber(12));
        options.put("lineNumbersMinChars", makeJSONNumber(1));
        options.put("lineDecorationsWidth", makeJSONNumber(1));

        options.put("overviewRulerBorder", makeJSONBoolean(false));
        options.put("scrollBeyondLastLine", makeJSONBoolean(false));
        options.put("snippetSuggestions", makeJSONBoolean(false));
        options.put("useTabStops", makeJSONBoolean(false));
        options.put("contextmenu", makeJSONBoolean(false));
        options.put("folding", makeJSONBoolean(false));
        miniMap.put("enabled", makeJSONBoolean(false));
        scrollbar.put("useShadows", makeJSONBoolean(false));

        options.put("automaticLayout", makeJSONBoolean(true));
        options.put("renderWhitespace", makeJSONBoolean(true));
        options.put("hideCursorInOverviewRuler", makeJSONBoolean(true));

        options.put("scrollbar", scrollbar);
        options.put("minimap", miniMap);

        return options.getJavaScriptObject();
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.istandalonethemedata.html
     */
    public JavaScriptObject getThemeData() {

        final JSONObject themeDefinition = makeJSONObject();
        final JSONObject colors = makeJSONObject();
        final JSONString colorHEXCode = makeJSONString("#000000");
        final JSONString base = makeJSONString("vs");
        final JSONBoolean inherit = makeJSONBoolean(false);
        final JSONArray rules = getRules();

        colors.put("editorLineNumber.foreground", colorHEXCode);
        themeDefinition.put("base", base);
        themeDefinition.put("inherit", inherit);
        themeDefinition.put("rules", rules);
        themeDefinition.put("colors", colors);

        return themeDefinition.getJavaScriptObject();
    }

    public JSONArray getRules() {

        final JSONObject rule1 = makeJSONObject();
        final JSONObject rule2 = makeJSONObject();
        final JSONObject rule3 = makeJSONObject();
        final JSONObject rule4 = makeJSONObject();
        final JSONObject rule5 = makeJSONObject();
        final JSONArray rules = makeJSONArray();

        rule1.put("token", makeJSONString("feel-keyword"));
        rule1.put("foreground", makeJSONString("26268C"));
        rule1.put("fontStyle", makeJSONString("bold"));

        rule2.put("token", makeJSONString("feel-numeric"));
        rule2.put("foreground", makeJSONString("3232E7"));

        rule3.put("token", makeJSONString("feel-boolean"));
        rule3.put("foreground", makeJSONString("26268D"));
        rule3.put("fontStyle", makeJSONString("bold"));

        rule4.put("token", makeJSONString("feel-string"));
        rule4.put("foreground", makeJSONString("2A9343"));
        rule4.put("fontStyle", makeJSONString("bold"));

        rule5.put("token", makeJSONString("feel-function"));
        rule5.put("foreground", makeJSONString("3232E8"));

        push(rules, rule1);
        push(rules, rule2);
        push(rules, rule3);
        push(rules, rule4);
        push(rules, rule5);

        return rules;
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionitemprovider.html
     */
    public JavaScriptObject getCompletionItemProvider(final MonacoFEELVariableSuggestions variableSuggestions) {
        return makeJavaScriptObject("provideCompletionItems", makeJSONObject(getProvideCompletionItemsFunction(variableSuggestions)));
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.completionlist.html
     */
    ProvideCompletionItemsFunction getProvideCompletionItemsFunction(final MonacoFEELVariableSuggestions variableSuggestions) {
        return (ITextModel model, Position position) -> {

            final JSONObject suggestions = makeJSONObject();
            suggestions.put("suggestions", getSuggestions(variableSuggestions, model, position));
            return suggestions.getJavaScriptObject();
        };
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.imonarchlanguage.html
     */
    public JavaScriptObject getLanguageDefinition() {
        return makeJavaScriptObject("tokenizer", getTokenizer());
    }

    public JSONValue getTokenizer() {
        final JSONObject tokenizer = makeJSONObject();
        tokenizer.put("root", getRoot());
        return tokenizer;
    }

    /*
     * This methods returns a collection of IShortMonarchLanguageRule1[JsRegExpt, IMonarchLanguageAction]
     * https://microsoft.github.io/monaco-editor/api/modules/monaco.languages.html#ishortmonarchlanguagerule1
     * Each item from the 'root' array represents a rule:
     * - 1st rule     - occurrences of booleans' are marked as "feel-boolean"
     * - 2nd rule     - occurrences of numbers are marked as "feel-numeric"
     * - 3rd rule     - occurrences of strings are marked as "feel-string"
     * - 4th rule     - occurrences of FEEL functions calls are marked as "feel-function"
     * - 5th/6th rule - occurrences of FEEL keywords(if, then, else, for, in, return) are marked as "feel-keyword"
     */
    public JSONArray getRoot() {
        final JSONArray root = makeJSONArray();
        push(root, row("(?:(\\btrue\\b)|(\\bfalse\\b))", "feel-boolean"));
        push(root, row("[0-9]+", "feel-numeric"));
        push(root, row("(?:\\\"(?:.*?)\\\")", "feel-string"));
        push(root, row("(?:(?:[a-z ]+\\()|(?:\\()|(?:\\)))", "feel-function"));
        push(root, row("(?:(\\b" + String.join("\\b)|(\\b", FEEL_RESERVED_KEYWORDS) + "\\b))", "feel-keyword"));
        return root;
    }

    JSONArray getSuggestions(final MonacoFEELVariableSuggestions variableSuggestions,
                             final ITextModel model,
                             final Position position) {

        final JSONArray suggestionTypes = makeJSONArray();

        final FEEL_1_1Parser parser = FEELParser.parse(null,
                                                       model.getValue(),
                                                       Collections.emptyMap(),
                                                       Collections.emptyMap(),
                                                       Collections.emptyList(),
                                                       Collections.emptyList(),
                                                       null);

        final CodeCompletionCore.CandidatesCollection candidates = getCandidatesCollection(model,
                                                                                           position,
                                                                                           parser);

        for (final Integer ruleId : candidates.rules.keySet()) {
            if (ruleId == FEEL_1_1Parser.RULE_functionDefinition) {
                listFunctions(suggestionTypes,
                              parser.getHelper().getSymbolTable().getBuiltInScope());
                listFunctions(suggestionTypes,
                              parser.getHelper().getSymbolTable().getGlobalScope());
            }
        }

        populateKeywordSuggestions(suggestionTypes);

        for (final Integer tokenId : candidates.tokens.keySet()) {
            final String displayName = getDisplayName(parser,
                                                      tokenId);

            if (isSkippable(displayName)) {
                // Skip
            } else if (Objects.equals(displayName, "Identifier")) {
                populateVariableSuggestions(variableSuggestions,
                                            suggestionTypes);
            } else {
                push(suggestionTypes,
                     getKeywordSuggestion(displayName));
            }
        }
        return suggestionTypes;
    }

    private CodeCompletionCore.CandidatesCollection getCandidatesCollection(final ITextModel model,
                                                                            final Position position,
                                                                            final FEEL_1_1Parser parser) {
        final ParseTree tree = parser.expression();

        final ASTBuilderVisitor v = new ASTBuilderVisitor(Collections.emptyMap(),
                                                          null);
        final BaseNode expr = v.visit(tree);

        final HashSet<Integer> preferredRules = new HashSet<>();
        preferredRules.add(FEEL_1_1Parser.RULE_functionDefinition);

        final CodeCompletionCore codeCompletionCore = new CodeCompletionCore(parser,
                                                                             preferredRules,
                                                                             null);

        final int caretIndex = getCaretPosition(model, position, tree);

        CodeCompletionCore.CandidatesCollection candidates = codeCompletionCore.collectCandidates(caretIndex,
                                                                                                  null);

        return candidates;
    }

    private boolean isSkippable(final String displayName) {
        return Objects.equals(displayName, "IntegerLiteral")
                || Objects.equals(displayName, "FloatingPointLiteral")
                || Objects.equals(displayName, "StringLiteral")
                || Objects.equals(displayName, "BooleanLiteral");
    }

    private int getCaretPosition(final ITextModel model,
                                 final Position position,
                                 final ParseTree parseTree) {

        if (parseTree instanceof TerminalNode) {
            int start = ((TerminalNode) parseTree).getSymbol().getCharPositionInLine();
            int stop = ((TerminalNode) parseTree).getSymbol().getCharPositionInLine() + parseTree.getText().length();
            if (((TerminalNode) parseTree).getSymbol().getLine() == position.getLineNumber() && start <= position.getColumn() && stop >= position.getColumn()) {
                return ((TerminalNode) parseTree).getSymbol().getTokenIndex();
            } else {
                return 0;
            }
        } else {
            for (int i = 0; i < parseTree.getChildCount(); i++) {
                int index = getCaretPosition(model, position, parseTree.getChild(i));
                if (index != -1) {
                    return 0;
                }
            }
            return 0;
        }
    }

    protected void listFunctions(final JSONArray suggestionTypes,
                                 final Scope builtInScope) {
        new FunctionPopulator(builtInScope).populate(suggestionTypes);
    }

    private String getDisplayName(final FEEL_1_1Parser parser,
                                  final Integer tokenId) {
        final String displayName = parser.getVocabulary().getDisplayName(tokenId);
        if (displayName.startsWith("'") && displayName.endsWith("'")) {
            return displayName.substring(1, displayName.length() - 1);
        } else {
            return displayName;
        }
    }

    private void populateKeywordSuggestions(JSONArray suggestionArray) {
        FEEL_RESERVED_KEYWORDS.forEach(reservedKeyword -> push(suggestionArray, getKeywordSuggestion(reservedKeyword)));
    }

    private void populateVariableSuggestions(final MonacoFEELVariableSuggestions variableSuggestions,
                                             final JSONArray suggestionArray) {
        variableSuggestions
                .getSuggestions()
                .forEach(variable -> push(suggestionArray, getVariableSuggestion(variable)));
    }

    JSONValue getKeywordSuggestion(final String keyword) {
        final JSONObject suggestion = makeJSONObject();
        final int completionItemKindKeyword = 17;
        final int completionItemInsertTextRuleInsertAsSnippet = 4;
        final JSONString keywordSuggestion = makeJSONString(keyword);

        suggestion.put(KIND_KEY, makeJSONNumber(completionItemKindKeyword));
        suggestion.put(INSERT_TEXT_RULES_KEY, makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet));
        suggestion.put(LABEL_KEY, keywordSuggestion);
        suggestion.put(INSERT_TEXT_KEY, keywordSuggestion);

        return suggestion;
    }

    JSONValue getVariableSuggestion(final String variable) {

        final JSONObject suggestion = makeJSONObject();
        final int completionItemKindVariable = 4;
        final int completionItemInsertTextRuleInsertAsSnippet = 4;
        final JSONString variableSuggestion = makeJSONString(variable);

        suggestion.put(KIND_KEY, makeJSONNumber(completionItemKindVariable));
        suggestion.put(INSERT_TEXT_RULES_KEY, makeJSONNumber(completionItemInsertTextRuleInsertAsSnippet));
        suggestion.put(LABEL_KEY, variableSuggestion);
        suggestion.put(INSERT_TEXT_KEY, variableSuggestion);

        return suggestion;
    }

    public JSONArray row(final String pattern,
                         final String name) {
        final JSONArray row = makeJSONArray();
        row.set(0, makeJSONObject(makeRegExp(pattern)));
        row.set(1, makeJSONString(name));
        return row;
    }

    /*
     * This method returns a JavaScript object with properties specified here:
     * https://microsoft.github.io/monaco-editor/api/interfaces/monaco.languages.ilanguageextensionpoint.html
     */
    public JavaScriptObject getLanguage() {
        return makeJavaScriptObject("id", makeJSONString(FEEL_LANGUAGE_ID));
    }
}
