package org.apache.fineract.infrastructure.interswitch.data;

import java.util.HashMap;
import java.util.List;

public class MinistatementDataWrapper 
{


	List<HashMap<String,String>> mini_statement;
	
	 String response_code;
	 
	 String authorization_number;
	
	private MinistatementDataWrapper( List<HashMap<String,String>> mini_statement,
			 String response_code,String authorization_number)
	{
	this.mini_statement=mini_statement;
	this.response_code=response_code;
	this.authorization_number=authorization_number;
	}
	
	public static MinistatementDataWrapper getInstance( List<HashMap<String,String>> mini_statement,
			 String response_code,String authorization_number)
	{
		return new MinistatementDataWrapper(  mini_statement,
				response_code,authorization_number);
	}
	
	
	
}
