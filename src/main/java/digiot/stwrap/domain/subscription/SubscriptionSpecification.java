package digiot.stwrap.domain.subscription;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SubscriptionSpecification {

    /**
     * Calculate the prorated amount for a subscription that is canceled before the end of the billing cycle.
     *
     * @param subscriptionStartDate The start date of the subscription.
     * @param cancellationDate      The date of cancellation.
     * @param monthlyRate           The monthly subscription rate.
     * @return The prorated amount to be refunded to the user.
     */
    public static double calculateProratedCancellationAmount(LocalDateTime subscriptionStartDate, LocalDateTime cancellationDate, double monthlyRate) {
        // Calculate the number of months between start and cancellation
        long monthsBetween = ChronoUnit.MONTHS.between(subscriptionStartDate, cancellationDate);

        // Calculate the prorated amount based on the remaining days in the current month
        int daysInCurrentMonth = subscriptionStartDate.getMonth().length(subscriptionStartDate.toLocalDate().isLeapYear());
        int daysRemainingInMonth = daysInCurrentMonth - subscriptionStartDate.getDayOfMonth() + 1;
        double proratedAmount = (double) daysRemainingInMonth / daysInCurrentMonth * monthlyRate;

        // Add full months' fees if applicable
        proratedAmount += monthsBetween * monthlyRate;

        return proratedAmount;
    }

    /**
     * Calculate the discounted subscription amount after a certain number of months.
     *
     * @param subscriptionStartDate The start date of the subscription.
     * @param currentDate           The current date.
     * @param monthlyRate           The monthly subscription rate.
     * @param discountMonths        The number of months before the discount applies.
     * @param discountRate          The discount rate (e.g., 0.1 for 10% discount).
     * @return The discounted subscription amount.
     */
    public static double calculateDiscountedAmount(LocalDateTime subscriptionStartDate, LocalDateTime currentDate, double monthlyRate, int discountMonths, double discountRate) {
        // Calculate the number of months between start and current date
        long monthsBetween = ChronoUnit.MONTHS.between(subscriptionStartDate, currentDate);

        // Check if the discount applies
        if (monthsBetween >= discountMonths) {
            // Apply the discount rate
            return (1 - discountRate) * monthlyRate;
        } else {
            // Regular monthly rate
            return monthlyRate;
        }
    }
}

