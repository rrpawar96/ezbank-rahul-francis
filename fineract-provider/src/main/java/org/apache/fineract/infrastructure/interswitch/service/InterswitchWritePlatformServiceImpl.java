package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.interswitch.domain.InterSwitchCardDetailsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequestRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchCardDetails;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchTransactionsRepository;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InterswitchWritePlatformServiceImpl implements InterswitchWritePlatformService
{

	private PlatformSecurityContext context;
	
	private InterSwitchCardDetailsRepository interSwitchCardDetailsRepository;
	
	private InterswitchTransactionsRepository interswitchTransactionsRepository;
	
	private InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository;
	
	private SavingsAccountRepository savingsAccountRepository;
	
	@Autowired
	public InterswitchWritePlatformServiceImpl(PlatformSecurityContext context,InterSwitchCardDetailsRepository interSwitchCardDetailsRepository,
			InterswitchTransactionsRepository interswitchTransactionsRepository,InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository,
			SavingsAccountRepository savingsAccountRepository)
	{
		this.context=context;
		this.interSwitchCardDetailsRepository=interSwitchCardDetailsRepository;
		this.interswitchTransactionsRepository=interswitchTransactionsRepository;
		this.interswitchAuthorizationRequestRepository=interswitchAuthorizationRequestRepository;
		this.savingsAccountRepository=savingsAccountRepository;
	}
	
	
	public CommandProcessingResult mapDebitCardToSavingsAccount(JsonCommand command)
	{
		this.context.authenticatedUser();
		
		final SavingsAccount savingsAccount=this.savingsAccountRepository.getOne(command.longValueOfParameterNamed("savingsAccountNumber"));
		
		final BigInteger primaryAccountNumber=command.bigDecimalValueOfParameterNamed("primaryAccountNumber").toBigInteger();
		
		final int cvv=command.integerValueOfParameterNamed("cvv");
		
		final LocalDate validFrom=command.localDateValueOfParameterNamed("validFrom");
		
		final LocalDate validThrough=command.localDateValueOfParameterNamed("validThrough");
		
		final String pin=command.stringValueOfParameterNamed("pin");
		
		InterswitchCardDetails cardDetails=InterswitchCardDetails.getInstance(primaryAccountNumber, savingsAccount, cvv, validFrom, validThrough, pin);
				
		this.interSwitchCardDetailsRepository.save(cardDetails);
		
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(cardDetails.getId())
				.build();
	}
	
	
	public CommandProcessingResult authorizetransaction(JsonCommand command)
	{
		this.context.authenticatedUser();
		
		final BigInteger primaryAccountNumber=command.bigDecimalValueOfParameterNamed("primaryAccountNumber").toBigInteger();
		
		InterswitchCardDetails cardDetails=this.interSwitchCardDetailsRepository.getOneByPrimaryAccountNumber(primaryAccountNumber);
		
		SavingsAccount savingsAccount=cardDetails.getSavingsAccount();
		
		// validate card
		
		if(cardDetails.getValidThrough().isBefore(LocalDate.now()))
		{
			 throw new GeneralPlatformDomainRuleException(
                     "card is expired !",
                     "card is expired !");
			 
			 // send back response code saying card is expired
		}
		
		BigDecimal transactionAmount=command.bigDecimalValueOfParameterNamed("transaction_amount");
		
		
		if(savingsAccount.getWithdrawableBalance().compareTo(transactionAmount)==-1)
		{
			// return response code as insufficient bal
		}
		
		// if everything is fine put funds on hold
		
		savingsAccount.holdAmount(transactionAmount);
		
		//send the response code
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(Long.valueOf(0100))
				.build();
		
		
	}
	
	
}
