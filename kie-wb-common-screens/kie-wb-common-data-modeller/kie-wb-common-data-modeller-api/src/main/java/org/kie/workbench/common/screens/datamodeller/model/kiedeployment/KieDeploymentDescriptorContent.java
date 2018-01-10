/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.model.kiedeployment;

import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class KieDeploymentDescriptorContent {

    private RuntimeStrategy runtimeStrategy;
    private String persistenceUnitName;
    private PersistenceMode persistenceMode;
    private String auditPersistenceUnitName;
    private AuditMode auditMode;

    private List<BlergsModel> marshallingStrategies;
    private List<BlergsModel> eventListeners;
    private List<BlergsModel> globals;
    private List<BlergsModel> requiredRoles;

    public RuntimeStrategy getRuntimeStrategy() {
        return runtimeStrategy;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public PersistenceMode getPersistenceMode() {
        return persistenceMode;
    }

    public String getAuditPersistenceUnitName() {
        return auditPersistenceUnitName;
    }

    public AuditMode getAuditMode() {
        return auditMode;
    }

    public List<BlergsModel> getMarshallingStrategies() {
        return marshallingStrategies;
    }

    public List<BlergsModel> getEventListeners() {
        return eventListeners;
    }

    public List<BlergsModel> getGlobals() {
        return globals;
    }

    public List<BlergsModel> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRuntimeStrategy(final RuntimeStrategy runtimeStrategy) {
        this.runtimeStrategy = runtimeStrategy;
    }

    public void setPersistenceUnitName(final String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public void setPersistenceMode(final PersistenceMode persistenceMode) {
        this.persistenceMode = persistenceMode;
    }

    public void setAuditPersistenceUnitName(final String auditPersistenceUnitName) {
        this.auditPersistenceUnitName = auditPersistenceUnitName;
    }

    public void setAuditMode(final AuditMode auditMode) {
        this.auditMode = auditMode;
    }

    public void setMarshallingStrategies(final List<BlergsModel> marshallingStrategies) {
        this.marshallingStrategies = marshallingStrategies;
    }

    public void setEventListeners(final List<BlergsModel> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public void setGlobals(final List<BlergsModel> globals) {
        this.globals = globals;
    }

    public void setRequiredRoles(final List<BlergsModel> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    @Portable
    public static class BlergsModel {

        private String id;
        private Resolver resolver;
        private List<Parameter> parameters;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public Resolver getResolver() {
            return resolver;
        }

        public void setResolver(final Resolver resolver) {
            this.resolver = resolver;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public void setParameters(final List<Parameter> parameters) {
            this.parameters = parameters;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KieDeploymentDescriptorContent that = (KieDeploymentDescriptorContent) o;
        return Objects.equals(runtimeStrategy, that.runtimeStrategy) &&
                Objects.equals(persistenceUnitName, that.persistenceUnitName) &&
                Objects.equals(persistenceMode, that.persistenceMode) &&
                Objects.equals(auditPersistenceUnitName, that.auditPersistenceUnitName) &&
                Objects.equals(auditMode, that.auditMode) &&
                Objects.equals(marshallingStrategies, that.marshallingStrategies) &&
                Objects.equals(eventListeners, that.eventListeners) &&
                Objects.equals(globals, that.globals) &&
                Objects.equals(requiredRoles, that.requiredRoles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(runtimeStrategy,
                            persistenceUnitName,
                            persistenceMode,
                            auditPersistenceUnitName,
                            auditMode,
                            marshallingStrategies,
                            eventListeners,
                            globals,
                            requiredRoles);
    }

    @Portable
    public enum RuntimeStrategy {
        SINGLETON,
        PER_REQUEST,
        PER_PROCESS_INSTANCE
    }

    @Portable
    public enum PersistenceMode {
        JPA,
        NONE;
    }

    @Portable
    public enum AuditMode {
        JPA,
        JMS,
        NONE;
    }

    @Portable
    public enum Resolver {
        MVEL,
        REFLECTION,
        SPRING;
    }

    @Portable
    public static class Parameter {

        private String name;
        private String value;

        public Parameter() {
        }

        public Parameter(final String name,
                         final String value) {

            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }
}
