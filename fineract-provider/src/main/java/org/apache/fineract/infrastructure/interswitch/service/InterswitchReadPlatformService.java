package org.apache.fineract.infrastructure.interswitch.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;
import org.apache.fineract.infrastructure.interswitch.data.SavingsProductChargesData;

public interface InterswitchReadPlatformService
{

	MinistatementDataWrapper getMinistatement(String json);

	InterswitchBalanceWrapper retrieveBalanceForUndoTransaction(long transactionId);

	InterswitchBalanceWrapper retrieveBalance(String json, boolean isInternalRequest);

	Collection<SavingsProductChargesData> retrieveSavingsProductChargesMapping(long savingsProductId);
	
}
