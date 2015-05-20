/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import org.kie.internal.persistence.conf.PersistenceDescriptor;
import org.kie.internal.persistence.conf.PersistenceDescriptorImpl;
import org.kie.internal.persistence.conf.PersistenceUnit;
import org.kie.internal.persistence.conf.PersistenceUnitCachingType;
import org.kie.internal.persistence.conf.PersistenceUnitTransactionType;
import org.kie.internal.persistence.conf.PersistenceUnitValidationModeType;
import org.kie.internal.persistence.conf.Properties;
import org.kie.workbench.common.screens.datamodeller.model.persistence.CachingType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.model.persistence.ValidationMode;

public class PersistenceDescriptorModelMarshaller {

    public static PersistenceDescriptor marshall( PersistenceDescriptorModel descriptorModel ) {
        if ( descriptorModel == null ) {
            return null;
        }

        PersistenceDescriptorImpl descriptor = new PersistenceDescriptorImpl();
        descriptor.setVersion( descriptorModel.getVersion() );
        if ( descriptorModel.getPersistenceUnit() != null ) {
            descriptor.getPersistenceUnit().add( marshall( descriptorModel.getPersistenceUnit() ) );
        }
        return descriptor;
    }

    public static PersistenceDescriptorModel unmarshall( PersistenceDescriptor descriptor ) {
        if ( descriptor == null ) {
            return null;
        }

        PersistenceDescriptorModel descriptorModel = new PersistenceDescriptorModel();
        descriptorModel.setVersion( descriptor.getVersion() );

        if ( descriptor.getPersistenceUnit() != null && descriptor.getPersistenceUnit().size() > 0 ) {
            descriptorModel.setPersistenceUnit( unmarshall( descriptor.getPersistenceUnit().get( 0 ) ) );
        }
        return descriptorModel;
    }

    public static PersistenceUnit marshall( PersistenceUnitModel persistenceUnitModel ) {
        if ( persistenceUnitModel == null ) {
            return null;
        }

        PersistenceUnit persistenceUnit = new PersistenceUnit();

        persistenceUnit.setName( persistenceUnitModel.getName() );
        persistenceUnit.setDescription( persistenceUnitModel.getDescription() );
        persistenceUnit.setProvider( persistenceUnitModel.getProvider() );
        persistenceUnit.setJtaDataSource( persistenceUnitModel.getJtaDataSource() );
        persistenceUnit.setNonJtaDataSource( persistenceUnitModel.getNonJtaDataSource() );
        persistenceUnit.setExcludeUnlistedClasses( persistenceUnitModel.getExcludeUnlistedClasses() );
        persistenceUnit.setTransactionType( marshall( persistenceUnitModel.getTransactionType() ) );
        persistenceUnit.setSharedCacheMode( marshall( persistenceUnitModel.getSharedCacheMode() ) );
        persistenceUnit.setValidationMode( marshall( persistenceUnitModel.getValidationMode() ) );

        if ( persistenceUnitModel.getProperties() != null ) {
            Properties properties = new Properties();
            persistenceUnit.setProperties( properties );
            for ( Property property : persistenceUnitModel.getProperties() ) {
                properties.getProperty().add( new org.kie.internal.persistence.conf.Property( property.getName(), property.getValue() ) );
            }
        }

        if ( persistenceUnitModel.getClasses() != null ) {
            persistenceUnit.getClazz().addAll( persistenceUnitModel.getClasses() );
        }
        if ( persistenceUnitModel.getJarFile() != null ) {
            persistenceUnit.getJarFile().addAll( persistenceUnitModel.getJarFile() );
        }
        if ( persistenceUnitModel.getMappingFile() != null ) {
            persistenceUnit.getMappingFile().addAll( persistenceUnitModel.getMappingFile() );
        }
        return persistenceUnit;
    }

    public static PersistenceUnitModel unmarshall( PersistenceUnit persistenceUnit ) {
        if ( persistenceUnit == null ) {
            return null;
        }
        PersistenceUnitModel persistenceUnitModel = new PersistenceUnitModel();

        persistenceUnitModel.setName( persistenceUnit.getName() );
        persistenceUnitModel.setDescription( persistenceUnit.getDescription() );
        persistenceUnitModel.setProvider( persistenceUnit.getProvider() );
        persistenceUnitModel.setJtaDataSource( persistenceUnit.getJtaDataSource() );
        persistenceUnitModel.setNonJtaDataSource( persistenceUnit.getNonJtaDataSource() );
        persistenceUnitModel.setExcludeUnlistedClasses( persistenceUnit.isExcludeUnlistedClasses() );
        persistenceUnitModel.setTransactionType( unmarshall( persistenceUnit.getTransactionType() ) );
        persistenceUnitModel.setSharedCacheMode( unmarshall( persistenceUnit.getSharedCacheMode() ) );
        persistenceUnitModel.setValidationMode( unmarshall( persistenceUnit.getValidationMode() ) );

        if ( persistenceUnit.getProperties() != null && persistenceUnit.getProperties().getProperty() != null ) {
            for ( org.kie.internal.persistence.conf.Property property : persistenceUnit.getProperties().getProperty() ) {
                persistenceUnitModel.getProperties().add( new Property( property.getName(), property.getValue() ) );
            }
        }
        if ( persistenceUnit.getClazz() != null ) {
            persistenceUnitModel.getClasses().addAll( persistenceUnit.getClazz() );
        }
        if ( persistenceUnit.getJarFile() != null ) {
            persistenceUnitModel.getJarFile().addAll( persistenceUnit.getJarFile() );
        }
        if ( persistenceUnit.getMappingFile() != null ) {
            persistenceUnitModel.getMappingFile().addAll( persistenceUnit.getMappingFile() );
        }
        return persistenceUnitModel;
    }

    public static PersistenceUnitTransactionType marshall( TransactionType transactionType ) {
        if ( transactionType == null ) {
            return null;
        }
        return PersistenceUnitTransactionType.valueOf( transactionType.name() );
    }

    public static TransactionType unmarshall( PersistenceUnitTransactionType persistenceUnitTransactionType ) {
        if ( persistenceUnitTransactionType == null ) {
            return null;
        }
        return TransactionType.valueOf( persistenceUnitTransactionType.name() );
    }

    public static PersistenceUnitCachingType marshall( CachingType cachingType ) {
        if ( cachingType == null ) {
            return null;
        }
        return PersistenceUnitCachingType.valueOf( cachingType.name() );
    }

    public static CachingType unmarshall( PersistenceUnitCachingType persistenceUnitCachingType ) {
        if ( persistenceUnitCachingType == null ) {
            return null;
        }
        return CachingType.valueOf( persistenceUnitCachingType.name() );
    }

    public static PersistenceUnitValidationModeType marshall( ValidationMode validationMode ) {
        if ( validationMode == null ) {
            return null;
        }
        return PersistenceUnitValidationModeType.valueOf( validationMode.name() );
    }

    public static ValidationMode unmarshall( PersistenceUnitValidationModeType validationModeType ) {
        if ( validationModeType == null ) {
            return null;
        }
        return ValidationMode.valueOf( validationModeType.name() );
    }
}
