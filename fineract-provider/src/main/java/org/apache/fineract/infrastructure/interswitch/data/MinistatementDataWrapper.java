package org.apache.fineract.infrastructure.interswitch.data;

import java.util.HashMap;
import java.util.List;

public class MinistatementDataWrapper 
{


	List<HashMap<String,Object>> mini_statement;
	
	List<InterswitchBalanceEnquiryData> additional_amount;
	
	 String response_code;
	 
	 String authorization_number;
	
	private MinistatementDataWrapper( List<HashMap<String,Object>> mini_statement,List<InterswitchBalanceEnquiryData> additional_amount,
			 String response_code,String authorization_number)
	{
	this.mini_statement=mini_statement;
	this.additional_amount=additional_amount;
	this.response_code=response_code;
	this.authorization_number=authorization_number;
	}
	
	public static MinistatementDataWrapper getInstance( List<HashMap<String,Object>> mini_statement,List<InterswitchBalanceEnquiryData> additional_amount,
			 String response_code,String authorization_number)
	{
		return new MinistatementDataWrapper(  mini_statement,additional_amount,
				response_code,authorization_number);
	}
	
	
	
}
