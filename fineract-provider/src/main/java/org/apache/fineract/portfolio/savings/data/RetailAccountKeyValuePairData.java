package org.apache.fineract.portfolio.savings.data;

public class RetailAccountKeyValuePairData 
{

	private long id;
	
	private long transactionId;
	
	private String dataType;
	
	private String entrykey;
	
	private String entryValue;
	
	private long retailAccountId;
	
	private boolean isConstant;
	
	private String constantValue;
	
	private RetailAccountKeyValuePairData(long id,long transactionId,String dataType,
			String entryKey,String entryValue,long retailAccountId,
			boolean isConstant,String constantValue)
	{
		this.id=id;
		this.transactionId=transactionId;
		this.dataType=dataType;
		this.entrykey=entryKey;
		this.entryValue=entryValue;
		this.retailAccountId=retailAccountId;
		this.isConstant=isConstant;
		this.constantValue=constantValue;
		
	}
	
	public static RetailAccountKeyValuePairData getInstance(long id,long transactionId,String dataType,
			String entryKey,String entryValue,long retailAccountId,
			boolean isConstant,String constantValue)
	{
		return new RetailAccountKeyValuePairData(id,transactionId,dataType,
				entryKey,entryValue,retailAccountId,isConstant,constantValue);
	}
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getEntrykey() {
		return entrykey;
	}

	public void setEntrykey(String entrykey) {
		this.entrykey = entrykey;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public long getRetailAccountId() {
		return retailAccountId;
	}

	public void setRetailAccountId(long retailAccountId) {
		this.retailAccountId = retailAccountId;
	}

	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}

	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	
	
	
}
