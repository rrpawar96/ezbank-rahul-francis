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

package org.apache.fineract.portfolio.savings.data;

public class RetailAccountKeyValuePairData 
{

	private Long id;
	
	private Long transactionId;
	
	private String dataType;
	
	private String entrykey;
	
	private String entryValue;
	
	private Long retailAccountId;
	
	private boolean isConstant;
	
	private String constantValue;
	
	private RetailAccountKeyValuePairData(Long id,Long transactionId,String dataType,
			String entryKey,String entryValue,Long retailAccountId,
			boolean isConstant,String constantValue)
	{
		this.id=id;
		this.transactionId=transactionId;
		this.dataType=dataType;
		this.entrykey=entryKey;
		this.entryValue=entryValue;
		this.retailAccountId=retailAccountId;
		this.isConstant=isConstant;
		this.constantValue=constantValue;
		
	}
	
	public static RetailAccountKeyValuePairData getInstance(Long id,Long transactionId,String dataType,
			String entryKey,String entryValue,Long retailAccountId,
			boolean isConstant,String constantValue)
	{
		return new RetailAccountKeyValuePairData(id,transactionId,dataType,
				entryKey,entryValue,retailAccountId,isConstant,constantValue);
	}
	
	public static RetailAccountKeyValuePairData getInstanceWithoutTransaction(Long id,String dataType,
			String entryKey,String entryValue,Long retailAccountId,
			boolean isConstant,String constantValue)
	{
		
		return new RetailAccountKeyValuePairData(id,null,dataType,
				entryKey,entryValue,retailAccountId,isConstant,constantValue);
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getEntrykey() {
		return entrykey;
	}

	public void setEntrykey(String entrykey) {
		this.entrykey = entrykey;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public Long getRetailAccountId() {
		return retailAccountId;
	}

	public void setRetailAccountId(Long retailAccountId) {
		this.retailAccountId = retailAccountId;
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
