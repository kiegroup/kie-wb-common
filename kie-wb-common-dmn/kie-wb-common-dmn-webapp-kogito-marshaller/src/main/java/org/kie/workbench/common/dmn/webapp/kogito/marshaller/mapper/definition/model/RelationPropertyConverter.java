/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;

public class RelationPropertyConverter {

    public static Relation wbFromDMN(final JSITRelation dmn,
                                     final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());

        final List<InformationItem> convertedColumns = new ArrayList<>();
        final JsArrayLike<JSITInformationItem> jsiColumns = JSITRelation.getColumn(dmn);
        for (int i = 0; i < jsiColumns.getLength(); i++) {
            final JSITInformationItem jsiColumn = Js.uncheckedCast(jsiColumns.getAt(i));
            convertedColumns.add(InformationItemPropertyConverter.wbFromDMN(jsiColumn));
        }

        final List<org.kie.workbench.common.dmn.api.definition.model.List> convertedRows = new ArrayList<>();
        final JsArrayLike<JSITList> jsiRows = JSITRelation.getRow(dmn);
        for (int i = 0; i < jsiRows.getLength(); i++) {
            final JSITList jsiRow = Js.uncheckedCast(jsiRows.getAt(i));
            convertedRows.add(ListPropertyConverter.wbFromDMN(jsiRow, hasComponentWidthsConsumer));
        }

        final Relation result = new Relation(id, description, typeRef, convertedColumns, convertedRows);
        for (InformationItem c : convertedColumns) {
            if (c != null) {
                c.setParent(result);
            }
        }
        for (org.kie.workbench.common.dmn.api.definition.model.List r : convertedRows) {
            if (r != null) {
                r.setParent(result);
            }
        }
        return result;
    }

    public static JSITRelation dmnFromWB(final Relation wb,
                                         final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final JSITRelation result = JSITRelation.newInstance();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        for (InformationItem iitem : wb.getColumn()) {
            final JSITInformationItem iitemConverted = InformationItemPropertyConverter.dmnFromWB(iitem);
            JSITRelation.addColumn(result, iitemConverted);
        }

        for (org.kie.workbench.common.dmn.api.definition.model.List list : wb.getRow()) {
            final JSITList listConverted = ListPropertyConverter.dmnFromWB(list, componentWidthsConsumer);
            JSITRelation.addRow(result, listConverted);
        }

        return result;
    }
}
