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

package org.kie.workbench.common.services.builder.tests.excel;

public class Driver {

    private int age;
    private String locationRiskProfile;
    private String priorClaims;

    public int getAge() {
        return age;
    }

    public void setAge( int age ) {
        this.age = age;
    }

    public String getLocationRiskProfile() {
        return locationRiskProfile;
    }

    public void setLocationRiskProfile( String locationRiskProfile ) {
        this.locationRiskProfile = locationRiskProfile;
    }

    public String getPriorClaims() {
        return priorClaims;
    }

    public void setPriorClaims( String priorClaims ) {
        this.priorClaims = priorClaims;
    }

}
