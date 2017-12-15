package org.apache.fineract.infrastructure.interswitch.service;

import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;

public interface InterswitchReadPlatformService
{

	InterswitchBalanceEnquiryData retrieveBalance(String json);
	
}
