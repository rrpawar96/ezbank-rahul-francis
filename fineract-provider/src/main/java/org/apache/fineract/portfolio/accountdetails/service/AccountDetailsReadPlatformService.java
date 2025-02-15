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
package org.apache.fineract.portfolio.accountdetails.service;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.accountdetails.data.AccountSummaryCollectionData;
import org.apache.fineract.portfolio.accountdetails.data.LoanAccountSummaryData;

public interface AccountDetailsReadPlatformService {

    public AccountSummaryCollectionData retrieveClientAccountDetails(final Long clientId);

    public AccountSummaryCollectionData retrieveGroupAccountDetails(final Long groupId);

    public Collection<LoanAccountSummaryData> retrieveClientLoanAccountsByLoanOfficerId(final Long clientId, final Long loanOfficerId);

    public Collection<LoanAccountSummaryData> retrieveGroupLoanAccountsByLoanOfficerId(final Long groupId, final Long loanOfficerId);

    public Collection<LoanAccountSummaryData> retrieveClientActiveLoanAccountSummary(final Long clientId);

	public List<LoanAccountSummaryData> retrieveLoanAccountDetailsByGroupIdAndGlimAccountNumber(Long groupId,final String glimAccount);

	AccountSummaryCollectionData retrieveGroupAccountDetails(Long groupId, Long gsimId);
}
