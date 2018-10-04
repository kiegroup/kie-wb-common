/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.processing.layout;

public final class Graphs {

    /*
     * 2 crossings
     *       A           B
     *      /\\         /
     *     /  \ -------+ --
     *    / /-- +-----/    \
     *   / /     \          \
     *  D         E          F
     * */

    public static final String[][] SimpleAcyclic = {
            {"A", "B"},
            {"A", "C"},
            {"C", "B"}};

    public static final String[][] SimpleCyclic = {
            {"A", "B"},
            {"B", "C"},
            {"C", "A"}};

    public static final String[][] CyclicGraph1 = {
            {"A", "B"},
            {"A", "C"},
            {"C", "D"},
            {"D", "B"},
            {"G", "B"},
            {"G", "A"},
            {"G", "H"},
            {"H", "I"},
            {"I", "G"},
            {"G", "J"},
            {"G", "F"},
            {"F", "E"},
            {"E", "A"},
            {"A", "I"}
    };

    public static final String[][] AcyclicGraph1 = {
            {"A", "B"},
            {"A", "C"},
            {"C", "D"},
            {"D", "B"},
            {"G", "B"},
            {"G", "A"},
            {"G", "H"},
            {"H", "I"},
            {"G", "I"},
            {"G", "J"},
            {"G", "F"},
            {"F", "E"},
            {"E", "A"},
            {"A", "I"}
    };


    /*{"M", "S"},
    {"R", "S"},
    {"W", "S"},
    {"X", "S"},*/

    public static final String[][] RealCase0004_Lending = {
            {"D", "B"},
            {"B", "A"},
            {"C", "A"},
            {"E", "C"},
            {"J", "C"},
            {"J", "B"},
            {"F", "C"},
            {"X", "C"},
            {"J", "F"},
            {"O", "J"},
            {"W", "P"},
            {"X", "P"},
            {"X", "J"},
            {"X", "F"},
            {"Q", "K"},
            {"R", "K"},
            {"K", "F"},
            {"L", "F"},
            {"L", "H"},
            {"X", "H"},
            {"S", "L"},
            {"G", "I"},
            {"H", "I"},
            {"M", "I"},
            {"M", "H"},
            {"P", "M"},
            {"X", "M"},
            {"X", "N"},
            {"T", "M"},
            {"U", "N"},
            {"V", "I"},
            {"V", "N"},
            {"V", "M"},
            {"K", "H"},
    };

    public static final String[][] RealCase1 ={
            {"L", "D"},
            {"D", "A"},
            {"D", "B"},
            {"E", "B"},
            {"Y", "T"},
            {"T", "E"},
            {"T", "U"},
            {"Z", "U"},
            {"A1", "U"},
            {"B1", "U"},
            {"C1", "U"},
            {"U", "N"},
            {"U", "O"},
            {"U", "P"},
            {"U", "Q"},
            {"D1", "V"},
            {"V", "Q"},
            {"Q", "E"},
            {"Q", "F"},
            {"Q", "G"},
            {"Q", "H"},
            {"Q", "I"},
            {"Q", "J"},
            {"Q", "K"},
            {"T", "F"},
            {"F", "C"}
    };
}