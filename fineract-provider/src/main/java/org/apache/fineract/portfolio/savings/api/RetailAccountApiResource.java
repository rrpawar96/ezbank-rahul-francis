package org.apache.fineract.portfolio.savings.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.SavingsApiConstants;
import org.apache.fineract.portfolio.savings.data.RetailAccountEntryTypeData;
import org.apache.fineract.portfolio.savings.data.RetailAccountKeyValuePairData;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/retailaccount")
@Component
@Scope("singleton")
public class RetailAccountApiResource 
{

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<RetailAccountEntryTypeData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    
    @Autowired
    public RetailAccountApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<RetailAccountEntryTypeData> toApiJsonSerializer,
    		final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final ApiRequestParameterHelper apiRequestParameterHelper,
    		final SavingsAccountReadPlatformService savingsAccountReadPlatformService)
    {
    	this.context=context;
    	this.toApiJsonSerializer=toApiJsonSerializer;
    	this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
    	this.apiRequestParameterHelper=apiRequestParameterHelper;
    	this.savingsAccountReadPlatformService=savingsAccountReadPlatformService;
    }
    
    @POST
    @Path("{retailAccountId}/retailentries")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson,@PathParam("retailAccountId") final Long retailAccountId) {
    	
    	// to do: check for permissions here after creating one in backend
    	//this.context.authenticatedUser().validateHasPermissionTo();
    	
    	   final CommandWrapper commandRequest = new CommandWrapperBuilder().createRetailEntry(retailAccountId).withJson(apiRequestBodyAsJson).build();

           final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

           return this.toApiJsonSerializer.serialize(result);
    }
    
    @GET
    @Path("{retailAccountId}/retailentries")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo,@PathParam("retailAccountId") final Long retailAccountId) {

        this.context.authenticatedUser().validateHasReadPermission(SavingsApiConstants.RETAIL_ACCOUNT_RESOURCE_NAME);

        final Collection<RetailAccountEntryTypeData> retailEntries = this.savingsAccountReadPlatformService.findEntriesByRetailAccountId(retailAccountId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, retailEntries,
				SavingsApiSetConstants.RETAIL_ACCOUNT_RESPONSE_DATA_PARAMETERS);
    }
    
    @PUT
	@Path("/{retailAccountId}/retailentries")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateretailEntries(@PathParam("retailAccountId") final long retailAccountId, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updadateRetailEntry(retailAccountId)
				.withJson(apiRequestBodyAsJson).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}
    
  
    
    
    
	
}
