package org.apache.fineract.portfolio.savings.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.savings.service.RetailAccountEntryWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "RETAILACCOUNTENTRIES", action = "UPDATE")
public class UpdateRetailAccountEntriesHandler implements NewCommandSourceHandler {
	
	private RetailAccountEntryWritePlatformService retailAccountEntryWritePlatformService;
	
	  @Autowired
	    public UpdateRetailAccountEntriesHandler(
	            final RetailAccountEntryWritePlatformService retailAccountEntryWritePlatformService) {
	        this.retailAccountEntryWritePlatformService = retailAccountEntryWritePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {
	        return this.retailAccountEntryWritePlatformService.updateEntries(command.entityId(), command);
	    }

}
