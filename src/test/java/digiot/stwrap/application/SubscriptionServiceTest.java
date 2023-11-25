package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.InvoiceUpcomingParams;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import digiot.stwrap.domain.repository.impl.DefaultStripeLinkedUserRepository;
import digiot.stwrap.infrastructure.DataSourceProvider;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import digiot.stwrap.infrastructure.helper.StripeTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionServiceTest {

    StripeLinkedUserRepository userLinkRepository = new DefaultStripeLinkedUserRepository(DataSourceProvider.getDataSource());
    CustomerService customerService = new CustomerService(userLinkRepository);
    SubscriptionService subscriptionService = new SubscriptionService(customerService);

    @BeforeAll
    static void setUpAll() {
        StripeApiKeyInitializer.initialize();
    }

    @AfterEach
    void tearDownEach() throws StripeException {
        StripeTestHelper.clean();
        userLinkRepository.findAll().forEach(u -> {
            try {
                Customer.retrieve(u.getStripeCustomerId()).delete();
            } catch (StripeException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void createSubscriptionWithPaymentMethodId_successful() throws StripeException {

        String userId = "service_user1";
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));
        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");
        Token token = StripeTestHelper.createTestToken();

        String paymentMethodId = StripeTestHelper.attachTokenToCustomer(Customer.retrieve(linkedUser.getStripeCustomerId()), token).getId();

        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithPaymentMethodId(UserId.valueOf(userId), planId, paymentMethodId, quantity);
        assertNotNull(subscription);
        assertEquals("active", subscription.getStatus());
    }

    @Test
    void createSubscriptionWithToken_successful() throws StripeException {

        String userId = "service_user2";
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));
        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");
        String token = StripeTestHelper.createTestToken().getId();
        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithToken(linkedUser.getUserId(), planId, token, quantity);
        assertNotNull(subscription);
        assertEquals("active", subscription.getStatus());
    }

    @Test
    void applyCouponToSubscription_successful() throws StripeException {

        // Create customer
        String userId = "service_user3";
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));

        // Create product
        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");

        // Create subscription
        Token token = StripeTestHelper.createTestToken();
        Coupon testCoupon = StripeTestHelper.createTestCoupon(10, "once"); // 10%オフのクーポン

        // Apply the coupon to the subscription
        Subscription subscription = subscriptionService.createSubscriptionWithToken(linkedUser.getUserId(), testPlan.getId(), token.getId(), 1);
        subscription = subscriptionService.applyCouponToSubscription(subscription.getId(), testCoupon.getId());
        assertNotNull(subscription);

        // Retrieve the upcoming invoice for the subscription
        InvoiceUpcomingParams invoiceParams = InvoiceUpcomingParams.builder()
                .setCustomer(linkedUser.getStripeCustomerId())
                .setSubscription(subscription.getId())
                .build();
        Invoice upcomingInvoice = Invoice.upcoming(invoiceParams);

        // Calculate the expected amount (10% off the plan's amount)
        long expectedAmountDue = (long) (testPlan.getAmount() - (testPlan.getAmount() * 0.10));

        // Assert the next billing amount is 10% off
        assertEquals(expectedAmountDue, upcomingInvoice.getAmountDue());

        System.out.println(upcomingInvoice.getTotal());
        System.out.println(upcomingInvoice.getAmountDue());
    }

    @Test
    void cancelSubscriptionAtDate_successful() throws StripeException {

        String userId = "service_user4";
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));
        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");
        Token token = StripeTestHelper.createTestToken();

        String paymentMethodId = StripeTestHelper.attachTokenToCustomer(Customer.retrieve(linkedUser.getStripeCustomerId()), token).getId();

        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithPaymentMethodId(UserId.valueOf(userId), planId, paymentMethodId, quantity);
        assertEquals("active", subscription.getStatus());

        OffsetDateTime cancelAt = OffsetDateTime.now().plusMonths(1L);
        subscription = subscriptionService.cancelSubscriptionAtDate(subscription.getId(), cancelAt);
        assertEquals(cancelAt.toInstant().getEpochSecond(), subscription.getCancelAt());
    }

    @Test
    void cancelSubscriptionMidTerm_successful() throws StripeException {

        String userId = "service_user5";
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));
        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");
        Token token = StripeTestHelper.createTestToken();

        String paymentMethodId = StripeTestHelper.attachTokenToCustomer(Customer.retrieve(linkedUser.getStripeCustomerId()), token).getId();

        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithPaymentMethodId(UserId.valueOf(userId), planId, paymentMethodId, quantity);
        assertEquals("active", subscription.getStatus());

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime cancelAt = now.plusDays(15);
        subscription = subscriptionService.cancelSubscriptionAtDate(subscription.getId(), cancelAt);
        assertEquals(cancelAt.toInstant().getEpochSecond(), subscription.getCancelAt());

        // Retrieve the upcoming invoice for the subscription
        InvoiceUpcomingParams invoiceParams = InvoiceUpcomingParams.builder()
                .setCustomer(linkedUser.getStripeCustomerId())
                .build();
        Invoice upcomingInvoice = Invoice.upcoming(invoiceParams);

        System.out.println(upcomingInvoice.getTotal());
        System.out.println(upcomingInvoice.getAmountDue());
    }

    @Test
    void cancelSubscriptionAtPeriodEnd_successful() throws StripeException {

        String userId = "service_user6";
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));
        Product testProduct = StripeTestHelper.createTestProduct("Product");
        Plan testPlan = StripeTestHelper.createTestPlan(testProduct.getId(), 1000, "usd", "month");
        Token token = StripeTestHelper.createTestToken();

        String paymentMethodId = StripeTestHelper.attachTokenToCustomer(Customer.retrieve(linkedUser.getStripeCustomerId()), token).getId();

        String planId = testPlan.getId();
        int quantity = 1;

        Subscription subscription = subscriptionService.createSubscriptionWithPaymentMethodId(UserId.valueOf(userId), planId, paymentMethodId, quantity);
        assertEquals("active", subscription.getStatus());

        subscription = subscriptionService.cancelSubscriptionAtPeriodEnd(subscription.getId());
        assertTrue(subscription.getCancelAtPeriodEnd());

        System.out.println(subscription.getCancelAt());
    }
}