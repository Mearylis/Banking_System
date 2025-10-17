
package banking.account;

import java.math.BigDecimal;
import java.util.UUID;

public class InvestmentAccount implements Account {
    private final String accountNumber;
    private BigDecimal balance;
    private boolean closed;
    private BigDecimal investmentReturns;

    public InvestmentAccount() {
        this.accountNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8);
        this.balance = BigDecimal.ZERO;
        this.investmentReturns = BigDecimal.ZERO;
        this.closed = false;
    }

    public InvestmentAccount(String accountNumber, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.investmentReturns = BigDecimal.ZERO;
        this.closed = false;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String getAccountType() {
        return "Investment Account";
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getInvestmentReturns() {
        return investmentReturns;
    }

    public void applyInvestmentReturns(BigDecimal returns) {
        if (closed) {
            throw new IllegalStateException("Cannot apply returns to closed account");
        }
        this.investmentReturns = this.investmentReturns.add(returns);
        this.balance = this.balance.add(returns);
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
        return "Basic Investment Account";
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