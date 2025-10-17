
package banking.account;

import java.math.BigDecimal;
import java.util.UUID;

public class SavingsAccount implements Account {
    private final String accountNumber;
    private BigDecimal balance;
    private boolean closed;

    public SavingsAccount() {
        this.accountNumber = "SAV-" + UUID.randomUUID().toString().substring(0, 8);
        this.balance = BigDecimal.ZERO;
        this.closed = false;
    }

    public SavingsAccount(String accountNumber, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.closed = false;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void deposit(BigDecimal amount) {
        if (closed) {
            throw new IllegalStateException("Cannot deposit to closed account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        if (closed) {
            throw new IllegalStateException("Cannot withdraw from closed account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }

    @Override
    public String getDescription() {
        return "Basic Savings Account";
    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
