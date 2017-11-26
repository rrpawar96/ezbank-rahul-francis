package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.joda.time.LocalDate;

@Entity
@Table(name = "idt_interswitch_authorization_requests")
public class InterswitchAuthorizationRequests extends AbstractPersistableCustom<Long>
{

	
	@Column(name="session_id")
	private String sessionId;
	
	@Column(name="authorization_number")
	private BigInteger authorizationNumber;
	
	@OneToOne
	@JoinColumn(name="transaction_id")
	private InterswitchTransactions interswitchtransaction;
	
	@Column(name="authorization_amount")
	private BigDecimal authorizationAmount;
	
	@Column(name="settlement_amount")
	private BigDecimal settlementAmount;
	
	@Column(name="settlement_currency")
	private String settlementCurrency;
	
	@Column(name="settlement_currency_rate")
	private BigDecimal settlementCurrencyRate;
	
	@Column(name="transaction_currency")
	private String transactionCurrency;
	
	@Column(name="transaction_date")
	private LocalDate transactionDate;
	
	@Column(name="settlement_date")
	private LocalDate settlementDate;
	
	@Column(name="is_settled")
	private boolean isSettled;
	
	
	public InterswitchAuthorizationRequests(String sessionId,BigInteger authorizationNumber,InterswitchTransactions interswitchtransaction,
			BigDecimal authorizationAmount,BigDecimal settlementAmount,String settlementCurrency,BigDecimal settlementCurrencyRate,
			String transactionCurrency,LocalDate transactionDate,LocalDate settlementDate,boolean isSettled)
	{
		this.sessionId=sessionId;
		this.authorizationNumber=authorizationNumber;
		this.interswitchtransaction=interswitchtransaction;
		this.authorizationAmount=authorizationAmount;
		this.settlementAmount=authorizationAmount;
		this.settlementCurrency=settlementCurrency;
		this.settlementCurrencyRate=settlementCurrencyRate;
		this.transactionCurrency=transactionCurrency;
		this.transactionDate=transactionDate;
		this.settlementDate=settlementDate;
		this.isSettled=isSettled;
				
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public BigInteger getAuthorizationNumber() {
		return authorizationNumber;
	}


	public void setAuthorizationNumber(BigInteger authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}





	public InterswitchTransactions getInterswitchtransaction() {
		return interswitchtransaction;
	}


	public void setInterswitchtransaction(InterswitchTransactions interswitchtransaction) {
		this.interswitchtransaction = interswitchtransaction;
	}


	public BigDecimal getAuthorizationAmount() {
		return authorizationAmount;
	}


	public void setAuthorizationAmount(BigDecimal authorizationAmount) {
		this.authorizationAmount = authorizationAmount;
	}


	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}


	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}


	public String getSettlementCurrency() {
		return settlementCurrency;
	}


	public void setSettlementCurrency(String settlementCurrency) {
		this.settlementCurrency = settlementCurrency;
	}


	public BigDecimal getSettlementCurrencyRate() {
		return settlementCurrencyRate;
	}


	public void setSettlementCurrencyRate(BigDecimal settlementCurrencyRate) {
		this.settlementCurrencyRate = settlementCurrencyRate;
	}


	public String getTransactionCurrency() {
		return transactionCurrency;
	}


	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}


	public LocalDate getTransactionDate() {
		return transactionDate;
	}


	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}


	public LocalDate getSettlementDate() {
		return settlementDate;
	}


	public void setSettlementDate(LocalDate settlementDate) {
		this.settlementDate = settlementDate;
	}


	public boolean isSettled() {
		return isSettled;
	}


	public void setSettled(boolean isSettled) {
		this.isSettled = isSettled;
	}
	
	
	
	
	
}
