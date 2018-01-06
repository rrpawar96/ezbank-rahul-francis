package org.apache.fineract.infrastructure.interswitch.service;

public enum InterswitchTransactionProcessingType 
{

	INVALID(0, "interswitchTransactionProcessingType.invalid"),
	CASH_WITHDRAWAL(1, "interswitchTransactionProcessingType.cashWithdrawal"),
	DEPOSIT(2, "interswitchTransactionProcessingType.deposit"),
	PAYMENT_AND_TRANSFER(3, "interswitchTransactionProcessingType.paymentAndTransfer"),
	PURCHASE(4, "interswitchTransactionProcessingType.purchase");
	
	

    private final Integer value;
    private final String code;

    private InterswitchTransactionProcessingType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    
    public static InterswitchTransactionProcessingType fromInt(final Integer transactionType) {

        if (transactionType == null) { return InterswitchTransactionProcessingType.INVALID; }

        InterswitchTransactionProcessingType interswitchEventType = InterswitchTransactionProcessingType.INVALID;
        
        switch (transactionType) 
        {
        
            case 1:
            	interswitchEventType = InterswitchTransactionProcessingType.CASH_WITHDRAWAL;
            break;
            
            case 2:
            	interswitchEventType = InterswitchTransactionProcessingType.DEPOSIT;
            break;
  
            case 3:
            	interswitchEventType = InterswitchTransactionProcessingType.PAYMENT_AND_TRANSFER;
            break;
  
            case 4:
            	interswitchEventType = InterswitchTransactionProcessingType.PURCHASE;
            break;
        }
        
        return interswitchEventType;
    }
	
	
}
