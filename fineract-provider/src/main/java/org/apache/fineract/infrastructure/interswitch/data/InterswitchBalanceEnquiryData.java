package org.apache.fineract.infrastructure.interswitch.data;

import java.math.BigDecimal;

public class InterswitchBalanceEnquiryData 
{
	
	String account_type;
	
	String amount_type;
	
	String currency;
	
	BigDecimal amount;
	
	String amount_sign;
	
	private InterswitchBalanceEnquiryData(String accountType,String amountType,String currency,
			BigDecimal amount,String amountSign)
	{
		this.account_type=accountType;
		this.amount_type=amountType;
		this.currency=currency;
		this.amount=amount;
		this.amount_sign=amountSign;
	}
	
	
	public static InterswitchBalanceEnquiryData getInstance(String accountType,String amountType,String currency,
			BigDecimal amount,String amountSign)
	{
		return new InterswitchBalanceEnquiryData(accountType,amountType,currency,
			amount,amountSign);
	}

	public String getAccountType() {
		return account_type;
	}

	public void setAccountType(String accountType) {
		this.account_type = accountType;
	}

	public String getAmountType() {
		return amount_type;
	}

	public void setAmountType(String amountType) {
		this.amount_type = amountType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getAmountSign() {
		return amount_sign;
	}

	public void setAmountSign(String amountSign) {
		this.amount_sign = amountSign;
	}
	
	
	
	
	
}
