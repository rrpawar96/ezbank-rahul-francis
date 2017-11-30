package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequestRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequests;
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
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.exception.SavingsAccountNotFoundException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class InterswitchWritePlatformServiceImpl implements InterswitchWritePlatformService
{

	private PlatformSecurityContext context;
	
	private InterswitchTransactionsRepository interswitchTransactionsRepository;
	
	private InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository;
	
	private SavingsAccountRepository savingsAccountRepository;
	
	 private final SavingsAccountDomainService savingsAccountDomainService;
	 
	final SavingsAccountAssembler savingAccountAssembler;
	
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public InterswitchWritePlatformServiceImpl(PlatformSecurityContext context,
			InterswitchTransactionsRepository interswitchTransactionsRepository,InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository,
			SavingsAccountRepository savingsAccountRepository,SavingsAccountDomainService savingsAccountDomainService,SavingsAccountAssembler savingAccountAssembler,
			FromJsonHelper fromApiJsonHelper)
	{
		this.context=context;
		this.interswitchTransactionsRepository=interswitchTransactionsRepository;
		this.interswitchAuthorizationRequestRepository=interswitchAuthorizationRequestRepository;
		this.savingsAccountRepository=savingsAccountRepository;
		this.savingsAccountDomainService=savingsAccountDomainService;
		this.savingAccountAssembler=savingAccountAssembler;
		this.fromApiJsonHelper=fromApiJsonHelper;
	}
	
	
	
	
	@Override
	public CommandProcessingResult authorizetransaction(JsonCommand command)
	{
		this.context.authenticatedUser();
		String authorizationNumber="";
		String responseCode=ResponseCodes.ERROR.getCode();
		
		
		 String sessionId="";
		if(command.stringValueOfParameterNamed("session")!="")
		{
			sessionId=command.stringValueOfParameterNamed("session");
		}
		else
		{
			throw new GeneralPlatformDomainRuleException("session number missing", "session number missing","session number missing");
		}
			
		
		 BigDecimal authorizationAmount;
		 if(command.bigDecimalValueOfParameterNamed("transaction_amount")!=null)
		 {
			 authorizationAmount=command.bigDecimalValueOfParameterNamed("transaction_amount");
		 }
		 else
		 {
			 throw new GeneralPlatformDomainRuleException("authorizationAmount missing", "authorizationAmount missing","authorizationAmount missing"); 
		 }
		
		 BigDecimal settlementAmount;
		 if(command.bigDecimalValueOfParameterNamed("settlement_amount")!=null)
		 {
			 settlementAmount= command.bigDecimalValueOfParameterNamed("settlement_amount");
		 }
		 else
		 {
			 throw new GeneralPlatformDomainRuleException("settlement Amount missing", "settlement Amount missing","settlement Amount missing");  
		 }
		 
		 int settlementCurrency=0;
		 //not a mandatory parameter as not all transaction are international
		 if(command.integerValueOfParameterNamed("settlement_currency_code")!=null)
		 {
			 settlementCurrency=command.integerValueOfParameterNamed("settlement_currency_code");
		 }
		
		 BigDecimal settlementCurrencyRate=BigDecimal.ZERO;
		//not a mandatory parameter as not all transaction are international
		 if(command.bigDecimalValueOfParameterNamed("settlement_conversion_rate")!=null)
		 {
			 settlementCurrencyRate=command.bigDecimalValueOfParameterNamed("settlement_conversion_rate");
		 }
		 
		 int transactionCurrency=0;
		 	if(command.integerValueOfParameterNamed("transaction_currency_code")!=null)
		 	{
		 		transactionCurrency=command.integerValueOfParameterNamed("transaction_currency_code");
		 	}
		 	else
		 	{
		 		throw new GeneralPlatformDomainRuleException("transaction currency code missing", "transaction currency code missing","transaction currency code missing");  
		 	}
		
		 	Date transactionDate=null;
		if(command.localDateValueOfParameterNamed("local_transaction_date")!=null)
		{
			transactionDate=command.DateValueOfParameterNamed("local_transaction_date");
					
		}
		else
		{
			throw new GeneralPlatformDomainRuleException("local transaction date missing", "local transaction date missing","local transaction date missing"); 	
		}
		
		Date settlementDate=null;
		if(command.DateValueOfParameterNamed("settlement_date")!=null)
		{
			settlementDate=command.DateValueOfParameterNamed("settlement_date");
		}
		else
		{
			throw new GeneralPlatformDomainRuleException("settlement date missing", "settlement date missing","settlement date missing"); 	
		}
		
		
		
		
		
		
		String accountNumber="";
		boolean isDebit=false;
		
		if(command.stringValueOfParameterNamed("account_debit")!="")
		{
			accountNumber=command.stringValueOfParameterNamed("account_debit");
			isDebit=true;
		}
		else if(command.stringValueOfParameterNamed("account_credit")!="")
		{
			accountNumber=command.stringValueOfParameterNamed("account_credit");
		}
		else
		{
			throw new GeneralPlatformDomainRuleException("account number missing", "account number missing","account number missing");
		}
		
		
		
		SavingsAccount savingsAccount=this.savingsAccountRepository.findSavingAccountByAccountNumber(accountNumber);
		
		savingsAccount=this.savingAccountAssembler.assembleFrom(savingsAccount.getId());
		
		if (savingsAccount == null) 
		{ 
			throw new SavingsAccountNotFoundException(accountNumber); 
			
		}
		
		
		boolean isSettled=false;
		
		boolean isAdviced=false;
		
		boolean isReversed=false;
		
		
		
		if(isDebit)
		{
		if(savingsAccount.getWithdrawableBalance().compareTo(settlementAmount)==-1)
		{
			// return response code as insufficient bal
			 responseCode=ResponseCodes.NOTSUFFICIENTFUNDS.getValue()+"";
			 InterswitchTransactions interswitchtransaction=null;
			 
			 
			 InterswitchAuthorizationRequests authorizationRequest= this.interswitchAuthorizationRequestRepository.save(InterswitchAuthorizationRequests.getInstance(sessionId, interswitchtransaction, authorizationAmount, settlementAmount,
					 settlementCurrency, settlementCurrencyRate, transactionCurrency, transactionDate, settlementDate, isSettled,isReversed,isAdviced,null));
			 
			 authorizationNumber= authorizationRequest.getId()+"";
			 
			 return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			
		}
	}
				
		// marking transaction as null ,because during authorization we don't execute transaction
		InterswitchAuthorizationRequests authorizationRequest= this.interswitchAuthorizationRequestRepository.save(InterswitchAuthorizationRequests.getInstance(sessionId, null, authorizationAmount, settlementAmount,
				 settlementCurrency, settlementCurrencyRate, transactionCurrency, transactionDate, settlementDate, isSettled,isReversed,isAdviced,null));
		
		authorizationNumber= authorizationRequest.getId()+"";
		responseCode=ResponseCodes.APPROVED.getValue()+"";
		
		
		
		
		
		//send the response code
		return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
		
		
	}
	
	@Override
	public CommandProcessingResult executeTransaction(JsonCommand command)
	{
		
		this.context.authenticatedUser();
		String authorizationNumber="";
		String responseCode=ResponseCodes.ERROR.getCode();
		
		final JsonElement element = command.parsedJson();
		
		
		final String sessionId=command.stringValueOfParameterNamed("session");
		
		BigDecimal transactionAmount=command.bigDecimalValueOfParameterNamed("transaction_amount"); // or settlement amount as the transaction is executed?
		
		final BigDecimal settlementAmount=command.bigDecimalValueOfParameterNamed("settlement_amount");

		
		final Date transactionDate=command.DateValueOfParameterNamed("local_transaction_date");
			//	localDateValueOfParameterNamed("local_transaction_date");
		
		final Locale locale = command.extractLocale();
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
		
		LocalDate transactionDateDep=this.fromApiJsonHelper.extractLocalDateNamed("local_transaction_date", element, command.dateFormat(), locale);
		
	
		
		boolean isReversed=false;
		
		boolean isAdviced=false; // what isAdviced?
		
		boolean isDebit=false;
		
		// get savings account
		
	String accountNumber="";
		
		if(command.stringValueOfParameterNamed("account_debit")!="")
		{
			accountNumber=command.stringValueOfParameterNamed("account_debit");
			isDebit=true;
			System.out.println("account number at debit "+accountNumber);
		}
		else if(command.stringValueOfParameterNamed("account_credit")!="")
		{
			accountNumber=command.stringValueOfParameterNamed("account_credit");
			System.out.println("account number at credit "+accountNumber);
		}
		else
		{
			throw new GeneralPlatformDomainRuleException("account number missing", "account number missing","account number missing");
		}
		
		System.out.println("account number is "+accountNumber);
		
		SavingsAccount savingsAccount=this.savingsAccountRepository.findSavingAccountByAccountNumber(accountNumber);
		
		savingsAccount=this.savingAccountAssembler.assembleFrom(savingsAccount.getId());
	
		
		System.out.println("account  is "+savingsAccount);
		if (savingsAccount == null) 
		{ 
			throw new SavingsAccountNotFoundException(accountNumber); 
			
		}
		
				
			
		
		
			// if everything goes well 
			
			  SavingsAccountTransaction applicationTransaction;
			  
			  
			
			if(isDebit)
			{
				if(savingsAccount.getWithdrawableBalance().compareTo(transactionAmount)==-1)
				{
					// return response code as insufficient bal
					 responseCode=ResponseCodes.NOTSUFFICIENTFUNDS.getValue()+"";
					 authorizationNumber=""; // because we did not execute transaction???
					 
					 return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
					
				}
		
		      checkClientOrGroupActive(savingsAccount); 
		      
		        final boolean isAccountTransfer = false;
		        final boolean isRegularTransaction = true;
		        final boolean isApplyWithdrawFee = true;
		        final boolean isInterestTransfer = false;
		        final boolean isWithdrawBalance = false;
		        final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(isAccountTransfer,
		                isRegularTransaction, isApplyWithdrawFee, isInterestTransfer, isWithdrawBalance);
		        applicationTransaction = this.savingsAccountDomainService.handleWithdrawal(savingsAccount, fmt, transactionDateDep,
		        		settlementAmount, null, transactionBooleanValues);
		        
		        
			}
			else
			{
				  checkClientOrGroupActive(savingsAccount);
			       

			 
			        boolean isAccountTransfer = false;
			        boolean isRegularTransaction = true;
			        
			        applicationTransaction=this.savingsAccountDomainService.handleDeposit(savingsAccount, fmt, transactionDateDep,
		                    transactionAmount, null, isAccountTransfer, isRegularTransaction);
			        
			}
		        
		        
		        InterswitchTransactions transaction=InterswitchTransactions.getInstance(sessionId, authorizationNumber, applicationTransaction, transactionAmount, transactionDate, isReversed, isAdviced);
		        
		    InterswitchTransactions  interswithTransaction   = this.interswitchTransactionsRepository.save(transaction);

				authorizationNumber= interswithTransaction.getId()+"";               // this time we return transaction id instead of authorization id
				responseCode=ResponseCodes.APPROVED.getValue()+"";
				
				
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
