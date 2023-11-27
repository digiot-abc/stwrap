package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripePaymentIntent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for a repository handling the Stripe payment intents.
 * Provides methods to manage the lifecycle of payment intents, including creation, read, update, and deletion.
 */
@Repository
public interface StripePaymentIntentRepository extends CrudRepository<StripePaymentIntent, String> {

    /**
     * Finds all payment intents associated with a given Stripe linked user ID.
     *
     * @param stripeLinkedUserId The unique identifier of the Stripe linked user.
     * @return A list of StripePaymentIntent associated with the Stripe linked user ID.
     */
    List<StripePaymentIntent> findByStripeLinkedUserId(String stripeLinkedUserId);
}