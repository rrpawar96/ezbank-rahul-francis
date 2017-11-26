package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.joda.time.LocalDate;

@Entity
@Table(name = "idt_interswitch_card_details")
public class InterswitchCardDetails extends AbstractPersistableCustom<Long>
{

	@Column(name="primary_account_number")
	private BigInteger primaryAccountNumber;
	
	@OneToOne
	@JoinColumn(name="savings_account_id")
	private SavingsAccount savingsAccount;
	
	@Column(name="cvv")
	private int cvv;
	
	@Column(name="valid_from")
	private LocalDate validFrom;
	
	@Column(name="valid_through")
	private LocalDate validThrough;
	
	@Column(name="pin")
	private String pin;
	
	public InterswitchCardDetails(final BigInteger primaryAccountNumber,SavingsAccount savingsAccount,
			int cvv,LocalDate validFrom,LocalDate validThrough,String pin)
	{
		this.primaryAccountNumber=primaryAccountNumber;
		this.savingsAccount=savingsAccount;
		this.cvv=cvv;
		this.validFrom=validFrom;
		this.validThrough=validThrough;
		this.pin=pin;
	}
	
	public static InterswitchCardDetails getInstance(final BigInteger primaryAccountNumber,SavingsAccount savingsAccount,
			int cvv,LocalDate validFrom,LocalDate validThrough,String pin)
	{
		return new InterswitchCardDetails(primaryAccountNumber,savingsAccount,cvv,validFrom,validThrough,pin);
	}
	
	

	public BigInteger getPrimaryAccountNumber() {
		return primaryAccountNumber;
	}

	public void setPrimaryAccountNumber(BigInteger primaryAccountNumber) {
		this.primaryAccountNumber = primaryAccountNumber;
	}

	public SavingsAccount getSavingsAccount() {
		return savingsAccount;
	}

	public void setSavingsAccount(SavingsAccount savingsAccount) {
		this.savingsAccount = savingsAccount;
	}

	public int getCvv() {
		return cvv;
	}

	public void setCvv(int cvv) {
		this.cvv = cvv;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public LocalDate getValidThrough() {
		return validThrough;
	}

	public void setValidThrough(LocalDate validThrough) {
		this.validThrough = validThrough;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	
	
	
	
}
