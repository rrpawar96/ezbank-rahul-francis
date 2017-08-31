package org.apache.fineract.portfolio.savings.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.account.data.AccountTransferData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymenttype.data.PaymentTypeData;
import org.apache.fineract.portfolio.savings.SavingsAccountTransactionType;
import org.apache.fineract.portfolio.savings.service.SavingsEnumerations;
import org.joda.time.LocalDate;

public class RetailSavingsAccountTransactionData 
{
	
	private final Long id;
    private final SavingsAccountTransactionEnumData transactionType;
    private final Long accountId;
    private final String accountNo;
    private final LocalDate date;
    private final CurrencyData currency;
    private final PaymentDetailData paymentDetailData;
    private final BigDecimal amount;
    private final BigDecimal outstandingChargeAmount;
    private final BigDecimal runningBalance;
    private final boolean reversed;
    private final AccountTransferData transfer;
    private final LocalDate submittedOnDate;
    private final boolean interestedPostedAsOn;

    // templates
    final Collection<PaymentTypeData> paymentTypeOptions;
    final Collection<RetailAccountKeyValuePairData> retailAccountEntries;

    public static RetailSavingsAccountTransactionData create(final Long id, final SavingsAccountTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
            final CurrencyData currency, final BigDecimal amount, final BigDecimal outstandingChargeAmount,final BigDecimal runningBalance, final boolean reversed,
            final AccountTransferData transfer, final boolean interestedPostedAsOn) {
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        return new RetailSavingsAccountTransactionData(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, date, currency,
                amount, outstandingChargeAmount,runningBalance, reversed, transfer, paymentTypeOptions,null, interestedPostedAsOn);
    }

    public static RetailSavingsAccountTransactionData create(final Long id, final SavingsAccountTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
            final CurrencyData currency, final BigDecimal amount, final BigDecimal outstandingChargeAmount,
            final BigDecimal runningBalance, final boolean reversed, final AccountTransferData transfer, final LocalDate submittedOnDate,
            final boolean interestedPostedAsOn) {
        final Collection<PaymentTypeData> paymentTypeOptions = null;
        return new RetailSavingsAccountTransactionData(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, date, currency,
                amount, outstandingChargeAmount, runningBalance, reversed, transfer, paymentTypeOptions,null, submittedOnDate,
                interestedPostedAsOn);
    }

    public static RetailSavingsAccountTransactionData template(final Long savingsId, final String savingsAccountNo,
            final LocalDate defaultLocalDate, final CurrencyData currency) {
        final Long id = null;
        final SavingsAccountTransactionEnumData transactionType = null;
        final BigDecimal amount = null;
        final BigDecimal outstandingChargeAmount = null;
        final BigDecimal runningBalance = null;
        final boolean reversed = false;
        final PaymentDetailData paymentDetailData = null;
        final Collection<CodeValueData> paymentTypeOptions = null;
        final boolean interestedPostedAsOn = false;
        return new RetailSavingsAccountTransactionData(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, defaultLocalDate,
                currency, amount, outstandingChargeAmount, runningBalance, reversed, null,null, null, interestedPostedAsOn);
    }

    public static RetailSavingsAccountTransactionData templateOnTop(final RetailSavingsAccountTransactionData RetailSavingsAccountTransactionData,
            final Collection<PaymentTypeData> paymentTypeOptions,final Collection<RetailAccountKeyValuePairData> retailAccountEntries) {
        return new RetailSavingsAccountTransactionData(RetailSavingsAccountTransactionData.id, RetailSavingsAccountTransactionData.transactionType,
                RetailSavingsAccountTransactionData.paymentDetailData, RetailSavingsAccountTransactionData.accountId,
                RetailSavingsAccountTransactionData.accountNo, RetailSavingsAccountTransactionData.date, RetailSavingsAccountTransactionData.currency,
                RetailSavingsAccountTransactionData.amount,RetailSavingsAccountTransactionData.outstandingChargeAmount, RetailSavingsAccountTransactionData.runningBalance, RetailSavingsAccountTransactionData.reversed,
                RetailSavingsAccountTransactionData.transfer, paymentTypeOptions,retailAccountEntries, RetailSavingsAccountTransactionData.interestedPostedAsOn);
    }

    private RetailSavingsAccountTransactionData(final Long id, final SavingsAccountTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
            final CurrencyData currency, final BigDecimal amount, final BigDecimal outstandingChargeAmount,
            final BigDecimal runningBalance, final boolean reversed, final AccountTransferData transfer,
            final Collection<PaymentTypeData> paymentTypeOptions,final Collection<RetailAccountKeyValuePairData> retailAccountEntries, final boolean interestedPostedAsOn) {

        this(id, transactionType, paymentDetailData, savingsId, savingsAccountNo, date, currency, amount, outstandingChargeAmount,
                runningBalance, reversed, transfer, paymentTypeOptions,retailAccountEntries, null, interestedPostedAsOn);
    }

    private RetailSavingsAccountTransactionData(final Long id, final SavingsAccountTransactionEnumData transactionType,
            final PaymentDetailData paymentDetailData, final Long savingsId, final String savingsAccountNo, final LocalDate date,
            final CurrencyData currency, final BigDecimal amount,final BigDecimal outstandingChargeAmount, final BigDecimal runningBalance, final boolean reversed,
            final AccountTransferData transfer, final Collection<PaymentTypeData> paymentTypeOptions,final Collection<RetailAccountKeyValuePairData> retailAccountEntries, final LocalDate submittedOnDate,
            final boolean interestedPostedAsOn) {
        this.id = id;
        this.transactionType = transactionType;
        this.paymentDetailData = paymentDetailData;
        this.accountId = savingsId;
        this.accountNo = savingsAccountNo;
        this.date = date;
        this.currency = currency;
        this.amount = amount;
        this.outstandingChargeAmount= outstandingChargeAmount;
        this.runningBalance = runningBalance;
        this.reversed = reversed;
        this.transfer = transfer;
        this.paymentTypeOptions = paymentTypeOptions;
        this.retailAccountEntries=retailAccountEntries;
        this.submittedOnDate = submittedOnDate;
        this.interestedPostedAsOn = interestedPostedAsOn;
    }

    public static RetailSavingsAccountTransactionData withWithDrawalTransactionDetails(
            final RetailSavingsAccountTransactionData RetailSavingsAccountTransactionData) {

        final LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        final SavingsAccountTransactionEnumData transactionType = SavingsEnumerations
                .transactionType(SavingsAccountTransactionType.WITHDRAWAL.getValue());

        return new RetailSavingsAccountTransactionData(RetailSavingsAccountTransactionData.id, transactionType,
                RetailSavingsAccountTransactionData.paymentDetailData, RetailSavingsAccountTransactionData.accountId,
                RetailSavingsAccountTransactionData.accountNo, currentDate, RetailSavingsAccountTransactionData.currency,
                RetailSavingsAccountTransactionData.amount, RetailSavingsAccountTransactionData.outstandingChargeAmount,
                RetailSavingsAccountTransactionData.runningBalance, RetailSavingsAccountTransactionData.reversed,
                RetailSavingsAccountTransactionData.transfer, RetailSavingsAccountTransactionData.paymentTypeOptions,RetailSavingsAccountTransactionData.retailAccountEntries,
                RetailSavingsAccountTransactionData.interestedPostedAsOn);
    }

}
