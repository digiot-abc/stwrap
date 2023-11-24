// package digiot.stwrap.application;

// import com.stripe.model.Product;
// import com.stripe.model.Subscription;
// import com.stripe.model.Token;
// import com.stripe.model.Customer;
// import com.stripe.model.Plan;

// import digiot.stwrap.domain.model.StripeLinkedUser;
// import digiot.stwrap.domain.model.StripeSubscription;
// import digiot.stwrap.domain.repository.StripeSubscriptionRepository;
// import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
// import digiot.stwrap.domain.repository.impl.DefaultStripeSubscriptionRepository;
// import digiot.stwrap.helper.StripeTestHelper;
// import digiot.stwrap.domain.repository.impl.DefaultStripeLinkedUserRepository;
// import digiot.stwrap.infrastructure.DataSourceProvider;

// import com.stripe.exception.StripeException;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.AfterEach;

// import static org.junit.jupiter.api.Assertions.*;

// import java.time.Instant;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class SubscriptionServiceTest {

//     private SubscriptionService<String> subscriptionService;

//     @BeforeEach
//     void setUp() throws StripeException {

//         // リポジトリの実際のインスタンスを生成
//         StripeLinkedUserRepository<String> userLinkRepository = new DefaultStripeLinkedUserRepository<>(DataSourceProvider.getDataSource());
//         StripeSubscriptionRepository subscriptionRepository = new DefaultStripeSubscriptionRepository(DataSourceProvider.getDataSource());
//         CustomerService<String> customerService = new CustomerService<>(userLinkRepository);

//         // SubscriptionServiceのインスタンスを生成
//         subscriptionService = new SubscriptionService<>(customerService, subscriptionRepository);
//     }

//     @AfterEach
//     void tearDownEach() throws StripeException {
//         StripeTestHelper.clean();
//     }

//     @Test
//     void createSubscriptionWithPaymentMethodId_successful() throws StripeException {
//         Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 1", 1000, "usd", "month");
//         Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");
//         Token token = StripeTestHelper.createTestToken();

//         String userId = testCustomer.getId();
//         String planId = testPlan.getId();
//         int quantity = 1;

//         Subscription subscription = subscriptionService.createSubscriptionWithPaymentMethodId(userId, planId, paymentMethodId, quantity);
//         assertNotNull(subscription);
//         assertEquals("active", subscription.getStatus());
//     }

//     @Test
//     void createSubscriptionWithToken_successful() throws StripeException {
//         Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 2", 1000, "usd", "month");
//         String token = StripeTestHelper.createTestToken().getId();
//         String userId = "testUserId"; // Stripeの顧客IDを使用する場合は適切に設定
//         String planId = testPlan.getId();
//         int quantity = 1;

//         Subscription subscription = subscriptionService.createSubscriptionWithToken(userId, planId, token, quantity);
//         assertNotNull(subscription);
//         assertEquals("active", subscription.getStatus());
//     }

//     @Test
//     void applyCouponToSubscription_successful() throws StripeException {
//         Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 3", 1000, "usd", "month");
//         Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");
//         Subscription testSubscription = createTestSubscription(testCustomer.getId(), testPlan.getId(), 1);
//         String subscriptionId = testSubscription.getId();
//         Coupon testCoupon = StripeTestHelper.createTestCoupon(10, "once"); // 10%オフのクーポン

//         Subscription subscription = subscriptionService.applyCouponToSubscription(subscriptionId, testCoupon.getId());
//         assertNotNull(subscription);
//         // クーポン適用後のアサーション
//     }

//     @Test
//     void cancelSubscriptionAtDate_successful() throws StripeException {
//         Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 4", 1000, "usd", "month");
//         Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");
//         Subscription testSubscription = createTestSubscription(testCustomer.getId(), testPlan.getId(), 1);
//         String subscriptionId = testSubscription.getId();
//         Instant cancelAt = Instant.now().plusSeconds(3600); // 1時間後

//         Subscription subscription = subscriptionService.cancelSubscriptionAtDate(subscriptionId, cancelAt);
//         assertNotNull(subscription);
//         assertEquals(cancelAt.getEpochSecond(), subscription.getCancelAt());
//     }

//     @Test
//     void cancelSubscriptionAtPeriodEnd_successful() throws StripeException {
//         Plan testPlan = StripeTestHelper.createTestPlan("Test Plan 5", 1000, "usd", "month");
//         Customer testCustomer = StripeTestHelper.createTestCustomer("test@example.com");
//         Subscription testSubscription = createTestSubscription(testCustomer.getId(), testPlan.getId(), 1);
//         String subscriptionId = testSubscription.getId();

//         Subscription subscription = subscriptionService.cancelSubscriptionAtPeriodEnd(subscriptionId);
//         assertNotNull(subscription);
//         assertTrue(subscription.getCancelAtPeriodEnd());
//     }
// }