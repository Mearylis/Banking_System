package banking.facade;

import banking.account.*;
import banking.account.decorators.*;
import banking.service.*;
import banking.model.*;
import banking.exception.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class BankingFacade {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    private final Map<String, Account> managedAccounts;
    private final Map<String, Customer> customers;

    public BankingFacade() {
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
        this.notificationService = new NotificationService();
        this.reportService = new ReportService(transactionService);

        this.managedAccounts = new HashMap<>();
        this.customers = new HashMap<>();
    }

    // Основные методы из требований
    public Account openAccountWithBenefits(String customerId, String accountType, BigDecimal initialDeposit) {
        Customer customer = getOrCreateCustomer(customerId);
        Account account = createBasicAccount(accountType, initialDeposit);

        // Apply benefits based on account type and deposit amount
        account = applyBenefitDecorators(account, accountType, initialDeposit);

        managedAccounts.put(account.getAccountNumber(), account);
        customer.addAccount(account.getAccountNumber());

        notificationService.sendAccountOpenedNotification(customerId,
                account.getAccountNumber(), account.getAccountType());

        System.out.println("✅ Successfully opened " + account.getDescription() + " for customer " + customerId);
        accountService.displayAccountInfo(account);

        return account;
    }

    public Account investWithSafetyMode(String customerId, BigDecimal initialInvestment) {
        InvestmentAccount investmentAccount = new InvestmentAccount();
        investmentAccount.deposit(initialInvestment);

        Account safeInvestmentAccount = new TaxOptimizerDecorator(
                new InsuranceDecorator(investmentAccount, BigDecimal.valueOf(50000)),
                BigDecimal.valueOf(0.20)
        );

        managedAccounts.put(safeInvestmentAccount.getAccountNumber(), safeInvestmentAccount);
        Customer customer = getOrCreateCustomer(customerId);
        customer.addAccount(safeInvestmentAccount.getAccountNumber());

        notificationService.sendNotification(customerId,
                "Safety Mode Investment Created",
                "Your safety-mode investment account has been created with $50,000 insurance and 20% tax optimization",
                Notification.NotificationType.SUCCESS
        );

        System.out.println("✅ Successfully opened safety-mode investment account");
        accountService.displayAccountInfo(safeInvestmentAccount);

        return safeInvestmentAccount;
    }

    public void closeAccount(String customerId, String accountNumber) {
        Customer customer = customers.get(customerId);
        if (customer == null || !customer.getAccountNumbers().contains(accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }

        Account account = managedAccounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }

        performCleanupOperations(account);
        account.close();
        managedAccounts.remove(accountNumber);
        customer.removeAccount(accountNumber);

        notificationService.sendNotification(customerId,
                "Account Closed",
                "Account " + accountNumber + " has been successfully closed",
                Notification.NotificationType.INFO
        );

        System.out.println("✅ Successfully closed account: " + accountNumber);
    }

    // Новые расширенные методы
    public void transferBetweenAccounts(String fromAccountNumber, String toAccountNumber,
                                        BigDecimal amount, String description) {
        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        transactionService.transfer(fromAccount, toAccount, amount, description);

        // Notify both account owners
        String fromCustomer = findCustomerByAccount(fromAccountNumber);
        String toCustomer = findCustomerByAccount(toAccountNumber);

        if (fromCustomer != null) {
            notificationService.sendLargeTransactionAlert(fromCustomer, fromAccountNumber, amount, "Transfer Out");
        }
        if (toCustomer != null) {
            notificationService.sendLargeTransactionAlert(toCustomer, toAccountNumber, amount, "Transfer In");
        }
    }

    public void applyInvestmentReturns(String accountNumber, BigDecimal returns) {
        Account account = getAccount(accountNumber);

        // Используем getBaseAccount() для получения оригинального аккаунта
        Account baseAccount = account.getBaseAccount();
        if (baseAccount instanceof InvestmentAccount) {
            accountService.applyInvestmentReturns((InvestmentAccount) baseAccount, returns);

            String customerId = findCustomerByAccount(accountNumber);
            if (customerId != null) {
                notificationService.sendNotification(customerId,
                        "Investment Returns Applied",
                        "Your investment account earned $" + returns + " in returns",
                        Notification.NotificationType.SUCCESS
                );
            }
        } else {
            throw new IllegalArgumentException("Account is not an investment account: " + accountNumber);
        }
    }

    public String generateAccountStatement(String accountNumber, LocalDate startDate, LocalDate endDate) {
        Account account = getAccount(accountNumber);
        return reportService.generateAccountStatement(account, startDate, endDate);
    }

    public String generateCustomerPortfolio(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        List<Account> customerAccounts = customer.getAccountNumbers().stream()
                .map(managedAccounts::get)
                .filter(Objects::nonNull)
                .toList();

        return reportService.generatePortfolioSummary(customerAccounts);
    }

    public List<Notification> getCustomerNotifications(String customerId) {
        return notificationService.getCustomerNotifications(customerId);
    }

    public void deposit(String accountNumber, BigDecimal amount, String description) {
        Account account = getAccount(accountNumber);
        transactionService.recordDeposit(account, amount, description);

        // Check for large deposit notification
        if (amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            String customerId = findCustomerByAccount(accountNumber);
            if (customerId != null) {
                notificationService.sendLargeTransactionAlert(customerId, accountNumber, amount, "Large Deposit");
            }
        }
    }

    public void withdraw(String accountNumber, BigDecimal amount, String description) {
        Account account = getAccount(accountNumber);
        transactionService.recordWithdrawal(account, amount, description);

        // Check for low balance alert
        if (account.getBalance().compareTo(BigDecimal.valueOf(100)) < 0) {
            String customerId = findCustomerByAccount(accountNumber);
            if (customerId != null) {
                notificationService.sendLowBalanceAlert(customerId, accountNumber, account.getBalance());
            }
        }
    }

    // Методы для поиска счетов
    public String findAccountNumberByType(String customerId, String accountType) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        return customer.getAccountNumbers().stream()
                .map(managedAccounts::get)
                .filter(account -> account != null &&
                        account.getAccountType().toLowerCase().contains(accountType.toLowerCase()))
                .findFirst()
                .map(Account::getAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No " + accountType + " account found for customer " + customerId));
    }

    public List<Account> getCustomerAccountsByType(String customerId, String accountType) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            return List.of();
        }

        return customer.getAccountNumbers().stream()
                .map(managedAccounts::get)
                .filter(account -> account != null &&
                        account.getAccountType().toLowerCase().contains(accountType.toLowerCase()))
                .toList();
    }

    public String findFirstAccountForCustomer(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null || customer.getAccountNumbers().isEmpty()) {
            throw new IllegalArgumentException("No accounts found for customer: " + customerId);
        }

        return customer.getAccountNumbers().get(0);
    }

    // Вспомогательные методы
    private Account createBasicAccount(String accountType, BigDecimal initialDeposit) {
        Account account;
        switch (accountType.toLowerCase()) {
            case "savings":
                account = new SavingsAccount();
                break;
            case "investment":
                account = new InvestmentAccount();
                break;
            case "checking":
                account = new CheckingAccount();
                break;
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }

        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            account.deposit(initialDeposit);
        }

        return account;
    }

    private Account applyBenefitDecorators(Account account, String accountType, BigDecimal initialDeposit) {
        // Apply decorators based on account type and initial deposit
        switch (accountType.toLowerCase()) {
            case "savings":
                account = new RewardPointsDecorator(account, BigDecimal.valueOf(2));
                if (initialDeposit.compareTo(BigDecimal.valueOf(1000)) > 0) {
                    account = new InsuranceDecorator(account, BigDecimal.valueOf(10000));
                }
                break;

            case "investment":
                account = new TaxOptimizerDecorator(account, BigDecimal.valueOf(0.15));
                if (initialDeposit.compareTo(BigDecimal.valueOf(5000)) > 0) {
                    account = new InsuranceDecorator(account, BigDecimal.valueOf(25000));
                    account = new PriorityBankingDecorator(account);
                }
                break;

            case "checking":
                account = new OverdraftProtectionDecorator(account, BigDecimal.valueOf(2000));
                if (initialDeposit.compareTo(BigDecimal.valueOf(5000)) > 0) {
                    account = new ForeignCurrencyDecorator(account, java.util.Currency.getInstance("USD"));
                }
                break;
        }

        return account;
    }

    private Customer getOrCreateCustomer(String customerId) {
        return customers.computeIfAbsent(customerId, id ->
                new Customer(id, "Customer " + id, id + "@bank.com", LocalDate.now().minusYears(25))
        );
    }

    private Account getAccount(String accountNumber) {
        Account account = managedAccounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    private String findCustomerByAccount(String accountNumber) {
        return customers.entrySet().stream()
                .filter(entry -> entry.getValue().getAccountNumbers().contains(accountNumber))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void performCleanupOperations(Account account) {
        // Handle decorator-specific cleanup
        if (account instanceof InsuranceDecorator) {
            ((InsuranceDecorator) account).cancelInsurance();
        }

        System.out.println("Performed cleanup operations for account: " + account.getAccountNumber());
    }

    // Методы для демонстрации
    public void displayAllAccounts() {
        System.out.println("\n=== MANAGED ACCOUNTS (" + managedAccounts.size() + ") ===");
        if (managedAccounts.isEmpty()) {
            System.out.println("No accounts managed.");
        } else {
            managedAccounts.values().forEach(accountService::displayAccountInfo);
        }
    }

    public void displayCustomerInfo(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer != null) {
            System.out.println("\n=== CUSTOMER: " + customerId + " ===");
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Total Accounts: " + customer.getAccountNumbers().size());
            System.out.println("Accounts:");
            customer.getAccountNumbers().forEach(acc -> {
                Account account = managedAccounts.get(acc);
                if (account != null) {
                    String status = account.isClosed() ? " (CLOSED)" : "";
                    System.out.println("  - " + account.getAccountNumber() +
                            " (" + account.getAccountType() +
                            ") - $" + account.getBalance() + status);
                }
            });
        } else {
            System.out.println("Customer not found: " + customerId);
        }
    }

    // Дополнительные методы для получения информации
    public int getTotalManagedAccounts() {
        return managedAccounts.size();
    }

    public int getTotalCustomers() {
        return customers.size();
    }

    public List<String> getAllCustomerIds() {
        return new ArrayList<>(customers.keySet());
    }

    public BigDecimal getTotalAssetsUnderManagement() {
        return managedAccounts.values().stream()
                .filter(account -> !account.isClosed())
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Метод для сброса системы (для тестирования)
    public void resetSystem() {
        managedAccounts.clear();
        customers.clear();
        System.out.println("🔄 Banking system has been reset");
    }
}