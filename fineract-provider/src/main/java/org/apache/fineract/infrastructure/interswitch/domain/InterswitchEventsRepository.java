package org.apache.fineract.infrastructure.interswitch.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterswitchEventsRepository  extends JpaRepository<InterswitchEvent, Long>, JpaSpecificationExecutor<InterswitchEvent>
{

	InterswitchEvent findOneBySessionIdAndAuthorizationNumber(String sessionId,String authorizationNumber);
	
	InterswitchEvent findByAuthorizationNumber(String authorizationNumber);
	
}
