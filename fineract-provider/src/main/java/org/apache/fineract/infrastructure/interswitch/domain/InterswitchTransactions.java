package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

@Entity
@Table(name = "idt_interswitch_transactions")
public class InterswitchTransactions extends AbstractPersistableCustom<Long>
{

	@Column(name="session_id")
	private String sessionId;
	
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
	
	@Column(name="is_reversed")
	private boolean isReversed;
	
	@Column(name="is_adviced")
	private boolean isAdviced;
	
	@Column(name="is_debit")
	private boolean isDebit;
	
	public InterswitchTransactions(String sessionId,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
			BigDecimal settlementAmount,Date settlementDate,String transactionTime,
			boolean isReversed,boolean isAdviced,boolean isDebit)
	{
		this.sessionId=sessionId;
		this.authorizationNumber=authorizationNumber;
		this.applicationTransaction=applicationTransaction;
		this.settlementAmount=settlementAmount;
		this.settlementDate=settlementDate;
		this.transactionTime=transactionTime;
		this.isReversed=isReversed;
		this.isAdviced=isAdviced;
		this.isDebit=isDebit;
	}
	
	public static InterswitchTransactions getInstance(String sessionId,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
			BigDecimal settlementAmount,Date settlementDate,String transactionTime,
			boolean isReversed,boolean isAdviced,boolean isDebit)
	{
		return new InterswitchTransactions(sessionId,authorizationNumber,applicationTransaction,
				settlementAmount,settlementDate,transactionTime,isReversed,isAdviced,isDebit);
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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

	public boolean isReversed() {
		return isReversed;
	}


	public void setReversed(boolean isReversed) {
		this.isReversed = isReversed;
	}


	public boolean isAdviced() {
		return isAdviced;
	}


	public void setAdviced(boolean isAdviced) {
		this.isAdviced = isAdviced;
	}


	public boolean isDebit() {
		return isDebit;
	}

	public void setDebit(boolean isDebit) {
		this.isDebit = isDebit;
	}
	
	
	
	
}
