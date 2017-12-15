package org.apache.fineract.infrastructure.interswitch.data;

import java.math.BigDecimal;

public class InterswitchBalanceEnquiryData 
{

	/*account_type : (savings=10)
	amount_type: available balance 02
	currency: uganda curruncy
	amount:  figure
	amount_sign*/
	
	
	String accountType;
	
	String amountType;
	
	String currency;
	
	BigDecimal amount;
	
	String amountSign;
	
	private InterswitchBalanceEnquiryData(String accountType,String amountType,String currency,
			BigDecimal amount,String amountSign)
	{
		this.accountType=accountType;
		this.amountType=amountType;
		this.currency=currency;
		this.amount=amount;
		this.amountSign=amountSign;
	}
	
	
	public static InterswitchBalanceEnquiryData getInstance(String accountType,String amountType,String currency,
			BigDecimal amount,String amountSign)
	{
		return new InterswitchBalanceEnquiryData(accountType,amountType,currency,
			amount,amountSign);
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
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
		return amountSign;
	}

	public void setAmountSign(String amountSign) {
		this.amountSign = amountSign;
	}
	
	
	
	
	
}
