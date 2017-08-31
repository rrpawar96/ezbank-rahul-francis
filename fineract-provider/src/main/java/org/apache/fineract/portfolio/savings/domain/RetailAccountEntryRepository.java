package org.apache.fineract.portfolio.savings.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RetailAccountEntryRepository extends JpaRepository<RetailAccountEntry, Long>, JpaSpecificationExecutor<RetailAccountEntry> 
{

	RetailAccountEntry findOneByRetailAccountEntryType(RetailAccountEntryType entryType);
	
}
