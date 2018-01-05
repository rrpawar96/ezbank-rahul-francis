package org.apache.fineract.infrastructure.interswitch.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterswitchEventsRepository  extends JpaRepository<InterswitchEvents, Long>, JpaSpecificationExecutor<InterswitchEvents>
{

	InterswitchEvents findOneBySessionIdAndAuthorizationNumber(String sessionId,String authorizationNumber);
	
}
