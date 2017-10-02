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

public class RetailAccountEntryData 
{
	
	private long id;
	
	private long retailEntryTypeId;
	
	private String entryValue;
	
	private long retailTransactionId;
	
	private RetailAccountEntryData(long id,long retailEntryTypeId,String entryValue,
			long retailTransactionId)
	{
		this.id=id;
		this.retailTransactionId=retailEntryTypeId;
		this.entryValue=entryValue;
		this.retailTransactionId=retailTransactionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRetailEntryTypeId() {
		return retailEntryTypeId;
	}

	public void setRetailEntryTypeId(long retailEntryTypeId) {
		this.retailEntryTypeId = retailEntryTypeId;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public long getRetailTransactionId() {
		return retailTransactionId;
	}

	public void setRetailTransactionId(long retailTransactionId) {
		this.retailTransactionId = retailTransactionId;
	}
	
	

}
