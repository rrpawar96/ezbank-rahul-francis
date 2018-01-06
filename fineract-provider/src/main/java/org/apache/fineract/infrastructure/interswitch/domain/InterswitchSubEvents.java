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
	@JoinColumn(name="interswitch_events_id")
	private InterswitchEvents interswitchEvents;
	
	
	@OneToOne
	@JoinColumn(name="interswitch_events_id")
	private SavingsAccountTransaction interswitchSubEvent;
	
	public InterswitchSubEvents(int transactionType,InterswitchEvents interswitchEvents,SavingsAccountTransaction interswitchSubEvent)
	{	
		this.transactionType=transactionType;
		this.interswitchEvents=interswitchEvents;
		this.interswitchEvents=interswitchEvents;
	}
	
	public static InterswitchSubEvents getInstance(int transactionType,InterswitchEvents interswitchEvents,SavingsAccountTransaction interswitchSubEvent)
	{
		return new InterswitchSubEvents(transactionType,interswitchEvents,interswitchSubEvent);
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

	public SavingsAccountTransaction getInterswitchSubEvent() {
		return interswitchSubEvent;
	}

	public void setInterswitchSubEvent(SavingsAccountTransaction interswitchSubEvent) {
		this.interswitchSubEvent = interswitchSubEvent;
	}

	
}
