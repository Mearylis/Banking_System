
package banking.account;

import java.math.BigDecimal;
import java.util.UUID;

public class CheckingAccount implements Account {
    private final String accountNumber;
    private BigDecimal balance;
    private boolean closed;
    private BigDecimal overdraftLimit;

    public CheckingAccount() {
        this.accountNumber = "CHK-" + UUID.randomUUID().toString().substring(0, 8);
        this.balance = BigDecimal.ZERO;
        this.closed = false;
        this.overdraftLimit = BigDecimal.valueOf(1000); // Default overdraft
    }

    public CheckingAccount(String accountNumber, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.closed = false;
        this.overdraftLimit = BigDecimal.valueOf(1000);
    }

    @Override
    public String getAccountNumber() { return accountNumber; }

    @Override
    public String getAccountType() { return "Checking Account"; }

    @Override
    public BigDecimal getBalance() { return balance; }

    public BigDecimal getOverdraftLimit() { return overdraftLimit; }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void deposit(BigDecimal amount) {
        if (closed) throw new IllegalStateException("Cannot deposit to closed account");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (closed) throw new IllegalStateException("Cannot withdraw from closed account");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        BigDecimal availableBalance = balance.add(overdraftLimit);
        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds including overdraft");
        }
        balance = balance.subtract(amount);
    }

    @Override
    public String getDescription() { return "Basic Checking Account with Overdraft"; }

    @Override
    public void close() { this.closed = true; }

    @Override
    public boolean isClosed() { return closed; }
}