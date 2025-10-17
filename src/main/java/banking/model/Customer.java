
package banking.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String customerId;
    private final String name;
    private final String email;
    private final LocalDate dateOfBirth;
    private final List<String> accountNumbers;

    public Customer(String customerId, String name, String email, LocalDate dateOfBirth) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.accountNumbers = new ArrayList<>();
    }

    public void addAccount(String accountNumber) {
        accountNumbers.add(accountNumber);
    }

    public void removeAccount(String accountNumber) {
        accountNumbers.remove(accountNumber);
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public List<String> getAccountNumbers() { return new ArrayList<>(accountNumbers); }
}