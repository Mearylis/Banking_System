
package banking.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String accountNumber, double available, double required) {
        super(String.format("Account %s: Insufficient funds. Available: $%.2f, Required: $%.2f",
                accountNumber, available, required));
    }
}

