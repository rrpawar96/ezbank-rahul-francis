package org.apache.fineract.portfolio.savings.data;

public class RetailAccountEntryTypeData 
{
	private long id;
	
	private String dataType;
	
	private String entryKey;
	
	private long retailSavingsId;
	
	private boolean isConstant;
	
	private String constantValue;
	
	
	private RetailAccountEntryTypeData(long id,String dataType,String entryKey,long retailSavingsId,
			boolean isConstant,String constantValue)
	{
		this.id=id;
		this.dataType=dataType;
		this.entryKey=entryKey;
		this.retailSavingsId=retailSavingsId;
		this.isConstant=isConstant;
		this.constantValue=constantValue;
	}
	
	public static RetailAccountEntryTypeData getInstance(long id,String dataType,String entryKey,long retailSavingsId,
			boolean isConstant,String constantValue)
	{
		return new RetailAccountEntryTypeData(id,dataType,entryKey,retailSavingsId,isConstant,constantValue);
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return entryKey;
	}

	public void setName(String name) {
		this.entryKey = name;
	}

	public long getRetailSavingsId() {
		return retailSavingsId;
	}

	public void setRetailSavingsId(long retailSavingsId) {
		this.retailSavingsId = retailSavingsId;
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
