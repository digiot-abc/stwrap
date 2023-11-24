package digiot.stwrap.application;

import java.util.Optional;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Token;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripeLinkedUserFactory;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomerService<T> {

    final StripeLinkedUserRepository<T> userLinkRepository;

    public Customer getOrCreateCustomer(T userId) throws StripeException {
        
        // DBからuserIdに対応するlinkを検索
        Optional<StripeLinkedUser<T>> link = userLinkRepository.findPrimaryByUserId(userId);
        
        if (link.isPresent()) {
            // 既存のStripe Customerを取得
            return Customer.retrieve(link.get().getStripeCustomerId());
        } 
        
        // 新規のStripe Customerを作成
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setDescription("Customer for user ID: " + userId)
                .build();

        Customer newCustomer = Customer.create(params);

        StripeLinkedUserFactory<T> linkFactory = new StripeLinkedUserFactory<>();
        StripeLinkedUser<T> newLink = linkFactory.create(userId, newCustomer.getId());
        userLinkRepository.insert(newLink);

        return newCustomer;
    }

    /**
     * Adds a new payment method to the Stripe customer using a token.
     *
     * @param userId The ID of the user.
     * @param token  The token representing the payment method.
     * @return PaymentMethod The attached Stripe PaymentMethod object.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public PaymentMethod addPaymentMethodToCustomer(T userId, Token token) throws StripeException {

        Customer customer = getOrCreateCustomer(userId);

        // 顧客にトークンを紐付けて支払い方法を更新
        CustomerUpdateParams customerUpdateParams = CustomerUpdateParams.builder()
            .setDefaultSource(token.getId())
            .build();
        customer.update(customerUpdateParams);

        // 顧客に紐付けられたデフォルトの支払い方法IDを取得
        String paymentMethodId = customer.getDefaultSource();

        // Payment Methodオブジェクトの取得
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        return paymentMethod;
    }
}