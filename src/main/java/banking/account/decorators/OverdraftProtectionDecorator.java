
package banking.account.decorators;

import banking.account.Account;
import java.math.BigDecimal;

public class OverdraftProtectionDecorator extends AccountDecorator {
    private final BigDecimal overdraftLimit;
    private BigDecimal usedOverdraft;

    public OverdraftProtectionDecorator(Account decoratedAccount, BigDecimal overdraftLimit) {
        super(decoratedAccount);
        this.overdraftLimit = overdraftLimit;
        this.usedOverdraft = BigDecimal.ZERO;
    }

    @Override
    public String getDescription() {
        return decoratedAccount.getDescription() + " + Overdraft Protection ($" + overdraftLimit + ")";
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal currentBalance = decoratedAccount.getBalance();

        if (currentBalance.compareTo(amount) >= 0) {
            // Sufficient balance
            decoratedAccount.withdraw(amount);
        } else {
            // Use overdraft
            BigDecimal overdraftNeeded = amount.subtract(currentBalance);
            if (overdraftNeeded.compareTo(overdraftLimit.subtract(usedOverdraft)) <= 0) {
                decoratedAccount.withdraw(currentBalance); // Withdraw all balance
                usedOverdraft = usedOverdraft.add(overdraftNeeded);
                System.out.println("Used overdraft: $" + overdraftNeeded + ". Total used: $" + usedOverdraft);
            } else {
                throw new IllegalArgumentException("Overdraft limit exceeded");
            }
        }
    }

    public void repayOverdraft(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }
        if (amount.compareTo(usedOverdraft) > 0) {
            throw new IllegalArgumentException("Repayment exceeds used overdraft");
        }
        usedOverdraft = usedOverdraft.subtract(amount);
        System.out.println("Overdraft repaid: $" + amount + ". Remaining: $" + usedOverdraft);
    }

    public BigDecimal getAvailableOverdraft() {
        return overdraftLimit.subtract(usedOverdraft);
    }

    public BigDecimal getUsedOverdraft() { return usedOverdraft; }
}