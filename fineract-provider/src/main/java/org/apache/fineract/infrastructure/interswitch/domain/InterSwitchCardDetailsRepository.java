package org.apache.fineract.infrastructure.interswitch.domain;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterSwitchCardDetailsRepository 
extends JpaRepository<InterswitchCardDetails, Long>, JpaSpecificationExecutor<InterswitchCardDetails>
{

	public InterswitchCardDetails getOneByPrimaryAccountNumber(BigInteger primaryAccountNumber);
}
