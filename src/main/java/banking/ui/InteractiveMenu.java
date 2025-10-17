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
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║                🏦 БАНКОВСКАЯ СИСТЕМА              ║");
        System.out.println("║           Digital Banking & Investment            ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println("┌─────────────────── ГЛАВНОЕ МЕНЮ ───────────────────┐");
        System.out.println("│  1. 🔐 Войти в систему                            │");
        System.out.println("│  2. 👤 Создать нового клиента                     │");
        System.out.println("│  3. 📊 Запустить демонстрацию системы             │");
        System.out.println("│  4. ℹ️  О системе                                 │");
        System.out.println("│  5. ❌ Выход                                      │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.print("🎯 Выберите опцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> loginCustomer();
            case 2 -> createNewCustomer();
            case 3 -> runDemo();
            case 4 -> showSystemInfo();
            case 5 -> exitSystem();
            default -> showError("Неверный выбор. Попробуйте снова.");
        }
    }

    private void showCustomerMenu() {
        System.out.println("┌───────────────── ЛИЧНЫЙ КАБИНЕТ ──────────────────┐");
        System.out.println("│ 👤 Клиент: " + padRight(currentCustomerId, 35) + "│");
        System.out.println("├────────────────────────────────────────────────────┤");
        System.out.println("│  1. 📝 Открыть новый счет                         │");
        System.out.println("│  2. 💰 Пополнить счет                             │");
        System.out.println("│  3. 💸 Снять средства                             │");
        System.out.println("│  4. 🔄 Перевод между счетами                      │");
        System.out.println("│  5. 📈 Инвестиционные операции                    │");
        System.out.println("│  6. 📋 Мои счета                                  │");
        System.out.println("│  7. 📊 Выписка по счету                           │");
        System.out.println("│  8. 📧 Уведомления (" + getUnreadNotificationsCount() + ")                          │");
        System.out.println("│  9. 📄 Портфельный отчет                          │");
        System.out.println("│ 10. ⚙️  Дополнительные операции                   │");
        System.out.println("│ 11. ↩️  Выйти из аккаунта                         │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.print("🎯 Выберите опцию: ");

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
            default -> showError("Неверный выбор. Попробуйте снова.");
        }
    }

    private void showAdvancedOperations() {
        System.out.println("┌───────────── ДОПОЛНИТЕЛЬНЫЕ ОПЕРАЦИИ ─────────────┐");
        System.out.println("│  1. 🛡️  Создать безопасный инвестиционный счет    │");
        System.out.println("│  2. 💳 Проверить овердрафт                        │");
        System.out.println("│  3. 💱 Мультивалютные операции                    │");
        System.out.println("│  4. ⭐ Привилегированное обслуживание             │");
        System.out.println("│  5. 🎁 Бонусные баллы                             │");
        System.out.println("│  6. 🗑️  Закрыть счет                              │");
        System.out.println("│  7. ↩️  Назад                                     │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.print("🎯 Выберите опцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> createSafetyInvestment();
            case 2 -> checkOverdraft();
            case 3 -> currencyOperations();
            case 4 -> priorityBanking();
            case 5 -> rewardPoints();
            case 6 -> closeAccount();
            case 7 -> { return; }
            default -> showError("Неверный выбор.");
        }
    }

    private void loginCustomer() {
        System.out.println();
        System.out.print("🔐 Введите ID клиента: ");
        String customerId = scanner.nextLine().trim();

        if (customerId.isEmpty()) {
            showError("ID клиента не может быть пустым.");
            return;
        }

        // Проверяем, есть ли у клиента счета
        try {
            List<Account> accounts = bankingFacade.getCustomerAccountsByType(customerId, "");
            if (accounts.isEmpty()) {
                showWarning("Клиент с ID '" + customerId + "' не найден или не имеет счетов.");
                System.out.print("Хотите создать нового клиента? (y/n): ");
                String createNew = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(createNew) || "д".equalsIgnoreCase(createNew)) {
                    createNewCustomerWithId(customerId);
                    return;
                } else {
                    showInfo("Вход отменен.");
                    return;
                }
            }

            currentCustomerId = customerId;
            showSuccess("Успешный вход! Добро пожаловать, " + customerId);

        } catch (Exception e) {
            showError("Ошибка при входе: " + e.getMessage());
        }
    }

    private void createNewCustomer() {
        System.out.println();
        System.out.print("👤 Введите ID нового клиента: ");
        String customerId = scanner.nextLine().trim();

        if (customerId.isEmpty()) {
            showError("ID клиента не может быть пустым.");
            return;
        }

        createNewCustomerWithId(customerId);
    }

    private void createNewCustomerWithId(String customerId) {
        // Проверяем, не существует ли уже клиент
        try {
            List<Account> existingAccounts = bankingFacade.getCustomerAccountsByType(customerId, "");
            if (!existingAccounts.isEmpty()) {
                showWarning("Клиент с ID '" + customerId + "' уже существует!");
                System.out.print("Хотите войти под этим ID? (y/n): ");
                String login = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(login) || "д".equalsIgnoreCase(login)) {
                    currentCustomerId = customerId;
                    showSuccess("Успешный вход! Добро пожаловать, " + customerId);
                }
                return;
            }
        } catch (Exception e) {
            // Клиент не существует - это нормально
        }

        showSuccess("Клиент " + customerId + " создан!");
        System.out.println("Теперь вы можете войти под этим ID или сразу открыть счет.");

        System.out.print("Хотите открыть счет сейчас? (y/n): ");
        String openAccount = scanner.nextLine().trim();
        if ("y".equalsIgnoreCase(openAccount) || "д".equalsIgnoreCase(openAccount)) {
            currentCustomerId = customerId;
            createAccount();
        } else {
            showInfo("Вы можете войти позже под ID: " + customerId);
        }
    }

    private void createAccount() {
        if (currentCustomerId == null) {
            showError("Сначала войдите в систему.");
            return;
        }

        System.out.println();
        System.out.println("┌───────────── ОТКРЫТИЕ НОВОГО СЧЕТА ──────────────┐");
        System.out.println("│  1. 💰 Сберегательный счет                       │");
        System.out.println("│     • Бонусные баллы                             │");
        System.out.println("│     • Страхование от $10,000                     │");
        System.out.println("│  2. 📈 Инвестиционный счет                       │");
        System.out.println("│     • Налоговая оптимизация 15%                  │");
        System.out.println("│     • Привилегии от $5,000                       │");
        System.out.println("│  3. 💳 Расчетный счет                            │");
        System.out.println("│     • Овердрафт до $2,000                        │");
        System.out.println("│     • Мультивалютность от $5,000                 │");
        System.out.println("│  4. ↩️  Назад                                    │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.print("🎯 Выберите тип счета: ");

        int typeChoice = readIntInput();
        if (typeChoice == 4) return;

        String accountType = switch (typeChoice) {
            case 1 -> "savings";
            case 2 -> "investment";
            case 3 -> "checking";
            default -> {
                showError("Неверный тип счета.");
                yield null;
            }
        };

        if (accountType == null) return;

        System.out.print("💵 Введите начальный депозит: $");
        BigDecimal deposit = readBigDecimalInput();

        if (deposit.compareTo(BigDecimal.ZERO) < 0) {
            showError("Сумма не может быть отрицательной.");
            return;
        }

        if (deposit.compareTo(BigDecimal.ZERO) == 0) {
            showWarning("Вы открываете счет с нулевым балансом.");
        }

        try {
            Account account = bankingFacade.openAccountWithBenefits(currentCustomerId, accountType, deposit);
            showSuccess("Счет успешно создан!");
            System.out.println("📋 Номер счета: " + account.getAccountNumber());
            System.out.println("📝 Тип: " + account.getAccountType());
            System.out.println("💎 Особенности: " + account.getDescription());
            System.out.println("💵 Начальный баланс: $" + account.getBalance());
        } catch (Exception e) {
            showError("Ошибка при создании счета: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void depositMoney() {
        if (currentCustomerId == null) {
            showError("Сначала войдите в систему.");
            return;
        }

        System.out.println();
        System.out.print("📥 Введите номер счета для пополнения: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Номер счета не может быть пустым.");
            return;
        }

        // Проверяем, существует ли счет и принадлежит ли клиенту
        if (!isAccountAccessible(accountNumber)) {
            showError("Счет не найден или у вас нет к нему доступа.");
            return;
        }

        System.out.print("💵 Введите сумму для пополнения: $");
        BigDecimal amount = readBigDecimalInput();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Сумма должна быть положительной.");
            return;
        }

        if (amount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            showWarning("Внимание: вы вносите очень крупную сумму (>$100,000).");
            System.out.print("Подтвердите операцию (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm) && !"д".equalsIgnoreCase(confirm)) {
                showInfo("Операция отменена.");
                return;
            }
        }

        try {
            bankingFacade.deposit(accountNumber, amount, "Депозит через систему");
            showSuccess("Успешно пополнено $" + amount);
        } catch (Exception e) {
            showError("Ошибка при пополнении: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void withdrawMoney() {
        if (currentCustomerId == null) {
            showError("Сначала войдите в систему.");
            return;
        }

        System.out.println();
        System.out.print("📤 Введите номер счета для снятия: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Номер счета не может быть пустым.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Счет не найден или у вас нет к нему доступа.");
            return;
        }

        System.out.print("💵 Введите сумму для снятия: $");
        BigDecimal amount = readBigDecimalInput();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Сумма должна быть положительной.");
            return;
        }

        // Проверяем баланс счета
        try {
            Account account = getAccountByNumber(accountNumber);
            if (account != null) {
                if (account.getBalance().compareTo(amount) < 0) {
                    showError("Недостаточно средств на счете.");
                    System.out.println("💳 Доступно: $" + account.getBalance());
                    return;
                }

                if (amount.compareTo(BigDecimal.valueOf(5000)) > 0) {
                    showWarning("Внимание: крупное снятие (>$5,000).");
                    System.out.print("Подтвердите операцию (y/n): ");
                    String confirm = scanner.nextLine().trim();
                    if (!"y".equalsIgnoreCase(confirm) && !"д".equalsIgnoreCase(confirm)) {
                        showInfo("Операция отменена.");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            showError("Ошибка при проверке счета: " + e.getMessage());
            return;
        }

        try {
            bankingFacade.withdraw(accountNumber, amount, "Снятие через систему");
            showSuccess("Успешно снято $" + amount);
        } catch (Exception e) {
            showError("Ошибка при снятии: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void transferMoney() {
        if (currentCustomerId == null) {
            showError("Сначала войдите в систему.");
            return;
        }

        System.out.println();
        System.out.print("➡️  Введите номер счета отправителя: ");
        String fromAccount = scanner.nextLine().trim();

        if (fromAccount.isEmpty()) {
            showError("Номер счета отправителя не может быть пустым.");
            return;
        }

        if (!isAccountAccessible(fromAccount)) {
            showError("Счет отправителя не найден или у вас нет к нему доступа.");
            return;
        }

        System.out.print("⬅️  Введите номер счета получателя: ");
        String toAccount = scanner.nextLine().trim();

        if (toAccount.isEmpty()) {
            showError("Номер счета получателя не может быть пустым.");
            return;
        }

        if (fromAccount.equals(toAccount)) {
            showError("Нельзя переводить средства на тот же счет.");
            return;
        }

        System.out.print("💵 Введите сумму перевода: $");
        BigDecimal amount = readBigDecimalInput();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Сумма должна быть положительной.");
            return;
        }

        // Проверяем баланс счета отправителя
        try {
            Account account = getAccountByNumber(fromAccount);
            if (account != null && account.getBalance().compareTo(amount) < 0) {
                showError("Недостаточно средств на счете отправителя.");
                System.out.println("💳 Доступно: $" + account.getBalance());
                return;
            }
        } catch (Exception e) {
            showError("Ошибка при проверке счета: " + e.getMessage());
            return;
        }

        System.out.print("📝 Введите назначение платежа: ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            description = "Перевод между счетами";
        }

        try {
            bankingFacade.transferBetweenAccounts(fromAccount, toAccount, amount, description);
            showSuccess("Успешно переведено $" + amount);
        } catch (Exception e) {
            showError("Ошибка при переводе: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void investmentOperations() {
        if (currentCustomerId == null) {
            showError("Сначала войдите в систему.");
            return;
        }

        System.out.println();
        System.out.println("┌───────────── ИНВЕСТИЦИОННЫЕ ОПЕРАЦИИ ─────────────┐");
        System.out.println("│  1. 📈 Начислить инвестиционный доход             │");
        System.out.println("│  2. 🏦 Создать инвестиционный счет                │");
        System.out.println("│  3. ↩️  Назад                                     │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.print("🎯 Выберите операцию: ");

        int choice = readIntInput();

        switch (choice) {
            case 1 -> applyInvestmentReturns();
            case 2 -> createInvestmentAccount();
            case 3 -> { return; }
            default -> showError("Неверный выбор.");
        }
    }

    private void applyInvestmentReturns() {
        System.out.println();
        System.out.print("📈 Введите номер инвестиционного счета: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Номер счета не может быть пустым.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Счет не найден или у вас нет к нему доступа.");
            return;
        }

        // Проверяем, что счет действительно инвестиционный
        try {
            Account account = getAccountByNumber(accountNumber);
            if (account != null && !account.getAccountType().contains("Investment")) {
                showError("Этот счет не является инвестиционным.");
                return;
            }
        } catch (Exception e) {
            showError("Ошибка при проверке счета: " + e.getMessage());
            return;
        }

        System.out.print("💰 Введите сумму дохода: $");
        BigDecimal returns = readBigDecimalInput();

        if (returns.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Сумма должна быть положительной.");
            return;
        }

        try {
            bankingFacade.applyInvestmentReturns(accountNumber, returns);
            showSuccess("Инвестиционный доход начислен");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void createInvestmentAccount() {
        System.out.println();
        System.out.print("💵 Введите сумму начальных инвестиций: $");
        BigDecimal investment = readBigDecimalInput();

        if (investment.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Сумма должна быть положительной.");
            return;
        }

        if (investment.compareTo(BigDecimal.valueOf(1000)) < 0) {
            showWarning("Рекомендуемый минимальный взнос для инвестиционного счета: $1,000");
        }

        try {
            bankingFacade.openAccountWithBenefits(currentCustomerId, "investment", investment);
            showSuccess("Инвестиционный счет создан");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void createSafetyInvestment() {
        System.out.println();
        System.out.print("🛡️  Введите сумму для безопасного инвестирования: $");
        BigDecimal investment = readBigDecimalInput();

        if (investment.compareTo(BigDecimal.ZERO) <= 0) {
            showError("Сумма должна быть положительной.");
            return;
        }

        try {
            bankingFacade.investWithSafetyMode(currentCustomerId, investment);
            showSuccess("Безопасный инвестиционный счет создан!");
        } catch (Exception e) {
            showError("Ошибка: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void checkOverdraft() {
        System.out.println();
        System.out.println("💳 Проверка овердрафта");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Checking");
        if (accounts.isEmpty()) {
            showError("У вас нет расчетных счетов.");
            return;
        }

        for (Account account : accounts) {
            if (account instanceof OverdraftProtectionDecorator) {
                OverdraftProtectionDecorator overdraftAccount = (OverdraftProtectionDecorator) account;
                System.out.println("📋 Счет: " + account.getAccountNumber());
                System.out.println("💳 Лимит овердрафта: $" + overdraftAccount.getAvailableOverdraft());
                System.out.println("💰 Использовано: $" + overdraftAccount.getUsedOverdraft());
            }
        }

        pressAnyKeyToContinue();
    }

    private void currencyOperations() {
        System.out.println();
        System.out.println("💱 Мультивалютные операции");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Checking");
        boolean hasCurrencyAccount = false;

        for (Account account : accounts) {
            if (account instanceof ForeignCurrencyDecorator) {
                hasCurrencyAccount = true;
                ForeignCurrencyDecorator currencyAccount = (ForeignCurrencyDecorator) account;
                System.out.println("📋 Счет: " + account.getAccountNumber());
                System.out.println("🌍 Поддерживаемые валюты: " +
                        String.join(", ", currencyAccount.getSupportedCurrencies().keySet()));
            }
        }

        if (!hasCurrencyAccount) {
            showInfo("Мультивалютность доступна для расчетных счетов с балансом от $5,000");
        }

        pressAnyKeyToContinue();
    }

    private void priorityBanking() {
        System.out.println();
        System.out.println("⭐ Привилегированное обслуживание");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Investment");
        boolean hasPriorityAccount = false;

        for (Account account : accounts) {
            if (account instanceof PriorityBankingDecorator) {
                hasPriorityAccount = true;
                PriorityBankingDecorator priorityAccount = (PriorityBankingDecorator) account;
                System.out.println("📋 Счет: " + account.getAccountNumber());
                System.out.println("✨ Бесплатные транзакции: " + priorityAccount.getRemainingFreeTransactions());
                System.out.println("💎 Преференциальная ставка: " +
                        priorityAccount.getPreferentialInterestRate().multiply(BigDecimal.valueOf(100)) + "%");
            }
        }

        if (!hasPriorityAccount) {
            showInfo("Привилегии доступны для инвестиционных счетов с балансом от $5,000");
        }

        pressAnyKeyToContinue();
    }

    private void rewardPoints() {
        System.out.println();
        System.out.println("🎁 Бонусные баллы");

        List<Account> accounts = bankingFacade.getCustomerAccountsByType(currentCustomerId, "Savings");
        boolean hasRewardsAccount = false;

        for (Account account : accounts) {
            if (account instanceof RewardPointsDecorator) {
                hasRewardsAccount = true;
                RewardPointsDecorator rewardsAccount = (RewardPointsDecorator) account;
                System.out.println("📋 Счет: " + account.getAccountNumber());
                System.out.println("⭐ Накоплено баллов: " + rewardsAccount.getRewardPoints());
                System.out.println("🎯 Ставка: " + rewardsAccount.getPointsPerDollar() + " балла за $1");
            }
        }

        if (!hasRewardsAccount) {
            showInfo("Бонусные баллы начисляются на сберегательные счета: 2 балла за каждый доллар");
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
        System.out.print("📊 Введите номер счета для выписки: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Номер счета не может быть пустым.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Счет не найден или у вас нет к нему доступа.");
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
            showError("Ошибка при получении выписки: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void viewNotifications() {
        System.out.println();
        List<Notification> notifications = bankingFacade.getCustomerNotifications(currentCustomerId);

        if (notifications.isEmpty()) {
            showInfo("📭 У вас нет уведомлений.");
        } else {
            System.out.println("┌─────────────────── УВЕДОМЛЕНИЯ ───────────────────┐");
            for (int i = 0; i < notifications.size(); i++) {
                Notification notif = notifications.get(i);
                String status = notif.isRead() ? "📭" : "📬";
                String index = String.format("%2d", i + 1);
                System.out.printf("│ %s %s %-45s │\n", status, index, notif.getTitle());
                System.out.printf("│    %-50s │\n", notif.getMessage());
                if (i < notifications.size() - 1) {
                    System.out.println("├────────────────────────────────────────────────────┤");
                }
            }
            System.out.println("└────────────────────────────────────────────────────┘");
        }

        pressAnyKeyToContinue();
    }

    private void viewPortfolio() {
        System.out.println();
        try {
            String portfolio = bankingFacade.generateCustomerPortfolio(currentCustomerId);
            System.out.println(portfolio);
        } catch (Exception e) {
            showError("Ошибка при генерации отчета: " + e.getMessage());
        }

        pressAnyKeyToContinue();
    }

    private void closeAccount() {
        System.out.println();
        System.out.print("🗑️  Введите номер счета для закрытия: ");
        String accountNumber = scanner.nextLine().trim();

        if (accountNumber.isEmpty()) {
            showError("Номер счета не может быть пустым.");
            return;
        }

        if (!isAccountAccessible(accountNumber)) {
            showError("Счет не найден или у вас нет к нему доступа.");
            return;
        }

        // Проверяем баланс счета
        try {
            Account account = getAccountByNumber(accountNumber);
            if (account != null && account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                showWarning("На счете остаются средства: $" + account.getBalance());
                System.out.print("Вы уверены, что хотите закрыть счет? (y/n): ");
                String confirm = scanner.nextLine().trim();
                if (!"y".equalsIgnoreCase(confirm) && !"д".equalsIgnoreCase(confirm)) {
                    showInfo("Закрытие счета отменено.");
                    return;
                }
            }
        } catch (Exception e) {
            showError("Ошибка при проверке счета: " + e.getMessage());
            return;
        }

        System.out.print("❓ Вы уверены, что хотите закрыть счет " + accountNumber + "? (y/n): ");
        String confirmation = scanner.nextLine().trim();

        if ("y".equalsIgnoreCase(confirmation) || "д".equalsIgnoreCase(confirmation)) {
            try {
                bankingFacade.closeAccount(currentCustomerId, accountNumber);
                showSuccess("Счет успешно закрыт.");
            } catch (Exception e) {
                showError("Ошибка при закрытии счета: " + e.getMessage());
            }
        } else {
            showInfo("Закрытие счета отменено.");
        }

        pressAnyKeyToContinue();
    }

    private void runDemo() {
        System.out.println();
        System.out.println("🚀 Запуск демонстрации системы...");
        banking.Application demoApp = new banking.Application();
        demoApp.runQuickDemo();
        pressAnyKeyToContinue();
    }

    private void showSystemInfo() {
        System.out.println();
        System.out.println("┌─────────────────── О СИСТЕМЕ ───────────────────┐");
        System.out.println("│ 🏦 Digital Banking & Investment System         │");
        System.out.println("│                                                │");
        System.out.println("│ 📊 Реализованные паттерны:                     │");
        System.out.println("│   • Decorator - для добавления функциональности│");
        System.out.println("│   • Facade - для упрощения интерфейса          │");
        System.out.println("│                                                │");
        System.out.println("│ 💎 Особенности системы:                        │");
        System.out.println("│   • 3 типа счетов с различными бенефитами      │");
        System.out.println("│   • 6 декораторов для расширения функций       │");
        System.out.println("│   • Полный цикл банковских операций            │");
        System.out.println("│   • Система уведомлений и отчетности           │");
        System.out.println("└────────────────────────────────────────────────────┘");
        pressAnyKeyToContinue();
    }

    private void logout() {
        System.out.println();
        showSuccess("👋 До свидания, " + currentCustomerId + "!");
        currentCustomerId = null;
        pressAnyKeyToContinue();
    }

    private void exitSystem() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║         Спасибо за использование системы!         ║");
        System.out.println("║                 До новых встреч! 👋               ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.exit(0);
    }

    // Вспомогательные методы
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
                System.out.print("❌ Пожалуйста, введите число: ");
            }
        }
    }

    private BigDecimal readBigDecimalInput() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Пожалуйста, введите корректную сумму: ");
            }
        }
    }

    private void pressAnyKeyToContinue() {
        System.out.print("↵ Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    private void showSuccess(String message) {
        System.out.println("✅ " + message);
    }

    private void showError(String message) {
        System.out.println("❌ " + message);
    }

    private void showWarning(String message) {
        System.out.println("⚠️  " + message);
    }

    private void showInfo(String message) {
        System.out.println("ℹ️  " + message);
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}