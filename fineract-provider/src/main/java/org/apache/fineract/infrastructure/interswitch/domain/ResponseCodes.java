package org.apache.fineract.infrastructure.interswitch.domain;

import org.apache.commons.lang.StringUtils;

public enum ResponseCodes 
{

	APPROVED(00,"responsecode.approved"),
	ERROR(06,"responsecode.error"),
	INVALIDCARDNUMBER(14,"responsecode.invalidcardnumber"),
	EXPIREDCARD_CAPTURE(33,"responsecode.expiredcard_capture"),
	LOSTCARD_CAPTURE(41,"responsecode.lostcard_capture"),
	STOLENCARD_CAPTURE(43,"responsecode.stolencard_capture"),
	NOTSUFFICIENTFUNDS(51,"responsecode.notsufficientfunds"),
	EXPIREDCARD(54,"responsecode.expiredcard"),
	INCORRECTPIN(55,"responsecode.incorrectpin");
	
	
	 private final Integer value;
	    private final String code;
	    private static final String SPACE = " ";

	    private ResponseCodes(final Integer value, final String code) {
	        this.value = value;
	        this.code = code;
	    }

	    public Integer getValue() {
	        return this.value;
	    }

	    public String getCode() {
	        return this.code;
	    }
	    
	    
	    public static ResponseCodes fromInt(final Integer transactionType) {

	        if (transactionType == null) { return ResponseCodes.ERROR; }

	        ResponseCodes responseCodesType = ResponseCodes.ERROR;
	        switch (transactionType) {
	            case 00:
	            	responseCodesType = ResponseCodes.APPROVED;
	            break;
	            case 14:
	            	responseCodesType = ResponseCodes.INVALIDCARDNUMBER;
	            break;
	            case 33:
	            	responseCodesType = ResponseCodes.EXPIREDCARD_CAPTURE;
	            break;
	            case 41:
	            	responseCodesType = ResponseCodes.LOSTCARD_CAPTURE;
	            break;
	            case 43:
	            	responseCodesType = ResponseCodes.STOLENCARD_CAPTURE;
	            break;
	            case 51:
	            	responseCodesType = ResponseCodes.NOTSUFFICIENTFUNDS;
	            break;
	            case 54:
	            	responseCodesType = ResponseCodes.EXPIREDCARD;
	            break;
	            case 55:
	            	responseCodesType = ResponseCodes.INCORRECTPIN;
	            break;
	        }
	        return responseCodesType;
	    }
	    
	    
	    @Override
	    public String toString() {
	        return StringUtils.replace(code, "_", SPACE);
	    }
	    
	    
	    
}
