package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import digiot.stwrap.domain.model.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {

    final CustomerService customerService;

    public SetupIntent createSetupIntentWithPaymentMethod(UserId userId, String token) throws Exception {

        // SetupIntentの作成パラメーターを設定
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                .setCustomer(customerService.getOrCreate(userId).getId())
                .setPaymentMethod(createPaymentMethod(token, PaymentMethodCreateParams.Type.CARD).getId())
                .build();

        // SetupIntentを作成
        return SetupIntent.create(params);
    }

    public PaymentMethod createPaymentMethod(String token, PaymentMethodCreateParams.Type type) throws StripeException {

        PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                .setType(type)
                .setCard(PaymentMethodCreateParams.Token.builder()
                        .setToken(token)
                        .build())
                .build();

        return PaymentMethod.create(params);
    }

    /**
     * Updates the default payment method for the Stripe customer associated with the given user ID.
     * This method sets a new default payment source using the provided token.
     *
     * @param userId The ID of the user within your system.
     * @param token  The token representing the payment method to be set as the default.
     * @return String The ID of the default payment source after the update.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public PaymentMethod attachPaymentMethodToCustomerFromToken(UserId userId, String token, PaymentMethodCreateParams.Type type) throws StripeException {
        return attachPaymentMethodToCustomerFromToken(userId, token, type, false);
    }

    public PaymentMethod attachPaymentMethodToCustomerFromToken(UserId userId, String token) throws StripeException {
        return attachPaymentMethodToCustomerFromToken(userId, token, false);
    }

    public PaymentMethod attachPaymentMethodToCustomerFromToken(UserId userId, String token, boolean defaultPaymentMethod) throws StripeException {
        return attachPaymentMethodToCustomerFromToken(userId, token, PaymentMethodCreateParams.Type.CARD, defaultPaymentMethod);
    }

    public PaymentMethod attachPaymentMethodToCustomerFromToken(UserId userId, String token, PaymentMethodCreateParams.Type type, boolean defaultPaymentMethod) throws StripeException {
        PaymentMethod paymentMethod = createPaymentMethod(token, type);
        attachPaymentMethodToCustomer(userId, paymentMethod.getId(), defaultPaymentMethod);
        return paymentMethod;
    }

    public void attachPaymentMethodToCustomer(UserId userId, String paymentMethodId) throws StripeException {
        attachPaymentMethodToCustomer(userId, paymentMethodId, false);
    }

    public void attachPaymentMethodToCustomer(UserId userId, String paymentMethodId, boolean defaultPaymentMethod) throws StripeException {

        Customer customer = customerService.getOrCreate(userId);

        // Retrieve the specified Payment Method from Stripe.
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

        // Check if the Payment Method is already attached to the specified customer.
        if (paymentMethod.getCustomer() != null && paymentMethod.getCustomer().equals(customer.getId())) {
            // The Payment Method is already attached to this customer, so no action is needed.
            return;
        }

        // Prepare the parameters to attach the Payment Method to the customer.
        PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
                .setCustomer(customer.getId())
                .build();

        // Attach the Payment Method to the customer.
        paymentMethod.attach(attachParams);

        if (defaultPaymentMethod) {
            CustomerUpdateParams customerUpdateParams =
                    CustomerUpdateParams.builder()
                            .setSource(paymentMethodId).build();
            customer.update(customerUpdateParams);
        }
    }
}
