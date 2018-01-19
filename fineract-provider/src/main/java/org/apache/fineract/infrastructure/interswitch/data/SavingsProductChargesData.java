package org.apache.fineract.infrastructure.interswitch.data;

public class SavingsProductChargesData 
{
	
	long savingsProductId;
	
	long chargesId;
	
	public SavingsProductChargesData(long savingsProductId,long chargesId)
	{
		this.savingsProductId=savingsProductId;
		this.chargesId=chargesId;
	}
	
	public static SavingsProductChargesData getInstance(long savingsProductId,long chargesId)
	{
		return new SavingsProductChargesData(savingsProductId,chargesId);
	}

	public long getSavingsProductId() {
		return savingsProductId;
	}

	public void setSavingsProductId(long savingsProductId) {
		this.savingsProductId = savingsProductId;
	}

	public long getChargesId() {
		return chargesId;
	}

	public void setChargesId(long chargesId) {
		this.chargesId = chargesId;
	}
	
	

}
