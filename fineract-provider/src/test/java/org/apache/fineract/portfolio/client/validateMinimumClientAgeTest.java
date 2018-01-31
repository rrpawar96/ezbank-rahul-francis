/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for data validation builder method for checking whether the minimum age of all new clients is 18
 */ 

public class validateMinimumClientAgeTest {


    @Test
    public void AgeOfClient17(){
        final List<ApiParameterError> dataValidationErrorsTest = new ArrayList<>();
        DataValidatorBuilder dataValidatorBuilderTest = new DataValidatorBuilder(dataValidationErrorsTest);
        LocalDate currentDateTest = LocalDate.now();
        LocalDate client17YearsOldDob = currentDateTest.minusYears(17);
        dataValidatorBuilderTest.reset().parameter(ClientApiConstants.dateOfBirthParamName).value(client17YearsOldDob).notNull().validateMinimumClientAge(18);
        Assert.assertFalse("Failed to reject client with age of 17 years", dataValidationErrorsTest.isEmpty());

    }



    @Test
    public void AgeOfClient18(){

        final List<ApiParameterError> dataValidationErrorsTest = new ArrayList<>();
        DataValidatorBuilder dataValidatorBuilderTest = new DataValidatorBuilder(dataValidationErrorsTest);
        LocalDate currentDateTest = LocalDate.now();
        LocalDate client18YearsOldDob = currentDateTest.minusYears(18);
        dataValidatorBuilderTest.reset().parameter(ClientApiConstants.dateOfBirthParamName).value(client18YearsOldDob).notNull().validateMinimumClientAge(18);
        Assert.assertTrue("Failed to confirm client with age of 18 years", dataValidationErrorsTest.isEmpty());
    }

    @Test
    public void AgeOfClient19(){

        final List<ApiParameterError> dataValidationErrorsTest = new ArrayList<>();
        DataValidatorBuilder dataValidatorBuilderTest = new DataValidatorBuilder(dataValidationErrorsTest);
        LocalDate currentDateTest = LocalDate.now();
        LocalDate client19YearsOldDob = currentDateTest.minusYears(19);
        dataValidatorBuilderTest.reset().parameter(ClientApiConstants.dateOfBirthParamName).value(client19YearsOldDob).notNull().validateMinimumClientAge(18);
        Assert.assertTrue("Failed to confirm client with age of 19 years", dataValidationErrorsTest.isEmpty());

    }

    @Test
    public void AgeOfClient50(){

        final List<ApiParameterError> dataValidationErrorsTest = new ArrayList<>();
        DataValidatorBuilder dataValidatorBuilderTest = new DataValidatorBuilder(dataValidationErrorsTest);
        LocalDate currentDateTest = LocalDate.now();
        LocalDate client50YearsOldDob = currentDateTest.minusYears(50);
        dataValidatorBuilderTest.reset().parameter(ClientApiConstants.dateOfBirthParamName).value(client50YearsOldDob).notNull().validateMinimumClientAge(18);
        Assert.assertTrue("Failed to confirm client with age of 50 years", dataValidationErrorsTest.isEmpty());


    }
}