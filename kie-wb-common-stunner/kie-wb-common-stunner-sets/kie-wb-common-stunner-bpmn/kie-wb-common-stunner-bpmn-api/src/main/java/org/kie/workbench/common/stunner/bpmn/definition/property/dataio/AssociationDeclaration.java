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

package org.kie.workbench.common.stunner.bpmn.definition.property.dataio;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AssociationDeclaration {

    public static AssociationDeclaration fromString(String encoded) {
        return AssociationParser.parse(encoded);
    }

    public enum Direction {
        Input("[din]"),
        Output("[dout]");

        private final String prefix;

        Direction(String prefix) {
            this.prefix = prefix;
        }

        public String prefix() {
            return prefix;
        }
    }

    public enum Type {
        FromTo("="),
        SourceTarget("->");

        private final String op;

        Type(String op) {
            this.op = op;
        }

        public String op() {
            return op;
        }
    }

    private Direction direction;
    private Type type;
    private String left;
    private String right;

    public AssociationDeclaration() {
    }

    public AssociationDeclaration(Direction direction, Type type, String left, String right) {
        this.direction = direction;
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return direction.prefix() + left + type.op() + right;
    }
}

class AssociationParser {

    public static AssociationDeclaration parse(String encoded) {
        for (AssociationDeclaration.Direction direction : AssociationDeclaration.Direction.values()) {
            if (encoded.startsWith(direction.prefix())) {
                String rest = encoded.substring(direction.prefix().length());
                return parseAssociation(direction, rest);
            }
        }

        throw new IllegalArgumentException("Cannot parse " + encoded);
    }

    static AssociationDeclaration parseAssociation(AssociationDeclaration.Direction direction, String rest) {
        for (AssociationDeclaration.Type type : AssociationDeclaration.Type.values()) {
            if (rest.contains(type.op())) {
                String[] association = rest.split(type.op());
                return new AssociationDeclaration(direction, type, association[0], association[1]);
            }
        }

        throw new IllegalArgumentException("Cannot parse " + rest);
    }
}
