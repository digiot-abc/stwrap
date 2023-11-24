package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import digiot.stwrap.domain.repository.StripeSubscriptionRepository;
import digiot.stwrap.domain.repository.impl.DefaultStripeLinkedUserRepository;
import digiot.stwrap.domain.repository.impl.DefaultStripeSubscriptionRepository;
import digiot.stwrap.helper.StripeTestHelper;
import digiot.stwrap.infrastructure.DataSourceProvider;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionServiceTest {

    private SubscriptionService<String> subscriptionService;

    // リポジトリの実際のインスタンスを生成
    StripeLinkedUserRepository<String> userLinkRepository = new DefaultStripeLinkedUserRepository<>(DataSourceProvider.getDataSource());
    StripeSubscriptionRepository subscriptionRepository = new DefaultStripeSubscriptionRepository(DataSourceProvider.getDataSource());
    CustomerService<String> customerService = new CustomerService<>(userLinkRepository);

    @BeforeAll
    static void setUpAll() {
        StripeApiKeyInitializer.initialize();
    }

    @BeforeEach
    void setUp() throws StripeException {

        // SubscriptionServiceのインスタンスを生成
        subscriptionService = new SubscriptionService<>(customerService, userLinkRepository, subscriptionRepository);
    }

    @AfterEach
    void tearDownEach() throws StripeException {
        StripeTestHelper.clean();
    }

    @Test
    void createSubscriptionWithPaymentMethodId_successful() throws StripeException {

        String userId = "service_user";

        StripeLinkedUser<?> linkedUser = customerService.getOrCreateCustomer(userId);

        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");
        Token token = StripeTestHelper.createTestToken();

        String paymentMethodId = StripeTestHelper.attachTokenToCustomer(Customer.retrieve(linkedUser.getStripeCustomerId()), token).getId();

        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithPaymentMethodId(userId, planId, paymentMethodId, quantity);
        assertNotNull(subscription);
        assertEquals("active", subscription.getStatus());
    }

    @Test
    void createSubscriptionWithToken_successful() throws StripeException {
        Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 2", 1000, "usd", "month");
        String token = StripeTestHelper.createTestToken().getId();
        String userId = "testUserId"; // Stripeの顧客IDを使用する場合は適切に設定
        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithToken(userId, planId, token, quantity);
        assertNotNull(subscription);
        assertEquals("active", subscription.getStatus());
    }

    @Test
    void applyCouponToSubscription_successful() throws StripeException {
        Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 3", 1000, "usd", "month");
        Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");

        Product product = StripeTestHelper.createTestProduct("Product");
        Plan plan = StripeTestHelper.createTestPlan(product.getId(), 1, "usd", "month");

        String subscriptionId = StripeTestHelper.createSubscription(testCustomer.getId(), plan.getId()).getId();
        Coupon testCoupon = StripeTestHelper.createTestCoupon(10, "once"); // 10%オフのクーポン

        Subscription subscription = subscriptionService.applyCouponToSubscription(subscriptionId, testCoupon.getId());
        assertNotNull(subscription);
    }

    @Test
    void cancelSubscriptionAtDate_successful() throws StripeException {
        Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 4", 1000, "usd", "month");
        Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");
        Subscription testSubscription = StripeTestHelper.createSubscription(testCustomer.getId(), testPlan.getId());
        String subscriptionId = testSubscription.getId();
        Instant cancelAt = Instant.now().plusSeconds(3600); // 1時間後

        Subscription subscription = subscriptionService.cancelSubscriptionAtDate(subscriptionId, cancelAt);
        assertNotNull(subscription);
        assertEquals(cancelAt.getEpochSecond(), subscription.getCancelAt());
    }

    @Test
    void cancelSubscriptionAtPeriodEnd_successful() throws StripeException {
        Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 5", 1000, "usd", "month");
        Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");
        Subscription testSubscription = StripeTestHelper.createSubscription(testCustomer.getId(), testPlan.getId());
        String subscriptionId = testSubscription.getId();

        Subscription subscription = subscriptionService.cancelSubscriptionAtPeriodEnd(subscriptionId);
        assertNotNull(subscription);
        assertTrue(subscription.getCancelAtPeriodEnd());
    }
}