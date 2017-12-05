package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "idt_interswitch_authorization_requests")
public class InterswitchAuthorizationRequests extends AbstractPersistableCustom<Long>
{

	
	@Column(name="session_id")
	private String sessionId;

	@Column(name="settlement_amount")
	private BigDecimal settlementAmount;

	
	@Temporal(TemporalType.DATE)
	@Column(name="settlement_date")
	private Date settlementDate;
	
	@Column(name="local_transaction_time")
	private String transactionTime;
	
	@Column(name="is_settled")
	private boolean isSettled;
	

	@Column(name="is_reversed")
	private boolean isReversed;
	
	@Column(name="is_adviced")
	private boolean isAdviced;
	
	@Column(name="response_code")
	private String responseCode;
	
	@Temporal(TemporalType.DATE)
	@Column(name="settled_on")
	private Date settledOn;
	
	@Column(name="is_debit")
	private boolean isDebit;
	
	public InterswitchAuthorizationRequests(String sessionId,
			BigDecimal settlementAmount,Date settlementDate,String transactionTime,boolean isSettled,boolean isReversed,boolean isAdviced,
			String responseCode,Date settledOn,boolean isDebit)
	{
		this.sessionId=sessionId;
		this.settlementAmount=settlementAmount;
		this.settlementDate=settlementDate;
		this.transactionTime=transactionTime;
		this.isSettled=isSettled;
		this.isReversed=isReversed;
		this.isAdviced=isAdviced;
		this.responseCode=responseCode;
		this.settledOn=settledOn;
		this.isDebit=isDebit;
				
	}
	
	public static InterswitchAuthorizationRequests getInstance(String sessionId,
			BigDecimal settlementAmount,Date settlementDate,String transactionTime,boolean isSettled,boolean isReversed,boolean isAdviced,
			String responseCode,Date settledOn,boolean isDebit)
	{
		return new InterswitchAuthorizationRequests(sessionId,settlementAmount,
				settlementDate,transactionTime,isSettled,isReversed,isAdviced,responseCode,settledOn,isDebit	);
	}

	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}


	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}


	public Date getTransactionDate() {
		return settlementDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.settlementDate = transactionDate;
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
