/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.authz;

import org.kie.workbench.common.screens.datamodeller.security.DataModelerFeatures;

/**
 * Interface which defines general workbench permissions non tied to any specific resource.
 */
public interface WorkbenchFeatures extends DataModelerFeatures {

    String PLANNER_AVAILABLE = "planner.available";

    String EDIT_GLOBAL_PREFERENCES = "globalpreferences.edit";

    String GUIDED_DECISION_TABLE_EDIT_COLUMNS = "guideddecisiontable.edit.columns";

    String EDIT_PROFILE_PREFERENCES = "profilepreferences.edit";
}
