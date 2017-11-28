package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.interswitch.domain.InterSwitchCardDetailsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequestRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequests;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchCardDetails;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchTransactions;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchTransactionsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.ResponseCodes;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
	
	 private final SavingsAccountDomainService savingsAccountDomainService;
	
	@Autowired
	public InterswitchWritePlatformServiceImpl(PlatformSecurityContext context,InterSwitchCardDetailsRepository interSwitchCardDetailsRepository,
			InterswitchTransactionsRepository interswitchTransactionsRepository,InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository,
			SavingsAccountRepository savingsAccountRepository,SavingsAccountDomainService savingsAccountDomainService)
	{
		this.context=context;
		this.interSwitchCardDetailsRepository=interSwitchCardDetailsRepository;
		this.interswitchTransactionsRepository=interswitchTransactionsRepository;
		this.interswitchAuthorizationRequestRepository=interswitchAuthorizationRequestRepository;
		this.savingsAccountRepository=savingsAccountRepository;
		this.savingsAccountDomainService=savingsAccountDomainService;
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
		String authorizationNumber="";
		String responseCode=ResponseCodes.ERROR.getCode();
		
		
		final String sessionId=command.stringValueOfParameterNamed("session");
		
		final BigInteger primaryAccountNumber=command.bigDecimalValueOfParameterNamed("primaryAccountNumber").toBigInteger();
		
		final BigDecimal authorizationAmount=command.bigDecimalValueOfParameterNamed("transaction_amount");
		
		final BigDecimal settlementAmount=command.bigDecimalValueOfParameterNamed("settlement_amount");
		
		final int settlementCurrency=command.integerValueOfParameterNamed("settlement_currency_code");
		
		final BigDecimal settlementCurrencyRate=command.bigDecimalValueOfParameterNamed("settlement_conversion_rate");
		
		final int transactionCurrency=command.integerValueOfParameterNamed("transaction_currency_code");
		
		final LocalDate transactionDate=command.localDateValueOfParameterNamed("local_transaction_date");
		
		final LocalDate settlementDate=command.localDateValueOfParameterNamed("settlement_date");
		
		boolean isSettled=false;
		
		
		
		
		InterswitchCardDetails cardDetails=this.interSwitchCardDetailsRepository.getOneByPrimaryAccountNumber(primaryAccountNumber);
		
		SavingsAccount savingsAccount=cardDetails.getSavingsAccount();
		
		
		
		// validate card
	
		
		if(cardDetails.getValidThrough().isBefore(LocalDate.now()))
		{
			
			 // send back response code saying card is expired
			 
			 responseCode=ResponseCodes.EXPIREDCARD.getValue()+"";
			 InterswitchTransactions interswitchtransaction=null;
			 
			 
			 InterswitchAuthorizationRequests authorizationRequest= this.interswitchAuthorizationRequestRepository.save(InterswitchAuthorizationRequests.getInstance(sessionId, interswitchtransaction, authorizationAmount, settlementAmount,
					 settlementCurrency, settlementCurrencyRate, transactionCurrency, transactionDate, settlementDate, isSettled));
			 
			 authorizationNumber= authorizationRequest.getId()+"";
			 
			 return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
		}
		
		BigDecimal transactionAmount=command.bigDecimalValueOfParameterNamed("transaction_amount");
		
		
		if(savingsAccount.getWithdrawableBalance().compareTo(transactionAmount)==-1)
		{
			// return response code as insufficient bal
			 responseCode=ResponseCodes.NOTSUFFICIENTFUNDS.getValue()+"";
			 InterswitchTransactions interswitchtransaction=null;
			 
			 
			 InterswitchAuthorizationRequests authorizationRequest= this.interswitchAuthorizationRequestRepository.save(InterswitchAuthorizationRequests.getInstance(sessionId, interswitchtransaction, authorizationAmount, settlementAmount,
					 settlementCurrency, settlementCurrencyRate, transactionCurrency, transactionDate, settlementDate, isSettled));
			 
			 authorizationNumber= authorizationRequest.getId()+"";
			 
			 return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			
		}
		
		// if everything is fine put funds on hold
		
		// marking transaction as null ,because during authorization we don't execute transaction
		InterswitchAuthorizationRequests authorizationRequest= this.interswitchAuthorizationRequestRepository.save(InterswitchAuthorizationRequests.getInstance(sessionId, null, authorizationAmount, settlementAmount,
				 settlementCurrency, settlementCurrencyRate, transactionCurrency, transactionDate, settlementDate, isSettled));
		
		authorizationNumber= authorizationRequest.getId()+"";
		responseCode=ResponseCodes.APPROVED.getValue()+"";
		
		savingsAccount.holdAmount(transactionAmount);
		
		
		
		//send the response code
		return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
		
		
	}
	
	
	public CommandProcessingResult executeTransaction(JsonCommand command)
	{
		
		this.context.authenticatedUser();
		String authorizationNumber="";
		String responseCode=ResponseCodes.ERROR.getCode();
		
		
		final String sessionId=command.stringValueOfParameterNamed("session");
		
		final BigInteger primaryAccountNumber=command.bigDecimalValueOfParameterNamed("primaryAccountNumber").toBigInteger();
		
		BigDecimal transactionAmount=command.bigDecimalValueOfParameterNamed("transaction_amount"); // or settlement amount as the transaction is executed?
		
		final BigDecimal settlementAmount=command.bigDecimalValueOfParameterNamed("settlement_amount");
		
		final int settlementCurrency=command.integerValueOfParameterNamed("settlement_currency_code");
		
		final BigDecimal settlementCurrencyRate=command.bigDecimalValueOfParameterNamed("settlement_conversion_rate");
		
		final int transactionCurrency=command.integerValueOfParameterNamed("transaction_currency_code");
		
		final LocalDate transactionDate=command.localDateValueOfParameterNamed("local_transaction_date");
		
		final LocalDate settlementDate=command.localDateValueOfParameterNamed("settlement_date");
		
		boolean isReversed=false;
		
		boolean isAdviced=false; // what isAdviced?
		
		// get savings account linked to card
		
		InterswitchCardDetails cardDetails=this.interSwitchCardDetailsRepository.getOneByPrimaryAccountNumber(primaryAccountNumber);
		
		SavingsAccount savingsAccount=cardDetails.getSavingsAccount();
		
		// validate card
		
		
			if(cardDetails.getValidThrough().isBefore(LocalDate.now()))
			{
				
				 // send back response code saying card is expired
				 
				 responseCode=ResponseCodes.EXPIREDCARD.getValue()+"";
				 
				 authorizationNumber=""; // because we did not execute transaction??
				 
				 return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}
			
			
			
			
			if(savingsAccount.getWithdrawableBalance().compareTo(transactionAmount)==-1)
			{
				// return response code as insufficient bal
				 responseCode=ResponseCodes.NOTSUFFICIENTFUNDS.getValue()+"";
				 authorizationNumber=""; // because we did not execute transaction???
				 
				 return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
				
			}
		
		
			// if everything goes well 
			
			  final Locale locale = command.extractLocale();
		        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
		
		      checkClientOrGroupActive(savingsAccount); 
		      
		        final boolean isAccountTransfer = false;
		        final boolean isRegularTransaction = true;
		        final boolean isApplyWithdrawFee = true;
		        final boolean isInterestTransfer = false;
		        final boolean isWithdrawBalance = false;
		        final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
		                isRegularTransaction, isApplyWithdrawFee, isInterestTransfer, isWithdrawBalance);
		        final SavingsAccountTransaction withdrawal = this.savingsAccountDomainService.handleWithdrawal(savingsAccount, fmt, transactionDate,
		                transactionAmount, null, transactionBooleanValues);
		        
		        
		        InterswitchTransactions transaction=InterswitchTransactions.getInstance(sessionId, authorizationNumber, withdrawal, transactionAmount, transactionDate, isReversed, isAdviced);
		        
		        

				authorizationNumber= transaction.getId()+"";               // this time we return transaction id instead of authorization id
				responseCode=ResponseCodes.APPROVED.getValue()+"";
				
				savingsAccount.holdAmount(transactionAmount);
				
				
				
				//send the response code
				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
		        
		        
		
	}
	
	  private void checkClientOrGroupActive(final SavingsAccount account) {
	        final Client client = account.getClient();
	        if (client != null) {
	            if (client.isNotActive()) { throw new ClientNotActiveException(client.getId()); }
	        }
	        final Group group = account.group();
	        if (group != null) {
	            if (group.isNotActive()) { throw new GroupNotActiveException(group.getId()); }
	        }
	    }
	
	
}
