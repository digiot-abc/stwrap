package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import digiot.stwrap.infrastructure.helper.StripeTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.UndeclaredThrowableException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootRunner.class)
class CreditCardServiceTest {

    static {
        StripeApiKeyInitializer.initialize();
    }

    @Autowired
    CreditCardService service;

    @Test
    void registerCreditCardToken() throws StripeException {

        UserId userId = new UserId("testUserId");
        String token = StripeTestHelper.createTestToken().getId();
        PaymentMethod paymentMethod = service.registerCreditCardToken(userId, token);

        assertEquals("testUserId", Customer.retrieve(paymentMethod.getCustomer()).getName());
    }


    @Test
    void registerCreditCardPaymentMethod() throws StripeException {

        UserId userId = new UserId("testUserId");
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();
        PaymentMethod paymentMethod = service.registerCreditCardPaymentMethod(userId, paymentMethodId);

        assertEquals("testUserId", Customer.retrieve(paymentMethod.getCustomer()).getName());
    }

    @Test
    void registerCreditCardPaymentMethod_別の人のクレジットカードがすでにアタッチされている場合() throws StripeException {

        UserId userId1 = new UserId("testUserId1");
        UserId userId2 = new UserId("testUserId2");
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();
        service.registerCreditCardPaymentMethod(userId1, paymentMethodId);
        try {
            service.registerCreditCardPaymentMethod(userId2, paymentMethodId);
            fail();
        } catch (UndeclaredThrowableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteCreditCard() throws StripeException {

        UserId userId = new UserId("testUserId");
        String paymentMethodId = StripeTestHelper.createPaymentMethod().getId();
        PaymentMethod paymentMethod = service.registerCreditCardPaymentMethod(userId, paymentMethodId);

        assertEquals("testUserId", Customer.retrieve(paymentMethod.getCustomer()).getName());

        PaymentMethod deleted = service.deleteCreditCard(userId, paymentMethodId);

        assertNull(deleted.getCustomer());
    }

    @Test
    void updateCreditCard() throws StripeException {

        UserId userId = new UserId("testUserId");
        String paymentMethodId1 = StripeTestHelper.createPaymentMethod().getId();
        PaymentMethod paymentMethod1 = service.registerCreditCardPaymentMethod(userId, paymentMethodId1);

        assertEquals("testUserId", Customer.retrieve(paymentMethod1.getCustomer()).getName());

        String token = StripeTestHelper.createTestToken().getId();
        PaymentMethod paymentMethod2 = service.updateCreditCard(userId, paymentMethodId1, token);

        assertEquals("testUserId", Customer.retrieve(paymentMethod2.getCustomer()).getName());
        assertNull(PaymentMethod.retrieve(paymentMethod1.getId()).getCustomer());
    }
}