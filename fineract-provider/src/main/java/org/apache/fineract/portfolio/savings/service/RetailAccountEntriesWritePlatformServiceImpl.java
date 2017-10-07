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

package org.apache.fineract.portfolio.savings.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.exception.EntryFieldException;
import org.apache.fineract.portfolio.savings.domain.RetailAccountEntryRepository;
import org.apache.fineract.portfolio.savings.domain.RetailAccountEntryType;
import org.apache.fineract.portfolio.savings.domain.RetailAccountEntryTypeRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class RetailAccountEntriesWritePlatformServiceImpl implements RetailAccountEntryWritePlatformService
{
	
	private final PlatformSecurityContext context;
	private final RetailAccountEntryTypeRepository retailAccountEntryTypeRepository;
	private final RetailAccountEntryRepository retailAccountEntryRepository;
	private final SavingsAccountRepository savingsAccountRepository;
	
	@Autowired
	public RetailAccountEntriesWritePlatformServiceImpl(final PlatformSecurityContext context,
			final RetailAccountEntryTypeRepository retailAccountEntryTypeRepository,
			final RetailAccountEntryRepository retailAccountEntryRepository,
			final SavingsAccountRepository savingsAccountRepository)
	{
		this.context=context;
		this.retailAccountEntryTypeRepository=retailAccountEntryTypeRepository;
		this.retailAccountEntryRepository=retailAccountEntryRepository;
		this.savingsAccountRepository=savingsAccountRepository;
		
	}

	@Override
	public CommandProcessingResult addEntries(Long retailAccountId, JsonCommand command) {
		
		SavingsAccount account=this.savingsAccountRepository.findOne(retailAccountId);
		
		  JsonArray entries= command.arrayOfParameterNamed("retailEntries");
    	  
    	  boolean isConstant=false;
    	  String constantValue=null;
    	  RetailAccountEntryType retailEntry=null;
    	  
    	  for(JsonElement entry:entries)
    	  {
    		  
    		  if(entry.getAsJsonObject().get("entryName")==null)
    		  {
    			  throw new EntryFieldException("entryName");
    		  }
    		  
    		  if(entry.getAsJsonObject().get("dataType")==null)
    		  {
    			  throw new EntryFieldException("dataType");
    		  }
    		  
    		  if(entry.getAsJsonObject().get("isConstant")!=null)
    		  {
    			  isConstant=entry.getAsJsonObject().get("isConstant").getAsBoolean();
    		  }
    		  
    		  if(isConstant)
    		  {
    			  if(entry.getAsJsonObject().get("constantValue")==null)
        		  {
        			  throw new EntryFieldException("constantValue");
        		  }  
    		  }
    		  
    		  
    		  
    		 
    		  if(isConstant)
    		  {
    			  	retailEntry= RetailAccountEntryType.getInstance(entry.getAsJsonObject().get("entryName").getAsString(),
          				  entry.getAsJsonObject().get("dataType").getAsString(), account,
          				 entry.getAsJsonObject().get("isConstant").getAsBoolean(), entry.getAsJsonObject().get("constantValue").getAsString());	  
    		  }
    		  else
    		  {
    			  retailEntry= RetailAccountEntryType.getInstance(entry.getAsJsonObject().get("entryName").getAsString(),
          				  entry.getAsJsonObject().get("dataType").getAsString(), account,
          				isConstant,constantValue);	
    		  }

    	
    		 
    		  this.retailAccountEntryTypeRepository.save(retailEntry);
    	  }
    	  
    	  if(retailEntry==null)
    	  {
    		  return CommandProcessingResult.empty();
    	  }
    	  else
    	  {
    		  return new CommandProcessingResultBuilder() //
                      .withEntityId(retailEntry.getId())
                      .build();  
    	  }
    	 
		
	}
	
	@Override
	public CommandProcessingResult updateEntries(Long retailAccountId, JsonCommand command) {
		
	
		
		  JsonArray entries= command.arrayOfParameterNamed("editRetailEntries");
		  				
		  
		  					
    	  
    	  boolean isConstant=false;
    	 
    	  RetailAccountEntryType retailAccountEntry=null;
    	  
    	  for(JsonElement entry:entries)
    	  {
    		 retailAccountEntry= retailAccountEntryTypeRepository.getOne(entry.getAsJsonObject().get("id").getAsLong());
    		  
    		  
    		  if(entry.getAsJsonObject().get("entryKey")!=null)
    		  {
    			  retailAccountEntry.setName(entry.getAsJsonObject().get("entryKey").getAsString());
    		  }
    		
    		  
    		  if(entry.getAsJsonObject().get("isConstant")!=null)
    		  {
    			  retailAccountEntry.setConstant(entry.getAsJsonObject().get("isConstant").getAsBoolean());
    			  
    			  isConstant= entry.getAsJsonObject().get("isConstant").getAsBoolean();
    		  }
    		  
    		  if(isConstant)
    		  {
    			  if(entry.getAsJsonObject().get("constantValue")!=null)
        		  {
    				  retailAccountEntry.setConstantValue(entry.getAsJsonObject().get("constantValue").getAsString()); 
    				  
        		  } 
    			  else
    			  {
    				  throw new EntryFieldException("constantValue");  
    			  }
    		  }
    		
    		 
    		  this.retailAccountEntryTypeRepository.save(retailAccountEntry);
    	  }
    	  
    	  if(retailAccountEntry==null)
    	  {
    		  return CommandProcessingResult.empty();
    	  }
    	  else
    	  {
    		  return new CommandProcessingResultBuilder() //
                      .withEntityId(retailAccountEntry.getId())
                      .build();  
    	  }
    	 
		
	}
	
}
