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
package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = FEELEditor.EDITOR_ID)
public class FEELEditor {

    public static final String EDITOR_ID = "test.FEELEditor";
    private FeelEditorView view;

    @Inject
    public FEELEditor(FeelEditorView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {

        FEELFunctionProvider functionProvider = GWT.create(FEELFunctionProvider.class);
        StringBuilder builder = new StringBuilder();
        for (final FunctionOverrideVariation definition : functionProvider.getDefinitions()) {

            builder.append(definition.getReturnType());
            builder.append(" - ");
            builder.append(definition.toHumanReadableStrings().getTemplate());
            builder.append("\n");
        }
        view.setAvailableMethods(builder.toString());
//        Window.alert("testing " + o.getDefinitions().size());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "FEEL Editor";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    public void onClick(String text) {
    }

    private void c3(BaseNode root, FEEL_1_1Parser parser) {

        CodeCompletionCore core = new CodeCompletionCore(parser, null, null);

        int caretIndex = view.getCaretIndex();

        CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(caretIndex, null);

        List<String> keywords = new ArrayList<>();
        String test = "";
        for (Integer integer : candidates.tokens.keySet()) {
            String displayName = parser.getVocabulary().getDisplayName(integer);
            keywords.add(displayName);
            test = test + "\n " + displayName + " -- " + candidates.tokens.get(integer);
        }
        for (Integer integer : candidates.rules.keySet()) {

        }
        view.setC3("caretIndex [ " + caretIndex + " ] " + core.candidates.toString());
//        view.setC3(test);
    }

    private void dump(final StringBuilder stringBuilder,
                      final ASTNode expr) {
        stringBuilder.append(expr.toString());
        stringBuilder.append("\n");
        for (ASTNode astNode : expr.getChildrenNode()) {
            dump(stringBuilder, astNode);
        }
    }

    public void onChange(String text) {
        final FEEL_1_1Parser parser = FEELParser.parse(null,
                                                       text,
                                                       Collections.emptyMap(),
                                                       Collections.emptyMap(),
                                                       Collections.emptyList(),
                                                       Collections.emptyList(),
                                                       null);

        final ParseTree tree = parser.expression();

        final ASTBuilderVisitor v = new ASTBuilderVisitor(Collections.emptyMap(),
                                                          null);
        final BaseNode expr = v.visit(tree);

//        final String s = expr.getText() + " - " + expr.getResultType() + " - " + expr.toString();
        final StringBuilder stringBuilder = new StringBuilder();
        dump(stringBuilder, expr);

        view.setASTDump(stringBuilder.toString());

        view.setEvaluation(expr.accept(new DMNDTAnalyserValueFromNodeVisitor(Collections.EMPTY_LIST)).toString());

        c3(expr, parser);
    }
}
