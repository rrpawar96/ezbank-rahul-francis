package org.apache.fineract.infrastructure.interswitch.data;

import java.util.List;

public class InterswitchBalanceWrapper 
{

	 List<InterswitchBalanceEnquiryData> additional_amount;
	
	 String response_code;
	 
	 String authorization_number;
	
	private InterswitchBalanceWrapper(  List<InterswitchBalanceEnquiryData> additional_amount,
			 String response_code,String authorization_number)
	{
	this.additional_amount=additional_amount;
	this.response_code=response_code;
	this.authorization_number=authorization_number;
	}
	
	public static InterswitchBalanceWrapper getInstance(  List<InterswitchBalanceEnquiryData> additional_amount,
			 String response_code,String authorization_number)
	{
		return new InterswitchBalanceWrapper(  additional_amount,
				response_code,authorization_number);
	}

	public   List<InterswitchBalanceEnquiryData> getAdditional_amount() {
		return additional_amount;
	}

	public String getResponse_code() {
		return response_code;
	}

	public String getAuthorization_number() {
		return authorization_number;
	}

	public void setAuthorization_number(String authorization_number) {
		this.authorization_number = authorization_number;
	}

	public void setAdditional_amount(  List<InterswitchBalanceEnquiryData> additional_amount) {
		this.additional_amount = additional_amount;
	}

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	
	
	
	
	
}
