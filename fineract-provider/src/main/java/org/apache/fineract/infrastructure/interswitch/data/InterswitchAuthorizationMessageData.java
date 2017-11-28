package org.apache.fineract.infrastructure.interswitch.data;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.joda.time.LocalDate;

public class InterswitchAuthorizationMessageData 
{
	
	private final String sessionId;
	
	private final BigInteger authorizationNumber;
	
	private final BigInteger transactionId;
	
	private final BigDecimal authorizationAmount;
	
	private final BigDecimal settlementAmount;
	
	private final int settlementCurrency;
	
	private final BigDecimal settlementCurrencyRate;
	
	private final int transactionCurrency;
	
	private final LocalDate transactionDate;
	
	private final LocalDate settlementDate;
	
	private final boolean isSettled;
	
	private final boolean isReversed;
	
	private final boolean isAdviced;
	
	private final LocalDate settledOn;
	
	
	private InterswitchAuthorizationMessageData(final String sessionId,final BigInteger authorizationNumber,final BigInteger transactionId,
			final BigDecimal authorizationAmount,final BigDecimal settlementAmount,final int settlementCurrency,
			final BigDecimal settlementCurrencyRate,final int transactionCurrency,final LocalDate transactionDate,
			final LocalDate settlementDate,final boolean isSettled,final boolean isReversed,final boolean isAdviced,
			final LocalDate settledOn)
	{
		this.sessionId=sessionId;
		this.authorizationNumber=authorizationNumber;
		this.transactionId=transactionId;
		this.authorizationAmount=authorizationAmount;
		this.settlementAmount=settlementAmount;
		this.settlementCurrency=settlementCurrency;
		this.settlementCurrencyRate=settlementCurrencyRate;
		this.transactionCurrency=transactionCurrency;
		this.transactionDate=transactionDate;
		this.settlementDate=settlementDate;
		this.isSettled=isSettled;
		this.isReversed=isReversed;
		this.isAdviced=isAdviced;
		this.settledOn=settledOn;
		
		
		
	}
	
	
	public String getSessionId() {
		return sessionId;
	}
	public BigInteger getAuthorizationNumber() {
		return authorizationNumber;
	}
	public BigInteger getTransactionId() {
		return transactionId;
	}
	public BigDecimal getAuthorizationAmount() {
		return authorizationAmount;
	}
	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}
	public int getSettlementCurrency() {
		return settlementCurrency;
	}
	public BigDecimal getSettlementCurrencyRate() {
		return settlementCurrencyRate;
	}
	public int getTransactionCurrency() {
		return transactionCurrency;
	}
	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	public LocalDate getSettlementDate() {
		return settlementDate;
	}
	public boolean isSettled() {
		return isSettled;
	}
	public boolean isReversed() {
		return isReversed;
	}
	public boolean isAdviced() {
		return isAdviced;
	}
	public LocalDate getSettledOn() {
		return settledOn;
	}
	
	
	
	

}
