package org.apache.fineract.infrastructure.interswitch.service;

import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;

public interface InterswitchReadPlatformService
{

	InterswitchBalanceWrapper retrieveBalance(String json);

	MinistatementDataWrapper getMinistatement(String json);

	InterswitchBalanceWrapper retrieveBalanceForUndoTransaction(long transactionId);
	
}
