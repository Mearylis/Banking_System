
package banking.account.decorators;

import banking.account.Account;

import java.math.BigDecimal;

public class InsuranceDecorator extends AccountDecorator {
    private final BigDecimal insuranceCoverage;
    private boolean insuranceActive;

    public InsuranceDecorator(Account decoratedAccount, BigDecimal insuranceCoverage) {
        super(decoratedAccount);
        this.insuranceCoverage = insuranceCoverage;
        this.insuranceActive = true;
    }

    @Override
    public String getDescription() {
        return decoratedAccount.getDescription() + " + Insurance Coverage ($" + insuranceCoverage + ")";
    }

    public void claimInsurance(BigDecimal amount) {
        if (!insuranceActive) {
            throw new IllegalStateException("Insurance is not active");
        }
        if (amount.compareTo(insuranceCoverage) > 0) {
            throw new IllegalArgumentException("Claim amount exceeds insurance coverage");
        }
        decoratedAccount.deposit(amount);
        System.out.println("Insurance claim processed: $" + amount);
    }

    public void cancelInsurance() {
        this.insuranceActive = false;
    }

    public boolean isInsuranceActive() {
        return insuranceActive;
    }

    public BigDecimal getInsuranceCoverage() {
        return insuranceCoverage;
    }
}