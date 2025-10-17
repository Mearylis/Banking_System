
package banking.account.decorators;

import banking.account.Account;

import java.math.BigDecimal;

public abstract class AccountDecorator implements Account {
    protected Account decoratedAccount;

    protected AccountDecorator(Account decoratedAccount) {
        this.decoratedAccount = decoratedAccount;
    }

    @Override
    public String getAccountNumber() {
        return decoratedAccount.getAccountNumber();
    }

    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType();
    }

    @Override
    public BigDecimal getBalance() {
        return decoratedAccount.getBalance();
    }

    @Override
    public void deposit(BigDecimal amount) {
        decoratedAccount.deposit(amount);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        decoratedAccount.withdraw(amount);
    }

    @Override
    public void close() {
        decoratedAccount.close();
    }

    @Override
    public boolean isClosed() {
        return decoratedAccount.isClosed();
    }
    @Override
    public Account getBaseAccount() {
        return decoratedAccount.getBaseAccount();
    }
}