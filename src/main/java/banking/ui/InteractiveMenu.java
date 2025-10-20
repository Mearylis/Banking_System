package banking.ui;

import banking.facade.BankingFacade;
import banking.model.Customer;
import banking.model.Notification;
import banking.account.Account;
import banking.account.decorators.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class InteractiveMenu {
    private final BankingFacade bankingFacade;
    private final Scanner scanner;
    private String currentCustomerId;

    public InteractiveMenu() {
        this.bankingFacade = new BankingFacade();
        this.scanner = new Scanner(System.in);
        this.currentCustomerId = null;
    }

    public void start() {
        displayWelcome();

        while (true) {
            if (currentCustomerId == null) {
                showMainMenu();
            } else {
                showCustomerMenu();
            }
        }
    }

    private void displayWelcome() {
        System.out.println("========================================================");
        System.out.println("|                BANKING SYSTEM                       |");
        System.out.println("|           Digital Banking & Investment              |");
        System.out.println("========================================================");
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println("------------------- MAIN MENU ----------------------");
        System.out.println("|  1. Sign in to the system                        |");
        System.out.println("|  2. Create new customer                          |");
        System.out.println("|  3. Run system demonstration                     |");
        System.out.println("|  4. About the system                             |");
        System.out.println("|  5. Exit                                         |");
        System.out.println("----------------------------------------------------");
        System.out.print("Select option: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> loginCustomer();
            case 2 -> createNewCustomer();
            case 3 -> runDemo();
            case 4 -> showSystemInfo();
            case 5 -> exitSystem();
            default -> showError("Invalid choice. Please try again.");
        }
    }

    private void showCustomerMenu() {
        System.out.println("----------------- PERSONAL ACCOUNT -----------------");
        System.out.println("| Customer: " + padRight(currentCustomerId, 35) + "|");
        System.out.println("----------------------------------------------------");
        System.out.println("|  1. Open new account                             |");
        System.out.println("|  2. Deposit funds                                |");
        System.out.println("|  3. Withdraw funds                               |");
        System.out.println("|  4. Transfer between accounts                    |");
        System.out.println("|  5. Investment operations                        |");
        System.out.println("|  6. My accounts                                  |");
        System.out.println("|  7. Account statement                            |");
        System.out.println("|  8. Notifications (" + getUnreadNotificationsCount() + ")                          |");
        System.out.println("|  9. Portfolio report                             |");
        System.out.println("| 10. Additional operations                        |");
        System.out.println("| 11. Logout                                       |");
        System.out.println("----------------------------------------------------");
        System.out.print("Select option: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> createAccount();
            case 2 -> depositMoney();
            case 3 -> withdrawMoney();
            case 4 -> transferMoney();
            case 5 -> investmentOperations();
            case 6 -> viewAccounts();
            case 7 -> viewStatement();
            case 8 -> viewNotifications();
            case 9 -> viewPortfolio();
            case 10 -> showAdvancedOperations();
            case 11 -> logout();
            default -> showError("Invalid choice. Please try again.");
        }
    }

    private void showAdvancedOperations() {
        System.out.println("------------- ADDITIONAL OPERATIONS ----------------");
        System.out.println("|  1. Create secure investment account             |");
        System.out.println("|  2. Check overdraft                              |");
        System.out.println("|  3. Multi-currency operations                    |");
        System.out.println("|  4. Priority banking                             |");
        System.out.println("|  5. Reward points                                |");
        System.out.println("|  6. Close account                                |");
        System.out.println("|  7. Back                                         |");
        System.out.println("----------------------------------------------------");
        System.out.print("Select option: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> createSafetyInvestment();
            case 2 -> checkOverdraft();
            case 3 -> currencyOperations();
            case 4 -> priorityBanking();
            case 5 -> rewardPoints();
            case 6 -> closeAccount();
            case 7 -> { return; }
            default -> showError("Invalid choice.");
        }
    }

    private void loginCustomer() {
        System.out.println();
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine().trim();

        if (customerId.isEmpty()) {
            showError("Customer ID cannot be empty.");
            return;
        }

        // Check if customer has accounts
        try {
            List<Account> accounts = bankingFacade.getCustomerAccountsByType(customerId, "");
            if (accounts.isEmpty()) {
                showWarning("Customer with ID '" + customerId + "' not found or has no accounts.");
                System.out.print("Do you want to create a new customer? (y/n): ");
                String createNew = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(createNew)) {
                    createNewCustomerWithId(customerId);
                    return;
                } else {
                    showInfo("Login cancelled.");
                    return;
                }
            }

            currentCustomerId = customerId;
            showSuccess("Login successful! Welcome, " + customerId);

        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
        }
    }

    private void createNewCustomer() {
        System.out.println();
        System.out.print("Enter new customer ID: ");
        String customerId = scanner.nextLine().trim();

        if (customerId.isEmpty()) {
            showError("Customer ID cannot be empty.");
            return;
        }

        createNewCustomerWithId(customerId);
    }

    private void createNewCustomerWithId(String customerId) {
        // Check if customer already exists
        try {
            List<Account> existingAccounts = bankingFacade.getCustomerAccountsByType(customerId, "");
            if (!existingAccounts.isEmpty()) {
                showWarning("Customer with ID '" + customerId + "' already exists!");
                System.out.print("Do you want to login with this ID? (y/n): ");
                String login = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(login)) {
                    currentCustomerId = customerId;
                    showSuccess("Login successful! Welcome, " + customerId);
                }
                return;
            }
        } catch (Exception e) {
            // Customer doesn't exist - this is normal
        }

        showSuccess("Customer " + customerId + " created!");
        System.out.println("You can now login with this ID or open an account immediately.");

        System.out.print("Do you want to open an account now? (y/n): ");
        String openAccount = scanner.nextLine().trim();
        if ("y".equalsIgnoreCase(openAccount)) {
            currentCustomerId = customerId;
            createAccount();
        } else {
            showInfo("You can login later with ID: " + customerId);
        }
    }

    private void createAccount() {
        if (currentCustomerId == null) {
            showError("Please login first.");
            return;
        }

        System.out.println();
        System.out.println("------------- OPEN NEW ACCOUNT ---------------------");
        System.out.println("|  1. Savings account                              |");
        System.out.println("|     * Reward points                              |");
        System.out.println("|     * Insurance from $10,000                     |");
        System.out.println("|  2. Investment account                           |");
        System.out.println("|     * Tax optimization 15%                       |");
        System.out.println("|     * Privileges from $5,000                     |");
        System.out.println("|  3. Checking account                             |");
        System.out.println("|     * Overdraft up to $2,000                     |");
        System.out.println("|     * Multi-currency from $5,000                 |");
        System.out.println("|  4. Back                                         |");
        System.out.println("----------------------------------------------------");
        System.out.print("Select account type: ");

        int typeChoice = readIntInput();
        if (typeChoice == 4) return;

        String accountType = switch (typeChoice) {
            case 1 -> "savings";
            case 2 -> "investment";
            case 3 -> "checking";
            default -> {
                showError("Invalid account type.");
                yield null;
            }
        };

        if (accountType == null) return;

        System.out.print("Enter initial deposit: $");
        BigDecimal deposit = readBigDecimalInput();

        if (deposit.compareTo(BigDecimal.ZERO) < 0) {
            showError("Amount cannot be negative.");
            return;
        }

        if (deposit.compareTo(BigDecimal.ZERO) == 0) {
            showWarning("You are opening an account with zero balance.");
        }

        try {
            Account account = bankingFacade.openAccountWithBenefits(currentCustomerId, accountType, deposit);
            showSuccess("Account successfully created!");
            System.out.println("Account number: " + account.getAccountNumber());
            System.out.println("Type: " + account.getAccountType());
            System.out.println("Features: " + account.getDescription());
            System.out.println("Initial balance: $" + account.getBalance());
        } catch (Exception e) {
            showError("Error creating account: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void depositMoney() {
        if (currentCustomerId == null) {
            showError("Please login first.");
            return;
        }

        System.out.println();
        System.out.print("Enter account number for deposit: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Account number cannot be empty.");
            return;
        }

        // Check if account exists and belongs to customer
        if (!isAccountAccessible(accountNumber)) {
            showError("Account not found or you don't have access.");
            return;
        }

        System.out.print("Enter deposit amount: $");
        BigDecimal amount = readBigDecimalInput();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return;
        }

        if (amount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            showWarning("Warning: you are depositing a very large amount (>$100,000).");
            System.out.print("Confirm operation (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                showInfo("Operation cancelled.");
                return;
            }
        }

        try {
            bankingFacade.deposit(accountNumber, amount, "Deposit via system");
            showSuccess("Successfully deposited $" + amount);
        } catch (Exception e) {
            showError("Deposit error: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void withdrawMoney() {
        if (currentCustomerId == null) {
            showError("Please login first.");
            return;
        }

        System.out.println();
        System.out.print("Enter account number for withdrawal: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Account number cannot be empty.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Account not found or you don't have access.");
            return;
        }

        System.out.print("Enter withdrawal amount: $");
        BigDecimal amount = readBigDecimalInput();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return;
        }

        // Check account balance
        try {
            Account account = getAccountByNumber(accountNumber);
            if (account != null) {
                if (account.getBalance().compareTo(amount) < 0) {
                    showError("Insufficient funds in account.");
                    System.out.println("Available: $" + account.getBalance());
                    return;
                }

                if (amount.compareTo(BigDecimal.valueOf(5000)) > 0) {
                    showWarning("Warning: large withdrawal (>$5,000).");
                    System.out.print("Confirm operation (y/n): ");
                    String confirm = scanner.nextLine().trim();
                    if (!"y".equalsIgnoreCase(confirm)) {
                        showInfo("Operation cancelled.");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            showError("Account check error: " + e.getMessage());
            return;
        }

        try {
            bankingFacade.withdraw(accountNumber, amount, "Withdrawal via system");
            showSuccess("Successfully withdrew $" + amount);
        } catch (Exception e) {
            showError("Withdrawal error: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void transferMoney() {
        if (currentCustomerId == null) {
            showError("Please login first.");
            return;
        }

        System.out.println();
        System.out.print("Enter sender account number: ");
        String fromAccount = scanner.nextLine().trim();

        if (fromAccount.isEmpty()) {
            showError("Sender account number cannot be empty.");
            return;
        }

        if (!isAccountAccessible(fromAccount)) {
            showError("Sender account not found or you don't have access.");
            return;
        }

        System.out.print("Enter recipient account number: ");
        String toAccount = scanner.nextLine().trim();

        if (toAccount.isEmpty()) {
            showError("Recipient account number cannot be empty.");
            return;
        }

        if (fromAccount.equals(toAccount)) {
            showError("Cannot transfer funds to the same account.");
            return;
        }

        System.out.print("Enter transfer amount: $");
        BigDecimal amount = readBigDecimalInput();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return;
        }

        // Check sender account balance
        try {
            Account account = getAccountByNumber(fromAccount);
            if (account != null && account.getBalance().compareTo(amount) < 0) {
                showError("Insufficient funds in sender account.");
                System.out.println("Available: $" + account.getBalance());
                return;
            }
        } catch (Exception e) {
            showError("Account check error: " + e.getMessage());
            return;
        }

        System.out.print("Enter payment description: ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            description = "Transfer between accounts";
        }

        try {
            bankingFacade.transferBetweenAccounts(fromAccount, toAccount, amount, description);
            showSuccess("Successfully transferred $" + amount);
        } catch (Exception e) {
            showError("Transfer error: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void investmentOperations() {
        if (currentCustomerId == null) {
            showError("Please login first.");
            return;
        }

        System.out.println();
        System.out.println("------------- INVESTMENT OPERATIONS ----------------");
        System.out.println("|  1. Apply investment returns                     |");
        System.out.println("|  2. Create investment account                    |");
        System.out.println("|  3. Back                                         |");
        System.out.println("----------------------------------------------------");
        System.out.print("Select operation: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> applyInvestmentReturns();
            case 2 -> createInvestmentAccount();
            case 3 -> { return; }
            default -> showError("Invalid choice.");
        }
    }

    private void applyInvestmentReturns() {
        System.out.println();
        System.out.print("Enter investment account number: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Account number cannot be empty.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Account not found or you don't have access.");
            return;
        }

        // Check if account is actually investment type
        try {
            Account account = getAccountByNumber(accountNumber);
            if (account != null && !account.getAccountType().contains("Investment")) {
                showError("This account is not an investment account.");
                return;
            }
        } catch (Exception e) {
            showError("Account check error: " + e.getMessage());
            return;
        }

        System.out.print("Enter returns amount: $");
        BigDecimal returns = readBigDecimalInput();

        if (returns.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return;
        }

        try {
            bankingFacade.applyInvestmentReturns(accountNumber, returns);
            showSuccess("Investment returns applied");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void createInvestmentAccount() {
        System.out.println();
        System.out.print("Enter initial investment amount: $");
        BigDecimal investment = readBigDecimalInput();

        if (investment.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return;
        }

        if (investment.compareTo(BigDecimal.valueOf(1000)) < 0) {
            showWarning("Recommended minimum investment: $1,000");
        }

        try {
            bankingFacade.openAccountWithBenefits(currentCustomerId, "investment", investment);
            showSuccess("Investment account created");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void createSafetyInvestment() {
        System.out.println();
        System.out.print("Enter amount for secure investment: $");
        BigDecimal investment = readBigDecimalInput();

        if (investment.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Amount must be positive.");
            return;
        }

        try {
            bankingFacade.investWithSafetyMode(currentCustomerId, investment);
            showSuccess("Secure investment account created!");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void checkOverdraft() {
        System.out.println();
        System.out.println("Overdraft check");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Checking");
        if (accounts.isEmpty()) {
            showError("You don't have checking accounts.");
            return;
        }

        for (Account account : accounts) {
            if (account instanceof OverdraftProtectionDecorator) {
                OverdraftProtectionDecorator overdraftAccount = (OverdraftProtectionDecorator) account;
                System.out.println("Account: " + account.getAccountNumber());
                System.out.println("Overdraft limit: $" + overdraftAccount.getAvailableOverdraft());
                System.out.println("Used: $" + overdraftAccount.getUsedOverdraft());
            }
        }

        pressAnyKeyToContinue();
    }

    private void currencyOperations() {
        System.out.println();
        System.out.println("Multi-currency operations");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Checking");
        boolean hasCurrencyAccount = false;

        for (Account account : accounts) {
            if (account instanceof ForeignCurrencyDecorator) {
                hasCurrencyAccount = true;
                ForeignCurrencyDecorator currencyAccount = (ForeignCurrencyDecorator) account;
                System.out.println("Account: " + account.getAccountNumber());
                System.out.println("Supported currencies: " +
                        String.join(", ", currencyAccount.getSupportedCurrencies().keySet()));
            }
        }

        if (!hasCurrencyAccount) {
            showInfo("Multi-currency available for checking accounts with balance from $5,000");
        }

        pressAnyKeyToContinue();
    }

    private void priorityBanking() {
        System.out.println();
        System.out.println("Priority banking");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Investment");
        boolean hasPriorityAccount = false;

        for (Account account : accounts) {
            if (account instanceof PriorityBankingDecorator) {
                hasPriorityAccount = true;
                PriorityBankingDecorator priorityAccount = (PriorityBankingDecorator) account;
                System.out.println("Account: " + account.getAccountNumber());
                System.out.println("Free transactions: " + priorityAccount.getRemainingFreeTransactions());
                System.out.println("Preferential rate: " +
                        priorityAccount.getPreferentialInterestRate().multiply(BigDecimal.valueOf(100)) + "%");
            }
        }

        if (!hasPriorityAccount) {
            showInfo("Privileges available for investment accounts with balance from $5,000");
        }

        pressAnyKeyToContinue();
    }

    private void rewardPoints() {
        System.out.println();
        System.out.println("Reward points");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Savings");
        boolean hasRewardsAccount = false;

        for (Account account : accounts) {
            if (account instanceof RewardPointsDecorator) {
                hasRewardsAccount = true;
                RewardPointsDecorator rewardsAccount = (RewardPointsDecorator) account;
                System.out.println("Account: " + account.getAccountNumber());
                System.out.println("Points earned: " + rewardsAccount.getRewardPoints());
                System.out.println("Rate: " + rewardsAccount.getPointsPerDollar() + " points per $1");
            }
        }

        if (!hasRewardsAccount) {
            showInfo("Reward points are earned on savings accounts: 2 points per dollar");
        }

        pressAnyKeyToContinue();
    }

    private void viewAccounts() {
        System.out.println();
        bankingFacade.displayCustomerInfo(currentCustomerId);
        pressAnyKeyToContinue();
    }

    private void viewStatement() {
        System.out.println();
        System.out.print("Enter account number for statement: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Account number cannot be empty.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Account not found or you don't have access.");
            return;
        }

        try {
            String statement = bankingFacade.generateAccountStatement(
                    accountNumber,
                    LocalDate.now().minusMonths(1),
                    LocalDate.now()
            );
            System.out.println(statement);
        } catch (Exception e) {
            showError("Error getting statement: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void viewNotifications() {
        System.out.println();
        List<Notification> notifications = bankingFacade.getCustomerNotifications(currentCustomerId);

        if (notifications.isEmpty()) {
            showInfo("You have no notifications.");
        } else {
            System.out.println("------------------- NOTIFICATIONS -------------------");
            for (int i = 0; i < notifications.size(); i++) {
                Notification notif = notifications.get(i);
                String status = notif.isRead() ? "READ" : "NEW";
                String index = String.format("%2d", i + 1);
                System.out.printf("| %s %s %-45s |\n", status, index, notif.getTitle());
                System.out.printf("|    %-50s |\n", notif.getMessage());
                if (i < notifications.size() - 1) {
                    System.out.println("----------------------------------------------------");
                }
            }
            System.out.println("----------------------------------------------------");
        }

        pressAnyKeyToContinue();
    }

    private void viewPortfolio() {
        System.out.println();
        try {
            String portfolio = bankingFacade.generateCustomerPortfolio(currentCustomerId);
            System.out.println(portfolio);
        } catch (Exception e) {
            showError("Error generating report: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void closeAccount() {
        System.out.println();
        System.out.print("Enter account number to close: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Account number cannot be empty.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Account not found or you don't have access.");
            return;
        }

        // Check account balance
        try {
            Account account = getAccountByNumber(accountNumber);
            if (account != null && account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                showWarning("Account still has funds: $" + account.getBalance());
                System.out.print("Are you sure you want to close the account? (y/n): ");
                String confirm = scanner.nextLine().trim();
                if (!"y".equalsIgnoreCase(confirm)) {
                    showInfo("Account closure cancelled.");
                    return;
                }
            }
        } catch (Exception e) {
            showError("Account check error: " + e.getMessage());
            return;
        }

        System.out.print("Are you sure you want to close account " + accountNumber + "? (y/n): ");
        String confirmation = scanner.nextLine().trim();

        if ("y".equalsIgnoreCase(confirmation)) {
            try {
                bankingFacade.closeAccount(currentCustomerId, accountNumber);
                showSuccess("Account successfully closed.");
            } catch (Exception e) {
                showError("Error closing account: " + e.getMessage());
            }
        } else {
            showInfo("Account closure cancelled.");
        }

        pressAnyKeyToContinue();
    }

    private void runDemo() {
        System.out.println();
        System.out.println("Starting system demonstration...");
        banking.Application demoApp = new banking.Application();
        demoApp.runQuickDemo();
        pressAnyKeyToContinue();
    }

    private void showSystemInfo() {
        System.out.println();
        System.out.println("------------------- ABOUT THE SYSTEM ---------------");
        System.out.println("| Digital Banking & Investment System              |");
        System.out.println("|                                                  |");
        System.out.println("| Implemented patterns:                            |");
        System.out.println("|   * Decorator - for adding functionality         |");
        System.out.println("|   * Facade - for simplifying interface           |");
        System.out.println("|                                                  |");
        System.out.println("| System features:                                 |");
        System.out.println("|   * 3 account types with various benefits        |");
        System.out.println("|   * 6 decorators for extending functions         |");
        System.out.println("|   * Full cycle of banking operations             |");
        System.out.println("|   * Notification and reporting system            |");
        System.out.println("----------------------------------------------------");
        pressAnyKeyToContinue();
    }

    private void logout() {
        System.out.println();
        showSuccess("Goodbye, " + currentCustomerId + "!");
        currentCustomerId = null;
        pressAnyKeyToContinue();
    }

    private void exitSystem() {
        System.out.println();
        System.out.println("========================================================");
        System.out.println("|         Thank you for using the system!             |");
        System.out.println("|                 See you again!                      |");
        System.out.println("========================================================");
        System.exit(0);
    }

    // Helper methods
    private boolean isAccountAccessible(String accountNumber) {
        try {
            List<Account> customerAccounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "");
            return customerAccounts.stream()
                    .anyMatch(account -> account.getAccountNumber().equals(accountNumber));
        } catch (Exception e) {
            return false;
        }
    }

    private Account getAccountByNumber(String accountNumber) {
        try {
            List<Account> customerAccounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "");
            return customerAccounts.stream()
                    .filter(account -> account.getAccountNumber().equals(accountNumber))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private int getUnreadNotificationsCount() {
        List<Notification> notifications = bankingFacade.getCustomerNotifications(currentCustomerId);
        return (int) notifications.stream().filter(notif -> !notif.isRead()).count();
    }

    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }

    private BigDecimal readBigDecimalInput() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount: ");
            }
        }
    }

    private void pressAnyKeyToContinue() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void showSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    private void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    private void showWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    private void showInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}