package digiot.stwrap.domain;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.SetupIntent;
import com.stripe.model.Subscription;
import digiot.stwrap.domain.model.StripeLinkedUser;

public class LinkedUserSpecification {

    public static void verifyUserAssociation(StripeLinkedUser user, Subscription subscription) throws StripeException {
        if (!subscription.getCustomer().equals(Customer.retrieve(user.getStripeCustomerId()).getId())) {
            throw new IllegalStateException("Subscription does not belong to the customer");
        }
    }

    public static void verifyUserAssociation(StripeLinkedUser user, SetupIntent setupIntent) throws StripeException {
        if (!setupIntent.getCustomer().equals(Customer.retrieve(user.getStripeCustomerId()).getId())) {
            throw new IllegalStateException("SetupIntent does not belong to the customer");
        }
    }
    public static void verifyUserAssociation(StripeLinkedUser user, PaymentMethod paymentMethod) throws StripeException {
        if (!paymentMethod.getCustomer().equals(Customer.retrieve(user.getStripeCustomerId()).getId())) {
            throw new IllegalStateException("PaymentMethod does not belong to the customer");
        }
    }
}
