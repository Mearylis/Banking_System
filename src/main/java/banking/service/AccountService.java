package banking.service;

import banking.account.Account;
import banking.account.InvestmentAccount;

import java.math.BigDecimal;

public class AccountService {

    public void processDeposit(Account account, BigDecimal amount) {
        try {
            BigDecimal oldBalance = account.getBalance();
            account.deposit(amount);
            System.out.println("💰 Deposited $" + amount + " to account " + account.getAccountNumber());
            System.out.println("📊 Balance: $" + oldBalance + " → $" + account.getBalance());
        } catch (Exception e) {
            System.err.println("❌ Deposit failed: " + e.getMessage());
            throw e;
        }
    }

    public void processWithdrawal(Account account, BigDecimal amount) {
        try {
            BigDecimal oldBalance = account.getBalance();
            account.withdraw(amount);
            System.out.println("💸 Withdrew $" + amount + " from account " + account.getAccountNumber());
            System.out.println("📊 Balance: $" + oldBalance + " → $" + account.getBalance());
        } catch (Exception e) {
            System.err.println("❌ Withdrawal failed: " + e.getMessage());
            throw e;
        }
    }

    public void applyInvestmentReturns(InvestmentAccount account, BigDecimal returns) {
        try {
            BigDecimal oldBalance = account.getBalance();
            BigDecimal oldReturns = account.getInvestmentReturns();

            account.applyInvestmentReturns(returns);

            System.out.println("📈 Applied investment returns: $" + returns + " to account " + account.getAccountNumber());
            System.out.println("📊 Balance: $" + oldBalance + " → $" + account.getBalance());
            System.out.println("🎯 Total returns: $" + oldReturns + " → $" + account.getInvestmentReturns());

        } catch (Exception e) {
            System.err.println("❌ Investment returns application failed: " + e.getMessage());
            throw e;
        }
    }

    public void displayAccountInfo(Account account) {
        System.out.println("\n=== Account Information ===");
        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Description: " + account.getDescription());
        System.out.println("Balance: $" + account.getBalance());
        System.out.println("Status: " + (account.isClosed() ? "🔴 Closed" : "🟢 Active"));

        // Дополнительная информация для InvestmentAccount
        if (account instanceof InvestmentAccount) {
            InvestmentAccount investmentAccount = (InvestmentAccount) account;
            System.out.println("Investment Returns: $" + investmentAccount.getInvestmentReturns());
        }

        System.out.println("===========================\n");
    }

    public void displayAccountSummary(Account account) {
        String status = account.isClosed() ? "🔴 CLOSED" : "🟢 ACTIVE";
        System.out.printf("📋 %s: %s | Balance: $%.2f | %s%n",
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                status);
    }

    public boolean canWithdraw(Account account, BigDecimal amount) {
        if (account.isClosed()) {
            return false;
        }

        try {
            // Проверяем, достаточно ли средств
            return account.getBalance().compareTo(amount) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public BigDecimal calculatePotentialBalance(Account account, BigDecimal... operations) {
        BigDecimal potentialBalance = account.getBalance();
        for (BigDecimal op : operations) {
            potentialBalance = potentialBalance.add(op);
        }
        return potentialBalance;
    }
}