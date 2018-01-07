package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.creditbureau.domain.CreditBureauLoanProductMapping;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

@Entity
@Table(name = "idt_interswitch_events")
public class InterswitchEvents extends AbstractPersistableCustom<Long>
{

	@Column(name="session_id")
	private String sessionId;
	
	@Column(name="event_type")
	private int eventType;
	
	@Column(name="transaction_processing_type")
	private int transactionProcessingType;
	
	@Column(name="transaction_amount_type")
	private int transactionAmountType;
	
	@Column(name="response_code")
	private int responseCode;
	
	@Column(name="stan")
	private String stan;
	
	@Column(name="authorization_number")
	private String authorizationNumber;
		
	@OneToOne
	@JoinColumn(name="application_transaction_id")
	private SavingsAccountTransaction applicationTransaction;

	@Column(name="settlement_amount")
	private BigDecimal settlementAmount;
	
	@Temporal(TemporalType.DATE)
	@Column(name="settlement_date")
	private Date settlementDate;
	
	@Column(name="local_transaction_time")
	private String transactionTime;
	
	@OneToMany(mappedBy = "interswitchEvents")
	private List<InterswitchSubEvents> interSwitchSubEvents;
	
	public InterswitchEvents(String sessionId,int eventType,int transactionProcessingType,
			int transactionAmountType,int responseCode,String stan,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
			BigDecimal settlementAmount,Date settlementDate,String transactionTime)
	{
		this.sessionId=sessionId;
		this.eventType=eventType;
		this.transactionProcessingType=transactionProcessingType;
		this.transactionAmountType=transactionAmountType;
		this.responseCode=responseCode;
		this.stan=stan;
		this.authorizationNumber=authorizationNumber;
		this.applicationTransaction=applicationTransaction;
		this.settlementAmount=settlementAmount;
		this.settlementDate=settlementDate;
		this.transactionTime=transactionTime;
		
	}
	
	public static InterswitchEvents getInstance(String sessionId,int eventType,int transactionProcessingType,
			int transactionAmountType,int responseCode,String stan,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
			BigDecimal settlementAmount,Date settlementDate,String transactionTime)
	{
		return new InterswitchEvents(sessionId, eventType, transactionProcessingType,
				 transactionAmountType, responseCode, stan, authorizationNumber, applicationTransaction,
				 settlementAmount, settlementDate, transactionTime);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public int getTransactionProcessingType() {
		return transactionProcessingType;
	}

	public void setTransactionProcessingType(int transactionProcessingType) {
		this.transactionProcessingType = transactionProcessingType;
	}

	public int getTransactionAmountType() {
		return transactionAmountType;
	}

	public void setTransactionAmountType(int transactionAmountType) {
		this.transactionAmountType = transactionAmountType;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getAuthorizationNumber() {
		return authorizationNumber;
	}

	public void setAuthorizationNumber(String authorizationNumber) {
		this.authorizationNumber = authorizationNumber;
	}

	public SavingsAccountTransaction getApplicationTransaction() {
		return applicationTransaction;
	}

	public void setApplicationTransaction(SavingsAccountTransaction applicationTransaction) {
		this.applicationTransaction = applicationTransaction;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public List<InterswitchSubEvents> getInterSwitchSubEvents() {
		return interSwitchSubEvents;
	}

	public void setInterSwitchSubEvents(List<InterswitchSubEvents> interSwitchSubEvents) {
		this.interSwitchSubEvents = interSwitchSubEvents;
	}




	
	
	
	
	
}
