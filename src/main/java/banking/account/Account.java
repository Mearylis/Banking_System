package banking.account;

import java.math.BigDecimal;

public interface Account {
    String getAccountNumber();
    String getAccountType();
    BigDecimal getBalance();
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    String getDescription();
    void close();
    boolean isClosed();

    // Новый метод для получения базового аккаунта
    default Account getBaseAccount() {
        return this;
    }
}