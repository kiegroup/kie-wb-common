/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.server.SocialConfiguration;

@ApplicationScoped
@Service
public class SocialConfigurationServiceImpl implements  SocialConfigurationService {

    @Inject
    private SocialConfiguration socialConfiguration;

    @Override
    public Boolean isSocialEnable() {
        return socialConfiguration.isSocialEnable();
    }
}
