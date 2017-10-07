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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name = "retail_account_entry_types")
public class RetailAccountEntryType extends AbstractPersistableCustom<Long> 
{

	@Column(name="name",nullable=false)
	private String name;
	
	@Column(name="data_type",nullable=false)
	private String dataType;
	
	@ManyToOne
    @JoinColumn(name = "retail_account_id", nullable = false)
    private SavingsAccount retailAccount;
	
	@Column(name="is_constant")
	private boolean isConstant;
	
	@Column(name="constant_value")
	private String constantValue;
	
	@OneToMany(mappedBy="retailAccountEntryType")
	private List<RetailAccountEntry> retailAccountEntryData;
	
	
	private RetailAccountEntryType(final String name,final String dataType,
			final SavingsAccount retailAccount,final boolean isConstant,
			final String constantValue)
	{
		this.name=name;
		this.dataType=dataType;
		this.retailAccount=retailAccount;
		this.isConstant=isConstant;
		this.constantValue=constantValue;
	}
	
	public static RetailAccountEntryType getInstance(final String name,final String dataType,
			final SavingsAccount retailAccount,final boolean isConstant,
			final String constantValue)
	{
		return new RetailAccountEntryType(name,dataType,
				retailAccount,isConstant,constantValue);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public SavingsAccount getRetailAccount() {
		return retailAccount;
	}

	public void setRetailAccount(SavingsAccount retailAccount) {
		this.retailAccount = retailAccount;
	}

	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}

	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	
	
}
