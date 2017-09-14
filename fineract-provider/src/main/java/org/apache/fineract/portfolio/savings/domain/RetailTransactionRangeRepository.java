package org.apache.fineract.portfolio.savings.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RetailTransactionRangeRepository extends JpaRepository<RetailTransactionRange, Long>, JpaSpecificationExecutor<RetailTransactionRange> 
{
	
	
	RetailTransactionRange findOneByRetailSavingsId(long savingsId );
	
	RetailTransactionRange findOneByRetailSavings(SavingsAccount account );
	
	
	
}
