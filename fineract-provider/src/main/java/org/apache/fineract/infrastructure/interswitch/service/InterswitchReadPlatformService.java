package org.apache.fineract.infrastructure.interswitch.service;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;

public interface InterswitchReadPlatformService
{

	InterswitchBalanceEnquiryData retrieveBalance(String json);

	List<HashMap<String, HashMap<String, String>>> getMinistatement(String json);
	
}
