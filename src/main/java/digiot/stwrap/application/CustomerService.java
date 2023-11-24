package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripeLinkedUserFactory;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CustomerService<T> {

    final StripeLinkedUserRepository<T> userLinkRepository;

    public StripeLinkedUser<T> getOrCreateCustomer(T userId) throws StripeException {
        
        // DBからuserIdに対応するlinkを検索
        Optional<StripeLinkedUser<T>> link = userLinkRepository.findPrimaryByUserId(userId);
        
        if (link.isPresent()) {
            // 既存のStripe Customerを取得
            return link.get();
        } 
        
        // 新規のStripe Customerを作成
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setDescription("Customer for user ID: " + userId)
                .build();

        Customer newCustomer = Customer.create(params);

        StripeLinkedUserFactory<T> linkFactory = new StripeLinkedUserFactory<>();
        StripeLinkedUser<T> newLink = linkFactory.create(userId, newCustomer.getId());
        userLinkRepository.insert(newLink);

        return newLink;
    }

    /**
     * Adds a new payment method to the Stripe customer using a token.
     *
     * @param userId The ID of the user.
     * @param token  The token representing the payment method.
     * @return PaymentMethod The attached Stripe PaymentMethod object.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public PaymentMethod addPaymentMethodToCustomer(T userId, String token) throws StripeException {

        StripeLinkedUser<T> linkedUser = getOrCreateCustomer(userId);
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());

        // 顧客にトークンを紐付けて支払い方法を更新
        CustomerUpdateParams customerUpdateParams = CustomerUpdateParams.builder()
                .setDefaultSource(token)
            .build();
        customer.update(customerUpdateParams);

        // 顧客に紐付けられたデフォルトの支払い方法IDを取得
        String paymentMethodId = customer.getDefaultSource();

        // Payment Methodオブジェクトの取得
        return PaymentMethod.retrieve(paymentMethodId);
    }

    /**
     * Attaches a specified Payment Method to a given customer in Stripe, if it's not already attached.
     *
     * @param customerId      The unique identifier of the customer in Stripe to whom the Payment Method is to be attached.
     * @param paymentMethodId The unique identifier of the Payment Method that is to be attached to the customer.
     * @throws StripeException If an error occurs during the API request to Stripe, such as invalid parameters or network issues.
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