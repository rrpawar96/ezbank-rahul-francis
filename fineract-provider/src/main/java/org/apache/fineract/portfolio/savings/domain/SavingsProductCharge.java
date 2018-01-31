package org.apache.fineract.portfolio.savings.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "m_savings_product_charge")
public class SavingsProductCharge
{	
	
	@Column(name = "savings_product_id")
	private long savingsProductId;
	
	@Column(name = "charge_id")
	private long chargeId;
	
	public SavingsProductCharge(long savingsProductId,long chargeId)
	{
		this.savingsProductId=savingsProductId;
		this.chargeId=chargeId;
	}

	public long getSavingsProductId() {
		return savingsProductId;
	}

	public void setSavingsProductId(long savingsProductId) {
		this.savingsProductId = savingsProductId;
	}

	public long getChargeId() {
		return chargeId;
	}

	public void setChargeId(long chargeId) {
		this.chargeId = chargeId;
	}

	


	
	
	
	
}
