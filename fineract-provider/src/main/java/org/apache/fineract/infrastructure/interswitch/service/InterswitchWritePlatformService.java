package org.apache.fineract.infrastructure.interswitch.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface InterswitchWritePlatformService {

	CommandProcessingResult authorizetransaction(JsonCommand command);

	CommandProcessingResult mapDebitCardToSavingsAccount(JsonCommand command);

	CommandProcessingResult executeTransaction(JsonCommand command);

}
