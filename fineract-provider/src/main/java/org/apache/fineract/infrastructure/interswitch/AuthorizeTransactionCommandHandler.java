package org.apache.fineract.infrastructure.interswitch;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.interswitch.service.InterswitchWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
@CommandType(entity = "TRANSACTION", action = "AUTHORIZE")
public class AuthorizeTransactionCommandHandler implements NewCommandSourceHandler
{
	private final InterswitchWritePlatformService writePlatformService;

	@Autowired
	public AuthorizeTransactionCommandHandler(final InterswitchWritePlatformService writePlatformService) {
		this.writePlatformService = writePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {

		return this.writePlatformService.authorizetransaction( command);
	}
}
