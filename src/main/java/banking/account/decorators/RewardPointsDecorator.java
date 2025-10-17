
package banking.account.decorators;

import banking.account.Account;

import java.math.BigDecimal;

public class RewardPointsDecorator extends AccountDecorator {
    private int rewardPoints;
    private final BigDecimal pointsPerDollar;

    public RewardPointsDecorator(Account decoratedAccount, BigDecimal pointsPerDollar) {
        super(decoratedAccount);
        this.pointsPerDollar = pointsPerDollar;
        this.rewardPoints = 0;
    }

    @Override
    public String getDescription() {
        return decoratedAccount.getDescription() + " + Reward Points (" + pointsPerDollar + " points/$)";
    }

    @Override
    public void deposit(BigDecimal amount) {
        decoratedAccount.deposit(amount);
        addRewardPoints(amount);
    }

    private void addRewardPoints(BigDecimal amount) {
        int points = amount.multiply(pointsPerDollar).intValue();
        rewardPoints += points;
        System.out.println("Earned " + points + " reward points. Total: " + rewardPoints);
    }

    public void redeemPoints(int points) {
        if (points > rewardPoints) {
            throw new IllegalArgumentException("Not enough reward points");
        }
        BigDecimal redemptionValue = BigDecimal.valueOf(points).divide(BigDecimal.valueOf(100)); // 100 points = $1
        decoratedAccount.deposit(redemptionValue);
        rewardPoints -= points;
        System.out.println("Redeemed " + points + " points for $" + redemptionValue);
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public BigDecimal getPointsPerDollar() {
        return pointsPerDollar;
    }
}