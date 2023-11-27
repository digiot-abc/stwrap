import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.SetupIntentCreateParams;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripeSetupIntent;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeSetupIntentRepository;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling payment related operations with Stripe API.
 */
@Service
@AllArgsConstructor
public class PaymentService {

    private final CustomerService customerService;
    private final StripeSetupIntentRepository setupIntentRepository;

    /**
     * Creates and attaches a new payment method to the customer based on the provided token.
     *
     * @param userId The user ID to associate the payment method with.
     * @param token  The token representing the payment information.
     * @return The newly created PaymentMethod.
     * @throws StripeException If an error occurs with the Stripe API call.
     */
    public PaymentMethod createAndAttachPaymentMethod(UserId userId, String token) throws StripeException {
        PaymentMethod paymentMethod = createPaymentMethod(token);
        attachPaymentMethodToCustomer(userId, paymentMethod.getId());
        return paymentMethod;
    }

    /**
     * Creates a SetupIntent for the specified user. This is used for setting up a payment method for future use.
     *
     * @param userId The user ID for whom the SetupIntent is being created.
     * @return The newly created SetupIntent.
     * @throws StripeException If an error occurs with the Stripe API call.
     */
    public SetupIntent createSetupIntentForUser(UserId userId) throws StripeException {
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(userId);
        SetupIntent setupIntent = createSetupIntent(linkedUser.getStripeCustomerId());
        saveSetupIntent(setupIntent, linkedUser);
        return setupIntent;
    }

    /**
     * Attaches a PaymentMethod to the specified customer.
     *
     * @param userId           The user ID of the customer.
     * @param paymentMethodId  The ID of the PaymentMethod to attach.
     * @throws StripeException If an error occurs with the Stripe API call.
     */
    public void attachPaymentMethodToCustomer(UserId userId, String paymentMethodId) throws StripeException {
        Customer customer = customerService.getOrCreate(userId);
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(PaymentMethodAttachParams.builder().setCustomer(customer.getId()).build());
    }

    // Helper methods below

    private PaymentMethod createPaymentMethod(String token) throws StripeException {
        PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.Card.builder().setToken(token).build())
                .build();
        return PaymentMethod.create(params);
    }

    private SetupIntent createSetupIntent(String customerId) throws StripeException {
        SetupIntentCreateParams params = SetupIntentCreateParams.builder().setCustomer(customerId).build();
        return SetupIntent.create(params);
    }

    private void saveSetupIntent(SetupIntent setupIntent, StripeLinkedUser linkedUser) {
        StripeSetupIntent setupIntentEntity = new StripeSetupIntent();
        setupIntentEntity.setId(setupIntent.getId());
        setupIntentEntity.setStripeLinkedUser(linkedUser);
        setupIntentEntity.setStatus(setupIntent.getStatus());
        setupIntentRepository.save(setupIntentEntity);
    }
}