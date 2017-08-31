package org.apache.fineract.portfolio.savings.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class TransactionIdExceededLowerLimitException extends AbstractPlatformDomainRuleException {

	public TransactionIdExceededLowerLimitException() {
		
		super("Transaction Range Exhausted, Please Exceed the transaction Range", "Transaction Range Exhausted, Please Exceed the transaction Range",
				"Transaction Range Exhausted, Please Exceed the transaction Range");
		
	}

}
