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

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain;

import javax.enterprise.event.Observes;

import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

public abstract class ObjectEditor extends BaseEditor {

    protected DataObject dataObject;

    protected ObjectEditor() {
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    protected abstract void loadDataObject( DataObject dataObject );

    protected void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            loadDataObject( event.getCurrentDataObject() );
        }
    }

    // Event notifications
    protected void notifyObjectChange( ChangeType changeType,
            String memberName,
            Object oldValue,
            Object newValue ) {

        //TODO check if data model for the event is needed
        DataObjectChangeEvent changeEvent = new DataObjectChangeEvent( changeType,
                getContext().getContextId(),
                getName(),
                null, getDataObject(), memberName, oldValue, newValue );
        // Notify helper directly
        getContext().getHelper().dataModelChanged( changeEvent );
        dataModelerEvent.fire( changeEvent );
    }
}
