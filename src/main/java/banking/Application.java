package banking;
import banking.facade.BankingFacade;
import banking.model.Notification;
import banking.account.Account;
import banking.account.decorators.TaxOptimizerDecorator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public class Application {
    private final BankingFacade bankingFacade;

    public Application() {
        this.bankingFacade = new BankingFacade();
    }

    public void runComprehensiveDemo() {
        System.out.println("=== COMPREHENSIVE BANKING SYSTEM DEMONSTRATION ===");
        System.out.println("Starting at: " + java.time.LocalDateTime.now() + "\n");

        runDemo();
        demonstrateSpecificScenarios();

        System.out.println("Comprehensive demonstration completed at: " +
                java.time.LocalDateTime.now());
        System.out.println("=== ALL DEMONSTRATIONS FINISHED SUCCESSFULLY ===");
    }

    public void runDemo() {
        System.out.println("=== DIGITAL BANKING & INVESTMENT SYSTEM ===\n");

        demonstrateAccountCreation();
        demonstrateBankingOperations();
        demonstrateTransfers();
        demonstrateInvestmentOperations();
        demonstrateNotificationSystem();
        demonstrateReporting();
        demonstrateAccountManagement();
        demonstrateFinalStatus();

        System.out.println("=== SYSTEM DEMONSTRATION COMPLETED ===");
    }

    private void demonstrateAccountCreation() {
        System.out.println(" DEMO 1: ACCOUNT CREATION WITH BENEFITS");
        System.out.println("Creating various account types with different benefit packages...");
        var premiumSavings = bankingFacade.openAccountWithBenefits(
                "CUST001", "savings", BigDecimal.valueOf(5000));
        System.out.println("Created: " + premiumSavings.getDescription());

        var safetyInvestment = bankingFacade.investWithSafetyMode(
                "CUST001", BigDecimal.valueOf(15000));
        System.out.println("Created: " + safetyInvestment.getDescription());

        var checkingAccount = bankingFacade.openAccountWithBenefits(
                "CUST001", "checking", BigDecimal.valueOf(3000));
        System.out.println("Created: " + checkingAccount.getDescription());

        var basicSavings = bankingFacade.openAccountWithBenefits(
                "CUST002", "savings", BigDecimal.valueOf(500));
        System.out.println("Created: " + basicSavings.getDescription());

        System.out.println("Account creation demo completed\n");
    }

    private void demonstrateBankingOperations() {
        System.out.println("DEMO 2: BANKING OPERATIONS");
        System.out.println("Performing deposit and withdrawal operations...");

        String savingsAccount = findFirstAccountOfType("CUST001", "Savings");
        String checkingAccount = findFirstAccountOfType("CUST001", "Checking");
        String cust2Savings = findFirstAccountOfType("CUST002", "Savings");

        bankingFacade.deposit(savingsAccount,
                BigDecimal.valueOf(2000), "Salary deposit");

        bankingFacade.deposit(checkingAccount,
                BigDecimal.valueOf(1500), "Freelance payment");

        bankingFacade.withdraw(checkingAccount,
                BigDecimal.valueOf(500), "ATM withdrawal");

        bankingFacade.withdraw(checkingAccount,
                BigDecimal.valueOf(300), "Online purchase");

        bankingFacade.deposit(cust2Savings,
                BigDecimal.valueOf(15000), "Bonus payment");

        System.out.println("Banking operations demo completed\n");
    }

    private void demonstrateTransfers() {
        System.out.println("DEMO 3: INTER-ACCOUNT TRANSFERS");
        System.out.println("Transferring funds between different accounts...");

        String savingsAccount = findFirstAccountOfType("CUST001", "Savings");
        String checkingAccount = findFirstAccountOfType("CUST001", "Checking");
        String investmentAccount = findFirstAccountOfType("CUST001", "Investment");

        bankingFacade.transferBetweenAccounts(
                savingsAccount,
                checkingAccount,
                BigDecimal.valueOf(1000),
                "Monthly budget transfer"
        );

        bankingFacade.transferBetweenAccounts(
                checkingAccount,
                investmentAccount,
                BigDecimal.valueOf(2000),
                "Additional investment"
        );

        System.out.println("✅ Transfer operations demo completed\n");
    }

    private void demonstrateInvestmentOperations() {
        System.out.println("DEMO 4: INVESTMENT OPERATIONS");
        System.out.println("Applying investment returns and additional investments...");

        String investmentAccount = findFirstAccountOfType("CUST001", "Investment");

        bankingFacade.applyInvestmentReturns(investmentAccount, BigDecimal.valueOf(1200));
        bankingFacade.applyInvestmentReturns(investmentAccount, BigDecimal.valueOf(800));
        bankingFacade.deposit(investmentAccount,
                BigDecimal.valueOf(3000), "Quarterly investment");

        System.out.println(" Investment operations demo completed\n");
    }

    private void demonstrateNotificationSystem() {
        System.out.println("DEMO 5: NOTIFICATION SYSTEM");
        System.out.println("Displaying system notifications for customers...");

        List<Notification> notifications = bankingFacade.getCustomerNotifications("CUST001");
        System.out.println("Notifications for CUST001:");
        if (notifications.isEmpty()) {
            System.out.println("   No notifications");
        } else {
            notifications.forEach(notif -> {
                String status = notif.isRead() ? "📭" : "📬";
                System.out.println("   " + status + " " + notif.getTitle() + " - " + notif.getMessage());
            });
        }
        List<Notification> notifications2 = bankingFacade.getCustomerNotifications("CUST002");
        System.out.println("\nNotifications for CUST002:");
        if (notifications2.isEmpty()) {
            System.out.println("   No notifications");
        } else {
            notifications2.forEach(notif -> {
                String status = notif.isRead() ? "📭" : "📬";
                System.out.println("   " + status + " " + notif.getTitle() + " - " + notif.getMessage());
            });
        }

        System.out.println("Notification system demo completed\n");
    }

    private void demonstrateReporting() {
        System.out.println("DEMO 6: REPORTING SYSTEM");
        System.out.println("Generating account statements and portfolio reports...");

        String savingsAccount = findFirstAccountOfType("CUST001", "Savings");
        String statement = bankingFacade.generateAccountStatement(
                savingsAccount,
                LocalDate.now().minusMonths(1),
                LocalDate.now()
        );
        System.out.println("Account Statement:");
        System.out.println(statement);

        String portfolio = bankingFacade.generateCustomerPortfolio("CUST001");
        System.out.println("Portfolio Summary:");
        System.out.println(portfolio);

        System.out.println("Reporting system demo completed\n");
    }

    private void demonstrateAccountManagement() {
        System.out.println("DEMO 7: ACCOUNT MANAGEMENT");
        System.out.println("Displaying customer info and closing accounts...");
        System.out.println("Customer Information:");
        bankingFacade.displayCustomerInfo("CUST001");
        bankingFacade.displayCustomerInfo("CUST002");
        String savingsAccount = findFirstAccountOfType("CUST001", "Savings");
        System.out.println("Closing account: " + savingsAccount);
        bankingFacade.closeAccount("CUST001", savingsAccount);
        System.out.println("Account management demo completed\n");
    }

    private void demonstrateFinalStatus() {
        System.out.println("DEMO 8: FINAL SYSTEM STATUS");
        System.out.println("Displaying final state of the system...");
        bankingFacade.displayAllAccounts();
        System.out.println("Final Customer Status:");
        bankingFacade.displayCustomerInfo("CUST001");
        bankingFacade.displayCustomerInfo("CUST002");
        System.out.println("Final status demonstration completed\n");
    }
    public void demonstrateSpecificScenarios() {
        System.out.println("=== SPECIFIC SCENARIOS DEMONSTRATION ===");

        demonstrateOverdraftScenario();
        demonstrateForeignCurrencyScenario();
        demonstratePriorityBankingScenario();
        demonstrateTaxOptimizationScenario();
        demonstrateRewardPointsScenario();

        System.out.println("=== SPECIFIC SCENARIOS COMPLETED ===\n");
    }

    private void demonstrateOverdraftScenario() {
        System.out.println("Overdraft Protection Scenario:");
        System.out.println("Testing overdraft functionality...");

        var checkingAccount = bankingFacade.openAccountWithBenefits(
                "CUST003", "checking", BigDecimal.valueOf(500));
        try {
            System.out.println("   Attempting to withdraw $800 from account with $500 balance...");
            bankingFacade.withdraw(checkingAccount.getAccountNumber(),
                    BigDecimal.valueOf(800), "Overdraft test");
            System.out.println("    Overdraft protection worked successfully");
        } catch (Exception e) {
            System.out.println("   ❌ Overdraft failed: " + e.getMessage());
        }
    }

    private void demonstrateForeignCurrencyScenario() {
        System.out.println("💱 Foreign Currency Scenario:");
        System.out.println("Testing multi-currency account features...");

        var premiumChecking = bankingFacade.openAccountWithBenefits(
                "CUST004", "checking", BigDecimal.valueOf(10000));

        // Проверка наличия декоратора иностранной валюты
        if (premiumChecking.getDescription().contains("Multi-Currency")) {
            System.out.println("   ✅ Multi-currency support enabled");
            System.out.println("   🌍 Account supports multiple currencies for international transactions");
        } else {
            System.out.println("   ℹ️  Multi-currency not available for this account level");
        }
    }

    private void demonstratePriorityBankingScenario() {
        System.out.println("⭐ Priority Banking Scenario:");
        System.out.println("Testing premium banking benefits...");

        var priorityAccount = bankingFacade.openAccountWithBenefits(
                "CUST005", "investment", BigDecimal.valueOf(30000));

        if (priorityAccount.getDescription().contains("Priority Banking")) {
            System.out.println("   ✅ Priority banking benefits activated");
            System.out.println("   ✨ Includes fee waivers, preferential rates, and dedicated support");
        } else {
            System.out.println("   ℹ️  Standard banking benefits - upgrade for premium features");
        }
    }

    private void demonstrateTaxOptimizationScenario() {
        System.out.println("💰 Tax Optimization Scenario:");
        System.out.println("Demonstrating tax savings features...");

        var taxOptimizedAccount = bankingFacade.openAccountWithBenefits(
                "CUST006", "investment", BigDecimal.valueOf(10000));

        if (taxOptimizedAccount.getDescription().contains("Tax Optimization")) {
            System.out.println("   ✅ Tax optimization features enabled");

            // Демонстрация расчета налоговой экономии
            if (taxOptimizedAccount instanceof TaxOptimizerDecorator) {
                var taxDecorator = (TaxOptimizerDecorator) taxOptimizedAccount;
                BigDecimal savings = taxDecorator.calculateTaxSavings(BigDecimal.valueOf(5000));
                System.out.println("   📊 Estimated tax savings on $5000: $" + savings);
                System.out.println("   💵 Total tax savings to date: $" + taxDecorator.getTotalTaxSavings());
            }
        } else {
            System.out.println("   ℹ️  Basic tax treatment applied");
        }
    }

    private void demonstrateRewardPointsScenario() {
        System.out.println("🎁 Reward Points Scenario:");
        System.out.println("Testing loyalty points system...");

        var rewardsAccount = bankingFacade.openAccountWithBenefits(
                "CUST007", "savings", BigDecimal.valueOf(2000));

        if (rewardsAccount.getDescription().contains("Reward Points")) {
            System.out.println("   ✅ Reward points system activated");
            System.out.println("   ⭐ Earn points on deposits and redeem for benefits");

            // Демонстрационные операции для накопления баллов
            bankingFacade.deposit(rewardsAccount.getAccountNumber(),
                    BigDecimal.valueOf(1000), "Rewards qualification deposit");
        }
    }


    private String findFirstAccountOfType(String customerId, String typeKeyword) {
        try {

            return bankingFacade.findAccountNumberByType(customerId, typeKeyword);
        } catch (Exception e) {
            System.out.println("⚠️  Account search issue: " + e.getMessage());
            try {
                return bankingFacade.findFirstAccountForCustomer(customerId);
            } catch (Exception ex) {
                throw new IllegalStateException("No accounts found for customer " + customerId);
            }
        }
    }

    public void runQuickDemo() {
        System.out.println("⚡ QUICK DEMO - Digital Banking System");

        demonstrateAccountCreation();
        demonstrateBankingOperations();
        demonstrateTransfers();
        demonstrateNotificationSystem();

        System.out.println("⚡ Quick demo completed");
    }

    public void demonstrateNotificationsOnly() {
        System.out.println("📧 NOTIFICATIONS DEMONSTRATION");
        bankingFacade.openAccountWithBenefits("NOTIF001", "savings", BigDecimal.valueOf(1000));
        bankingFacade.openAccountWithBenefits("NOTIF001", "investment", BigDecimal.valueOf(5000));
        String savingsAccount = findFirstAccountOfType("NOTIF001", "Savings");
        bankingFacade.deposit(savingsAccount, BigDecimal.valueOf(15000), "Large deposit");
        bankingFacade.withdraw(savingsAccount, BigDecimal.valueOf(50), "Small withdrawal");
        demonstrateNotificationSystem();
    }
}