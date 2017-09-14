package org.apache.fineract.portfolio.savings.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name = "retail_account_entry_data")
public class RetailAccountEntry extends AbstractPersistableCustom<Long>
{

	@ManyToOne
	@JoinColumn(name="retail_account_entry_type_id",nullable=false)
	private RetailAccountEntryType retailAccountEntryType;
	
	@Column(name="retail_account_entry_value",nullable=false)
	private String entryValue;
	
	@ManyToOne
	@JoinColumn(name="retail_account_transaction_id",nullable=false)
	private SavingsAccountTransaction retailTransaction;
	
	private RetailAccountEntry(RetailAccountEntryType retailAccountEntryType,
			String entryValue,SavingsAccountTransaction retailTransaction)
	{
		this.retailAccountEntryType=retailAccountEntryType;
		this.entryValue=entryValue;
		this.retailTransaction=retailTransaction;
	}
	
	public static RetailAccountEntry getInstance(RetailAccountEntryType retailAccountEntryType,
			String entryValue,SavingsAccountTransaction retailTransaction)
	{
		return new RetailAccountEntry(retailAccountEntryType,
				entryValue,retailTransaction);
	}

	public RetailAccountEntryType getRetailAccountEntryType() {
		return retailAccountEntryType;
	}

	public void setRetailAccountEntryType(RetailAccountEntryType retailAccountEntryType) {
		this.retailAccountEntryType = retailAccountEntryType;
	}

	public String getEntryValue() {
		return entryValue;
	}

	public void setEntryValue(String entryValue) {
		this.entryValue = entryValue;
	}

	public SavingsAccountTransaction getRetailTransaction() {
		return retailTransaction;
	}

	public void setRetailTransaction(SavingsAccountTransaction retailTransaction) {
		this.retailTransaction = retailTransaction;
	}
	
	
	
	
	
	
}
