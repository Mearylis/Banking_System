
package banking.account.decorators;

import banking.account.Account;
import java.math.BigDecimal;

public class PriorityBankingDecorator extends AccountDecorator {
    private final BigDecimal feeWaiverThreshold;
    private final BigDecimal preferentialInterestRate;
    private int freeTransactions;

    public PriorityBankingDecorator(Account decoratedAccount) {
        super(decoratedAccount);
        this.feeWaiverThreshold = BigDecimal.valueOf(25000);
        this.preferentialInterestRate = BigDecimal.valueOf(0.025); // 2.5%
        this.freeTransactions = 50; // 50 free transactions per month
    }

    @Override
    public String getDescription() {
        return decoratedAccount.getDescription() + " + Priority Banking Benefits";
    }

    public boolean isFeeWaived() {
        return decoratedAccount.getBalance().compareTo(feeWaiverThreshold) >= 0;
    }

    public BigDecimal calculatePreferentialInterest() {
        return decoratedAccount.getBalance().multiply(preferentialInterestRate);
    }

    public boolean useFreeTransaction() {
        if (freeTransactions > 0) {
            freeTransactions--;
            return true;
        }
        return false;
    }

    public int getRemainingFreeTransactions() {
        return freeTransactions;
    }

    public void resetMonthlyBenefits() {
        this.freeTransactions = 50;
        System.out.println("Monthly priority banking benefits reset");
    }

    public BigDecimal getFeeWaiverThreshold() { return feeWaiverThreshold; }
    public BigDecimal getPreferentialInterestRate() { return preferentialInterestRate; }
}