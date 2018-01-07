package org.apache.fineract.infrastructure.interswitch.service;

public enum InterswitchEventType 
{
	
	INVALID(0, "interswitchEventType.invalid"),
	BALANCE_ENQUIRY(1, "interswitchEventType.balanceEnquiry"),
	REVERSAL(2, "interswitchEventType.reversal"),
	TRANSACTION(3, "interswitchEventType.transaction"),
	STATEMENT(4, "interswitchEventType.statement"),
	CHARGE(5, "interswitchEventType.charge"),
	FEES(6, "interswitchEventType.fees");
	
	

    private final Integer value;
    private final String code;

    private InterswitchEventType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    
    public static InterswitchEventType fromInt(final Integer eventType) {

        if (eventType == null) { return InterswitchEventType.INVALID; }

        InterswitchEventType interswitchEventType = InterswitchEventType.INVALID;
        
        switch (eventType) {
        
            case 1:
            	interswitchEventType = InterswitchEventType.BALANCE_ENQUIRY;
            break;
            
            case 2:
            	interswitchEventType = InterswitchEventType.REVERSAL;
            break;
  
            case 3:
            	interswitchEventType = InterswitchEventType.TRANSACTION;
            break;
  
            case 4:
            	interswitchEventType = InterswitchEventType.STATEMENT;
            break;
            
            case 5:
            	interswitchEventType = InterswitchEventType.CHARGE;
            break;
  
            case 6:
            	interswitchEventType = InterswitchEventType.FEES;
            break;
        }
        
        return interswitchEventType;
    }
}
