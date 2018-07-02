package bankslip.unity;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import bankslip.model.BankSlip;
import junit.framework.Assert;

public class BankSlipTest {

    @Test
    public void shoulHasOnePercentFineIfMostTenDays() throws Exception {
        BigDecimal totalInCents = new BigDecimal(100000);
        BigDecimal fine = getFineFromDuedBankSlip("PENDING", totalInCents, 11);
        BigDecimal expected = new BigDecimal(1000);
        Assert.assertEquals(expected.stripTrailingZeros(), fine.stripTrailingZeros());
    }

    @Test
    public void shoulHasHalfPercentFineIfMostFiveDays() throws Exception {
        BigDecimal totalInCents = new BigDecimal(100000);
        BigDecimal fine = getFineFromDuedBankSlip("PENDING", totalInCents, 6);
        BigDecimal expected = new BigDecimal(500);
        Assert.assertEquals(expected.stripTrailingZeros(), fine.stripTrailingZeros());
    }

    @Test
    public void shouldHasZeroIfPaid() throws Exception {
        BigDecimal totalInCents = new BigDecimal(100000);
        BigDecimal fine = getFineFromDuedBankSlip("PAID", totalInCents, 6);
        BigDecimal expected = BigDecimal.ZERO;
        Assert.assertEquals(expected.stripTrailingZeros(), fine.stripTrailingZeros());
    }

    @Test
    public void shouldHasZeroIfCanceled() throws Exception {
        BigDecimal totalInCents = new BigDecimal(100000);
        BigDecimal fine = getFineFromDuedBankSlip("CANCELED", totalInCents, 11);
        BigDecimal expected = BigDecimal.ZERO;
        Assert.assertEquals(expected.stripTrailingZeros(), fine.stripTrailingZeros());
    }

    private BigDecimal getFineFromDuedBankSlip(String status, BigDecimal totalInCents, int duedDays) {
        BankSlip bankslip = new BankSlip();
        Date now = new Date();
        bankslip.setCustomer("Test customer");
        bankslip.setStatus(status);
        Date dueDate = new Date(now.getYear(), now.getMonth(), now.getDay() - duedDays);
        bankslip.setDueDate(dueDate);
        bankslip.setTotalInCents(totalInCents);
        return bankslip.getFine();
    }
}