package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import digiot.stwrap.infrastructure.helper.StripeTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SpringBootRunner.class)
class SubscriptionServiceTest {

    static {
        StripeApiKeyInitializer.initialize();
    }

    @Autowired
    SubscriptionService service;

    @Autowired
    CreditCardService creditCardService;

    @Test
    void createSubscriptionSuccess() throws StripeException {

        UserId userId = new UserId("testUserId");
        String productId = StripeTestHelper.createTestProduct("prod_test").getId();
        String planId = StripeTestHelper.createTestPlan(productId, 1, "jpy", "month").getId();
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();

        creditCardService.registerCreditCardPaymentMethod(userId, paymentMethodId);

        Subscription subscription = service.createSubscription(userId, planId, paymentMethodId);

        assertNotNull(subscription);
        assertEquals(subscription.getStatus(), "active");
    }

    @Test
    void updateSubscriptionPaymentMethod() throws StripeException {
        UserId userId = new UserId("testUserIdForUpdatePaymentMethod");
        String productId = StripeTestHelper.createTestProduct("prod_test_for_update").getId();
        String planId = StripeTestHelper.createTestPlan(productId, 1, "jpy", "month").getId();
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();
        String newPaymentMethodId = StripeTestHelper.createPaymentMethod().getId();
    
        creditCardService.registerCreditCardPaymentMethod(userId, paymentMethodId);
        Subscription subscription = service.createSubscription(userId, planId, paymentMethodId);
        
        service.updateSubscriptionPaymentMethod(userId, subscription.getId(), newPaymentMethodId);
        Subscription updatedSubscription = Subscription.retrieve(subscription.getId());
    
        assertEquals(newPaymentMethodId, updatedSubscription.getDefaultPaymentMethod());
    }

    @Test
    void cancelSubscription() throws StripeException {
        UserId userId = new UserId("testUserIdForCancel");
        String productId = StripeTestHelper.createTestProduct("prod_test_for_cancel").getId();
        String planId = StripeTestHelper.createTestPlan(productId, 1, "jpy", "month").getId();
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();
    
        creditCardService.registerCreditCardPaymentMethod(userId, paymentMethodId);
        Subscription subscription = service.createSubscription(userId, planId, paymentMethodId);
    
        service.cancelSubscription(userId, subscription.getId());
        Subscription cancelledSubscription = Subscription.retrieve(subscription.getId());
    
        assertEquals("canceled", cancelledSubscription.getStatus());
    }

    @Test
    void applyCoupon() throws StripeException {
        UserId userId = new UserId("testUserIdForCoupon");
        String productId = StripeTestHelper.createTestProduct("prod_test_for_coupon").getId();
        String planId = StripeTestHelper.createTestPlan(productId, 1, "jpy", "month").getId();
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();
        String couponId = StripeTestHelper.createTestCoupon(10, "forever").getId();
    
        creditCardService.registerCreditCardPaymentMethod(userId, paymentMethodId);
        Subscription subscription = service.createSubscription(userId, planId, paymentMethodId);
    
        service.applyCoupon(userId, subscription.getId(), couponId);
        Subscription updatedSubscription = Subscription.retrieve(subscription.getId());
    
        assertEquals(couponId, updatedSubscription.getDiscount().getCoupon().getId());
    }
}