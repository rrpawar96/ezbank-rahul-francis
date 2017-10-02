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

package org.apache.fineract.portfolio.savings.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name = "retail_account_entry_data")
public class RetailAccountEntry extends AbstractPersistableCustom<Long>
{

	@ManyToOne
	@JoinColumn(name="retail_account_entry_type_id",nullable=false)
	private RetailAccountEntryType retailAccountEntryType;
	
	@Column(name="retail_account_entry_value",nullable=false)
	private String entryValue;
	
	@ManyToOne
	@JoinColumn(name="retail_account_transaction_id",nullable=false)
	private SavingsAccountTransaction retailTransaction;
	
	private RetailAccountEntry(RetailAccountEntryType retailAccountEntryType,
			String entryValue,SavingsAccountTransaction retailTransaction)
	{
		this.retailAccountEntryType=retailAccountEntryType;
		this.entryValue=entryValue;
		this.retailTransaction=retailTransaction;
	}
	
	public static RetailAccountEntry getInstance(RetailAccountEntryType retailAccountEntryType,
			String entryValue,SavingsAccountTransaction retailTransaction)
	{
		return new RetailAccountEntry(retailAccountEntryType,
				entryValue,retailTransaction);
	}

	public RetailAccountEntryType getRetailAccountEntryType() {
		return retailAccountEntryType;
	}

	public void setRetailAccountEntryType(RetailAccountEntryType retailAccountEntryType) {
		this.retailAccountEntryType = retailAccountEntryType;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public SavingsAccountTransaction getRetailTransaction() {
		return retailTransaction;
	}

	public void setRetailTransaction(SavingsAccountTransaction retailTransaction) {
		this.retailTransaction = retailTransaction;
	}
	
	
	
	
	
	
}
