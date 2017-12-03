package org.apache.fineract.infrastructure.interswitch.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.apache.fineract.infrastructure.creditbureau.data.CreditBureauData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchAuthorizationMessageData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/interswitch")
@Component
@Scope("singleton")
public class InterswitchAPI 
{

	private final PlatformSecurityContext context;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> toApiJsonSerializer;
	private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
	
	 @Autowired
	 public InterswitchAPI(PlatformSecurityContext context,ApiRequestParameterHelper apiRequestParameterHelper,
			 PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> toApiJsonSerializer,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> authorizationToApiJsonSerializer,
			 SavingsAccountTransactionRepository savingsAccountTransactionRepository)
	 {
		 this.context=context;
		 this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.savingsAccountTransactionRepository=savingsAccountTransactionRepository;
		
	 }
	 
	 

	  	
	  	
	  	@POST
	    @Path("authorize")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String authorizeTransaction(final String apiRequestBodyAsJson) {
	    	
	    	// to do: check for permissions here after creating one in backend
	    	//this.context.authenticatedUser().validateHasPermissionTo();
	    	
	    	   final CommandWrapper commandRequest = new CommandWrapperBuilder().authorizeTransaction().withJson(apiRequestBodyAsJson).build();

	           final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

	           return this.toApiJsonSerializer.serialize(result);
	    }
	  	
	  	@POST
	    @Path("transaction")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String executeTransaction(final String apiRequestBodyAsJson) {
	    	
	    	// to do: check for permissions here after creating one in backend
	    	//this.context.authenticatedUser().validateHasPermissionTo();
	    	
	    	   final CommandWrapper commandRequest = new CommandWrapperBuilder().executeTransaction().withJson(apiRequestBodyAsJson).build();

	           final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

	           return this.toApiJsonSerializer.serialize(result);
	    }
	  	
	  	@POST
	    @Path("/undotransaction/{authorizationNumber}")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String undoTransaction(final String apiRequestBodyAsJson,@QueryParam("session") final String sessionId,
	    		 @PathParam("authorizationNumber") final String authorizationNumber) {
	    	
	    	// to do: check for permissions here after creating one in backend
	    	//this.context.authenticatedUser().validateHasPermissionTo();
	  		
	  		// get savingsAccountId from authorisation, note session is not required, but its there
	  		// for future forward compatibility
	  									
	  		SavingsAccountTransaction transaction=this.savingsAccountTransactionRepository.findOne(Long.parseLong(authorizationNumber));
	  		
			long savingsAccountId=transaction.getSavingsAccount().getId();
	    	
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().undoTransaction(authorizationNumber,savingsAccountId).withJson(apiRequestBodyAsJson).build();

	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

	        return this.toApiJsonSerializer.serialize(result);
	    }
	  	
		@GET
		@Path("/transactionAdvice")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String getTransactionAdvice(@Context final UriInfo uriInfo) {
			
			return "work in progress";
		}
		
		@GET
		@Path("/balanceEnquiry")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String serveBalanceEnquiry(@Context final UriInfo uriInfo) {
			
			return "work in progress";
		}
}
