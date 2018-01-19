package org.apache.fineract.infrastructure.interswitch.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;


@Entity
@Table(name = "idt_interswitch_subevents")
public class InterswitchSubEvent extends AbstractPersistableCustom<Long>
{	
	


	@Column(name="transaction_type")
	private int transactionType;
	
	@ManyToOne
	@JoinColumn(name="interswitch_events_id",nullable=true)
	private InterswitchEvent interswitchEvents;
	
	
	@OneToOne
	@JoinColumn(name="interswitch_subevent_id",nullable=true)
	private SavingsAccountTransaction interswitchSubTransaction;
	
	public InterswitchSubEvent(int transactionType,InterswitchEvent interswitchEvents,SavingsAccountTransaction interswitchSubTransactions)
	{	
		this.transactionType=transactionType;
		this.interswitchEvents=interswitchEvents;
		this.interswitchSubTransaction=interswitchSubTransactions;
	}
	
	public static InterswitchSubEvent getInstance(int transactionType,InterswitchEvent interswitchEvents,SavingsAccountTransaction interswitchSubTransactions)
	{
		return new InterswitchSubEvent(transactionType,interswitchEvents,interswitchSubTransactions);
	}
	
	

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public InterswitchEvent getInterswitchEvents() {
		return interswitchEvents;
	}

	public void setInterswitchEvents(InterswitchEvent interswitchEvents) {
		this.interswitchEvents = interswitchEvents;
	}

	public SavingsAccountTransaction getInterswitchSubTransactions() {
		return interswitchSubTransaction;
	}

	public void setInterswitchSubTransactions(SavingsAccountTransaction interswitchSubTransactions) {
		this.interswitchSubTransaction = interswitchSubTransactions;
	}



	
}
