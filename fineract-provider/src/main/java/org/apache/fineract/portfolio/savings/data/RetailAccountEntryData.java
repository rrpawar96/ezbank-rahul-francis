package org.apache.fineract.portfolio.savings.data;

public class RetailAccountEntryData 
{
	
	private long id;
	
	private long retailEntryTypeId;
	
	private String entryValue;
	
	private long retailTransactionId;
	
	private RetailAccountEntryData(long id,long retailEntryTypeId,String entryValue,
			long retailTransactionId)
	{
		this.id=id;
		this.retailTransactionId=retailEntryTypeId;
		this.entryValue=entryValue;
		this.retailTransactionId=retailTransactionId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRetailEntryTypeId() {
		return retailEntryTypeId;
	}

	public void setRetailEntryTypeId(long retailEntryTypeId) {
		this.retailEntryTypeId = retailEntryTypeId;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public long getRetailTransactionId() {
		return retailTransactionId;
	}

	public void setRetailTransactionId(long retailTransactionId) {
		this.retailTransactionId = retailTransactionId;
	}
	
	

}
