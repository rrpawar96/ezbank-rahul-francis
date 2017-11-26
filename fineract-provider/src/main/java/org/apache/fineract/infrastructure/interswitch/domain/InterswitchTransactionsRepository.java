package org.apache.fineract.infrastructure.interswitch.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterswitchTransactionsRepository 
extends JpaRepository<InterswitchTransactions, Long>, JpaSpecificationExecutor<InterswitchTransactions>
{

}
