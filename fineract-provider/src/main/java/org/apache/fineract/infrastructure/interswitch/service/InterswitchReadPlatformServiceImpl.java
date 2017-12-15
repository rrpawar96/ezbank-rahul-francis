package org.apache.fineract.infrastructure.interswitch.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.interswitch.data.InterswitchBalanceEnquiryData;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountRepository;
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

	@Autowired
	public InterswitchReadPlatformServiceImpl(final PlatformSecurityContext context,
			final RoutingDataSource dataSource,final FromJsonHelper fromApiJsonHelper,
			final SavingsAccountRepository savingsAccountRepository) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fromApiJsonHelper=fromApiJsonHelper;
		this.savingsAccountRepository=savingsAccountRepository;
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
	public InterswitchBalanceEnquiryData retrieveBalance(String json) {
		this.context.authenticatedUser();
		final String accountType = "10";// hard code
		final String amountType = "02";  // hard code
		final String currency = "UGX";     // need to find better method
		final String amountSign = "C";  // hard code
		
		final JsonElement element = this.fromApiJsonHelper.parse(json);
		JsonObject requestBody=element.getAsJsonObject();
		
		String accountNumber=requestBody.get("account_debit").getAsString();
		
		SavingsAccount savingsAccount = this.savingsAccountRepository.findNonClosedAccountByAccountNumber(accountNumber);

		final BigDecimal amount = savingsAccount.getWithdrawableBalance();
		
		return InterswitchBalanceEnquiryData.getInstance(accountType, amountType, currency, amount,
				amountSign);
	}
	
	
	
	
}
