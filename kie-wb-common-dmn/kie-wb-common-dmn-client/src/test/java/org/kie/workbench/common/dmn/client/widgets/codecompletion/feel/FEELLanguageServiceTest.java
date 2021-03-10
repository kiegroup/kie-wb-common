/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.feel;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.api.Parameter;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class FEELLanguageServiceTest {

    private FEELLanguageService service;

    private List<FunctionOverrideVariation> functionOverrideVariations;

    @Before
    public void setup() {
        service = spy(new FEELLanguageService(new TypeStackUtils()));
        functionOverrideVariations = getFunctionOverrideVariations();

        doReturn(functionOverrideVariations).when(service).getFunctionOverrideVariations();
    }

    @Test
    public void testGetCandidatesForNumberScenario() {

        final String expression = "2 + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        assertCandidate("Decision-2", "Decision-2", CompletionItemKind.Variable, actualCandidates.get(0));
        assertCandidate("Decision-4", "Decision-4", CompletionItemKind.Variable, actualCandidates.get(1));
        assertCandidate("abs(duration)", "abs($1)", CompletionItemKind.Function, actualCandidates.get(2));
        assertCandidate("abs(number)", "abs($1)", CompletionItemKind.Function, actualCandidates.get(3));
        assertCandidate("sum(list)", "sum($1)", CompletionItemKind.Function, actualCandidates.get(4));
        assertCandidate("not", "not", CompletionItemKind.Keyword, actualCandidates.get(5));
        assertCandidate("for", "for", CompletionItemKind.Keyword, actualCandidates.get(6));
        assertCandidate("if", "if", CompletionItemKind.Keyword, actualCandidates.get(7));
        assertCandidate("some", "some", CompletionItemKind.Keyword, actualCandidates.get(8));
        assertCandidate("every", "every", CompletionItemKind.Keyword, actualCandidates.get(9));
        assertCandidate("function", "function", CompletionItemKind.Keyword, actualCandidates.get(10));
    }

    @Test
    public void testGetCandidatesForStringScenario() {

        final String expression = "\"\" + |";
        final Position position = new Position(1, expression.indexOf("|"));
        final String expressionToParse = expression.replace("|", "");
        final List<Variable> variables = asList(new Variable("Decision-1", BuiltInType.STRING),
                                                new Variable("Decision-2", BuiltInType.NUMBER),
                                                new Variable("Decision-3", BuiltInType.BOOLEAN),
                                                new Variable("Decision-4", BuiltInType.NUMBER));

        final List<Candidate> actualCandidates = service.getCandidates(expressionToParse, variables, position);

        assertCandidate("Decision-1", "Decision-1", CompletionItemKind.Variable, actualCandidates.get(0));
        assertCandidate("string(number)", "string($1)", CompletionItemKind.Function, actualCandidates.get(1));
        assertCandidate("not", "not", CompletionItemKind.Keyword, actualCandidates.get(2));
        assertCandidate("for", "for", CompletionItemKind.Keyword, actualCandidates.get(3));
        assertCandidate("if", "if", CompletionItemKind.Keyword, actualCandidates.get(4));
        assertCandidate("some", "some", CompletionItemKind.Keyword, actualCandidates.get(5));
        assertCandidate("every", "every", CompletionItemKind.Keyword, actualCandidates.get(6));
        assertCandidate("function", "function", CompletionItemKind.Keyword, actualCandidates.get(7));
    }

    @Test
    public void testGetTypeSingleLine() {
        assertGetType("|(1 + (2 + (\"\" + (4 + 5))))", BuiltInType.UNKNOWN);
        assertGetType("(1| + (2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 |+ (2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 +| (2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + |(2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (|2 + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2| + (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 |+ (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 +| (\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + |(\"\" + (4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (|\"\" + (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\"| + (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" |+ (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" +| (4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" + |(4 + 5))))", BuiltInType.STRING);
        assertGetType("(1 + (2 + (\"\" + (|4 + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4| + 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 |+ 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 +| 5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + |5))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5|))))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5)|)))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5))|))", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5)))|)", BuiltInType.NUMBER);
        assertGetType("(1 + (2 + (\"\" + (4 + 5))))|", BuiltInType.NUMBER);
    }

    @Test
    public void testGetTypeMultiLine() {
        assertGetType("" +
                              "(                    \n" +
                              "  1| + (             \n" +
                              "    2 + (            \n" +
                              "      \"\" + (4 + 5) \n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.NUMBER);
        assertGetType("" +
                              "(                    \n" +
                              "  1 + (              \n" +
                              "    2| + (           \n" +
                              "      \"\" + (4 + 5) \n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.NUMBER);
        assertGetType("" +
                              "(                    \n" +
                              "  1 + (              \n" +
                              "    2 + (            \n" +
                              "      \"\"| + (4 + 5)\n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.STRING);
        assertGetType("" +
                              "(                    \n" +
                              "  1 + (              \n" +
                              "    2 + (            \n" +
                              "      \"\" + (4| + 5)\n" +
                              "    )                \n" +
                              "  )                  \n" +
                              ")                    \n", BuiltInType.NUMBER);
    }

    @Test
    public void testLiteralTypes() {
        assertGetType("\"\"           |", BuiltInType.STRING);
        assertGetType("[1, 2, 3]      |", BuiltInType.LIST);
        assertGetType("2              |", BuiltInType.NUMBER);
        assertGetType("false          |", BuiltInType.BOOLEAN);
        assertGetType("[1..10]        |", BuiltInType.RANGE);
        assertGetType("function() { } |", BuiltInType.FUNCTION);
        assertGetType("\"\" + 2       |", BuiltInType.UNKNOWN);
    }

    private void assertCandidate(final String expectedLabel,
                                 final CompletionItemKind expectedKind,
                                 final Candidate candidate) {
        assertEquals(expectedLabel, candidate.getLabel());
        assertEquals(expectedKind, candidate.getKind());
    }

    private void assertCandidate(final String expectedLabel,
                                 final String expectedInsertText,
                                 final CompletionItemKind expectedKind,
                                 final Candidate candidate) {
        assertEquals(expectedInsertText, candidate.getInsertText());
        assertCandidate(expectedLabel, expectedKind, candidate);
    }

    private void assertGetType(final String expression,
                               final BuiltInType expected) {

        final String cursor = "|";
        final int line = expression.substring(0, expression.indexOf(cursor)).split("\n").length;
        final int column = expression.split("\n")[line - 1].indexOf(cursor);
        final Position position = new Position(line, column);
        final String expressionToParse = expression.replace(cursor, "");
        final BaseNode astNode = getASTNode(expressionToParse);

        final Type actual = service.getType(astNode, position);

        assertEquals(expected, actual);
    }

    private BaseNode getASTNode(final String expressionToParse) {
        final FEEL_1_1Parser parser = service.getParser(expressionToParse);
        final ParseTree parseTree = parser.expression();
        return service.getASTNode(parseTree);
    }

    private List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        return asList(
                new FunctionOverrideVariation(BuiltInType.NUMBER, "abs", new Parameter("duration", BuiltInType.DURATION)),
                new FunctionOverrideVariation(BuiltInType.NUMBER, "abs", new Parameter("number", BuiltInType.NUMBER)),
                new FunctionOverrideVariation(BuiltInType.NUMBER, "sum", new Parameter("list", BuiltInType.LIST)),
                new FunctionOverrideVariation(BuiltInType.STRING, "string", new Parameter("from", BuiltInType.NUMBER))
        );
    }
}
