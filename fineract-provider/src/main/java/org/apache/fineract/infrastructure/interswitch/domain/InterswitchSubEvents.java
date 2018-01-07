package org.apache.fineract.infrastructure.interswitch.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;


@Entity
@Table(name ="idt_interswitch_subevents")
public class InterswitchSubEvents 
{	
	
	@Column(name="transaction_type")
	private int transactionType;
	
	@ManyToOne
	@JoinColumn(name="interswitch_events_id",nullable=true)
	private InterswitchEvents interswitchEvents;
	
	
	@OneToOne
	@JoinColumn(name="interswitch_subevent_id",nullable=true)
	private SavingsAccountTransaction interswitchSubTransactions;
	
	public InterswitchSubEvents(int transactionType,InterswitchEvents interswitchEvents,SavingsAccountTransaction interswitchSubTransactions)
	{	
		this.transactionType=transactionType;
		this.interswitchEvents=interswitchEvents;
		this.interswitchSubTransactions=interswitchSubTransactions;
	}
	
	public static InterswitchSubEvents getInstance(int transactionType,InterswitchEvents interswitchEvents,SavingsAccountTransaction interswitchSubTransactions)
	{
		return new InterswitchSubEvents(transactionType,interswitchEvents,interswitchSubTransactions);
	}
	
	

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public InterswitchEvents getInterswitchEvents() {
		return interswitchEvents;
	}

	public void setInterswitchEvents(InterswitchEvents interswitchEvents) {
		this.interswitchEvents = interswitchEvents;
	}

	public SavingsAccountTransaction getInterswitchSubTransactions() {
		return interswitchSubTransactions;
	}

	public void setInterswitchSubTransactions(SavingsAccountTransaction interswitchSubTransactions) {
		this.interswitchSubTransactions = interswitchSubTransactions;
	}



	
}
