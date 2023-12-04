package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import digiot.stwrap.domain.LinkedUserSpecification;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class SubscriptionService {

    private final StripeLinkService stripeLinkService;

    /**
     * Creates a new subscription in Stripe.
     *
     * @param userId          The unique identifier of the user for whom the subscription is being created.
     * @param planId       The ID of the product/plan to which the user is subscribing.
     * @param paymentMethodId The ID of the payment method to be used for the subscription.
     * @return The created Subscription object.
     */
    @SneakyThrows(StripeException.class)
    public Subscription createSubscription(UserId userId, String planId, String paymentMethodId) {

        // Retrieves or creates a Stripe-linked user based on the provided userId.
        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        // Retrieves the Stripe Customer object using the Stripe Customer ID from the linked user.
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());

        // Configures the subscription parameters.
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .addItem(SubscriptionCreateParams.Item.builder()
                        .setPlan(planId) // Sets the plan (product) for the subscription.
                        .build())
                .setCustomer(customer.getId()) // Associates the subscription with the retrieved Stripe Customer.
                .setDefaultPaymentMethod(paymentMethodId) // Sets the default payment method for the subscription.
                .build();

        // Creates a new subscription in Stripe with the specified parameters.
        return Subscription.create(params);
    }

    /**
     * Update the payment method for a Stripe subscription for a given user.
     *
     * @param userId             The ID of the user whose subscription's payment method is to be updated.
     * @param subscriptionId     The ID of the subscription to update.
     * @param newPaymentMethodId The ID of the new payment method to set as the default.
     * @return The updated Subscription object.
     */
    @SneakyThrows(StripeException.class)
    public Subscription updateSubscriptionPaymentMethod(UserId userId, String subscriptionId, String newPaymentMethodId) {

        // Retrieves or creates a Stripe linked user.
        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        // Retrieves the subscription based on the provided ID.
        Subscription subscription = Subscription.retrieve(subscriptionId);

        // Verifies if the subscription belongs to the retrieved customer.
        LinkedUserSpecification.verifyUserAssociation(linkedUser, subscription);

        // Attach the new payment method to the customer.
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build();
        PaymentMethod.retrieve(newPaymentMethodId).attach(attachParams);

        // Update the subscription with the new default payment method.
        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setDefaultPaymentMethod(newPaymentMethodId)
                .build();

        // Update the subscription and return the updated object.
        return subscription.update(params);
    }

    /**
     * Cancels a Stripe subscription for a given user.
     *
     * @param userId         The ID of the user whose subscription is to be canceled.
     * @param subscriptionId The ID of the subscription to cancel.
     * @return The canceled Subscription object.
     */
    @SneakyThrows(StripeException.class)
    public Subscription cancelSubscription(UserId userId, String subscriptionId) {

        // Retrieves or creates a Stripe linked user.
        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        // Retrieves the subscription based on the provided ID.
        Subscription subscription = Subscription.retrieve(subscriptionId);

        // Verifies if the subscription belongs to the retrieved customer.
        LinkedUserSpecification.verifyUserAssociation(linkedUser, subscription);

        // Cancels the subscription.
        return subscription.cancel();
    }

    /**
     * Applies a coupon to a Stripe subscription for a given user.
     *
     * @param userId         The ID of the user whose subscription will receive the coupon.
     * @param subscriptionId The ID of the subscription to which the coupon is applied.
     * @param couponCode     The coupon code to apply to the subscription.
     * @return The updated Subscription object.
     */
    @SneakyThrows(StripeException.class)
    public Subscription applyCoupon(UserId userId, String subscriptionId, String couponCode) {

        // Retrieves or creates a Stripe linked user.
        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        // Retrieves the subscription based on the provided ID.
        Subscription subscription = Subscription.retrieve(subscriptionId);

        // Verifies if the subscription belongs to the retrieved customer.
        LinkedUserSpecification.verifyUserAssociation(linkedUser, subscription);

        // Creates parameters for updating the subscription with the coupon.
        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCoupon(couponCode)
                .build();

        // Applies the coupon to the subscription and returns the updated object.
        return subscription.update(params);
    }
}
