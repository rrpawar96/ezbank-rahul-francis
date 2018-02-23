package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.accounting.journalentry.service.JournalEntryWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;
import org.apache.fineract.infrastructure.interswitch.data.SavingsProductChargesData;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchEvent;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchEventsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.InterswitchSubEventsRepository;
import org.apache.fineract.infrastructure.interswitch.domain.ResponseCodes;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeRepository;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentTypeRepositoryWrapper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountCharge;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargeAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountChargeRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsProductChargeRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsProductRepository;
import org.apache.fineract.portfolio.savings.exception.InsufficientAccountBalanceException;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
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
	private final SavingsAccountAssembler savingAccountAssembler;
	private final SavingsAccountWritePlatformService savingsAccountWritePlatformService;
	private final InterswitchEventsRepository interswitchTransactionsRepository;
	private final ChargeRepository chargeRepository;
	private final SavingsAccountChargeRepository savingsAccountChargeRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentTypeRepositoryWrapper PaymentTypeRepositoryWrapper;

	@Autowired
	public InterswitchReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
			final FromJsonHelper fromApiJsonHelper, final SavingsAccountRepository savingsAccountRepository,
			final SavingsAccountTransactionRepository savingsAccountTransactionRepository,
			final SavingsAccountAssembler savingAccountAssembler,
			final SavingsAccountWritePlatformService savingsAccountWritePlatformService,
			final InterswitchEventsRepository interswitchTransactionsRepository,
			final ChargeRepository chargeRepository,
			final SavingsAccountChargeRepository savingsAccountChargeRepository,
			final PaymentDetailRepository paymentDetailRepository,
			final PaymentTypeRepositoryWrapper PaymentTypeRepositoryWrapper) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.savingsAccountRepository = savingsAccountRepository;
		this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
		this.savingAccountAssembler = savingAccountAssembler;
		this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
		this.interswitchTransactionsRepository = interswitchTransactionsRepository;
		this.chargeRepository = chargeRepository;
		this.savingsAccountChargeRepository = savingsAccountChargeRepository;
		this.paymentDetailRepository=paymentDetailRepository;
		this.PaymentTypeRepositoryWrapper=PaymentTypeRepositoryWrapper;

	}

	private static final class SavingsProductChargesMapping implements RowMapper<SavingsProductChargesData> {

		public String schema() {
			return "savings_product_id as savingsProductId,charge_id as chargeId from m_savings_product_charge";

		}

		@Override
		public SavingsProductChargesData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			final long savingsProductId = rs.getLong("savingsProductId");

			final long chargesId = rs.getLong("chargeId");

			return SavingsProductChargesData.getInstance(savingsProductId, chargesId);

		}
	}

	@Override
	public Collection<SavingsProductChargesData> retrieveSavingsProductChargesMapping(long savingsProductId) {
		this.context.authenticatedUser();

		final SavingsProductChargesMapping rm = new SavingsProductChargesMapping();
		final String sql = "select " + rm.schema() + " where savings_product_id=?";

		return this.jdbcTemplate.query(sql, rm, new Object[] { savingsProductId });
	}

	@Override
	public InterswitchBalanceWrapper retrieveBalance(String json, boolean isInternalRequest) {
		this.context.authenticatedUser();
		final String accountType = "10";// hard code
		final String amountType = "02"; // hard code
		final String amountType_ledger = "01";
		final String currency = "800"; // need to find better method
		final String amountSign = "C"; // hard code

		List<InterswitchBalanceEnquiryData> balances;
		String responseCode = ResponseCodes.ERROR.getValue() + "";
		int authorizationNumber = (int) (100000 + Math.random() * 999999);

		// try{
		final JsonElement element = this.fromApiJsonHelper.parse(json);
		JsonObject requestBody = element.getAsJsonObject();

		//

		String accountNumber = null;
		SavingsAccount savingsAccount = null;

		///
		String sessionId = "";
		if (requestBody.get("session_id") != null) {
			sessionId = requestBody.get("session_id").getAsString();
		}

		String stan = "";
		if (requestBody.get("stan") != null) {
			stan = requestBody.get("stan").getAsString();
		}

		String processingType = null;
		if (requestBody.get("processing_type") != null) {
			processingType = requestBody.get("processing_type").getAsString();
		}

		BigDecimal surCharge = BigDecimal.ZERO;
		if (requestBody.get("transaction_fee") != null) {
			JsonObject transactionfee = requestBody.get("transaction_fee").getAsJsonObject();

			System.out.println("surcharge is " + transactionfee.get("amount").getAsString());
			if (transactionfee.get("amount") != null) {
				surCharge = BigDecimal.valueOf(Double.parseDouble(transactionfee.get("amount").getAsString()));
			}

		}
		
		if (processingType != null) {
			switch (processingType) {

			case "cash_withdrawal":
				if (requestBody.get("account_debit") != null)
					accountNumber = requestBody.get("account_debit").getAsString();
				break;

			case "deposit":
				if (requestBody.get("account_credit") != null)
					accountNumber = requestBody.get("account_credit").getAsString();
				break;

			case "payment_and_transfers":
				if (requestBody.get("account_debit") != null)
					accountNumber = requestBody.get("account_debit").getAsString();
				break;

			case "purchase":
				if (requestBody.get("account_debit") != null)
					accountNumber = requestBody.get("account_debit").getAsString();
				break;
			}

			savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);

		} else {
			if (requestBody.get("account_debit") != null) {
				accountNumber = requestBody.get("account_debit").getAsString();
				savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			}

			if (requestBody.get("account_credit") != null) {

				accountNumber = requestBody.get("account_credit").getAsString();

				savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			}

		}

		InterswitchEvent event = InterswitchEvent.getInstance(sessionId,
				InterswitchEventType.BALANCE_ENQUIRY.getValue(), 0, 0, Integer.parseInt(responseCode), stan,
				authorizationNumber + "", null, null, null, "");

		if (!isInternalRequest) {

			this.interswitchTransactionsRepository.save(event);

		}

		if (savingsAccount == null) {
			responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
			if (!isInternalRequest) {
				event.setResponseCode(Integer.parseInt(responseCode));
				this.interswitchTransactionsRepository.save(event);
			}

			return InterswitchBalanceWrapper.getInstance(null, responseCode, authorizationNumber + "");
		}

		if (!isInternalRequest) {
			try{

			int chargeType = 0;
			ChargeTimeType chargeTimeType;
			SavingsAccountCharge savingsAccountCharge;
			Charge charge;
			savingsAccount = savingAccountAssembler.assembleFrom(savingsAccount.getId());

			List<SavingsProductChargesData> chargeData = (List<SavingsProductChargesData>) retrieveSavingsProductChargesMapping(
					savingsAccount.getProduct().getId());

			for (SavingsProductChargesData productCharge : chargeData) {

				System.out.println("savings product charge:  savings product id" + productCharge.getSavingsProductId()
						+ " charge id" + productCharge.getChargesId());

				charge = this.chargeRepository.findOne(productCharge.getChargesId());

				chargeType = charge.getChargeTimeType();

				chargeTimeType = ChargeTimeType.fromInt(chargeType);
				System.out.println(chargeTimeType.getCode());

				if (chargeTimeType.isATMBalanceEnquiryFee()) {

					if (this.savingsAccountChargeRepository.findByChargeAndSavingsAccountId(charge,
							savingsAccount.getId()) == null) {
						savingsAccountCharge = SavingsAccountCharge.createNewFromJson(savingsAccount, charge);
						this.savingsAccountChargeRepository.save(savingsAccountCharge);
						savingsAccount.charges().add(savingsAccountCharge);
						this.savingsAccountRepository.save(savingsAccount);
					} else {
						savingsAccountCharge = this.savingsAccountChargeRepository
								.findByChargeAndSavingsAccountId(charge, savingsAccount.getId());
					}
					
					SavingsAccountTransaction chargeTransaction=this.savingsAccountWritePlatformService.applyInterswitchChargeDue(savingsAccountCharge.getId(),
							savingsAccount.getId(), event, surCharge);
					chargeTransaction=this.savingsAccountTransactionRepository.findOne(chargeTransaction.getId());
					
					PaymentDetail paymentDetail=buildAndPersistPaymentDetails(event.getAuthorizationNumber(),chargeTimeType.name());
					chargeTransaction.setPaymentDetail(paymentDetail);
					this.savingsAccountTransactionRepository.save(chargeTransaction);

				}
			}

			event = this.interswitchTransactionsRepository.getOne(event.getId());
			event.setResponseCode(ResponseCodes.APPROVED.getValue());
			this.interswitchTransactionsRepository.save(event);
			
		}
			catch (InsufficientAccountBalanceException e) {

				responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";
			
				// event=this.interswitchTransactionsRepository.findOne(event.getId());
				event.setResponseCode(ResponseCodes.NOTSUFFICIENTFUNDS.getValue());
				this.interswitchTransactionsRepository.save(event);

				return InterswitchBalanceWrapper.getInstance(null, responseCode, authorizationNumber + "");
			}	

		}

		final BigDecimal amount = savingsAccount.getWithdrawableBalance();

		InterswitchBalanceEnquiryData actualBalance = InterswitchBalanceEnquiryData.getInstance(accountType, amountType,
				currency, amount, amountSign);

		InterswitchBalanceEnquiryData ledgerBalance = InterswitchBalanceEnquiryData.getInstance(accountType,
				amountType_ledger, currency, amount, amountSign);

		balances = new ArrayList<InterswitchBalanceEnquiryData>();
		balances.add(ledgerBalance);
		balances.add(actualBalance);

		return InterswitchBalanceWrapper.getInstance(balances, String.format("%02d", ResponseCodes.APPROVED.getValue()),
				authorizationNumber + "");

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
		int authorizationNumber = (int) (100000 + Math.random() * 999999);
		String responseCode = ResponseCodes.ERROR.getValue() + "";

		InterswitchBalanceWrapper balance = retrieveBalance(json, true);

		final JsonElement element = this.fromApiJsonHelper.parse(json);
		JsonObject requestBody = element.getAsJsonObject();

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		String accountNumber = null;
		if (requestBody.get("account_debit") != null) {
			accountNumber = requestBody.get("account_debit").getAsString();
		}

		BigDecimal surCharge = BigDecimal.ZERO;
		if (requestBody.get("surcharge") != null) {
			surCharge = requestBody.get("surcharge").getAsBigDecimal();
		}

		///
		String sessionId = "";
		if (requestBody.get("session_id") != null) {
			sessionId = requestBody.get("session_id").getAsString();
		}

		String stan = "";
		if (requestBody.get("stan") != null) {
			stan = requestBody.get("stan").getAsString();
		}

		SavingsAccount savingsAccount = this.savingsAccountRepository
				.findNonClosedAccountByAccountNumber(accountNumber);

		InterswitchEvent event = InterswitchEvent.getInstance(sessionId, InterswitchEventType.STATEMENT.getValue(), 0,
				0, Integer.parseInt(responseCode), stan, authorizationNumber + "", null, null, null, "");

		this.interswitchTransactionsRepository.save(event);

		if (savingsAccount == null) {
			responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
			event.setResponseCode(Integer.parseInt(responseCode));
			this.interswitchTransactionsRepository.save(event);

			return MinistatementDataWrapper.getInstance(null, null, responseCode, authorizationNumber + "");
		}

		// charges

		try
		{
		int chargeType = 0;
		ChargeTimeType chargeTimeType;
		SavingsAccountCharge savingsAccountCharge;
		Charge charge;
		savingsAccount = savingAccountAssembler.assembleFrom(savingsAccount.getId());

		List<SavingsProductChargesData> chargeData = (List<SavingsProductChargesData>) retrieveSavingsProductChargesMapping(
				savingsAccount.getProduct().getId());

		for (SavingsProductChargesData productCharge : chargeData) {

			charge = this.chargeRepository.findOne(productCharge.getChargesId());

			chargeType = charge.getChargeTimeType();

			chargeTimeType = ChargeTimeType.fromInt(chargeType);
			System.out.println(chargeTimeType.getCode());

			if (chargeTimeType.isATMMinistatementFee()) {

				if (this.savingsAccountChargeRepository.findByChargeAndSavingsAccountId(charge,
						savingsAccount.getId()) == null) {
					savingsAccountCharge = SavingsAccountCharge.createNewFromJson(savingsAccount, charge);
					this.savingsAccountChargeRepository.save(savingsAccountCharge);
					savingsAccount.charges().add(savingsAccountCharge);
					this.savingsAccountRepository.save(savingsAccount);
				} else {
					savingsAccountCharge = this.savingsAccountChargeRepository.findByChargeAndSavingsAccountId(charge,
							savingsAccount.getId());
				}

				SavingsAccountTransaction chargeTransaction=this.savingsAccountWritePlatformService.applyInterswitchChargeDue(savingsAccountCharge.getId(),
						savingsAccount.getId(), event, surCharge);
				
				chargeTransaction=this.savingsAccountTransactionRepository.findOne(chargeTransaction.getId());
				
				PaymentDetail paymentDetail=buildAndPersistPaymentDetails(event.getAuthorizationNumber(),chargeTimeType.name());
				
				chargeTransaction.setPaymentDetail(paymentDetail);
				this.savingsAccountTransactionRepository.save(chargeTransaction);

			}
		}
		
		}
		catch (InsufficientAccountBalanceException e) {

			responseCode = ResponseCodes.NOTSUFFICIENTFUNDS.getValue() + "";
		
			// event=this.interswitchTransactionsRepository.findOne(event.getId());
			event.setResponseCode(ResponseCodes.NOTSUFFICIENTFUNDS.getValue());
			this.interswitchTransactionsRepository.save(event);

			return MinistatementDataWrapper.getInstance(null, null, responseCode,
					authorizationNumber + "");
		}	
		
		// end of charges

		List<SavingsAccountTransaction> transactions = this.savingsAccountTransactionRepository
				.findBySavingsAccountId(savingsAccount.getId());

		if (transactions == null) {

			responseCode = ResponseCodes.ERROR.getValue() + "";
			event = this.interswitchTransactionsRepository.findOne(event.getId());
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

				if (SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue().length() >= 10) {
					transactionType = SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue()
							.substring(0, 9);
				} else {
					transactionType = SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue();
				}

				transactionMap.put("tran_type", transactionType);
				transactionMap.put("curr_code", 800);
				transactionMap.put("tran_amount", transaction.getAmount());

				miniStatement.add(transactionMap);
				i--;
				numberOfTransactions--;

			}

			responseCode = ResponseCodes.APPROVED.getValue() + "";
			event = this.interswitchTransactionsRepository.findOne(event.getId());
			event.setResponseCode(Integer.parseInt(responseCode));
			this.interswitchTransactionsRepository.save(event);

			return MinistatementDataWrapper.getInstance(miniStatement, balance.getAdditional_amount(),
					String.format("%02d", ResponseCodes.APPROVED.getValue()), authorizationNumber + "");

		} catch (Exception e) {

			event.setResponseCode(Integer.parseInt(responseCode));
			this.interswitchTransactionsRepository.save(event);

			return MinistatementDataWrapper.getInstance(null, null, ResponseCodes.ERROR.getValue() + "",
					authorizationNumber + "");
		}

	}
	
	private PaymentDetail buildAndPersistPaymentDetails(String authorizationNumber, String processingType) {

		// add is atm transaction payment detail type
		String paymentDescription = "";
		switch (processingType) {

		case "cash_withdrawal":
			paymentDescription = "Cash Withdrawal Request From InterSwitch";
			break;

		case "deposit":
			paymentDescription = "Deposit Request From InterSwitch";
			break;

		case "payment_and_transfers":
			paymentDescription = "Payment Transfer Request From InterSwitch";
			break;

		case "purchase":
			paymentDescription = "Purchase Request From InterSwitch";
			break;

		case "ATM_WITHDRAWAL_FEE":
			paymentDescription = "charge applied for InterSwitch Withdrawal transaction ";
			break;

		case "ATM_BALANCE_ENQUIRY_FEE":
			paymentDescription = "charge applied for InterSwitch Balance Enquiry transaction ";
			break;

		case "ATM_MINISTATEMENT_FEE":
			paymentDescription = "charge applied for InterSwitch Minitstatement transaction ";
			break;

		case "ATM_PURCHASE_FEE":
			paymentDescription = "charge applied for InterSwitch Purchase transaction ";
			break;
			
		case "ATM_TRANSFER_FEE":
			paymentDescription = "charge applied for InterSwitch Transfer transaction ";
			break;	

		}

		PaymentType paymentType = PaymentTypeRepositoryWrapper.findOneByValueWithNotFoundDetection("ATM");
		PaymentDetail paymentDetail = PaymentDetail.instance(paymentType, null, null, null, authorizationNumber, null,
				null, paymentDescription);
		this.paymentDetailRepository.save(paymentDetail);
		return paymentDetail;

	}

}
