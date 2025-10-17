package banking.service;

import banking.model.Transaction;
import banking.account.Account;

import java.math.BigDecimal;
import java.util.*;

public class TransactionService {
    private final Map<String, List<Transaction>> accountTransactions;

    public TransactionService() {
        this.accountTransactions = new HashMap<>();
    }

    public Transaction recordDeposit(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(
                account.getAccountNumber(),
                Transaction.TransactionType.DEPOSIT,
                amount,
                description
        );

        try {
            BigDecimal balanceBefore = account.getBalance();
            account.deposit(amount);
            transaction.markCompleted();
            transaction.setBalanceAfter(account.getBalance());

            addTransaction(account.getAccountNumber(), transaction);

            System.out.println("✅ Deposit recorded: " + description + " - $" + amount +
                    " | Balance: $" + balanceBefore + " → $" + account.getBalance());

        } catch (Exception e) {
            transaction.markFailed();
            System.err.println("❌ Deposit failed: " + e.getMessage());
            throw e;
        }

        return transaction;
    }

    public Transaction recordWithdrawal(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(
                account.getAccountNumber(),
                Transaction.TransactionType.WITHDRAWAL,
                amount,
                description
        );

        try {
            BigDecimal balanceBefore = account.getBalance();
            account.withdraw(amount);
            transaction.markCompleted();
            transaction.setBalanceAfter(account.getBalance());

            addTransaction(account.getAccountNumber(), transaction);

            System.out.println("✅ Withdrawal recorded: " + description + " - $" + amount +
                    " | Balance: $" + balanceBefore + " → $" + account.getBalance());

        } catch (Exception e) {
            transaction.markFailed();
            System.err.println("❌ Withdrawal failed: " + e.getMessage());
            throw e;
        }

        return transaction;
    }

    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount, String description) {
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Записываем транзакцию списания
        Transaction withdrawal = recordWithdrawal(fromAccount, amount,
                "Transfer to " + toAccount.getAccountNumber() + ": " + description);

        // Записываем транзакцию зачисления
        Transaction deposit = recordDeposit(toAccount, amount,
                "Transfer from " + fromAccount.getAccountNumber() + ": " + description);

        System.out.println("✅ Transfer completed: $" + amount + " from " +
                fromAccount.getAccountNumber() + " to " + toAccount.getAccountNumber());
    }

    public Transaction recordInvestmentReturn(Account account, BigDecimal returns, String description) {
        Transaction transaction = new Transaction(
                account.getAccountNumber(),
                Transaction.TransactionType.INVESTMENT,
                returns,
                description
        );

        try {
            BigDecimal balanceBefore = account.getBalance();
            account.deposit(returns);
            transaction.markCompleted();
            transaction.setBalanceAfter(account.getBalance());

            addTransaction(account.getAccountNumber(), transaction);

            System.out.println("✅ Investment return recorded: " + description + " - $" + returns +
                    " | Balance: $" + balanceBefore + " → $" + account.getBalance());

        } catch (Exception e) {
            transaction.markFailed();
            System.err.println("❌ Investment return failed: " + e.getMessage());
            throw e;
        }

        return transaction;
    }

    public Transaction recordFee(Account account, BigDecimal fee, String description) {
        Transaction transaction = new Transaction(
                account.getAccountNumber(),
                Transaction.TransactionType.FEE,
                fee,
                description
        );

        try {
            BigDecimal balanceBefore = account.getBalance();
            account.withdraw(fee);
            transaction.markCompleted();
            transaction.setBalanceAfter(account.getBalance());

            addTransaction(account.getAccountNumber(), transaction);

            System.out.println("💸 Fee recorded: " + description + " - $" + fee +
                    " | Balance: $" + balanceBefore + " → $" + account.getBalance());

        } catch (Exception e) {
            transaction.markFailed();
            System.err.println("❌ Fee charge failed: " + e.getMessage());
            throw e;
        }

        return transaction;
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return accountTransactions.getOrDefault(accountNumber, new ArrayList<>());
    }

    public BigDecimal getAccountBalance(String accountNumber) {
        List<Transaction> transactions = getTransactionHistory(accountNumber);
        return transactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .map(t -> {
                    switch (t.getType()) {
                        case DEPOSIT:
                        case INVESTMENT:
                        case DIVIDEND:
                            return t.getAmount();
                        case WITHDRAWAL:
                        case FEE:
                            return t.getAmount().negate();
                        default:
                            return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, Object> getTransactionStatistics(String accountNumber) {
        List<Transaction> transactions = getTransactionHistory(accountNumber);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransactions", transactions.size());

        BigDecimal totalDeposits = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEPOSIT && t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalDeposits", totalDeposits);

        BigDecimal totalWithdrawals = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.WITHDRAWAL && t.getStatus() == Transaction.TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalWithdrawals", totalWithdrawals);

        // Последняя транзакция
        transactions.stream()
                .max(Comparator.comparing(Transaction::getTimestamp))
                .ifPresent(lastTransaction -> stats.put("lastTransaction", lastTransaction));

        return stats;
    }

    public void cancelTransaction(String transactionId) {
        // Находим транзакцию во всех аккаунтах
        for (List<Transaction> transactions : accountTransactions.values()) {
            for (Transaction transaction : transactions) {
                if (transaction.getTransactionId().equals(transactionId)) {
                    transaction.markCancelled();
                    System.out.println("❌ Transaction cancelled: " + transactionId);
                    return;
                }
            }
        }
        System.err.println("❌ Transaction not found: " + transactionId);
    }

    private void addTransaction(String accountNumber, Transaction transaction) {
        accountTransactions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
    }

    // Метод для очистки истории (для тестирования)
    public void clearTransactionHistory(String accountNumber) {
        accountTransactions.remove(accountNumber);
        System.out.println("🧹 Transaction history cleared for account: " + accountNumber);
    }

    // Метод для получения всех транзакций (для администрирования)
    public Map<String, List<Transaction>> getAllTransactions() {
        return new HashMap<>(accountTransactions);
    }
}