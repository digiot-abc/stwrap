package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.SetupIntent;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.SetupIntentCreateParams;
import digiot.stwrap.domain.LinkedUserSpecification;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripeSetupIntent;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.model.factory.StripeSetupIntentFactory;
import digiot.stwrap.domain.repository.StripeSetupIntentRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class PaymentMethodService {

    private final StripeLinkService stripeLinkService;
    private final StripeSetupIntentRepository stripeSetupIntentRepository;

    /**
     * Creates a new SetupIntent for a specified user. This SetupIntent is used to set up a payment method for future use.
     *
     * @param userId The unique identifier of the user for whom the SetupIntent is being created.
     * @return The newly created SetupIntent.
     * @throws StripeException If an error occurs with the Stripe API call.
     */
    public SetupIntent createSetupIntent(UserId userId) throws StripeException {

        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        SetupIntentCreateParams setupIntentCreateParams = SetupIntentCreateParams.builder()
                .setCustomer(Customer.retrieve(linkedUser.getStripeCustomerId()).getId())
                .build();

        SetupIntent setupIntent = SetupIntent.create(setupIntentCreateParams);

        StripeSetupIntent stripeSetupIntent = StripeSetupIntentFactory.create(linkedUser, setupIntent);
        stripeSetupIntentRepository.save(stripeSetupIntent);

        return setupIntent;
    }

    /**
     * Deletes a Stripe SetupIntent associated with a user.
     *
     * @param userId        The ID of the user whose SetupIntent is to be deleted.
     * @param setupIntentId The ID of the SetupIntent to be deleted.
     * @return The deleted SetupIntent object.
     * @throws StripeException       If an error occurs in the Stripe API call.
     * @throws IllegalStateException If an error occurs in the application state.
     */
    public SetupIntent deleteSetupIntent(UserId userId, String setupIntentId) throws StripeException, IllegalStateException {

        // Retrieves or creates a Stripe linked user.
        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        // Retrieves the SetupIntent based on the provided ID.
        SetupIntent setupIntent = SetupIntent.retrieve(setupIntentId);

        // Checks if the SetupIntent belongs to the retrieved customer.
        LinkedUserSpecification.verifyUserAssociation(linkedUser, setupIntent);

        // Retrieves the internal representation of the SetupIntent.
        StripeSetupIntent stripeSetupIntent = stripeSetupIntentRepository.findById(setupIntentId).get();
        stripeSetupIntent.setDeleted(true);

        // Saves the updated state to the repository.
        stripeSetupIntentRepository.save(stripeSetupIntent);

        return setupIntent;
    }

    /**
     * Retrieves and updates the status of all SetupIntents associated with a given user.
     *
     * @param userId The ID of the user whose SetupIntents are to be retrieved and updated.
     * @return A list of updated SetupIntent objects.
     * @throws Exception Propagates any exceptions that occur within the method.
     */
    @SneakyThrows
    public List<SetupIntent> getAndUpdateSetupIntentStatus(UserId userId) {

        // Retrieves or creates a Stripe linked user.
        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);

        // Processes each SetupIntent associated with the user.
        return linkedUser.getSetupIntents().stream()
                .map(intent -> getAndUpdateSetupIntentStatus(userId, intent.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single SetupIntent by its ID and updates its status in the system.
     *
     * @param userId        The ID of the user associated with the SetupIntent.
     * @param setupIntentId The ID of the SetupIntent to retrieve and update.
     * @return The updated SetupIntent object.
     * @throws Exception Propagates any exceptions that occur within the method.
     */
    @SneakyThrows
    public SetupIntent getAndUpdateSetupIntentStatus(UserId userId, String setupIntentId) {

        // Retrieves the SetupIntent from Stripe.
        SetupIntent setupIntent = SetupIntent.retrieve(setupIntentId);

        // Verifies if the SetupIntent belongs to the retrieved user.
        LinkedUserSpecification.verifyUserAssociation(stripeLinkService.getOrCreateStripeLinkedUser(userId), setupIntent);

        // Retrieves the corresponding record from the database.
        StripeSetupIntent stripeSetupIntent = stripeSetupIntentRepository.findById(setupIntentId)
                .orElseThrow(() -> new RuntimeException("SetupIntent not found in the database"));

        // Updates the status and marks as deleted if the payment method setup succeeded.
        if ("succeeded".equals(setupIntent.getStatus())) {
            stripeSetupIntent.setDeleted(true);
        }

        stripeSetupIntent.setStatus(setupIntent.getStatus());
        stripeSetupIntentRepository.save(stripeSetupIntent);

        return setupIntent;
    }

    public List<PaymentMethod> listPaymentMethods(UserId userId) throws StripeException {

        StripeLinkedUser linkedUser = stripeLinkService.getOrCreateStripeLinkedUser(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());

        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customer.getId())
                .setType(PaymentMethodListParams.Type.CARD)
                .build();

        PaymentMethodCollection paymentMethods = PaymentMethod.list(params);

        return paymentMethods.getData();
    }

    /**
     * 新しい支払い方法を追加する
     */
    public PaymentMethod addPaymentMethod(UserId userId, String paymentMethodId, String newPaymentMethodDetails) {
        // 新しい支払い方法の追加
        return null;
    }

    /**
     * 既存の支払い方法を更新する
     */
    public PaymentMethod updatePaymentMethod(UserId userId, String paymentMethodId, String newPaymentMethodDetails) {
        // 既存の支払い方法の更新
        return null;
    }
}

