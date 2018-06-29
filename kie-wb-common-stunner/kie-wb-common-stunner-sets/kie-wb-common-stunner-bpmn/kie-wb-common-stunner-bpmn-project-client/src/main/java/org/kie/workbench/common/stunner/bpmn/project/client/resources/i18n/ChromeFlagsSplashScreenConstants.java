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

package org.kie.workbench.common.stunner.bpmn.project.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface ChromeFlagsSplashScreenConstants extends Messages {

    ChromeFlagsSplashScreenConstants INSTANCE = GWT.create(ChromeFlagsSplashScreenConstants.class);

    String flagsTitle();

    String flagsMessage1();

    String flagsMessage2();

    String flag1();

    String flag1URL();

    String flag1Value();

    String flag2();

    String flag2URL();

    String flag2Value();

    String flag3();

    String flag3URL();

    String flag3Value();

    String copyLink();

    String changeValue();

    String flagsMessage3();

    String copySuccessStart();

    String copySuccessEnd();

    String copyError();
}