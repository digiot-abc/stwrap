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
    void updateSubscriptionPaymentMethod() {
    }

    @Test
    void cancelSubscription() {
    }

    @Test
    void applyCoupon() {
    }
}