
package banking.service;

import banking.account.Account;
import banking.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportService {
    private final TransactionService transactionService;

    public ReportService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public String generateAccountStatement(Account account, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionService.getTransactionHistory(account.getAccountNumber())
                .stream()
                .filter(t -> !t.getTimestamp().toLocalDate().isBefore(startDate) &&
                        !t.getTimestamp().toLocalDate().isAfter(endDate))
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .toList();

        StringBuilder report = new StringBuilder();
        report.append("ACCOUNT STATEMENT\n");
        report.append("=================\n");
        report.append("Account: ").append(account.getAccountNumber()).append("\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Current Balance: $").append(account.getBalance()).append("\n\n");

        report.append("TRANSACTIONS:\n");
        report.append("-------------\n");

        BigDecimal runningBalance = BigDecimal.ZERO;
        List<Transaction> reversedTransactions = new ArrayList<>(transactions);
        Collections.reverse(reversedTransactions);

        for (Transaction transaction : reversedTransactions) {
            String type = String.format("%-12s", transaction.getType());
            String amount = String.format("%10s", "$" + transaction.getAmount());
            String date = transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String status = String.format("%-10s", transaction.getStatus());
            String description = String.format("%-30s",
                    transaction.getDescription().length() > 30 ?
                            transaction.getDescription().substring(0, 27) + "..." :
                            transaction.getDescription());

            if (transaction.getType() == Transaction.TransactionType.DEPOSIT) {
                runningBalance = runningBalance.add(transaction.getAmount());
            } else {
                runningBalance = runningBalance.subtract(transaction.getAmount());
            }

            report.append(String.format("%s | %s | %s | %s | %s | Balance: $%s\n",
                    date, type, amount, status, description, runningBalance));
        }

        return report.toString();
    }

    public String generatePortfolioSummary(List<Account> accounts) {
        StringBuilder report = new StringBuilder();
        report.append("PORTFOLIO SUMMARY\n");
        report.append("=================\n");
        report.append("Generated: ").append(LocalDate.now()).append("\n\n");

        BigDecimal totalBalance = BigDecimal.ZERO;
        Map<String, BigDecimal> balancesByType = new HashMap<>();
        Map<String, Integer> countByType = new HashMap<>();

        for (Account account : accounts) {
            if (!account.isClosed()) {
                BigDecimal balance = account.getBalance();
                totalBalance = totalBalance.add(balance);

                String accountType = account.getAccountType();
                balancesByType.merge(accountType, balance, BigDecimal::add);
                countByType.merge(accountType, 1, Integer::sum);
            }
        }

        report.append("TOTAL BALANCE: $").append(totalBalance).append("\n\n");
        report.append("ACCOUNT BREAKDOWN:\n");
        report.append("-----------------\n");

        for (Map.Entry<String, BigDecimal> entry : balancesByType.entrySet()) {
            String type = entry.getKey();
            BigDecimal balance = entry.getValue();
            int count = countByType.get(type);

            double percentage = 0.0;
            if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
                percentage = balance.divide(totalBalance, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            }

            report.append(String.format("%-20s: $%-10s (%2d accounts, %.1f%%)\n",
                    type, balance, count, percentage));
        }

        return report.toString();
    }
}