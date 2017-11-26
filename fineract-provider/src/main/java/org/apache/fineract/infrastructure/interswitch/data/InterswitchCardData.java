package org.apache.fineract.infrastructure.interswitch.data;

import java.math.BigInteger;

import org.joda.time.LocalDate;

public class InterswitchCardData 
{

	private final BigInteger primaryAccountNumber;
	
	private final long savingsAccountid;
	
	private final int cvv;
	
	private final LocalDate validFrom;
	
	private final LocalDate validThrough; 
	
	private final String pin;
	
	private InterswitchCardData(final BigInteger primaryAccountNumber,final long savingsAccountid,
			 final int cvv,final LocalDate validFrom,final LocalDate validThrough,
			 final String pin)
	{
		this.primaryAccountNumber=primaryAccountNumber;
		this.savingsAccountid=savingsAccountid;
		this.cvv=cvv;
		this.validFrom=validFrom;
		this.validThrough=validThrough;
		this.pin=pin;
	}
	
	public static InterswitchCardData getInterswitchCardDetailsInstance(final BigInteger primaryAccountNumber,final long savingsAccountid,
			 final int cvv,final LocalDate validFrom,final LocalDate validThrough,
			 final String pin)
	{
		return new InterswitchCardData(primaryAccountNumber,savingsAccountid,cvv,validFrom,validThrough,
				pin);
	}

	public BigInteger getPrimaryAccountNumber() {
		return primaryAccountNumber;
	}

	public long getSavingsAccountid() {
		return savingsAccountid;
	}

	public int getCvv() {
		return cvv;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public LocalDate getValidThrough() {
		return validThrough;
	}

	public String getPin() {
		return pin;
	}
	
	
}
