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

    @SneakyThrows(StripeException.class)
    public PaymentMethod registerCreditCardPaymentMethod(UserId userId, String paymentMethodId) {

        StripeLinkedUser linkedUser = linkService.getOrCreateStripeLinkedUser(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        return paymentMethod.attach(PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build());
    }
    @SneakyThrows(StripeException.class)
    public PaymentMethod deleteCreditCard(UserId userId, String cardPaymentMethodId) {
        StripeLinkedUser linkedUser = linkService.getOrCreateStripeLinkedUser(userId);
        PaymentMethod paymentMethod = PaymentMethod.retrieve(cardPaymentMethodId);
        LinkedUserSpecification.verifyUserAssociation(linkedUser, paymentMethod);
        return paymentMethod.detach();
    }

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
