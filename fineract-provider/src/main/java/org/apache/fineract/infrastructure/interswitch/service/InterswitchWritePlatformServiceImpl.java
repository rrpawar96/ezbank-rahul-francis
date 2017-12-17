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
import org.apache.fineract.portfolio.account.PortfolioAccountType;
import org.apache.fineract.portfolio.account.data.AccountTransferDTO;
import org.apache.fineract.portfolio.account.domain.AccountTransferType;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.exception.ClientNotActiveException;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.group.exception.GroupNotActiveException;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.SavingsTransactionBooleanValues;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class InterswitchWritePlatformServiceImpl implements InterswitchWritePlatformService {

	private final PlatformSecurityContext context;

	private final InterswitchTransactionsRepository interswitchTransactionsRepository;

	private final InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository;

	private final SavingsAccountRepository savingsAccountRepository;

	private final SavingsAccountDomainService savingsAccountDomainService;

	private final SavingsAccountAssembler savingAccountAssembler;

	private final FromJsonHelper fromApiJsonHelper;

	private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;

	private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;

	@Autowired
	public InterswitchWritePlatformServiceImpl(PlatformSecurityContext context,
			InterswitchTransactionsRepository interswitchTransactionsRepository,
			InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository,
			SavingsAccountRepository savingsAccountRepository, SavingsAccountDomainService savingsAccountDomainService,
			SavingsAccountAssembler savingAccountAssembler, FromJsonHelper fromApiJsonHelper,
			AccountTransfersWritePlatformService accountTransfersWritePlatformService,
			SavingsAccountTransactionRepository savingsAccountTransactionRepository) {
		this.context = context;
		this.interswitchTransactionsRepository = interswitchTransactionsRepository;
		this.interswitchAuthorizationRequestRepository = interswitchAuthorizationRequestRepository;
		this.savingsAccountRepository = savingsAccountRepository;
		this.savingsAccountDomainService = savingsAccountDomainService;
		this.savingAccountAssembler = savingAccountAssembler;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
		this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
	}

	@Override
	public CommandProcessingResult authorizetransaction(JsonCommand command) {
		this.context.authenticatedUser();
		String authorizationNumber = "";
		String responseCode = ResponseCodes.ERROR.getCode();

		String sessionId = "";
		if (command.stringValueOfParameterNamed("session") != "") {
			sessionId = command.stringValueOfParameterNamed("session");
		} else {
			throw new GeneralPlatformDomainRuleException("session number missing", "session number missing",
					"session number missing");
		}

		BigDecimal settlementAmount;
		if (command.bigDecimalValueOfParameterNamed("settlement_amount") != null) {
			settlementAmount = command.bigDecimalValueOfParameterNamed("settlement_amount");
		} else {
			throw new GeneralPlatformDomainRuleException("settlement Amount missing", "settlement Amount missing",
					"settlement Amount missing");
		}

		String time = "";
		if (command.stringValueOfParameterNamed("local_transaction_time") != "") {
			time = command.stringValueOfParameterNamed("local_transaction_time");
		} else {
			throw new GeneralPlatformDomainRuleException("local_transaction_time missing",
					"local_transaction_time missing", "local_transaction_time missing");
		}

		Date localTransactionDate = null;
		if (command.DateValueOfParameterNamed("local_transaction_date") != null) {
			localTransactionDate = command.DateValueOfParameterNamed("local_transaction_date");
		} else {
			throw new GeneralPlatformDomainRuleException("transaction date missing", "transaction date missing",
					"transaction date missing");
		}

		String accountNumber = "";
		boolean isDebit = false;

		if (command.stringValueOfParameterNamed("account_debit") != "") {
			accountNumber = command.stringValueOfParameterNamed("account_debit");
			isDebit = true;
		} else if (command.stringValueOfParameterNamed("account_credit") != "") {
			accountNumber = command.stringValueOfParameterNamed("account_credit");
		} else {

			responseCode = ResponseCodes.ERROR.getValue() + "";
			authorizationNumber = ""; // because we did not execute
			// transaction???

			return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

		}

		SavingsAccount savingsAccount = this.savingsAccountRepository.findSavingAccountByAccountNumber(accountNumber);

		savingsAccount = this.savingAccountAssembler.assembleFrom(savingsAccount.getId());

		if (savingsAccount == null) {

			responseCode = ResponseCodes.ERROR.getValue() + "";
			authorizationNumber = ""; // because we did not execute
			// transaction???

			return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

		}

		boolean isSettled = false;

		boolean isAdviced = false;

		boolean isReversed = false;

		if (isDebit) {
			if (savingsAccount.getWithdrawableBalance().compareTo(settlementAmount) == -1) {
				// return response code as insufficient bal
				responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";

				InterswitchAuthorizationRequests authorizationRequest = this.interswitchAuthorizationRequestRepository
						.save(InterswitchAuthorizationRequests.getInstance(sessionId, settlementAmount,
								localTransactionDate, time, isSettled, isReversed, isAdviced, responseCode, null,
								isDebit));

				authorizationNumber = authorizationRequest.getId() + "";

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

			}
		}

		responseCode = ResponseCodes.APPROVED.getValue() + "";

		// marking transaction as null ,because during authorization we don't
		// execute transaction
		InterswitchAuthorizationRequests authorizationRequest = this.interswitchAuthorizationRequestRepository
				.save(InterswitchAuthorizationRequests.getInstance(sessionId, settlementAmount, localTransactionDate,
						time, isSettled, isReversed, isAdviced, responseCode, null, isDebit));

		authorizationNumber = authorizationRequest.getId() + "";

		// send the response code
		return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

	}

	@Override
	public CommandProcessingResult executeTransaction(JsonCommand command) {

		this.context.authenticatedUser();
		String authorizationNumber = "";
		String responseCode = ResponseCodes.ERROR.getCode();

		final JsonElement element = command.parsedJson();

		final String sessionId = command.stringValueOfParameterNamed("session");

		final BigDecimal settlementAmount = command.bigDecimalValueOfParameterNamed("settlement_amount");

		final Date settlementDate = command.DateValueOfParameterNamed("settlement_date");

		String time = "";
		if (command.stringValueOfParameterNamed("local_transaction_time") != "") {
			time = command.stringValueOfParameterNamed("local_transaction_time");
		} else {
			throw new GeneralPlatformDomainRuleException("local_transaction_time missing",
					"local_transaction_time missing", "local_transaction_time missing");
		}

		final Locale locale = command.extractLocale();
		final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

		LocalDate transactionDateDep = this.fromApiJsonHelper.extractLocalDateNamed("local_transaction_date", element,
				command.dateFormat(), locale);

		boolean isReversed = false;

		boolean isAdviced = false; // what isAdviced?

		boolean isDebit = false;

		boolean isTransferTransaction = false;

		// get savings account

		String accountNumberCredit = "";
		String accountNumberDebit = "";

		if ((command.stringValueOfParameterNamed("account_debit") != "")
				&& (command.stringValueOfParameterNamed("account_credit") != "")) 
		{

			accountNumberDebit = command.stringValueOfParameterNamed("account_debit");
			accountNumberCredit = command.stringValueOfParameterNamed("account_credit");
			isTransferTransaction = true;

		} else if (command.stringValueOfParameterNamed("account_debit") != "") 
		{
			accountNumberDebit = command.stringValueOfParameterNamed("account_debit");
			isDebit = true;
			
		} 
		else if (command.stringValueOfParameterNamed("account_credit") != "") 
		{
			accountNumberCredit = command.stringValueOfParameterNamed("account_credit");
			
		} 
		else {
			responseCode = ResponseCodes.ERROR.getValue() + "";
			authorizationNumber = ""; // because we did not execute
			// transaction???

			return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
		}

		SavingsAccount savingsAccountCredit = null;
		SavingsAccount savingsAccountDebit = null;

		if (isTransferTransaction) {
			savingsAccountCredit = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumberCredit);

			savingsAccountCredit = this.savingAccountAssembler.assembleFrom(savingsAccountCredit.getId());

			savingsAccountDebit = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumberDebit);

			savingsAccountDebit = this.savingAccountAssembler.assembleFrom(savingsAccountDebit.getId());

			if (savingsAccountCredit == null || savingsAccountDebit == null) {
				responseCode = ResponseCodes.ERROR.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}

		} else if (isDebit) {
			savingsAccountDebit = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumberDebit);

			savingsAccountDebit = this.savingAccountAssembler.assembleFrom(savingsAccountDebit.getId());

			if (savingsAccountDebit == null) {
				responseCode = ResponseCodes.ERROR.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}

			if (savingsAccountDebit.getWithdrawableBalance().compareTo(settlementAmount) == -1) {
				// return response code as insufficient bal
				responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

			}
		} else {
			savingsAccountCredit = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumberCredit);

			savingsAccountCredit = this.savingAccountAssembler.assembleFrom(savingsAccountCredit.getId());

			if (savingsAccountCredit == null) {
				responseCode = ResponseCodes.ERROR.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}
		}

		// if everything goes well

		/*
		 * AccountTransferDTO(final LocalDate transactionDate, final BigDecimal
		 * transactionAmount, final PortfolioAccountType fromAccountType, final
		 * PortfolioAccountType toAccountType, final Long fromAccountId, final
		 * Long toAccountId, final String description, final Locale locale,
		 * final DateTimeFormatter fmt, final PaymentDetail paymentDetail, final
		 * Integer fromTransferType, final Integer toTransferType, final Long
		 * chargeId, Integer loanInstallmentNumber, Integer transferType, final
		 * AccountTransferDetails accountTransferDetails, final String noteText,
		 * final String txnExternalId, final Loan loan, SavingsAccount
		 * toSavingsAccount, final SavingsAccount fromSavingsAccount, final
		 * Boolean isRegularTransaction, Boolean isExceptionForBalanceCheck)
		 */

		SavingsAccountTransaction applicationTransaction;

		if (isTransferTransaction) {

			
			final boolean isExceptionForBalanceCheck = false;
			final boolean isRegularTransaction = true;
			final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDateDep, settlementAmount,
					PortfolioAccountType.SAVINGS, PortfolioAccountType.SAVINGS, savingsAccountDebit.getId(),
					savingsAccountDebit.getId(), "Interswitch intra bank transfer", locale, fmt, null,
					SavingsAccountTransactionType.WITHDRAWAL.getValue(),
					SavingsAccountTransactionType.DEPOSIT.getValue(), null, null,
					AccountTransferType.ACCOUNT_TRANSFER.getValue(), null, null, null, null, savingsAccountCredit,
					savingsAccountDebit, isRegularTransaction, isExceptionForBalanceCheck);

			long transferTransactionId = this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO)
					.longValue();

			applicationTransaction = this.savingsAccountTransactionRepository.getOne(transferTransactionId);

		}

		else if (isDebit) {

			checkClientOrGroupActive(savingsAccountDebit);

			final boolean isAccountTransfer = false;
			final boolean isRegularTransaction = true;
			final boolean isApplyWithdrawFee = true;
			final boolean isInterestTransfer = false;
			final boolean isWithdrawBalance = false;
			final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(
					isAccountTransfer, isRegularTransaction, isApplyWithdrawFee, isInterestTransfer, isWithdrawBalance);

			applicationTransaction = this.savingsAccountDomainService.handleWithdrawal(savingsAccountDebit, fmt,
					transactionDateDep, settlementAmount, null, transactionBooleanValues, true);

		} else {
			checkClientOrGroupActive(savingsAccountCredit);

			boolean isAccountTransfer = false;
			boolean isRegularTransaction = true;

			applicationTransaction = this.savingsAccountDomainService.handleInterswitchDeposit(savingsAccountCredit,
					fmt, transactionDateDep, settlementAmount, null, isAccountTransfer, isRegularTransaction);

		}

		// very imp: we are returning transaction id as authorization number,
		// this is to facilitate undo of transaction, as ezbank would need
		// savings account and transaction id
		// of the transaction to be undoed
		authorizationNumber = applicationTransaction.getId() + "";
		responseCode = ResponseCodes.APPROVED.getValue() + "";

		InterswitchTransactions transaction = InterswitchTransactions.getInstance(sessionId, authorizationNumber,
				applicationTransaction, settlementAmount, settlementDate, time, isReversed, isAdviced, isDebit);

		 this.interswitchTransactionsRepository.save(transaction);

		// send the response code
		return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

	}

	private void checkClientOrGroupActive(final SavingsAccount account) {
		final Client client = account.getClient();
		if (client != null) {
			if (client.isNotActive()) {
				throw new ClientNotActiveException(client.getId());
			}
		}
		final Group group = account.group();
		if (group != null) {
			if (group.isNotActive()) {
				throw new GroupNotActiveException(group.getId());
			}
		}
	}

}
