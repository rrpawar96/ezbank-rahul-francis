package org.apache.fineract.portfolio.savings.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RetailAccountEntryTypeRepository extends JpaRepository<RetailAccountEntryType, Long>, JpaSpecificationExecutor<RetailAccountEntryType> 
{
	
	List<RetailAccountEntryType> findByRetailAccount(SavingsAccount account);
	
	RetailAccountEntryType findOneByRetailAccountAndName(SavingsAccount account,String name );

}
