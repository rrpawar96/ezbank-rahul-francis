package org.apache.fineract.infrastructure.interswitch.data;

import java.util.List;

public class InterSwitchUndoTransactionData 
{

  String authorization_number;
    
  String response_code;
  
  List<InterswitchBalanceEnquiryData> additional_amount;
  
  
  InterSwitchUndoTransactionData(String authorization_number, String response_code,List<InterswitchBalanceEnquiryData> additional_amount)
  {
	  
	  this.authorization_number=authorization_number;
	  this.response_code=response_code;
	  this.additional_amount=additional_amount;
	  
  }
  
  
  public static InterSwitchUndoTransactionData getInstance(String authorization_number, String response_code,List<InterswitchBalanceEnquiryData> additional_amount)
  {
	  return new InterSwitchUndoTransactionData(authorization_number,response_code,additional_amount);
  }


public String getAuthorization_number() {
	return authorization_number;
}


public void setAuthorization_number(String authorization_number) {
	this.authorization_number = authorization_number;
}


public String getResponse_code() {
	return response_code;
}


public void setResponse_code(String response_code) {
	this.response_code = response_code;
}


public List<InterswitchBalanceEnquiryData> getAdditional_amount() {
	return additional_amount;
}


public void setAdditional_amount(List<InterswitchBalanceEnquiryData> additional_amount) {
	this.additional_amount = additional_amount;
}


  
  
  
}
