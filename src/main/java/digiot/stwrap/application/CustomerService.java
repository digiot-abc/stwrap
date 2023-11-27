package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.SetupIntent;
import com.stripe.param.*;
import digiot.stwrap.domain.customer.StripeLinkedUserFactory;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    final StripeLinkedUserRepository userLinkRepository;

    public Customer getOrCreate(UserId userId) throws StripeException {
        return Customer.retrieve(getOrCreateStripeLinkedUser(userId).getStripeCustomerId());
    }

    /**
     * Retrieves an existing StripeLinkedUser or creates a new one if it doesn't exist.
     *
     * @param userId The unique identifier of the user within your system.
     * @return StripeLinkedUser The retrieved or newly created StripeLinkedUser.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public StripeLinkedUser getOrCreateStripeLinkedUser(UserId userId) throws StripeException {

        Optional<StripeLinkedUser> link = userLinkRepository.findByUserId(userId);
        
        if (link.isPresent()) {
            return link.get();
        }

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(userId.toString())
                .setDescription("Customer for user ID: " + userId.getValue())
                .build();

        Customer newCustomer = Customer.create(params);
    
        SetupIntentCreateParams setupIntentCreateParams = SetupIntentCreateParams.builder()
                .setCustomer(newCustomer.getId())
                .build();

        SetupIntent setupIntent = SetupIntent.create(setupIntentCreateParams);

        return linkStripeCustomer(userId, newCustomer);
    }

    /**
     * Links a Stripe customer to an existing user.
     *
     * @param userId   The unique identifier of the user within your system.
     * @param customer The Stripe Customer object to be linked to the user.
     * @return StripeLinkedUser The newly created StripeLinkedUser representing the link.
     * @throws StripeException If there is an issue with the Stripe API call.
     */
    public StripeLinkedUser linkStripeCustomer(UserId userId, Customer customer) throws StripeException {

        StripeLinkedUserFactory linkFactory = new StripeLinkedUserFactory();
        StripeLinkedUser link = linkFactory.create(userId, customer.getId());
        userLinkRepository.save(link);

        return link;
    }

}