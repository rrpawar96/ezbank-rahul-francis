package org.apache.fineract.portfolio.client.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class EntryFieldException extends AbstractPlatformDomainRuleException
{

	public EntryFieldException(String missingField) {
		super(missingField+" is required", missingField+" is required", missingField+" is required");
		
	}

}
