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
package org.apache.fineract.portfolio.accountdetails.data;

import java.util.Collection;

/**
 * Immutable data object representing a summary of various accounts.
 */
@SuppressWarnings("unused")
public class AccountSummaryCollectionData {

    private final Collection<LoanAccountSummaryData> loanAccounts;
    private final Collection<LoanAccountSummaryData> groupLoanIndividualMonitoringAccounts;
    private final Collection<SavingsAccountSummaryData> savingsAccounts;
    private final Collection<ShareAccountSummaryData> shareAccounts ;
    
    private final Collection<LoanAccountSummaryData> memberLoanAccounts;
    private final Collection<SavingsAccountSummaryData> memberSavingsAccounts;
    
	   /* METHOD SIGNATURE CHANGE NOTICE: Method's signature 
	    was changed for GLIM & GSIM implementation*/
    
    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,final Collection<LoanAccountSummaryData> groupLoanIndividualMonitoringAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<ShareAccountSummaryData> shareAccounts) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.groupLoanIndividualMonitoringAccounts=groupLoanIndividualMonitoringAccounts;
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.shareAccounts = defaultShareAccountsIfEmpty(shareAccounts) ;
        this.memberLoanAccounts = null;
        this.memberSavingsAccounts = null;
    }
    
   /* Note to Self: GSIM not passed in*/
    
    public AccountSummaryCollectionData(final Collection<LoanAccountSummaryData> loanAccounts,final Collection<LoanAccountSummaryData> groupLoanIndividualMonitoringAccounts,
            final Collection<SavingsAccountSummaryData> savingsAccounts, final Collection<LoanAccountSummaryData> memberLoanAccounts,
            final Collection<SavingsAccountSummaryData> memberSavingsAccounts) {
        this.loanAccounts = defaultLoanAccountsIfEmpty(loanAccounts);
        this.groupLoanIndividualMonitoringAccounts=groupLoanIndividualMonitoringAccounts;
        this.savingsAccounts = defaultSavingsAccountsIfEmpty(savingsAccounts);
        this.shareAccounts = null ;
        this.memberLoanAccounts = defaultLoanAccountsIfEmpty(memberLoanAccounts);
        this.memberSavingsAccounts = defaultSavingsAccountsIfEmpty(memberSavingsAccounts);
    }

    private Collection<LoanAccountSummaryData> defaultLoanAccountsIfEmpty(final Collection<LoanAccountSummaryData> collection) {
        Collection<LoanAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

    private Collection<SavingsAccountSummaryData> defaultSavingsAccountsIfEmpty(final Collection<SavingsAccountSummaryData> collection) {
        Collection<SavingsAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }
    
    private Collection<ShareAccountSummaryData> defaultShareAccountsIfEmpty(final Collection<ShareAccountSummaryData> collection) {
        Collection<ShareAccountSummaryData> returnCollection = null;
        if (collection != null && !collection.isEmpty()) {
            returnCollection = collection;
        }
        return returnCollection;
    }

}