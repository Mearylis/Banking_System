
package banking.account.decorators;

import banking.account.Account;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class ForeignCurrencyDecorator extends AccountDecorator {
    private final Map<String, BigDecimal> exchangeRates;
    private final Currency baseCurrency;

    public ForeignCurrencyDecorator(Account decoratedAccount, Currency baseCurrency) {
        super(decoratedAccount);
        this.baseCurrency = baseCurrency;
        this.exchangeRates = new HashMap<>();
        initializeExchangeRates();
    }

    private void initializeExchangeRates() {
        exchangeRates.put("USD", BigDecimal.ONE);
        exchangeRates.put("EUR", BigDecimal.valueOf(0.85));
        exchangeRates.put("GBP", BigDecimal.valueOf(0.73));
        exchangeRates.put("JPY", BigDecimal.valueOf(110.0));
        exchangeRates.put("CAD", BigDecimal.valueOf(1.25));
    }

    @Override
    public String getDescription() {
        return decoratedAccount.getDescription() + " + Multi-Currency Support";
    }

    public void depositInCurrency(BigDecimal amount, String currencyCode) {
        BigDecimal exchangeRate = getExchangeRate(currencyCode);
        BigDecimal amountInUSD = amount.multiply(exchangeRate);
        decoratedAccount.deposit(amountInUSD);
        System.out.println("Deposited " + amount + " " + currencyCode + " (=$ " + amountInUSD + " USD)");
    }

    public BigDecimal getBalanceInCurrency(String currencyCode) {
        BigDecimal exchangeRate = getExchangeRate(currencyCode);
        return decoratedAccount.getBalance().divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getExchangeRate(String currencyCode) {
        BigDecimal rate = exchangeRates.get(currencyCode.toUpperCase());
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + currencyCode);
        }
        return rate;
    }

    public void updateExchangeRate(String currencyCode, BigDecimal newRate) {
        exchangeRates.put(currencyCode.toUpperCase(), newRate);
    }

    public Map<String, BigDecimal> getSupportedCurrencies() {
        return new HashMap<>(exchangeRates);
    }
}