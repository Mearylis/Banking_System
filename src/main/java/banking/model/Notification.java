
package banking.model;

import java.time.LocalDateTime;

public class Notification {
    private final String notificationId;
    private final String customerId;
    private final String title;
    private final String message;
    private final LocalDateTime timestamp;
    private final NotificationType type;
    private boolean read;

    public enum NotificationType {
        INFO, WARNING, SUCCESS, ERROR
    }

    public Notification(String customerId, String title, String message, NotificationType type) {
        this.notificationId = "NOTIF-" + System.currentTimeMillis();
        this.customerId = customerId;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.read = false;
    }

    public void markAsRead() { this.read = true; }

    // Getters
    public String getNotificationId() { return notificationId; }
    public String getCustomerId() { return customerId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public NotificationType getType() { return type; }
    public boolean isRead() { return read; }
}