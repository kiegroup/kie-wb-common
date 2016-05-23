/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.datamodeller.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.EditorModelContent;
import org.kie.workbench.common.screens.datamodeller.security.DataModelerFeatures;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * BZ 1169777 - Source tab inconsistencies - editing allowed.
 */
public class SourceEditorReadOnlyTest extends DataModelerScreenPresenterTestBase {

    private static final String ROLE_CAN_EDIT_JAVA_SOURCE = "edit 1";
    private final Set<String> rolesThatCanEditJavaSource;
    private final Set<Role> userRoles;
    @Mock
    private User user;
    @Mock
    private Role roleCanEditJavaSource;
    @Mock
    private Role roleOther1;
    @Mock
    private Role roleOther2;

    public SourceEditorReadOnlyTest() {
        HashSet<String> r1 = new HashSet<>();
        r1.add(ROLE_CAN_EDIT_JAVA_SOURCE);
        r1.add("edit 2");
        rolesThatCanEditJavaSource = Collections.unmodifiableSet(r1);
        userRoles = new HashSet<>();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        // usual boilerplate code
        final boolean loadTypesInfo = true;
        EditorModelContent content = createContent(loadTypesInfo, false);
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(modelerService.loadContent(path, loadTypesInfo)).thenReturn(content);
        when(javaSourceEditor.getContent()).thenReturn(content.getSource());

        // roles related setup
        when(kieACL.getGrantedRoles(DataModelerFeatures.EDIT_SOURCES)).thenReturn(rolesThatCanEditJavaSource);
        when(sessionInfo.getIdentity()).thenReturn(user);
        when(user.getRoles()).thenReturn(userRoles);
        when(roleCanEditJavaSource.getName()).thenReturn(ROLE_CAN_EDIT_JAVA_SOURCE);
        when(roleOther1.getName()).thenReturn("role 1");
        when(roleOther2.getName()).thenReturn("role 2");

        // each test starts with an empty set of user roles
        userRoles.clear();
    }

    @Test
    public void testSourceEditorEditable() {
        userRoles.add(roleOther1);
        userRoles.add(roleCanEditJavaSource);
        presenter.onStartup(path, placeRequest);
        presenter.loadContent();
        verify(javaSourceEditor, atLeastOnce()).setReadonly(false);
    }

    @Test
    public void testSourceEditorReadOnly() {
        userRoles.add(roleOther1);
        userRoles.add(roleOther2);
        presenter.onStartup(path, placeRequest);
        presenter.loadContent();
        verify(javaSourceEditor, atLeastOnce()).setReadonly(true);
    }
}
