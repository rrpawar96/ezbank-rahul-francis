package org.apache.fineract.infrastructure.interswitch.api;

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
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.interswitch.data.InterSwitchUndoTransactionData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchAuthorizationMessageData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchTransactionData;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;
import org.apache.fineract.infrastructure.interswitch.service.InterswitchReadPlatformServiceImpl;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
	private final FromJsonHelper fromApiJsonHelper;
	
	 @Autowired
	 public InterswitchAPI(PlatformSecurityContext context,ApiRequestParameterHelper apiRequestParameterHelper,
			 PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> toApiJsonSerializer,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> authorizationToApiJsonSerializer,
			 SavingsAccountTransactionRepository savingsAccountTransactionRepository,
			 InterswitchReadPlatformServiceImpl interswitchReadPlatformServiceImpl,
			 FromJsonHelper fromApiJsonHelper)
	 {
		 this.context=context;
		 this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.savingsAccountTransactionRepository=savingsAccountTransactionRepository;
		this.interswitchReadPlatformServiceImpl=interswitchReadPlatformServiceImpl;
		this.fromApiJsonHelper=fromApiJsonHelper;
		
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
	           
	           
	           InterswitchBalanceWrapper interswitchBalanceEnquiryData = this.interswitchReadPlatformServiceImpl
						.retrieveBalance(apiRequestBodyAsJson,true);
	           
	           String authorizationNumber=null;
	           if(result.getAuthorizationNumber()!="")
	           {
	        	   authorizationNumber= String.format("%06d",Integer.parseInt(result.getAuthorizationNumber()) );
	           }
	           
	           String responseCode= String.format("%02d", Integer.parseInt(result.getResponseCode()) );
	           
	           InterswitchTransactionData transactionResponse =InterswitchTransactionData.getInstance(authorizationNumber,responseCode,
	        		   interswitchBalanceEnquiryData.getAdditional_amount());

	           return this.toApiJsonSerializer.serialize(transactionResponse);
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
	        
	        final JsonElement element = this.fromApiJsonHelper.parse(apiRequestBodyAsJson);
			JsonObject requestBody=element.getAsJsonObject();
			
			Long transactionId=Long.parseLong(requestBody.get("authorization_number").getAsString());
			
			
			InterswitchBalanceWrapper balance=interswitchReadPlatformServiceImpl.retrieveBalanceForUndoTransaction(transactionId);
			
			InterSwitchUndoTransactionData interSwitchUndoTransactionData=InterSwitchUndoTransactionData.getInstance(balance.getAuthorization_number(),
					result.getResponseCode(), balance.getAdditional_amount());

	        return this.toApiJsonSerializer.serialize(interSwitchUndoTransactionData);
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

			final InterswitchBalanceWrapper interswitchBalanceEnquiryData = this.interswitchReadPlatformServiceImpl
					.retrieveBalance(apiRequestBodyAsJson,false);
			
			return this.toApiJsonSerializer.serialize(interswitchBalanceEnquiryData);
		}
		
		
		@POST
		@Path("/ministatement")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String getMinistatement(final String apiRequestBodyAsJson) {
			
			//this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

			final MinistatementDataWrapper miniStatement = this.interswitchReadPlatformServiceImpl
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
