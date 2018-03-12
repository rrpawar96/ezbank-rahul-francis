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
package org.apache.fineract.portfolio.self.savings.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.api.SavingsAccountChargesApiResource;
import org.apache.fineract.portfolio.savings.api.SavingsAccountTransactionsApiResource;
import org.apache.fineract.portfolio.savings.api.SavingsAccountsApiResource;
import org.apache.fineract.portfolio.savings.api.SavingsApiSetConstants;
import org.apache.fineract.portfolio.savings.data.RetailAccountEntryTypeData;
import org.apache.fineract.portfolio.savings.data.RetailSavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountNotFoundException;
import org.apache.fineract.portfolio.savings.service.RetailAccountReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.self.savings.data.SelfSavingsDataValidator;
import org.apache.fineract.portfolio.self.savings.service.AppuserSavingsMapperReadService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/self/savingsaccounts")
@Component
@Scope("singleton")
public class SelfSavingsApiResource {

	private final PlatformSecurityContext context;
	private final SavingsAccountsApiResource savingsAccountsApiResource;
	private final SavingsAccountChargesApiResource savingsAccountChargesApiResource;
	private final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource;
	private final AppuserSavingsMapperReadService appuserSavingsMapperReadService;
	private final SelfSavingsDataValidator dataValidator;
	private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
	private final DefaultToApiJsonSerializer<RetailSavingsAccountTransactionData> retailToApiJsonSerializer;
	private final DefaultToApiJsonSerializer<RetailAccountEntryTypeData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final RetailAccountReadPlatformService retailAccountReadPlatformService;

	@Autowired
	public SelfSavingsApiResource(
			final PlatformSecurityContext context,
			final SavingsAccountsApiResource savingsAccountsApiResource,
			final SavingsAccountChargesApiResource savingsAccountChargesApiResource,
			final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource,
			final AppuserSavingsMapperReadService appuserSavingsMapperReadService,
			final SelfSavingsDataValidator dataValidator,
			final SavingsAccountReadPlatformService savingsAccountReadPlatformService,
			final DefaultToApiJsonSerializer<RetailSavingsAccountTransactionData> retailToApiJsonSerializer,
			final DefaultToApiJsonSerializer<RetailAccountEntryTypeData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final RetailAccountReadPlatformService retailAccountReadPlatformService) {
		this.context = context;
		this.savingsAccountsApiResource = savingsAccountsApiResource;
		this.savingsAccountChargesApiResource = savingsAccountChargesApiResource;
		this.savingsAccountTransactionsApiResource = savingsAccountTransactionsApiResource;
		this.appuserSavingsMapperReadService = appuserSavingsMapperReadService;
		this.dataValidator = dataValidator;
		this.savingsAccountReadPlatformService=savingsAccountReadPlatformService;
		this.retailToApiJsonSerializer=retailToApiJsonSerializer;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.retailAccountReadPlatformService=retailAccountReadPlatformService;
	}

	@GET
	@Path("{accountId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSavings(
			@PathParam("accountId") final Long accountId,
			@DefaultValue("all") @QueryParam("chargeStatus") final String chargeStatus,
			@Context final UriInfo uriInfo) {

		this.dataValidator.validateRetrieveSavings(uriInfo);

		validateAppuserSavingsAccountMapping(accountId);

		final boolean staffInSelectedOfficeOnly = false;
		return this.savingsAccountsApiResource.retrieveOne(accountId,
				staffInSelectedOfficeOnly, chargeStatus, uriInfo);
	}

	@GET
	@Path("{accountId}/transactions/{transactionId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSavingsTransaction(
			@PathParam("accountId") final Long accountId,
			@PathParam("transactionId") final Long transactionId,
			@Context final UriInfo uriInfo) {

		this.dataValidator.validateRetrieveSavingsTransaction(uriInfo);

		validateAppuserSavingsAccountMapping(accountId);

		return this.savingsAccountTransactionsApiResource.retrieveOne(
				accountId, transactionId, uriInfo);
	}

	@GET
	@Path("{accountId}/charges")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllSavingsAccountCharges(
			@PathParam("accountId") final Long accountId,
			@DefaultValue("all") @QueryParam("chargeStatus") final String chargeStatus,
			@Context final UriInfo uriInfo) {

		validateAppuserSavingsAccountMapping(accountId);

		return this.savingsAccountChargesApiResource
				.retrieveAllSavingsAccountCharges(accountId, chargeStatus,
						uriInfo);
	}

	@GET
	@Path("{accountId}/charges/{savingsAccountChargeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSavingsAccountCharge(
			@PathParam("accountId") final Long accountId,
			@PathParam("savingsAccountChargeId") final Long savingsAccountChargeId,
			@Context final UriInfo uriInfo) {

		validateAppuserSavingsAccountMapping(accountId);

		return this.savingsAccountChargesApiResource
				.retrieveSavingsAccountCharge(accountId,
						savingsAccountChargeId, uriInfo);
	}
	
	   @GET
	    @Path("{retailAccountId}/retailentries")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrieveAll(@Context final UriInfo uriInfo,@PathParam("retailAccountId") final Long retailAccountId) {

			validateAppuserSavingsAccountMapping(retailAccountId);

	        final Collection<RetailAccountEntryTypeData> retailEntries= this.savingsAccountReadPlatformService.findEntriesByRetailAccountId(retailAccountId);

	        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(settings, retailEntries,
					SavingsApiSetConstants.RETAIL_ACCOUNT_RESPONSE_DATA_PARAMETERS);
	    }
	    
	    
	    @GET
	    @Path("{retailAccountId}/retailTransactions")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrieveRetailTransactions(@Context final UriInfo uriInfo,@PathParam("retailAccountId") final Long retailAccountId,
	    		@QueryParam("startDate") final String startDate,@QueryParam("endDate") final String endDate ) {

	    	validateAppuserSavingsAccountMapping(retailAccountId);
	        
	        final Collection<RetailSavingsAccountTransactionData> currentTransactions =this.retailAccountReadPlatformService.retrieveRetailTransactions(retailAccountId, DepositAccountType.SAVINGS_DEPOSIT,
	        		startDate,endDate);
	        
	        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.retailToApiJsonSerializer.serialize(settings, currentTransactions,
					SavingsApiSetConstants.RETAIL_TRANSACTION_RESPONSE_DATA_PARAMETERS);
	    }
	

	private void validateAppuserSavingsAccountMapping(final Long accountId) {
		AppUser user = this.context.authenticatedUser();
		final boolean isMappedSavings = this.appuserSavingsMapperReadService
				.isSavingsMappedToUser(accountId, user.getId());
		if (!isMappedSavings) {
			throw new SavingsAccountNotFoundException(accountId);
		}
	}

}
