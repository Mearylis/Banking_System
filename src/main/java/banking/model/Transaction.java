package banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, INVESTMENT, DIVIDEND, FEE
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }

    private final String transactionId;
    private final String accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime timestamp;
    private TransactionStatus status;
    private BigDecimal balanceAfter;

    public Transaction(String accountNumber, TransactionType type, BigDecimal amount, String description) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.balanceAfter = BigDecimal.ZERO;
    }

    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
    }

    public void markFailed() {
        this.status = TransactionStatus.FAILED;
    }

    public void markCancelled() {
        this.status = TransactionStatus.CANCELLED;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }

    // Setter for balanceAfter
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    @Override
    public String toString() {
        return String.format("Transaction[%s: %s %s $%.2f - %s]",
                transactionId, type, accountNumber, amount, description);
    }
}