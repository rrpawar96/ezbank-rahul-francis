package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityTransaction;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchEvents;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchEventsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchSubEvents;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchSubEventsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.ResponseCodes;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountCharge;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargeRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class InterswitchReadPlatformServiceImpl implements InterswitchReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private final SavingsAccountRepository savingsAccountRepository;
	private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;
	private final SavingsAccountChargeRepository repository;
	private final SavingsAccountAssembler savingAccountAssembler;
	private final SavingsAccountDomainService savingsDomainService;
	private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
	private final JournalEntryWritePlatformService journalEntryWritePlatformService;
	private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper;
	private final BusinessEventNotifierService businessEventNotifierService;
	private final InterswitchEventsRepository interswitchTransactionsRepository;
	private final InterswitchSubEventsRepository interswitchSubEventsRepository;

	@Autowired
	public InterswitchReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
			final FromJsonHelper fromApiJsonHelper, final SavingsAccountRepository savingsAccountRepository,
			final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
			final SavingsAccountChargeRepository repository, final SavingsAccountAssembler savingAccountAssembler,
			final SavingsAccountDomainService savingsDomainService,
			final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
			final JournalEntryWritePlatformService journalEntryWritePlatformService,
			final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepositoryWrapper,
			final BusinessEventNotifierService businessEventNotifierService,
			final InterswitchEventsRepository interswitchTransactionsRepository,
			final InterswitchSubEventsRepository interswitchSubEventsRepository) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.savingsAccountRepository = savingsAccountRepository;
		this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
		this.repository = repository;
		this.savingAccountAssembler = savingAccountAssembler;
		this.savingsDomainService = savingsDomainService;
		this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
		this.journalEntryWritePlatformService = journalEntryWritePlatformService;
		this.applicationCurrencyRepositoryWrapper = applicationCurrencyRepositoryWrapper;
		this.businessEventNotifierService = businessEventNotifierService;
		this.interswitchTransactionsRepository=interswitchTransactionsRepository;
		this.interswitchSubEventsRepository=interswitchSubEventsRepository;

	}

	private static final class InterswitchBalanceEnquiryMapper implements RowMapper<InterswitchBalanceEnquiryData> {
		public String schema() {
			return "running_balance_derived as balance,max(id) from m_savings_account_transaction";

		}

		@Override
		public InterswitchBalanceEnquiryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			final String accountType = "10";// hard code
			final String amountType = "02"; // hard code
			final String currency = "UGX"; // need to find better method
			final BigDecimal amount = rs.getBigDecimal("balance"); // query
			final String amountSign = "C"; // hard code

			return InterswitchBalanceEnquiryData.getInstance(accountType, amountType, currency, amount, amountSign);

		}
	}

	@Override
	public InterswitchBalanceWrapper retrieveBalance(String json,boolean isInternalRequest) {
		this.context.authenticatedUser();
		final String accountType = "10";// hard code
		final String amountType = "02"; // hard code
		final String amountType_ledger = "01";
		final String currency = "800"; // need to find better method
		final String amountSign = "C"; // hard code

		List<InterswitchBalanceEnquiryData> balances;
		String responseCode = ResponseCodes.ERROR.getValue()+"";
		int authorizationNumber=(int) (100000 + Math.random() * 999999) ;

		// try{
		final JsonElement element = this.fromApiJsonHelper.parse(json);
		JsonObject requestBody = element.getAsJsonObject();

		//

		String accountNumber = null;
		SavingsAccount savingsAccount = null;

		///
		String sessionId="";
		if(requestBody.get("session_id") !=null)
		{
			 sessionId =requestBody.get("session_id").getAsString();
		}
		
		String stan="";
		if(requestBody.get("stan")!=null)
		{
			stan =requestBody.get("stan").getAsString();
		}
	
		
		String processingType=null;
		if(requestBody.get("processing_type")!=null)
		{
			processingType = requestBody.get("processing_type").getAsString();
		}
	 

		if (processingType != null) {
			switch (processingType) {

			case "cash_withdrawal":
				if(requestBody.get("account_debit")!=null)
				accountNumber = requestBody.get("account_debit").getAsString();
				break;

			case "deposit":
				if(requestBody.get("account_credit")!=null)
				accountNumber = requestBody.get("account_credit").getAsString();
				break;

			case "payment_and_transfers":
				if(requestBody.get("account_debit")!=null)
				accountNumber = requestBody.get("account_debit").getAsString();
				break;

			case "purchase":
				if(requestBody.get("account_debit")!=null)
				accountNumber = requestBody.get("account_debit").getAsString();
				break;
			}

			savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);

		} else {
			if (requestBody.get("account_debit") != null) {
				accountNumber = requestBody.get("account_debit").getAsString();
				savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			}

			if (requestBody.get("account_credit") != null ) {

				accountNumber = requestBody.get("account_credit").getAsString();

				savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			}

		}
		

		///
		
	/*	InterswitchEvents(String sessionId,int eventType,int transactionProcessingType,
				int transactionAmountType,int responseCode,String stan,String authorizationNumber,SavingsAccountTransaction applicationTransaction,
				BigDecimal settlementAmount,Date settlementDate,String transactionTime)*/
		
		
		InterswitchEvents event=InterswitchEvents.getInstance(sessionId, InterswitchEventType.BALANCE_ENQUIRY.getValue(),0,
				0, Integer.parseInt(responseCode), stan, authorizationNumber+"",null,
				null, null, "");
		
		if(!isInternalRequest)
		{

			this.interswitchTransactionsRepository.saveAndFlush(event);
			
		}
		
		
		

		if (savingsAccount == null) {
			responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
			if(!isInternalRequest)
			{
			event.setResponseCode(Integer.parseInt(responseCode));
			this.interswitchTransactionsRepository.save(event);
			}
			
			return InterswitchBalanceWrapper.getInstance(null, responseCode,
					authorizationNumber + "");
		}

		final BigDecimal amount = savingsAccount.getWithdrawableBalance();

		InterswitchBalanceEnquiryData actualBalance = InterswitchBalanceEnquiryData.getInstance(accountType, amountType,
				currency, amount, amountSign);

		InterswitchBalanceEnquiryData ledgerBalance = InterswitchBalanceEnquiryData.getInstance(accountType,
				amountType_ledger, currency, amount, amountSign);

		balances = new ArrayList<InterswitchBalanceEnquiryData>();
		balances.add(ledgerBalance);
		balances.add(actualBalance);

		// AppUser user = getAppUserIfPresent();

		// savingsAccount.payATMBalanceEnquiryFee(charge, new LocalDate(),
		// user);

		// this.savingsAccountRepository.save(savingsAccount);
		
		
		if(!isInternalRequest)
		{

		Set<Long> existingTransactionIds = new HashSet<>();
		Set<Long> existingReversedTransactionIds = new HashSet<>();

		List<SavingsAccountCharge> charges = new ArrayList<>();

		charges = this.repository.findBySavingsAccountId(savingsAccount.getId());

		for (SavingsAccountCharge charge : charges) {
			
			if (charge.isActive() && charge.isATMBalanceEnquiryFee()) {

				AppUser user = getAppUserIfPresent();

				savingsAccount = savingAccountAssembler.assembleFrom(savingsAccount.getId());

				/*
				 * System.out.println("using savings account with id "
				 * +savingsAccount.getId());
				 * 
				 * savingsAccount.payATMBalanceEnquiryFee(charge, new
				 * LocalDate(), user);
				 * 
				 * this.savingAccountAssembler.assignSavingAccountHelpers(
				 * savingsAccount);
				 * 
				 * updateExistingTransactionsDetails(savingsAccount,
				 * existingTransactionIds, existingReversedTransactionIds);
				 * 
				 * this.savingsAccountRepository.saveAndFlush(savingsAccount);
				 * 
				 * 
				 * postJournalEntries(savingsAccount, existingTransactionIds,
				 * existingReversedTransactionIds);
				 */

				this.savingsAccountWritePlatformService.applyCustomChargeDue(charge.getId(), savingsAccount.getId());
				/*
				 * savingsAccount.payATMPurchaseFee(transactionAmoount,
				 * transactionDate, user);
				 * 
				 * Money chargeAmount = Money.of(savingsAccount.getCurrency(),
				 * BigDecimal.valueOf(10.0));
				 * 
				 * SavingsAccountTransaction transaction =
				 * SavingsAccountTransaction.atmBalanceEnquiryFee(
				 * savingsAccount, savingsAccount.office(), new LocalDate(),
				 * chargeAmount, user);
				 * 
				 * final SavingsAccountChargePaidBy chargePaidBy =
				 * SavingsAccountChargePaidBy.instance(transaction, charge,
				 * transaction.getAmount(savingsAccount.getCurrency()).getAmount
				 * ());
				 * 
				 * transaction.getSavingsAccountChargesPaid().add(chargePaidBy);
				 * 
				 * savingsAccount.addTransaction(transaction);
				 * 
				 * this.savingsAccountRepository.save(savingsAccount);
				 */
				InterswitchEvents tempEvent=this.interswitchTransactionsRepository.findOne(event.getId());
				System.out.println("event fetched "+tempEvent.getId());
				InterswitchSubEvents subEvent=InterswitchSubEvents.getInstance(InterswitchEventType.CHARGE.getValue(), tempEvent, null);
				this.interswitchSubEventsRepository.save(subEvent);
				

			}
		}
		
		}
		
	
		
		
		if(!isInternalRequest)
		{
			event.setResponseCode(ResponseCodes.APPROVED.getValue());
			this.interswitchTransactionsRepository.save(event);
		}

		return InterswitchBalanceWrapper.getInstance(balances, String.format("%02d", ResponseCodes.APPROVED.getValue()),
				authorizationNumber+"" );

		// return transactionMap;
		// }
		/*
		 * catch(Exception e) {
		 * 
		 * System.out.println(e); return
		 * InterswitchBalanceWrapper.getInstance(null,
		 * ResponseCodes.ERROR.getValue() + "", (int)(100000 + Math.random() *
		 * 999999)+""); }
		 */

	}

	private Map<BUSINESS_ENTITY, Object> constructEntityMap(final BUSINESS_ENTITY entityEvent, Object entity) {
		Map<BUSINESS_ENTITY, Object> map = new HashMap<>(1);
		map.put(entityEvent, entity);
		return map;
	}

	private void updateExistingTransactionsDetails(SavingsAccount account, Set<Long> existingTransactionIds,
			Set<Long> existingReversedTransactionIds) {
		existingTransactionIds.addAll(account.findExistingTransactionIds());
		existingReversedTransactionIds.addAll(account.findExistingReversedTransactionIds());
	}

	private void postJournalEntries(final SavingsAccount savingsAccount, final Set<Long> existingTransactionIds,
			final Set<Long> existingReversedTransactionIds) {

		final MonetaryCurrency currency = savingsAccount.getCurrency();
		final ApplicationCurrency applicationCurrency = this.applicationCurrencyRepositoryWrapper
				.findOneWithNotFoundDetection(currency);
		boolean isAccountTransfer = false;
		final Map<String, Object> accountingBridgeData = savingsAccount.deriveAccountingBridgeData(
				applicationCurrency.toData(), existingTransactionIds, existingReversedTransactionIds,
				isAccountTransfer);
		this.journalEntryWritePlatformService.createJournalEntriesForSavings(accountingBridgeData);
	}

	private AppUser getAppUserIfPresent() {
		AppUser user = null;
		if (this.context != null) {
			user = this.context.getAuthenticatedUserIfPresent();
		}
		return user;
	}

	@Override
	public InterswitchBalanceWrapper retrieveBalanceForUndoTransaction(long transactionId) {
		this.context.authenticatedUser();
		final String accountType = "10";// hard code
		final String amountType = "02"; // hard code
		final String amountType_ledger = "01";
		final String currency = "800"; // need to find better method
		final String amountSign = "C"; // hard code
	

		List<InterswitchBalanceEnquiryData> balances;

		SavingsAccountTransaction transaction = this.savingsAccountTransactionRepository.findOne(transactionId);

		try {
			// SavingsAccount savingsAccount =
			// this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			long savingsAccountId = transaction.getSavingsAccount().getId();

			SavingsAccount savingsAccount = this.savingsAccountRepository.getOne(savingsAccountId);

			if (savingsAccount == null) {

				return InterswitchBalanceWrapper.getInstance(null, ResponseCodes.ERROR.getValue() + "",
						(int) (100000 + Math.random() * 999999) + "");
			}

			final BigDecimal amount = savingsAccount.getWithdrawableBalance();

			InterswitchBalanceEnquiryData actualBalance = InterswitchBalanceEnquiryData.getInstance(accountType,
					amountType, currency, amount, amountSign);

			InterswitchBalanceEnquiryData ledgerBalance = InterswitchBalanceEnquiryData.getInstance(accountType,
					amountType_ledger, currency, amount, amountSign);

			balances = new ArrayList<InterswitchBalanceEnquiryData>();
			balances.add(ledgerBalance);
			balances.add(actualBalance);

			return InterswitchBalanceWrapper.getInstance(balances,
					String.format("%02d", ResponseCodes.APPROVED.getValue()),
					(int) (100000 + Math.random() * 999999) + "");

			// return transactionMap;
		} catch (Exception e) {
			return InterswitchBalanceWrapper.getInstance(null, ResponseCodes.ERROR.getValue() + "",
					(int) (100000 + Math.random() * 999999) + "");
		}

	}

	@Override
	public MinistatementDataWrapper getMinistatement(String json) {
		List<HashMap<String, Object>> miniStatement = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> transactionMap;
		int authorizationNumber=(int) (100000 + Math.random() * 999999);
		String responseCode = ResponseCodes.ERROR.getValue()+"";
		

		InterswitchBalanceWrapper balance = retrieveBalance(json,true);

		final JsonElement element = this.fromApiJsonHelper.parse(json);
		JsonObject requestBody = element.getAsJsonObject();

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
		String accountNumber = null;
			if(requestBody.get("account_debit") !=null)
			{
				accountNumber=requestBody.get("account_debit").getAsString();
			}
		
		///
		String sessionId="";
		if(requestBody.get("session_id") !=null)
		{
			 sessionId =requestBody.get("session_id").getAsString();
		}
		
		String stan="";
		if(requestBody.get("stan")!=null)
		{
			stan =requestBody.get("stan").getAsString();
		}

		SavingsAccount savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			
			
			InterswitchEvents event=InterswitchEvents.getInstance(sessionId, InterswitchEventType.STATEMENT.getValue(),0,
					0, Integer.parseInt(responseCode), stan, authorizationNumber+"",null,
					null, null, "");
			 this.interswitchTransactionsRepository.save(event);
			 
			
		
			 
			if(savingsAccount==null )
			{
				responseCode=ResponseCodes.NOSAVINGSACCOUNT.getValue()+"";
				event.setResponseCode(Integer.parseInt(responseCode));
				this.interswitchTransactionsRepository.save(event);
				
				return MinistatementDataWrapper.getInstance(null, null,   responseCode,
						authorizationNumber + "");	
			}
			
			 
			

			List<SavingsAccountTransaction> transactions = this.savingsAccountTransactionRepository
					.findBySavingsAccountId(savingsAccount.getId());
			// savingsAccount.getTransactions();
			
			
			
			if(transactions==null)
			{
				
				responseCode=ResponseCodes.ERROR.getValue()+"";
				event.setResponseCode(Integer.parseInt(responseCode));
				this.interswitchTransactionsRepository.save(event);
				
				return MinistatementDataWrapper.getInstance(null, null, ResponseCodes.ERROR.getValue() + "",
						authorizationNumber + "");	
			}
			
			

			String transactionType = "";

			int numberOfTransactions = transactions.size() - 1;

			int i = 5;
			try {
			SavingsAccountTransaction transaction;
			while (i > 0 && numberOfTransactions >= 0) {
				transaction = transactions.get(numberOfTransactions);
				transactionMap = new HashMap<String, Object>();
				
				transactionMap.put("seq_nr", transaction.getId() + "");
				transactionMap.put("date_time", dateFormat.format(transaction.getDateOf()) + "");
				
				
				if(SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue().length()>=10)
				{
					transactionType = SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue().substring(0,9);	
				}
				else
				{
					transactionType = SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue();
				}
				
				transactionMap.put("tran_type", transactionType);
				transactionMap.put("curr_code", 800);
				transactionMap.put("tran_amount", transaction.getAmount());

				miniStatement.add(transactionMap);
				i--;
				numberOfTransactions--;

			}
			
			responseCode=ResponseCodes.APPROVED.getValue()+"";
			event.setResponseCode(Integer.parseInt(responseCode));
			this.interswitchTransactionsRepository.save(event);

			return MinistatementDataWrapper.getInstance(miniStatement, balance.getAdditional_amount(),
					String.format("%02d", ResponseCodes.APPROVED.getValue()),
					authorizationNumber + "");

		} catch (Exception e) {
			
			
			event.setResponseCode(Integer.parseInt(responseCode));
			this.interswitchTransactionsRepository.save(event);
			
			
			return MinistatementDataWrapper.getInstance(null, null, ResponseCodes.ERROR.getValue() + "",
					authorizationNumber + "");
		}

	}

}
