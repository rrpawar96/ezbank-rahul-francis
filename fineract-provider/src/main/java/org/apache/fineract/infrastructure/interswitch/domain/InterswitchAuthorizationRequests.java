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
	@Column(name="transaction_date")
	private Date transactionDate;
	
	@Temporal(TemporalType.TIME)
	@Column(name="transaction_time")
	private LocalTime transactionTime;
	
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
	
	
	public InterswitchAuthorizationRequests(String sessionId,
			BigDecimal settlementAmount,Date transactionDate,LocalTime transactionTime,Date settlementDate,boolean isSettled,boolean isReversed,boolean isAdviced,
			Date settledOn)
	{
		this.sessionId=sessionId;
		this.settlementAmount=settlementAmount;
		this.transactionDate=transactionDate;
		this.transactionTime=transactionTime;
		this.settlementDate=settlementDate;
		this.isSettled=isSettled;
		this.isReversed=isReversed;
		this.isAdviced=isAdviced;
		this.settledOn=settledOn;
				
	}
	
	public static InterswitchAuthorizationRequests getInstance(String sessionId,
			BigDecimal settlementAmount,Date transactionDate,LocalTime transactionTime,Date settlementDate,boolean isSettled,boolean isReversed,boolean isAdviced,
			Date settledOn)
	{
		return new InterswitchAuthorizationRequests(sessionId,settlementAmount,
				transactionDate,transactionTime,settlementDate,isSettled,isReversed,isAdviced,settledOn	);
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
