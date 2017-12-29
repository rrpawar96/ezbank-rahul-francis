package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceWrapper;
import org.apache.fineract.infrastructure.interswitch.data.MinistatementDataWrapper;
import org.apache.fineract.infrastructure.interswitch.domain.ResponseCodes;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransactionRepository;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class InterswitchReadPlatformServiceImpl implements InterswitchReadPlatformService
{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private final SavingsAccountRepository savingsAccountRepository;
	private final SavingsAccountTransactionRepository savingsAccountTransactionRepository;

	@Autowired
	public InterswitchReadPlatformServiceImpl(final PlatformSecurityContext context,
			final RoutingDataSource dataSource,final FromJsonHelper fromApiJsonHelper,
			final SavingsAccountRepository savingsAccountRepository,
			final SavingsAccountTransactionRepository savingsAccountTransactionRepository) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.savingsAccountRepository=savingsAccountRepository;
		this.savingsAccountTransactionRepository=savingsAccountTransactionRepository;
	}
	
	private static final class InterswitchBalanceEnquiryMapper implements RowMapper<InterswitchBalanceEnquiryData> 
	{
		public String schema() {
			return "running_balance_derived as balance,max(id) from m_savings_account_transaction";
						

		}

		@Override
		public InterswitchBalanceEnquiryData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			
			final String accountType = "10";// hard code
			final String amountType = "02";  // hard code
			final String currency = "UGX";     // need to find better method
			final BigDecimal amount = rs.getBigDecimal("balance");  //query
			final String amountSign = "C";  // hard code
			
			return InterswitchBalanceEnquiryData.getInstance(accountType, amountType, currency, amount,
					amountSign);

		}
	}

	
	
	
	@Override
	public InterswitchBalanceWrapper retrieveBalance(String json) {
		this.context.authenticatedUser();
		final String accountType = "10";// hard code
		final String amountType = "02";  // hard code
		final String amountType_ledger = "01";
		final String currency = "800";     // need to find better method
		final String amountSign = "C";  // hard code
		
		List<InterswitchBalanceEnquiryData> balances;
		String responseCode="";
		
		
		try{
			final JsonElement element = this.fromApiJsonHelper.parse(json);
			JsonObject requestBody=element.getAsJsonObject();
			
			String accountNumber="";
			if(requestBody.get("account_debit")!=null)
			{
				accountNumber=requestBody.get("account_debit").getAsString();
			}
			else if(requestBody.get("account_credit")!=null)
			{
				accountNumber=requestBody.get("account_credit").getAsString();
			}
			
			
			SavingsAccount savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			
			if (savingsAccount == null) {
				responseCode = ResponseCodes.NOSAVINGSACCOUNT.getValue() + "";
				
				return InterswitchBalanceWrapper.getInstance(null,  null, null);
			}
			
			final BigDecimal amount = savingsAccount.getWithdrawableBalance();
		
			
			InterswitchBalanceEnquiryData actualBalance=InterswitchBalanceEnquiryData.getInstance(accountType, amountType, currency, amount,
					amountSign);
			
			InterswitchBalanceEnquiryData ledgerBalance=InterswitchBalanceEnquiryData.getInstance(accountType, amountType_ledger, currency, amount,
					amountSign);
			
			balances=new ArrayList<InterswitchBalanceEnquiryData>();
			balances.add(ledgerBalance);
			balances.add(actualBalance);
			
			
			
			return InterswitchBalanceWrapper.getInstance(balances,  String.format("%02d", ResponseCodes.APPROVED.getValue()), (int)(100000 + Math.random() * 999999)+"");
			
			
			
			//return transactionMap;
		}
		catch(Exception e)
		{
			return InterswitchBalanceWrapper.getInstance(null,  ResponseCodes.ERROR.getValue() + "", (int)(100000 + Math.random() * 999999)+"");
		}
		
		
		
		
		
		
		}
	
	
	@Override
	public InterswitchBalanceWrapper retrieveBalanceForUndoTransaction(long transactionId) {
		this.context.authenticatedUser();
		final String accountType = "10";// hard code
		final String amountType = "02";  // hard code
		final String amountType_ledger = "01";
		final String currency = "800";     // need to find better method
		final String amountSign = "C";  // hard code
		
		List<InterswitchBalanceEnquiryData> balances;
		
		SavingsAccountTransaction transaction=this.savingsAccountTransactionRepository.findOne(transactionId);
		
		
		try{
			//SavingsAccount savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
			long savingsAccountId=transaction.getSavingsAccount().getId();
			
			SavingsAccount savingsAccount =this.savingsAccountRepository.getOne(savingsAccountId);
			
			if (savingsAccount == null) {
				
				
				return InterswitchBalanceWrapper.getInstance(null,  ResponseCodes.ERROR.getValue() + "", (int)(100000 + Math.random() * 999999)+"");
			}
			
			final BigDecimal amount = savingsAccount.getWithdrawableBalance();
		
			
			InterswitchBalanceEnquiryData actualBalance=InterswitchBalanceEnquiryData.getInstance(accountType, amountType, currency, amount,
					amountSign);
			
			InterswitchBalanceEnquiryData ledgerBalance=InterswitchBalanceEnquiryData.getInstance(accountType, amountType_ledger, currency, amount,
					amountSign);
			
			balances=new ArrayList<InterswitchBalanceEnquiryData>();
			balances.add(ledgerBalance);
			balances.add(actualBalance);
			
			
			
			return InterswitchBalanceWrapper.getInstance(balances,  String.format("%02d", ResponseCodes.APPROVED.getValue()), (int)(100000 + Math.random() * 999999)+"");
			
			
			
			//return transactionMap;
		}
		catch(Exception e)
		{
			return InterswitchBalanceWrapper.getInstance(null,  ResponseCodes.ERROR.getValue() + "", (int)(100000 + Math.random() * 999999)+"");
		}
		
		
		
		
		
		
		}
	

	@Override
	public MinistatementDataWrapper getMinistatement(String json)
	{
		List<HashMap<String,Object>> miniStatement=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> transactionMap;
		
		InterswitchBalanceWrapper balance=retrieveBalance(json);
		
		final JsonElement element = this.fromApiJsonHelper.parse(json);
		JsonObject requestBody=element.getAsJsonObject();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
		
		try {
		
		String accountNumber=requestBody.get("account_debit").getAsString();
		
		SavingsAccount savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);
		
		List<SavingsAccountTransaction> transactions= this.savingsAccountTransactionRepository.findBySavingsAccountId(savingsAccount.getId());
			//	savingsAccount.getTransactions();
		
		
		String transactionType="";
		
		
		int numberOfTransactions=transactions.size()-1;
		
		int i=5;
		SavingsAccountTransaction transaction;
			while(i>0 && numberOfTransactions>=0)
			{
				transaction=transactions.get(numberOfTransactions);
				transactionMap=new HashMap<String,Object>();
				transactionMap.put("seq_nr",transaction.getId()+"" );
				transactionMap.put("date_time",dateFormat.format(transaction.getDateOf())+"" );
		
				
				transactionType=SavingsEnumerations.transactionType(transaction.getTypeOf()).getValue().substring(0,10);
				
				transactionMap.put("tran_type", transactionType);
				transactionMap.put("curr_code",800 );
				transactionMap.put("tran_amount", transaction.getAmount() );
				
				miniStatement.add(transactionMap);
				i--;
				numberOfTransactions--;
				
			}
			
			
		return	MinistatementDataWrapper.getInstance(miniStatement,balance.getAdditional_amount(), String.format("%02d", ResponseCodes.APPROVED.getValue()), (int)(100000 + Math.random() * 999999)+"");
	
		
		}
		catch(Exception e)
		{
			return MinistatementDataWrapper.getInstance(null,null,  ResponseCodes.ERROR.getValue() + "", (int)(100000 + Math.random() * 999999)+"");
		}
		
	}
	
	
	
	
}
