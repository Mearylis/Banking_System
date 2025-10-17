package banking;

import banking.ui.InteractiveMenu;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0 && "demo".equals(args[0])) {
                System.out.println("🚀 Запуск демонстрационного режима...");
                Application application = new Application();
                application.runComprehensiveDemo();
            } else {
                InteractiveMenu menu = new InteractiveMenu();
                menu.start();
            }

        } catch (Exception e) {
            System.err.println("❌ Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}