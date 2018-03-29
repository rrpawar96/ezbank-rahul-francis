/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.portfolio.savings.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.DepositAccountType;
import org.apache.fineract.portfolio.savings.data.RetailAccountEntryTypeData;
import org.apache.fineract.portfolio.savings.data.RetailAccountKeyValuePairData;
import org.apache.fineract.portfolio.savings.data.RetailSavingsAccountTransactionData;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionEnumData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class RetailAccountReadPlatformServiceImpl implements RetailAccountReadPlatformService 
{
	
	private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final RetailAccountTransactionsMapper transactionsMapper;
    private final RetailEntryTypeMapper retailEntryTypeMapper;
    private final RetailEntryMapper retailEntryMapper;
    
    
    @Autowired
    public RetailAccountReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource
    		)
    {
    	  this.context = context;
          this.jdbcTemplate = new JdbcTemplate(dataSource);
          this.transactionsMapper=new RetailAccountTransactionsMapper();
          this.retailEntryTypeMapper=new RetailEntryTypeMapper();
          this.retailEntryMapper=new RetailEntryMapper();
    }
    
    
    
    public static final class RetailEntryTypeMapper implements RowMapper<RetailAccountEntryTypeData>
    {
    	private final String schemaSql;
    	
    	public RetailEntryTypeMapper()
    	{
    		final StringBuilder sqlBuilder = new StringBuilder(400);
    		sqlBuilder.append("rt.id as id, rt.name as entryKey, rt.data_type as dataType, ");
    		sqlBuilder.append("rt.retail_account_id as retailAccountId, rt.is_constant as isConstant, ");
    		sqlBuilder.append("rt.constant_value as constantValue ");
    		sqlBuilder.append("from  retail_account_entry_types  rt ");
    	
    		
    		this.schemaSql=sqlBuilder.toString();
    	}
    	
    	 public String schema() {
             return this.schemaSql;
         }

		@Override
		public RetailAccountEntryTypeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException
		{
			
			final long id=rs.getLong("id");
			
			final String dataType=rs.getString("dataType");
			
			final String entryKey=rs.getString("entryKey");
			
			final long retailAccountId=rs.getLong("retailAccountId");
			
			final boolean isConstant=rs.getBoolean("isConstant");
			
			final String constantValue=rs.getString("constantValue"); 
			
			return RetailAccountEntryTypeData.getInstance(id, dataType, entryKey,
					 retailAccountId, isConstant, constantValue);
		}

    }
    
    @Override
    public Collection<RetailAccountEntryTypeData> findEntriesByRetailAccountId(long accountId)
    {
    	
    	final String sql="select "+this.retailEntryTypeMapper.schema()+"  where rt.retail_account_id=? ";
    	
    	return this.jdbcTemplate.query(sql,this.retailEntryTypeMapper,new Object[] {accountId	} );
    }
    
    
    public static final class RetailEntryMapper implements RowMapper<RetailAccountKeyValuePairData>
    {
    	private final String schemaSql;
    	
    	public RetailEntryMapper()
    	{
    		final StringBuilder sqlBuilder = new StringBuilder(400);
    		sqlBuilder.append("rt.id as id, rd.retail_account_transaction_id as transactionId, ");
    		sqlBuilder.append("rt.data_type as dataType, rt.name as entryKey, rd.retail_account_entry_value as entryValue, ");
    		sqlBuilder.append("rt.retail_account_id as retailAccountId, rt.is_constant as isConstant, ");
    		sqlBuilder.append("rt.constant_value as constantValue ");
    		sqlBuilder.append("from retail_account_entry_types rt ");
    		sqlBuilder.append("left join retail_account_entry_data rd on rd.retail_account_entry_type_id=rt.id");
    		
    		this.schemaSql=sqlBuilder.toString();
    	}
    	
    	 public String schema() {
             return this.schemaSql;
         }

		@Override
		public RetailAccountKeyValuePairData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException
		{
			
			final long id=rs.getLong("id");
			
			final long transactionId=rs.getLong("transactionId");
			
			final String dataType=rs.getString("dataType");
			
			final String entryKey=rs.getString("entryKey");
			
			final String entryValue=rs.getString("entryValue");
			
			final long retailAccountId=rs.getLong("retailAccountId");
			
			final boolean isConstant=rs.getBoolean("isConstant");
			
			final String constantValue=rs.getString("constantValue"); 
			
			return RetailAccountKeyValuePairData.getInstance(id, transactionId, dataType, entryKey,
					entryValue, retailAccountId, isConstant, constantValue);
		}

    }
    
    @Override
    public Collection<RetailAccountKeyValuePairData> getEntriesBySavingsId(long savingsId)
    {
    	final String sql="select "+this.retailEntryMapper.schema()+"  where rt.retail_account_id=? ";
    	
    	return this.jdbcTemplate.query(sql,this.retailEntryMapper,new Object[] {savingsId} );
    }
    // and rd.retail_account_transaction_id
    
    @Override
    public Collection<RetailAccountKeyValuePairData> getEntriesBySavingsIdAndTransaction(long savingsId,long transactionId)
    {
    	final String sql="select "+this.retailEntryMapper.schema()+"  where rt.retail_account_id=? and rd.retail_account_transaction_id=?";
    			
    	
    	return this.jdbcTemplate.query(sql,this.retailEntryMapper,new Object[] {savingsId,transactionId} );
    }
    
    
    public Collection<RetailAccountKeyValuePairData> getEntriesBySavingsIdAndTransactionBetween(long savingsId,long transactionId,long lowerTPM,long upperTPM)
    {
    	final String sql="select "+this.retailEntryMapper.schema()+"  where rt.retail_account_id=? and rd.retail_account_transaction_id=?"
    			+ " and rd.retail_account_entry_value between '"+lowerTPM+"' and '"+upperTPM+"'";
    	
    	return this.jdbcTemplate.query(sql,this.retailEntryMapper,new Object[] {savingsId,transactionId} );
    }
    
    private static final class RetailAccountTransactionsMapper implements RowMapper<RetailSavingsAccountTransactionData> {

        private final String schemaSql;

        public RetailAccountTransactionsMapper() {

            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("tr.id as transactionId, tr.transaction_type_enum as transactionType, ");
            sqlBuilder.append("tr.transaction_date as transactionDate, tr.amount as transactionAmount,");
            sqlBuilder.append("tr.created_date as submittedOnDate,");
            sqlBuilder.append("tr.running_balance_derived as runningBalance, tr.is_reversed as reversed,tr.is_loan_disbursement as isLoanDisbursement,");
            sqlBuilder.append("fromtran.id as fromTransferId, fromtran.is_reversed as fromTransferReversed,");
            sqlBuilder.append("fromtran.transaction_date as fromTransferDate, fromtran.amount as fromTransferAmount,");
            sqlBuilder.append("fromtran.description as fromTransferDescription,");
            sqlBuilder.append("totran.id as toTransferId, totran.is_reversed as toTransferReversed,");
            sqlBuilder.append("totran.transaction_date as toTransferDate, totran.amount as toTransferAmount,");
            sqlBuilder.append("totran.description as toTransferDescription,");
            sqlBuilder.append("sa.id as savingsId, sa.account_no as accountNo,sa.deposit_type_enum as savingsAccountType,");
            sqlBuilder.append("pd.payment_type_id as paymentType,pd.account_number as accountNumber,pd.check_number as checkNumber, ");
            sqlBuilder.append("pd.receipt_number as receiptNumber, pd.bank_number as bankNumber,pd.routing_code as routingCode, ");
            sqlBuilder.append("pd.voucher_number as voucherNumber, pd.payment_description as paymentDescription, ");

            sqlBuilder
                    .append("sa.currency_code as currencyCode, sa.currency_digits as currencyDigits, sa.currency_multiplesof as inMultiplesOf, ");
            sqlBuilder.append("curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, ");
            sqlBuilder.append("curr.display_symbol as currencyDisplaySymbol, ");
            sqlBuilder.append("pt.value as paymentTypeName, ");
            sqlBuilder.append("tr.is_manual as postInterestAsOn ");
            sqlBuilder.append("from m_savings_account sa ");
            sqlBuilder.append("join m_savings_account_transaction tr on tr.savings_account_id = sa.id ");
            sqlBuilder.append("join m_currency curr on curr.code = sa.currency_code ");
            sqlBuilder.append("left join m_account_transfer_transaction fromtran on fromtran.from_savings_transaction_id = tr.id ");
            sqlBuilder.append("left join m_account_transfer_transaction totran on totran.to_savings_transaction_id = tr.id ");
            sqlBuilder.append("left join m_payment_detail pd on tr.payment_detail_id = pd.id ");
            sqlBuilder.append("left join m_payment_type pt on pd.payment_type_id = pt.id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public RetailSavingsAccountTransactionData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("transactionId");
            final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
            SavingsAccountTransactionEnumData transactionType = SavingsEnumerations.transactionType(transactionTypeInt);
            final boolean isLoanDisbursement=rs.getBoolean("isLoanDisbursement");
            		
			if (isLoanDisbursement) {
				transactionType = SavingsEnumerations.transactionType(22);
			}
            
            final LocalDate date = JdbcSupport.getLocalDate(rs, "transactionDate");
            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final BigDecimal amount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "transactionAmount");
            final BigDecimal outstandingChargeAmount = null;
            final BigDecimal runningBalance = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "runningBalance");
            final boolean reversed = rs.getBoolean("reversed");

            final Long savingsId = rs.getLong("savingsId");
            final String accountNo = rs.getString("accountNo");
            final boolean postInterestAsOn = rs.getBoolean("postInterestAsOn");

            PaymentDetailData paymentDetailData = null;
            if (transactionType.isDepositOrWithdrawal()) {
                final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentType");
                if (paymentTypeId != null) {
                    final String typeName = rs.getString("paymentTypeName");
                    final PaymentTypeData paymentType = PaymentTypeData.instance(paymentTypeId, typeName);
                    final String accountNumber = rs.getString("accountNumber");
                    final String checkNumber = rs.getString("checkNumber");
                    final String routingCode = rs.getString("routingCode");
                    final String receiptNumber = rs.getString("receiptNumber");
                    final String bankNumber = rs.getString("bankNumber");
                    final String voucherNumber = rs.getString("voucherNumber");
                    final String paymentDescription = rs.getString("paymentDescription");



                    paymentDetailData = new PaymentDetailData(id, paymentType, accountNumber, checkNumber, routingCode, receiptNumber,
                            bankNumber,voucherNumber,paymentDescription);
                }
            }

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");
            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDigits, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            AccountTransferData transfer = null;
            final Long fromTransferId = JdbcSupport.getLong(rs, "fromTransferId");
            final Long toTransferId = JdbcSupport.getLong(rs, "toTransferId");
            if (fromTransferId != null) {
                final LocalDate fromTransferDate = JdbcSupport.getLocalDate(rs, "fromTransferDate");
                final BigDecimal fromTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fromTransferAmount");
                final boolean fromTransferReversed = rs.getBoolean("fromTransferReversed");
                final String fromTransferDescription = rs.getString("fromTransferDescription");

                transfer = AccountTransferData.transferBasicDetails(fromTransferId, currency, fromTransferAmount, fromTransferDate,
                        fromTransferDescription, fromTransferReversed);
            } else if (toTransferId != null) {
                final LocalDate toTransferDate = JdbcSupport.getLocalDate(rs, "toTransferDate");
                final BigDecimal toTransferAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "toTransferAmount");
                final boolean toTransferReversed = rs.getBoolean("toTransferReversed");
                final String toTransferDescription = rs.getString("toTransferDescription");

                

                transfer = AccountTransferData.transferBasicDetails(toTransferId, currency, toTransferAmount, toTransferDate,
                        toTransferDescription, toTransferReversed);
            }

            return RetailSavingsAccountTransactionData.create(id, transactionType, paymentDetailData, savingsId, accountNo, date, currency,
                    amount, outstandingChargeAmount, runningBalance, reversed, transfer, submittedOnDate, postInterestAsOn);
        }
    }
    
    @Override
    public Collection<RetailSavingsAccountTransactionData> retrieveRetailTransaction(final Long retailAccountId,final Long transactionId,
    		DepositAccountType depositAccountType) {

        final String sql = "select " + this.transactionsMapper.schema()
                + " where sa.id = ? and sa.deposit_type_enum = ? and tr.id=? order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";

        return this.jdbcTemplate.query(sql, this.transactionsMapper, new Object[] { retailAccountId, depositAccountType.getValue(),transactionId });
        
    }
    
    
    
    @Override
    public Collection<RetailSavingsAccountTransactionData> retrieveRetailAllTransactions(final Long retailAccountId, DepositAccountType depositAccountType) {

        final String sql = "select " + this.transactionsMapper.schema()
                + " where sa.id = ? and sa.deposit_type_enum = ? order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";

        List<RetailSavingsAccountTransactionData> retailTransactions= (List)this.jdbcTemplate.query(sql, this.transactionsMapper, new Object[] { retailAccountId, depositAccountType.getValue() });
        
        List<RetailSavingsAccountTransactionData> enhancedRetailTransactions=new ArrayList<RetailSavingsAccountTransactionData>();
        
        for(RetailSavingsAccountTransactionData retailTransaction:retailTransactions)
        {
        	enhancedRetailTransactions.add(RetailSavingsAccountTransactionData.retailTemplate(retailTransaction, getEntriesBySavingsIdAndTransaction(retailAccountId,retailTransaction.getId())));
        }
        	
        return enhancedRetailTransactions;
        
    }
    
    @Override
    public Collection<RetailSavingsAccountTransactionData> retrieveRetailTransactions(final Long retailAccountId, DepositAccountType depositAccountType,
    		String startDate,String endDate,Long lowerTPM,Long upperTPM) {

        final String sql = "select " + this.transactionsMapper.schema()
                + " where sa.id = ? and sa.deposit_type_enum = ? and tr.transaction_date between '"+startDate+"' and '"+endDate+"' "
                + "order by tr.transaction_date DESC, tr.created_date DESC, tr.id DESC";

        List<RetailSavingsAccountTransactionData> retailTransactions= (List)this.jdbcTemplate.query(sql, this.transactionsMapper, new Object[] { retailAccountId, depositAccountType.getValue() });
        
        List<RetailSavingsAccountTransactionData> enhancedRetailTransactions=new ArrayList<RetailSavingsAccountTransactionData>();
        
        Collection<RetailAccountKeyValuePairData> retailData;
        
        	if(lowerTPM==null||upperTPM==null)
        	{
        		for(RetailSavingsAccountTransactionData retailTransaction:retailTransactions)
    	        {
    	        	enhancedRetailTransactions.add(RetailSavingsAccountTransactionData.retailTemplate(retailTransaction, getEntriesBySavingsIdAndTransaction(retailAccountId,retailTransaction.getId())));
    	        }
        	}
        	else
        	{
        		  for(RetailSavingsAccountTransactionData retailTransaction:retailTransactions)
        	        {
        			  retailData=getEntriesBySavingsIdAndTransactionBetween(retailAccountId,retailTransaction.getId(),lowerTPM,upperTPM);
        			  
        			  if(retailData!=null)
        			  {	
        				  if(!retailData.isEmpty())
        		enhancedRetailTransactions.add(RetailSavingsAccountTransactionData.retailTemplate(retailTransaction, retailData));  
        			  }
        	        	
        	        }
        	}
      
        	
        return enhancedRetailTransactions;
        
    }

}
