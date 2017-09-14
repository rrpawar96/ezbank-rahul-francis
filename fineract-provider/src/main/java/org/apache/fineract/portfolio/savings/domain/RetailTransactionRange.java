package org.apache.fineract.portfolio.savings.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

@Entity
@Table(name = "idt_retail_transaction_range")
public class RetailTransactionRange  extends AbstractPersistableCustom<Long>  
{

	@OneToOne
    @JoinColumn(name = "retail_savings_id", nullable = false)
    private SavingsAccount retailSavings;
	
	@Column(name="transaction_upper_limit", nullable = false)
	private BigDecimal upperLimit;
	
	@Column(name="transaction_lower_limit", nullable = false)
	private BigDecimal lowerLimit;
	
	@Column(name="current_transaction_id_used", nullable = false)
	private BigDecimal currentTransactionId;
	
	private RetailTransactionRange(final SavingsAccount retailSavings,final BigDecimal upperLimit,
			final BigDecimal lowerLimit,final BigDecimal currentTransactionId)
	{
		this.retailSavings=retailSavings;
		this.upperLimit=upperLimit;
		this.lowerLimit=lowerLimit;
		this.currentTransactionId=currentTransactionId;
		
	}
	
	public static RetailTransactionRange getInstance(final SavingsAccount retailSavings,final BigDecimal upperLimit,
			final BigDecimal lowerLimit,final BigDecimal currentTransactionId)
	{
		return new RetailTransactionRange (retailSavings,upperLimit,lowerLimit,currentTransactionId);
	}

	public SavingsAccount getRetailSavings() {
		return retailSavings;
	}

	public void setRetailSavings(SavingsAccount retailSavings) {
		this.retailSavings = retailSavings;
	}

	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public BigDecimal getCurrentTransactionId() {
		return currentTransactionId;
	}

	public void setCurrentTransactionId(BigDecimal currentTransactionId) {
		this.currentTransactionId = currentTransactionId;
	}
	
	
	
	
	
}
