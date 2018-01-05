package org.apache.fineract.infrastructure.interswitch.service;

import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;

public interface InterswitchReadPlatformService
{

	MinistatementDataWrapper getMinistatement(String json);

	InterswitchBalanceWrapper retrieveBalanceForUndoTransaction(long transactionId);

	InterswitchBalanceWrapper retrieveBalance(String json, boolean isInternalRequest);
	
}
