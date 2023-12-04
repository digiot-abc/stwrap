package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
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
public class CreditCardService {

    final StripeLinkService linkService;

    /**
     * Registers a credit card to a user's Stripe account using a card token.
     *
     * @param userId    The unique identifier of the user.
     * @param cardToken The token representing the credit card information.
     * @return The attached PaymentMethod object representing the registered credit card.
     * @throws StripeException If an error occurs during communication with the Stripe API.
     */
    @SneakyThrows(StripeException.class)
    public PaymentMethod registerCreditCardToken(UserId userId, String cardToken) {

        StripeLinkedUser linkedUser = linkService.getOrCreateStripeLinkedUser(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());

        PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.Token.builder()
                        .setToken(cardToken)
                        .build())
                .build();

        PaymentMethod paymentMethod = PaymentMethod.create(params);

        return paymentMethod.attach(PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build());
    }

    /**
     * Registers a credit card to a user's Stripe account using a payment method ID.
     *
     * @param userId           The unique identifier of the user.
     * @param paymentMethodId  The ID of the payment method to be registered.
     * @return The attached PaymentMethod object representing the registered credit card.
     * @throws StripeException If an error occurs during communication with the Stripe API.
     */
    @SneakyThrows(StripeException.class)
    public PaymentMethod registerCreditCardPaymentMethod(UserId userId, String paymentMethodId) {

        StripeLinkedUser linkedUser = linkService.getOrCreateStripeLinkedUser(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        return paymentMethod.attach(PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build());
    }

    /**
     * Deletes a credit card from a user's Stripe account.
     *
     * @param userId               The unique identifier of the user.
     * @param cardPaymentMethodId  The ID of the credit card payment method to be deleted.
     * @return The detached PaymentMethod object representing the deleted credit card.
     * @throws StripeException If an error occurs during communication with the Stripe API.
     */
    @SneakyThrows(StripeException.class)
    public PaymentMethod deleteCreditCard(UserId userId, String cardPaymentMethodId) {
        StripeLinkedUser linkedUser = linkService.getOrCreateStripeLinkedUser(userId);
        PaymentMethod paymentMethod = PaymentMethod.retrieve(cardPaymentMethodId);
        LinkedUserSpecification.verifyUserAssociation(linkedUser, paymentMethod);
        return paymentMethod.detach();
    }

    /**
     * Updates a credit card on a user's Stripe account by replacing an old card with a new one.
     *
     * @param userId                The unique identifier of the user.
     * @param cardPaymentMethodId   The ID of the existing credit card payment method to be replaced.
     * @param newCardToken          The token of the new credit card to be registered.
     * @return The attached PaymentMethod object representing the newly registered credit card.
     * @throws StripeException If an error occurs during communication with the Stripe API.
     */
    @SneakyThrows(StripeException.class)
    public PaymentMethod updateCreditCard(UserId userId, String cardPaymentMethodId, String newCardToken) {

        StripeLinkedUser linkedUser = linkService.getOrCreateStripeLinkedUser(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());

        // 既存のカードを削除
        PaymentMethod oldCard = PaymentMethod.retrieve(cardPaymentMethodId);
        LinkedUserSpecification.verifyUserAssociation(linkedUser, oldCard);
        deleteCreditCard(userId, oldCard.getId());

        // 新しいカードを登録
        PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.Token.builder()
                        .setToken(newCardToken)
                        .build())
                .build();

        PaymentMethod newCard = PaymentMethod.create(params);

        return newCard.attach(PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build());
    }

}
