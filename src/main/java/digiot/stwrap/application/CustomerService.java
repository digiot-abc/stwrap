package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import digiot.stwrap.domain.customer.StripeLinkedUserFactory;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CustomerService {

    final StripeLinkedUserRepository userLinkRepository;

    /**
     * Retrieves an existing StripeLinkedUser or creates a new one if it doesn't exist.
     *
     * @param userId The unique identifier of the user within your system.
     * @return StripeLinkedUser The retrieved or newly created StripeLinkedUser.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public StripeLinkedUser getOrCreateStripeLinkedUser(UserId userId) throws StripeException {
        return getOrCreateStripeLinkedUser(userId, null);
    }

    /**
     * Retrieves an existing StripeLinkedUser or creates a new one if it doesn't exist for the specified user.
     *
     * @param userId The unique identifier of the user within your system.
     * @param email  The email address associated with the user for the Stripe Customer object.
     * @return StripeLinkedUser The retrieved or newly created StripeLinkedUser.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public StripeLinkedUser getOrCreateStripeLinkedUser(UserId userId, String email) throws StripeException {

        Optional<StripeLinkedUser> link = userLinkRepository.findPrimaryByUserId(userId);
        
        if (link.isPresent()) {
            return link.get();
        }

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(String.valueOf(userId))
                .setEmail(email)
                .setDescription("Customer for user ID: " + userId)
                .build();

        Customer newCustomer = Customer.create(params);

        return linkStripeCustomer(userId, newCustomer);
    }

    /**
     * Links a Stripe customer to an existing user.
     *
     * @param userId   The unique identifier of the user within your system.
     * @param customer The Stripe Customer object to be linked to the user.
     * @return StripeLinkedUser The newly created StripeLinkedUser representing the link.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public StripeLinkedUser linkStripeCustomer(UserId userId, Customer customer) throws StripeException {

        StripeLinkedUserFactory linkFactory = new StripeLinkedUserFactory();
        StripeLinkedUser link = linkFactory.create(userId, customer.getId());
        userLinkRepository.save(link);

        return link;
    }

    /**
     * Adds a new payment method to the Stripe customer associated with the given user ID.
     *
     * @param userId The ID of the user within your system.
     * @param token  The token representing the payment method to be added.
     * @param type   The type of the payment method (e.g., PaymentMethodCreateParams.Type.CARD).
     * @return PaymentMethod The Stripe PaymentMethod object that was attached.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public PaymentMethod addPaymentMethodToCustomer(UserId userId, String token, PaymentMethodCreateParams.Type type) throws StripeException {

        StripeLinkedUser linkedUser = getOrCreateStripeLinkedUser(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());

        PaymentMethodCreateParams paymentMethodCreateParams = PaymentMethodCreateParams.builder()
                .setType(type)
                .setCard(PaymentMethodCreateParams.Token.builder().setToken(token).build())
                .build();
        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodCreateParams);
        attachPaymentMethodToCustomer(customer.getId(), paymentMethod.getId());

        return PaymentMethod.retrieve(customer.getDefaultSource());
    }

    /**
     * Attaches a specified Payment Method to a given customer in Stripe if it is not already attached.
     *
     * @param customerId      The unique identifier of the customer in Stripe.
     * @param paymentMethodId The unique identifier of the Payment Method to attach.
     * @throws StripeException If the Payment Method is already attached to a customer or
     *                         if there is an error during the API request to Stripe.
     */
    public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) throws StripeException {

        // Retrieve the specified Payment Method from Stripe.
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Check if the Payment Method is already attached to the specified customer.
        if (paymentMethod.getCustomer() != null && paymentMethod.getCustomer().equals(customerId)) {
            // The Payment Method is already attached to this customer, so no action is needed.
            return;
        }

        // Prepare the parameters to attach the Payment Method to the customer.
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();

        // Attach the Payment Method to the customer.
        paymentMethod.attach(attachParams);
    }
}