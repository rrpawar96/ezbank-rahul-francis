package org.apache.fineract.portfolio.savings.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SavingsAccountChargePaidByRepository extends JpaRepository<SavingsAccountChargePaidBy, Long>, JpaSpecificationExecutor<SavingsAccountChargePaidBy>
{
	SavingsAccountChargePaidBy findOneBySavingsAccountTransactionId(long id);
}
