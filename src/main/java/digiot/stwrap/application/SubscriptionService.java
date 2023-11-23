package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.param.SubscriptionUpdateParams;
import digiot.stwrap.domain.model.StripeSubscription;
import digiot.stwrap.domain.model.StripeSubscriptionFactory;
import digiot.stwrap.domain.model.StripeUserLink;
import digiot.stwrap.domain.repository.StripeSubscriptionRepository;
import digiot.stwrap.domain.repository.StripeUserLinkRepository;
import digiot.stwrap.domain.subscription.SubscriptionItemFactory;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class SubscriptionService {

    final StripeUserLinkRepository<?> userLinkRepository;
    final StripeSubscriptionRepository subscriptionRepository;

    /**
     * Create a subscription for a customer.
     *
     * @param entity          the User Stripe linked entity.
     * @param planId          the ID of the subscription plan.
     * @param paymentMethodId the ID of the payment method to use for the subscription.
     * @param quantity        the quantity of the subscription.
     * @return Subscription object representing the created subscription.
     * @throws StripeException if an error occurs during subscription creation.
     */
    public Subscription subscription(StripeUserLink<?> entity, String planId, String paymentMethodId, int quantity) throws StripeException {
        Map<String, Object> subscriptionParams = new HashMap<>();
        subscriptionParams.put("customer", entity.getStripeCustomerId());
        subscriptionParams.put("items", SubscriptionItemFactory.createSubscriptionItem(planId, quantity));
        subscriptionParams.put("default_payment_method", paymentMethodId);

        Subscription subscription = Subscription.create(subscriptionParams);

        StripeSubscriptionFactory factory = new StripeSubscriptionFactory();
        StripeSubscription stripeSubscription = factory.create(entity.getId(), subscription.getId(), planId, subscription.getStatus());
        subscriptionRepository.create(stripeSubscription);

        return subscription;
    }

    /**
     * Applies a discount to the next billing cycle of a subscription using a coupon code.
     *
     * @param stripeSubscription The StripeSubscription object representing the subscription to update.
     * @param couponCode         the coupon code to apply to the subscription.
     * @return Subscription object representing the updated subscription.
     * @throws StripeException if an error occurs during the subscription update.
     */
    public Subscription applyDiscountToNextBilling(StripeSubscription stripeSubscription, String couponCode) throws StripeException {
        Subscription subscription = Subscription.retrieve(stripeSubscription.getSubscriptionId());

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCoupon(couponCode)
                .build();

        // TODO
        Subscription updatedSubscription = subscription.update(params);
        StripeSubscription foundStripeSubscription = subscriptionRepository.findBySubscriptionId(updatedSubscription.getId()).get();
        foundStripeSubscription.setStatus(updatedSubscription.getStatus());
//        subscriptionRepository.update(foundStripeSubscription);

        return updatedSubscription;
    }

    /**
     * Cancels a subscription at a specified date.
     *
     * @param stripeSubscription The StripeSubscription object representing the subscription to cancel.
     * @param cancelAt           the date at which the subscription should be cancelled.
     * @return Subscription object representing the updated subscription.
     * @throws StripeException if an error occurs during the subscription update.
     */
    public Subscription cancelSubscriptionAtDate(StripeSubscription stripeSubscription, Instant cancelAt) throws StripeException {
        Subscription subscription = Subscription.retrieve(stripeSubscription.getSubscriptionId());

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(false)
                .setCancelAt(cancelAt.getEpochSecond())
                .build();

        // TODO
        Subscription updatedSubscription = subscription.update(params);
        StripeSubscription foundStripeSubscription = subscriptionRepository.findBySubscriptionId(updatedSubscription.getId()).get();
        foundStripeSubscription.setStatus(updatedSubscription.getStatus());
//        subscriptionRepository.update(foundStripeSubscription);

        return updatedSubscription;
    }

    /**
     * Schedules a subscription for cancellation at the end of the current billing period.
     *
     * @param stripeSubscription The StripeSubscription object representing the subscription to cancel.
     * @return Subscription object representing the updated subscription.
     * @throws StripeException if an error occurs during the subscription update.
     */
    public Subscription cancelSubscriptionAtPeriodEnd(StripeSubscription stripeSubscription) throws StripeException {
        Subscription subscription = Subscription.retrieve(stripeSubscription.getSubscriptionId());

        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();

        Subscription updatedSubscription = subscription.update(params);

        return updatedSubscription;
    }
}
