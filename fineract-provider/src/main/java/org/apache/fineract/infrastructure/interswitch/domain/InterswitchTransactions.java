package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate;

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

	
	@Column(name="transaction_amount")
	private BigDecimal transactionAmount;
	
	@Column(name="transaction_date")
	private LocalDate transactionDate;
	
	@Column(name="is_reversed")
	private boolean isReversed;
	
	@Column(name="is_adviced")
	private boolean isAdviced;
	
	@OneToOne(mappedBy="interswitchtransaction")
	private InterswitchAuthorizationRequests authorizationRequests;
	
	
	public InterswitchTransactions(String sessionId,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
			BigDecimal transactionAmount,LocalDate transactionDate,boolean isReversed,boolean isAdviced)
	{
		this.sessionId=sessionId;
		this.authorizationNumber=authorizationNumber;
		this.applicationTransaction=applicationTransaction;
		this.transactionAmount=transactionAmount;
		this.transactionDate=transactionDate;
		this.isReversed=isReversed;
		this.isAdviced=isAdviced;
	}
	
	public static InterswitchTransactions getInstance(String sessionId,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
			BigDecimal transactionAmount,LocalDate transactionDate,boolean isReversed,boolean isAdviced)
	{
		return new InterswitchTransactions(sessionId,authorizationNumber,applicationTransaction,
				transactionAmount,transactionDate,isReversed,isAdviced);
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
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


	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}


	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}


	public LocalDate getTransactionDate() {
		return transactionDate;
	}


	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
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
	
	
	
	
}
