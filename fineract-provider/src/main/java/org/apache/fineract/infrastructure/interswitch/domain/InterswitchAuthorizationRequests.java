package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.joda.time.LocalDate;

@Entity
@Table(name = "idt_interswitch_authorization_requests")
public class InterswitchAuthorizationRequests extends AbstractPersistableCustom<Long>
{

	
	@Column(name="session_id")
	private String sessionId;
	
	@OneToOne
	@JoinColumn(name="transaction_id")
	private InterswitchTransactions interswitchtransaction;
	
	@Column(name="authorization_amount")
	private BigDecimal authorizationAmount;
	
	@Column(name="settlement_amount")
	private BigDecimal settlementAmount;
	
	@Column(name="settlement_currency")
	private int settlementCurrency;
	
	@Column(name="settlement_currency_rate")
	private BigDecimal settlementCurrencyRate;
	
	@Column(name="transaction_currency")
	private int transactionCurrency;
	
	@Temporal(TemporalType.DATE)
	@Column(name="transaction_date")
	private Date transactionDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="settlement_date")
	private Date settlementDate;
	
	@Column(name="is_settled")
	private boolean isSettled;
	
	@Column(name="is_reversed")
	private boolean isReversed;
	
	@Column(name="is_adviced")
	private boolean isAdviced;
	
	@Temporal(TemporalType.DATE)
	@Column(name="settled_on")
	private Date settledOn;
	
	
	public InterswitchAuthorizationRequests(String sessionId,InterswitchTransactions interswitchtransaction,
			BigDecimal authorizationAmount,BigDecimal settlementAmount,int settlementCurrency,BigDecimal settlementCurrencyRate,
			int transactionCurrency,Date transactionDate,Date settlementDate,boolean isSettled,boolean isReversed,boolean isAdviced,Date settledOn)
	{
		this.sessionId=sessionId;
		this.interswitchtransaction=interswitchtransaction;
		this.authorizationAmount=authorizationAmount;
		this.settlementAmount=authorizationAmount;
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
	
	public static InterswitchAuthorizationRequests getInstance(String sessionId,InterswitchTransactions interswitchtransaction,
			BigDecimal authorizationAmount,BigDecimal settlementAmount,int settlementCurrency,BigDecimal settlementCurrencyRate,
			int transactionCurrency,Date transactionDate,Date settlementDate,boolean isSettled,boolean isReversed,boolean isAdviced,Date settledOn)
	{
		return new InterswitchAuthorizationRequests(sessionId,interswitchtransaction,authorizationAmount,settlementAmount,settlementCurrency,
				settlementCurrencyRate,transactionCurrency,transactionDate,settlementDate,isSettled,isReversed,isAdviced,settledOn	);
	}

	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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


	public int getSettlementCurrency() {
		return settlementCurrency;
	}


	public void setSettlementCurrency(int settlementCurrency) {
		this.settlementCurrency = settlementCurrency;
	}


	public BigDecimal getSettlementCurrencyRate() {
		return settlementCurrencyRate;
	}


	public void setSettlementCurrencyRate(BigDecimal settlementCurrencyRate) {
		this.settlementCurrencyRate = settlementCurrencyRate;
	}


	public int getTransactionCurrency() {
		return transactionCurrency;
	}


	public void setTransactionCurrency(int transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}


	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public boolean isSettled() {
		return isSettled;
	}


	public void setSettled(boolean isSettled) {
		this.isSettled = isSettled;
	}
	
	
	
	
	
}
