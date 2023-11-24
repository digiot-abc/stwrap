package digiot.stwrap.helper;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.SubscriptionCreateParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StripeTestHelper {

    private static final List<Customer> createdCustomers = new ArrayList<>();
    private static final List<Product> createdProducts = new ArrayList<>();
    private static final List<Subscription> createdSubscriptions = new ArrayList<>();
    private static final List<Plan> createdPlans = new ArrayList<>();
    private static final List<Coupon> createdCoupons = new ArrayList<>();
    private static final List<PaymentMethod> createdPaymentMethods = new ArrayList<>();

    public static Customer createTestCustomer(String email) throws StripeException {
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("email", email);
        Customer customer = Customer.create(customerParams);
        createdCustomers.add(customer);
        return customer;
    }

    public static Token createTestToken() throws StripeException {
        return Token.retrieve("tok_visa");
    }

    // テスト実施不可
    public static PaymentMethod attachTokenToCustomer(Customer customer, Token token) throws StripeException {

        CustomerUpdateParams customerUpdateParams = CustomerUpdateParams.builder()
                .setSource(token.getId())
                .build();
        customer.update(customerUpdateParams);

        // 更新された顧客情報を取得
        customer = Customer.retrieve(customer.getId());
        String paymentMethodId = customer.getDefaultSource();
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        createdPaymentMethods.add(paymentMethod);
        return paymentMethod;
    }

    public static Product createTestProduct(String name) throws StripeException {
        Map<String, Object> productParams = new HashMap<>();
        productParams.put("name", name);
        Product product = Product.create(productParams);
        createdProducts.add(product);
        return product;
    }

    public static Plan createTestPlan(String productId, long amount, String currency, String interval) throws StripeException {
        Map<String, Object> planParams = new HashMap<>();
        planParams.put("amount", amount);
        planParams.put("currency", currency);
        planParams.put("interval", interval);
        planParams.put("product", productId);
        Plan plan = Plan.create(planParams);
        createdPlans.add(plan);
        return plan;
    }

    public static Coupon createTestCoupon(int percentOff, String duration) throws StripeException {
        Map<String, Object> couponParams = new HashMap<>();
        couponParams.put("percent_off", percentOff);
        couponParams.put("duration", duration);
        Coupon coupon = Coupon.create(couponParams);
        createdCoupons.add(coupon);
        return coupon;
    }

    public static Subscription createSubscription(String customerId, String planId) throws StripeException {
        SubscriptionCreateParams.Item item = SubscriptionCreateParams.Item.builder()
                .setPlan(planId)
                .setQuantity(1L)
                .build();

        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(item)
                .build();

        Subscription subscription = Subscription.create(params);
        createdSubscriptions.add(subscription);
        return subscription;
    }

    public static void clean() throws StripeException {
        for (Coupon coupon : createdCoupons) {
            coupon.delete();
        }
        createdCoupons.clear();

        for (Plan plan : createdPlans) {
            plan.delete();
        }
        createdPlans.clear();

        for (Product product : createdProducts) {
            product.delete();
        }
        createdProducts.clear();

        for (Subscription subscription : createdSubscriptions) {
            subscription.cancel();
        }
        createdSubscriptions.clear();

        for (PaymentMethod paymentMethod : createdPaymentMethods) {
            paymentMethod.detach();
        }
        createdPaymentMethods.clear();

        for (Customer customer : createdCustomers) {
            customer.delete();
        }
        createdCustomers.clear();
    }
}
