
package banking.service;

import banking.model.Notification;
import java.math.BigDecimal;
import java.util.*;

public class NotificationService {
    private final Map<String, List<Notification>> customerNotifications;

    public NotificationService() {
        this.customerNotifications = new HashMap<>();
    }

    public void sendNotification(String customerId, String title, String message,
                                 Notification.NotificationType type) {
        Notification notification = new Notification(customerId, title, message, type);
        customerNotifications.computeIfAbsent(customerId, k -> new ArrayList<>()).add(notification);

        System.out.println("Notification sent to " + customerId + ": " + title + " - " + message);
    }

    public void sendAccountOpenedNotification(String customerId, String accountNumber, String accountType) {
        sendNotification(customerId,
                "Account Opened",
                "Your new " + accountType + " (" + accountNumber + ") has been successfully opened",
                Notification.NotificationType.SUCCESS
        );
    }

    public void sendLowBalanceAlert(String customerId, String accountNumber, BigDecimal balance) {
        sendNotification(customerId,
                "Low Balance Alert",
                "Account " + accountNumber + " has low balance: $" + balance,
                Notification.NotificationType.WARNING
        );
    }

    public void sendLargeTransactionAlert(String customerId, String accountNumber,
                                          BigDecimal amount, String transactionType) {
        sendNotification(customerId,
                "Large Transaction Alert",
                transactionType + " of $" + amount + " on account " + accountNumber,
                Notification.NotificationType.INFO
        );
    }

    public List<Notification> getCustomerNotifications(String customerId) {
        return customerNotifications.getOrDefault(customerId, new ArrayList<>());
    }

    public List<Notification> getUnreadNotifications(String customerId) {
        return getCustomerNotifications(customerId).stream()
                .filter(notification -> !notification.isRead())
                .toList();
    }

    public void markNotificationAsRead(String customerId, String notificationId) {
        getCustomerNotifications(customerId).stream()
                .filter(notification -> notification.getNotificationId().equals(notificationId))
                .findFirst()
                .ifPresent(Notification::markAsRead);
    }
}