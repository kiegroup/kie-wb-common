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

package org.kie.workbench.common.forms.cms.persistence.jpa;

import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.DynamicPersistenceUnitInfoImpl;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.JTAPersistenceManager;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.NonJTAPersistenceManager;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.util.PersistenceDescriptorXMLMarshaller;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@Dependent
public class JPAPersistenceManagerBuilder {

    public static final String PERSISTENCE_DESCRIPTOR_PATH = "src/main/resources/META-INF/persistence.xml";

    private GuvnorM2Repository m2Repository;
    private IOService ioService;

    @Inject
    public JPAPersistenceManagerBuilder(GuvnorM2Repository m2Repository, @Named("ioStrategy") IOService ioService) {
        this.m2Repository = m2Repository;
        this.ioService = ioService;
    }

    public JPAPersistenceManager getPersistenceManager(BackendApplicationRuntime runtime) {
        Path xmlPath = Paths.convert(runtime.getDeployedModule().getRootPath()).resolve(PERSISTENCE_DESCRIPTOR_PATH);

        if (!ioService.exists(xmlPath)) {
            throw new IllegalStateException("Cannot load persisntece.xml!");
        }

        try {
            URL rootURL = m2Repository.getArtifactFileFromRepository(runtime.getDeployedModule().getPom().getGav()).toURI().toURL();

            BufferedInputStream inputStream = new BufferedInputStream(ioService.newInputStream(xmlPath));

            final PersistenceDescriptorModel persistenceDescriptorModel = PersistenceDescriptorXMLMarshaller.fromXML(inputStream, false);

            final PersistenceUnitModel unit = persistenceDescriptorModel.getPersistenceUnit();

            DynamicPersistenceUnitInfoImpl persistenceUnitInfo = new DynamicPersistenceUnitInfoImpl(persistenceDescriptorModel.getVersion(),
                                                                                                    unit.getName(),
                                                                                                    runtime.getModuleClassLoader(),
                                                                                                    getUnitProperties(unit),
                                                                                                    unit.getClasses(),
                                                                                                    getUnitJars(unit),
                                                                                                    unit.getExcludeUnlistedClasses(),
                                                                                                    getUnitCacheMode(unit),
                                                                                                    getUnitValidationMode(unit),
                                                                                                    rootURL);

            if (unit.getTransactionType().equals(TransactionType.JTA)) {
                persistenceUnitInfo.setJtaDataSource(lookup(unit.getJtaDataSource()));
            } else {
                persistenceUnitInfo.setNonJtaDataSource(lookup(unit.getNonJtaDataSource()));
            }

            EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(persistenceUnitInfo, new HashMap());

            EntityManager entityManager = entityManagerFactory.createEntityManager();

            if (persistenceUnitInfo.getTransactionType().equals(PersistenceUnitTransactionType.JTA)) {
                return new JTAPersistenceManager(entityManagerFactory, entityManager);
            } else {
                return new NonJTAPersistenceManager(entityManagerFactory, entityManager);
            }
        } catch (Exception ex) {

        }

        return null;
    }

    private DataSource lookup(final String jndiName) throws NamingException {
        final Context context = new InitialContext();

        try {
            return (DataSource) context.lookup(jndiName);
        } finally {
            context.close();
        }
    }

    private Properties getUnitProperties(final PersistenceUnitModel persistenceUnitModel) {
        Properties properties = new Properties();

        persistenceUnitModel.getProperties().stream().forEach(property -> properties.put(property.getName(), property.getValue()));

        return properties;
    }

    private List<URL> getUnitJars(PersistenceUnitModel persistenceUnitModel) {

        List<URL> urls = new ArrayList<>();

        if (persistenceUnitModel.getJarFile() != null) {
            persistenceUnitModel.getJarFile().stream()
                    .map(this::toURL)
                    .filter(url -> url != null)
                    .collect(Collectors.toCollection(() -> urls));
        }
        return urls;
    }

    private SharedCacheMode getUnitCacheMode(PersistenceUnitModel persistenceUnitModel) {
        if (persistenceUnitModel.getSharedCacheMode() == null) {
            return SharedCacheMode.UNSPECIFIED;
        }
        switch (persistenceUnitModel.getSharedCacheMode()) {
            case ALL:
                return SharedCacheMode.ALL;
            case NONE:
                return SharedCacheMode.NONE;
            case ENABLE_SELECTIVE:
                return SharedCacheMode.ENABLE_SELECTIVE;
            case DISABLE_SELECTIVE:
                return SharedCacheMode.DISABLE_SELECTIVE;
            default:
                return SharedCacheMode.UNSPECIFIED;
        }
    }

    private ValidationMode getUnitValidationMode(PersistenceUnitModel persistenceUnitModel) {
        if (persistenceUnitModel.getValidationMode() == null) {
            return ValidationMode.AUTO;
        }
        switch (persistenceUnitModel.getValidationMode()) {
            case CALLBACK:
                return ValidationMode.CALLBACK;
            case NONE:
                return ValidationMode.NONE;
            default:
                return ValidationMode.AUTO;
        }
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {

        }
        return null;
    }
}
