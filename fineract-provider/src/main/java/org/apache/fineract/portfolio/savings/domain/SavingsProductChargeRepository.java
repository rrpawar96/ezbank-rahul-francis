package org.apache.fineract.portfolio.savings.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SavingsProductChargeRepository extends JpaRepository<SavingsProductCharge, Long>, JpaSpecificationExecutor<SavingsProductCharge> 
{
	List<SavingsProductCharge> findBySavingsProductId(long id);
}