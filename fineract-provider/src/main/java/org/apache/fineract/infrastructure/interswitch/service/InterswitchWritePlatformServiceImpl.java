package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.exception.GeneralPlatformDomainRuleException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequestRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchAuthorizationRequests;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchEvents;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchEventsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchSubEvents;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchSubEventsRepository;
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
import org.apache.fineract.portfolio.savings.exception.InsufficientAccountBalanceException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class InterswitchWritePlatformServiceImpl implements InterswitchWritePlatformService {

	private final PlatformSecurityContext context;

	private final InterswitchEventsRepository interswitchTransactionsRepository;

	private final InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository;

	private final SavingsAccountRepository savingsAccountRepository;

	private final SavingsAccountDomainService savingsAccountDomainService;

	private final SavingsAccountAssembler savingAccountAssembler;

	private final FromJsonHelper fromApiJsonHelper;

	private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;

	private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;

	private final InterswitchSubEventsRepository interswitchSubEventsRepository;

	@Autowired
	public InterswitchWritePlatformServiceImpl(PlatformSecurityContext context,
			InterswitchEventsRepository interswitchTransactionsRepository,
			InterswitchAuthorizationRequestRepository interswitchAuthorizationRequestRepository,
			SavingsAccountRepository savingsAccountRepository, SavingsAccountDomainService savingsAccountDomainService,
			SavingsAccountAssembler savingAccountAssembler, FromJsonHelper fromApiJsonHelper,
			AccountTransfersWritePlatformService accountTransfersWritePlatformService,
			SavingsAccountTransactionRepository savingsAccountTransactionRepository,
			InterswitchSubEventsRepository interswitchSubEventsRepository) {
		this.context = context;
		this.interswitchTransactionsRepository = interswitchTransactionsRepository;
		this.interswitchAuthorizationRequestRepository = interswitchAuthorizationRequestRepository;
		this.savingsAccountRepository = savingsAccountRepository;
		this.savingsAccountDomainService = savingsAccountDomainService;
		this.savingAccountAssembler = savingAccountAssembler;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
		this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
		this.interswitchSubEventsRepository = interswitchSubEventsRepository;
	}

	@Override
	public CommandProcessingResult authorizetransaction(JsonCommand command) {
		this.context.authenticatedUser();
		String authorizationNumber = "";
		String responseCode = ResponseCodes.ERROR.getValue() + "";

		String sessionId = "";
		if (command.stringValueOfParameterNamed("session") != "") {
			sessionId = command.stringValueOfParameterNamed("session");
		} else {
			throw new GeneralPlatformDomainRuleException("session number missing", "session number missing",
					"session number missing");
		}

		BigDecimal settlementAmount;
		if (command.bigDecimalValueOfParameterNamed("settlement_amount", Locale.ENGLISH) != null) {
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

		responseCode = String.format("%02d", ResponseCodes.APPROVED.getValue());

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
		String responseCode = ResponseCodes.ERROR.getValue() + "";

		final JsonElement element = command.parsedJson();

		final String sessionId = command.stringValueOfParameterNamed("session");

		final String stan = command.stringValueOfParameterNamed("stan");

		final BigDecimal settlementAmount = command.bigDecimalValueOfParameterNamed("transaction_amount",
				Locale.ENGLISH);

		// final Date settlementDate =
		// command.DateValueOfParameterNamed("settlement_date");

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String settlementDateString = command.stringValueOfParameterNamed("settlement_date");

		Date settlementDate = new Date();
		try {
			settlementDate = formatter.parse(settlementDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String time = "";
		if (command.stringValueOfParameterNamed("local_transaction_time") != "") {
			time = command.stringValueOfParameterNamed("local_transaction_time");
		} else {
			throw new GeneralPlatformDomainRuleException("local_transaction_time missing",
					"local_transaction_time missing", "local_transaction_time missing");
		}

		final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

		LocalDate transactionDateDep = this.fromApiJsonHelper.extractLocalDateNamed("local_transaction_date", element,
				"yyyy-MM-dd", Locale.ENGLISH);

		boolean isDebit = false;

		boolean isTransferTransaction = false;

		boolean isCredit = false;

		boolean isPurchase = false;

		int transactionProcessingType = 0; // invalid;
		int transactionAmountType = 0; // invalid

		// determine the transaction
		String processingType = command.stringValueOfParameterNamed("processing_type");

		switch (processingType) {

		case "cash_withdrawal":
			isDebit = true;
			transactionProcessingType = InterswitchTransactionProcessingType.CASH_WITHDRAWAL.getValue();
			transactionAmountType = InterswitchTransactionAmountType.DEBIT.getValue();

			break;

		case "deposit":
			isCredit = true;
			transactionProcessingType = InterswitchTransactionProcessingType.DEPOSIT.getValue();
			transactionAmountType = InterswitchTransactionAmountType.CREDIT.getValue();
			break;

		case "payment_and_transfers":
			isTransferTransaction = true;
			transactionProcessingType = InterswitchTransactionProcessingType.PAYMENT_AND_TRANSFER.getValue();
			transactionAmountType = InterswitchTransactionAmountType.DEBIT.getValue();
			break;

		case "purchase":
			isPurchase = true;
			transactionProcessingType = InterswitchTransactionProcessingType.PURCHASE.getValue();
			transactionAmountType = InterswitchTransactionAmountType.DEBIT.getValue();
			break;
		}

		InterswitchEvents event = InterswitchEvents.getInstance(sessionId, InterswitchEventType.TRANSACTION.getValue(),
				transactionProcessingType, transactionAmountType, Integer.parseInt(responseCode), stan,
				authorizationNumber, null, settlementAmount, settlementDate, time);

		this.interswitchTransactionsRepository.save(event);
		
		

		// get savings account

		String accountNumberCredit = "";
		String accountNumberDebit = "";
		SavingsAccount savingsAccountCredit = null;
		SavingsAccount savingsAccountDebit = null;

		if (isTransferTransaction) {

			accountNumberDebit = command.stringValueOfParameterNamed("account_debit");
			accountNumberCredit = command.stringValueOfParameterNamed("account_credit");

			savingsAccountCredit = this.savingsAccountRepository
					.findNonClosedAccountByAccountNumber(accountNumberCredit);
			savingsAccountDebit = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumberDebit);

			if (savingsAccountCredit == null || savingsAccountDebit == null) {
				responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				event.setResponseCode(ResponseCodes.NOSAVINGSACCOUNT.getValue());
				this.interswitchTransactionsRepository.save(event);

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}

			savingsAccountDebit = this.savingAccountAssembler.assembleFrom(savingsAccountDebit.getId());

			savingsAccountCredit = this.savingAccountAssembler.assembleFrom(savingsAccountCredit.getId());

			if (savingsAccountDebit.getWithdrawableBalance().compareTo(settlementAmount) == -1) {
				// return response code as insufficient bal
				responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				event.setResponseCode(ResponseCodes.NOTSUFFICIENTFUNDS.getValue());
				this.interswitchTransactionsRepository.save(event);

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

			}

		} else if (isDebit || isPurchase) {

			accountNumberDebit = command.stringValueOfParameterNamed("account_debit");

			savingsAccountDebit = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumberDebit);

			if (savingsAccountDebit == null) {
				responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				event.setResponseCode(ResponseCodes.NOSAVINGSACCOUNT.getValue());
				this.interswitchTransactionsRepository.save(event);

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}

			savingsAccountDebit = this.savingAccountAssembler.assembleFrom(savingsAccountDebit.getId());

			if (savingsAccountDebit.getWithdrawableBalance().compareTo(settlementAmount) == -1) {
				// return response code as insufficient bal
				responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				event.setResponseCode(ResponseCodes.NOTSUFFICIENTFUNDS.getValue());
				this.interswitchTransactionsRepository.save(event);

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);

			}
		} else if (isCredit) {
			accountNumberCredit = command.stringValueOfParameterNamed("account_credit");
		
			savingsAccountCredit = this.savingsAccountRepository
					.findNonClosedAccountByAccountNumber(accountNumberCredit);
			

			if (savingsAccountCredit == null) {
				responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				event.setResponseCode(ResponseCodes.NOSAVINGSACCOUNT.getValue());
				this.interswitchTransactionsRepository.save(event);

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}

			savingsAccountCredit = this.savingAccountAssembler.assembleFrom(savingsAccountCredit.getId());
		} else {
			responseCode = ResponseCodes.ERROR.getValue() + "";
			authorizationNumber = ""; // because we did not execute
			// transaction???

			event.setResponseCode(ResponseCodes.ERROR.getValue());
			this.interswitchTransactionsRepository.save(event);

			return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
		}

		// if everything goes well

		SavingsAccountTransaction applicationTransaction;

		if (isTransferTransaction) {

			final boolean isExceptionForBalanceCheck = false;
			final boolean isRegularTransaction = true;
			final AccountTransferDTO accountTransferDTO = new AccountTransferDTO(transactionDateDep, settlementAmount,
					PortfolioAccountType.SAVINGS, PortfolioAccountType.SAVINGS, savingsAccountDebit.getId(),
					savingsAccountCredit.getId(), "Interswitch intra bank transfer", Locale.ENGLISH, fmt, null,
					SavingsAccountTransactionType.WITHDRAWAL.getValue(),
					SavingsAccountTransactionType.DEPOSIT.getValue(), null, null,
					AccountTransferType.ACCOUNT_TRANSFER.getValue(), null, null, null, null, savingsAccountCredit,
					savingsAccountDebit, isRegularTransaction, isExceptionForBalanceCheck);

			long transferTransactionId = 0;
			try {
				transferTransactionId = this.accountTransfersWritePlatformService.transferFunds(accountTransferDTO)
						.longValue();
				applicationTransaction = this.savingsAccountTransactionRepository.getOne(transferTransactionId);
				

			} catch (InsufficientAccountBalanceException e) {

				responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";
				authorizationNumber = ""; // because we did not execute
				// transaction???

				event.setResponseCode(ResponseCodes.NOTSUFFICIENTFUNDS.getValue());
				this.interswitchTransactionsRepository.save(event);

				return CommandProcessingResult.interswitchResponse(authorizationNumber, responseCode);
			}

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
					transactionDateDep, settlementAmount, null, transactionBooleanValues, true, false,event);
			
	

		} else if (isPurchase) {
			checkClientOrGroupActive(savingsAccountDebit);

			final boolean isAccountTransfer = false;
			final boolean isRegularTransaction = true;
			final boolean isApplyWithdrawFee = true;
			final boolean isInterestTransfer = false;
			final boolean isWithdrawBalance = false;
			final SavingsTransactionBooleanValues transactionBooleanValues = new SavingsTransactionBooleanValues(
					isAccountTransfer, isRegularTransaction, isApplyWithdrawFee, isInterestTransfer, isWithdrawBalance);

			applicationTransaction = this.savingsAccountDomainService.handleWithdrawal(savingsAccountDebit, fmt,
					transactionDateDep, settlementAmount, null, transactionBooleanValues, false, true,event);
			
		
		}

		else {
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
		// responseCode = String.format("%02d",
		// ResponseCodes.APPROVED.getValue());

		event.setResponseCode(ResponseCodes.APPROVED.getValue());
		event.setApplicationTransaction(applicationTransaction);
		event.setAuthorizationNumber(authorizationNumber);
		this.interswitchTransactionsRepository.save(event);
		
		InterswitchSubEvents subEvent=InterswitchSubEvents.getInstance(InterswitchEventType.TRANSACTION.getValue(), event, applicationTransaction);
		this.interswitchSubEventsRepository.save(subEvent);

		// send the response code
		return CommandProcessingResult.interswitchResponse(authorizationNumber, ResponseCodes.APPROVED.getValue() + "");

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
