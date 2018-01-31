package org.apache.fineract.infrastructure.interswitch.service;

public enum InterswitchTransactionAmountType 
{
	
	
	INVALID(0, "interswitchTransactionAmountType.invalid"),
	CREDIT(1, "interswitchTransactionAmountType.credit"),
	DEBIT(2, "interswitchTransactionAmountType.debit");
	
	

    private final Integer value;
    private final String code;

    private InterswitchTransactionAmountType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    
    public static InterswitchTransactionAmountType fromInt(final Integer transactionType) {

        if (transactionType == null) { return InterswitchTransactionAmountType.INVALID; }

        InterswitchTransactionAmountType interswitchTransactionAmountType = InterswitchTransactionAmountType.INVALID;
        
        switch (transactionType) 
        {
        
            case 1:
            	interswitchTransactionAmountType = InterswitchTransactionAmountType.CREDIT;
            break;
            
            case 2:
            	interswitchTransactionAmountType = InterswitchTransactionAmountType.CREDIT;
            break;
  
        }
        
        return interswitchTransactionAmountType;
    }
    
    
    

}
