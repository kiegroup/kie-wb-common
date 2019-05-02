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

package org.kie.workbench.common.stunner.core.marshaller;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.diagram.Metadata;

public class MarshallingRequest<I, M extends Metadata> {

    /**
     * Mode
     * <p>
     * ERROR:
     * - return errors when it has unsupported nodes
     * - error messages
     * AUTO:
     * - try to adapt unsupported nodes do generic ones
     * - warn messages
     * IGNORE:
     * - remove unsupported nodes and relationships to them
     * - warn messages
     */
    public enum Mode {
        ERROR,
        AUTO,
        IGNORE
    }

    private I input;
    private M metadata;
    private Mode mode;

    public MarshallingRequest(I input, M metadata, Mode mode) {
        this.input = input;
        this.metadata = metadata;
        this.mode = mode;
    }

    public I getInput() {
        return input;
    }

    public M getMetadata() {
        return metadata;
    }

    public Mode getMode() {
        return mode;
    }

    public static <I, M extends Metadata> MarshallingRequestBuilder<I, M> builder() {
        return new MarshallingRequestBuilder<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MarshallingRequest{");
        sb.append("input=").append(input);
        sb.append(", metadata=").append(metadata);
        sb.append(", mode=").append(mode);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarshallingRequest)) {
            return false;
        }
        MarshallingRequest<?, ?> that = (MarshallingRequest<?, ?>) o;
        return Objects.equals(getInput(), that.getInput()) &&
                Objects.equals(getMetadata(), that.getMetadata()) &&
                getMode() == that.getMode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInput(), getMetadata(), getMode());
    }

    public static class MarshallingRequestBuilder<I, M extends Metadata> {

        private I input;
        private M metadata;
        private Mode mode;

        public MarshallingRequestBuilder input(I input) {
            this.input = input;
            return this;
        }

        public MarshallingRequestBuilder metadata(M metadata) {
            this.metadata = metadata;
            return this;
        }

        public MarshallingRequestBuilder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public MarshallingRequest build() {
            return new MarshallingRequest(input, metadata, mode);
        }
    }
}
