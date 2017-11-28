package org.apache.fineract.infrastructure.interswitch.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchAuthorizationMessageData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchCardData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
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
	private final DefaultToApiJsonSerializer<InterswitchCardData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> authorizationToApiJsonSerializer;
	
	
	
	 @Autowired
	 public InterswitchAPI(PlatformSecurityContext context,ApiRequestParameterHelper apiRequestParameterHelper,
			 PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			 DefaultToApiJsonSerializer<InterswitchCardData> toApiJsonSerializer,
			 DefaultToApiJsonSerializer<InterswitchAuthorizationMessageData> authorizationToApiJsonSerializer)
	 {
		 this.context=context;
		 this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.authorizationToApiJsonSerializer=authorizationToApiJsonSerializer;
	 }
	 
	 
	 
	  	@POST
	    @Path("debitcard")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String mapDebitCardWithSavings(final String apiRequestBodyAsJson) {
	    	
	    	// to do: check for permissions here after creating one in backend
	    	//this.context.authenticatedUser().validateHasPermissionTo();
	    	
	    	   final CommandWrapper commandRequest = new CommandWrapperBuilder().mapDebitCardToSavingsAccount().withJson(apiRequestBodyAsJson).build();

	           final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

	           return this.toApiJsonSerializer.serialize(result);
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
}
