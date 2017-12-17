package org.apache.fineract.infrastructure.interswitch.api;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchAuthorizationMessageData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;
import org.apache.fineract.infrastructure.interswitch.service.InterswitchReadPlatformServiceImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
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
	private final InterswitchReadPlatformServiceImpl interswitchReadPlatformServiceImpl;
	
	 @Autowired
	 public InterswitchAPI(PlatformSecurityContext context,ApiRequestParameterHelper apiRequestParameterHelper,
			 PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> toApiJsonSerializer,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> authorizationToApiJsonSerializer,
			 SavingsAccountTransactionRepository savingsAccountTransactionRepository,
			 InterswitchReadPlatformServiceImpl interswitchReadPlatformServiceImpl)
	 {
		 this.context=context;
		 this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.savingsAccountTransactionRepository=savingsAccountTransactionRepository;
		this.interswitchReadPlatformServiceImpl=interswitchReadPlatformServiceImpl;
		
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
	    @Path("/undotransaction")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String undoTransaction(final String apiRequestBodyAsJson) {
	    	
	    	// to do: check for permissions here after creating one in backend
	    	//this.context.authenticatedUser().validateHasPermissionTo();
	  		
	  		// get savingsAccountId from authorisation, note session is not required, but its there
	  		// for future forward compatibility
	  									
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().undoTransaction().withJson(apiRequestBodyAsJson).build();

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
		
		@POST
		@Path("/balanceEnquiry")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String serveBalanceEnquiry(final String apiRequestBodyAsJson) {
			
			//this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

			final InterswitchBalanceEnquiryData interswitchBalanceEnquiryData = this.interswitchReadPlatformServiceImpl
					.retrieveBalance(apiRequestBodyAsJson);
			
			return this.toApiJsonSerializer.serialize(interswitchBalanceEnquiryData);
		}
		
		
		@POST
		@Path("/ministatement")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String getMinistatement(final String apiRequestBodyAsJson) {
			
			//this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

			final List<HashMap<String,String>> miniStatement = this.interswitchReadPlatformServiceImpl
					.getMinistatement(apiRequestBodyAsJson);
			
			return this.toApiJsonSerializer.serialize(miniStatement);
		}
		
		@POST
		@Path("/transferAdvice")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String handleTransfer(final String apiRequestBodyAsJson) {
			
			//this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

			 final CommandWrapper commandRequest = new CommandWrapperBuilder().executeTransaction().withJson(apiRequestBodyAsJson).build();

	         final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

	         return this.toApiJsonSerializer.serialize(result);
	           
		}
}
