
package banking.account.decorators;

import banking.account.Account;

import java.math.BigDecimal;

public class TaxOptimizerDecorator extends AccountDecorator {
    private final BigDecimal taxRateReduction;
    private BigDecimal taxSavings;

    public TaxOptimizerDecorator(Account decoratedAccount, BigDecimal taxRateReduction) {
        super(decoratedAccount);
        this.taxRateReduction = taxRateReduction;
        this.taxSavings = BigDecimal.ZERO;
    }

    @Override
    public String getDescription() {
        return decoratedAccount.getDescription() + " + Tax Optimization (" +
                taxRateReduction.multiply(BigDecimal.valueOf(100)) + "% reduction)";
    }

    public BigDecimal calculateTaxSavings(BigDecimal taxableAmount) {
        BigDecimal savings = taxableAmount.multiply(taxRateReduction);
        taxSavings = taxSavings.add(savings);
        return savings;
    }

    public BigDecimal getTotalTaxSavings() {
        return taxSavings;
    }

    public BigDecimal getTaxRateReduction() {
        return taxRateReduction;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        // Tax-optimized withdrawal logic
        BigDecimal taxSavings = calculateTaxSavings(amount.multiply(BigDecimal.valueOf(0.1))); // Assume 10% taxable
        System.out.println("Tax savings on this withdrawal: $" + taxSavings);
        decoratedAccount.withdraw(amount);
    }
}