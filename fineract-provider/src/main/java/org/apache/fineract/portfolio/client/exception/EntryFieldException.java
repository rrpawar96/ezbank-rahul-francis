package org.apache.fineract.portfolio.client.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class EntryFieldException extends AbstractPlatformDomainRuleException
{

	public EntryFieldException(String missingField) {
		super(missingField+" is required", missingField+" is required", missingField+" is required");
		
	}
	
	public EntryFieldException() {
		super("Entry key and Entry Value fields are required per key value pair", "Entry key and Entry Value fields are required per key value pair",
				"Entry key and Entry Value fields are required per key value pair");
		
	}
	
	public EntryFieldException(String key, String accountNumber) {
		super(" Account Number : "+ accountNumber+" is not configured for the key : "+key," Account Number : "+ accountNumber+" is not configured for the key : "+key,
				" Account Number : "+ accountNumber+" is not configured for the key : "+key);
		
	}


}
