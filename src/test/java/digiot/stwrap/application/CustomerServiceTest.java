package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Token;
import com.stripe.param.CustomerCreateParams;
import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import digiot.stwrap.infrastructure.PropertiesLoader;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import digiot.stwrap.infrastructure.helper.StripeTestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootRunner.class)
@TestPropertySource(locations = "classpath:spring/user_id_int/application.properties")
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    StripeLinkedUserRepository repository;

    @BeforeAll
    public static void setUp() {
        StripeApiKeyInitializer.initialize();
        PropertiesLoader.load("spring/user_id_int/stwrap.properties");
    }

    @Test
    void getOrCreate() throws StripeException {
        UserId userId = UserId.valueOf(1);
        Customer customer = customerService.getOrCreate(UserId.valueOf(userId));
        try {
            Optional<StripeLinkedUser> linkedUser = repository.findByUserId(userId);
            assertEquals(1, linkedUser.get().getUserId().getValue());
            assertEquals(customer.getId(), linkedUser.get().getStripeCustomerId());
        } finally {
            customer.delete();
        }
    }

    @Test
    void getOrCreateStripeLinkedUser() throws StripeException {
        UserId userId = UserId.valueOf(2);
        StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(userId));
        Customer customer = Customer.retrieve(linkedUser.getStripeCustomerId());
        try {
            Optional<StripeLinkedUser> fetchedLinkedUser = repository.findById(linkedUser.getId());
            assertEquals(linkedUser.getId(), fetchedLinkedUser.get().getId());
            assertEquals(2, fetchedLinkedUser.get().getUserId().getValue());
            assertEquals(customer.getId(), fetchedLinkedUser.get().getStripeCustomerId());
        } finally {
            customer.delete();
        }
    }

    @Test
    void linkStripeCustomer() throws StripeException {

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName("name1").build();

        Customer customer = Customer.create(params);

        try {
            StripeLinkedUser linkedUser = customerService.linkStripeCustomer(UserId.valueOf(100), customer);
            Optional<StripeLinkedUser> fetchedLinkedUser = repository.findById(linkedUser.getId());
            assertEquals(linkedUser.getId(), fetchedLinkedUser.get().getId());
            assertEquals(100, fetchedLinkedUser.get().getUserId().getValue());
            assertEquals(customer.getId(), fetchedLinkedUser.get().getStripeCustomerId());
            assertEquals("name1", customer.getName());
        } finally {
            customer.delete();
        }
    }
//
//    @Test
//    void listCardPaymentMethods() throws StripeException {
//
//        Customer customer = StripeTestHelper.createTestCustomer("mail@excample.com");
//        Token token = StripeTestHelper.createTestToken();
//        PaymentMethod paymentMethod = StripeTestHelper.attachTokenToCustomer(customer, token);
//
//        try {
//            UserId userId = UserId.valueOf(123);
//            customerService.linkStripeCustomer(userId, customer);
//            List<PaymentMethod> paymentMethodList = customerService.listCardPaymentMethods(userId);
//            assertEquals(paymentMethod.getId(), paymentMethodList.get(0).getId());
//        } finally {
//            customer.delete();
//        }
//    }
//
//    @Test
//    void attachPaymentMethodToCustomerFromToken() throws StripeException {
//
//        Customer customer = StripeTestHelper.createTestCustomer("mail@excample.com");
//        Token token = StripeTestHelper.createTestToken();
//
//        try {
//            UserId userId = UserId.valueOf(123);
//            PaymentMethod paymentMethod = customerService.attachPaymentMethodToCustomerFromToken(userId, token.getId(), true);
//            assertEquals(paymentMethod.getId(), customerService.getOrCreate(userId).getDefaultSource());
//        } finally {
//            customer.delete();
//        }
//    }
//
//    @Test
//    void attachPaymentMethodToCustomerFromToken2() throws StripeException {
//
//        Customer customer = StripeTestHelper.createTestCustomer("mail@excample.com");
//        Token token = StripeTestHelper.createTestToken();
//
//        try {
//            UserId userId = UserId.valueOf(123);
//            PaymentMethod paymentMethod = customerService.attachPaymentMethodToCustomerFromToken(userId, token.getId(), false);
//
//            Token token2 = StripeTestHelper.createTestToken();
//            PaymentMethod paymentMethod2 = customerService.attachPaymentMethodToCustomerFromToken(userId, token2.getId(), false);
//
//            assertEquals(2, customerService.listCardPaymentMethods(userId).size());
//            assertNotEquals(paymentMethod.getId(), paymentMethod2.getId());
//        } finally {
//            customer.delete();
//        }
//    }
//
//    @Test
//    void attachPaymentMethodToCustomer() throws StripeException {
//
//        UserId userId = UserId.valueOf(999);
//        Customer customer = customerService.getOrCreate(userId);
//        PaymentMethod paymentMethod = StripeTestHelper.createPaymentMethod();
//
//        try {
//            customerService.attachPaymentMethodToCustomer(userId, paymentMethod.getId());
//            assertNull(customerService.getOrCreate(userId).getDefaultSource());
//            assertEquals(paymentMethod.getId(), customerService.listCardPaymentMethods(userId).get(0).getId());
//        } finally {
//            customer.delete();
//        }
//    }
//
//    @Test
//    void attachPaymentMethodToCustomer2() throws StripeException {
//
//        UserId userId = UserId.valueOf(999);
//        Customer customer = customerService.getOrCreate(userId);
//        PaymentMethod paymentMethod = StripeTestHelper.createPaymentMethod();
//
//        try {
//            customerService.attachPaymentMethodToCustomer(userId, paymentMethod.getId());
//            assertNull(customerService.getOrCreate(userId).getDefaultSource());
//            assertEquals(paymentMethod.getId(), customerService.listCardPaymentMethods(userId).get(0).getId());
//        } finally {
//            customer.delete();
//        }
//    }
}