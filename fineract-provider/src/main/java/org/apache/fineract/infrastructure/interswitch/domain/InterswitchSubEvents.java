package org.apache.fineract.infrastructure.interswitch.domain;

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
	@ManyToOne
	@JoinColumn(name="interswitch_events_id")
	private InterswitchEvents interswitchEvents;
	
	
	@OneToOne
	@JoinColumn(name="interswitch_events_id")
	private SavingsAccountTransaction interswitchSubEvent;
	
	public InterswitchSubEvents(InterswitchEvents interswitchEvents,SavingsAccountTransaction interswitchSubEvent)
	{
		this.interswitchEvents=interswitchEvents;
		this.interswitchEvents=interswitchEvents;
	}
	
	public InterswitchSubEvents getInstance(InterswitchEvents interswitchEvents,SavingsAccountTransaction interswitchSubEvent)
	{
		return new InterswitchSubEvents(interswitchEvents,interswitchSubEvent);
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
