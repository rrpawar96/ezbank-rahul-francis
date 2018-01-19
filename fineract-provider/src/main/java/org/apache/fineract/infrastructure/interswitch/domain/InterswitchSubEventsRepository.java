package org.apache.fineract.infrastructure.interswitch.domain;

import java.util.List;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterswitchSubEventsRepository extends JpaRepository<InterswitchSubEvent, Long>, JpaSpecificationExecutor<InterswitchSubEvent>
{

List<InterswitchSubEvent>  findByInterswitchEvents(InterswitchEvent transaction);
	
}

