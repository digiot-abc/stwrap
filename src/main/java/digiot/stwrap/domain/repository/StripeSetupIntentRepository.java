package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripeSetupIntent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for a repository handling the Stripe setup intents.
 * Provides methods to manage the lifecycle of setup intents, including creation, read, update, and deletion.
 */
@Repository
public interface StripeSetupIntentRepository extends CrudRepository<StripeSetupIntent, String> {

    /**
     * Finds all setup intents associated with a given Stripe linked user ID.
     *
     * @param stripeLinkedUserId The unique identifier of the Stripe linked user.
     * @return A list of StripeSetupIntent associated with the Stripe linked user ID.
     */
    List<StripeSetupIntent> findByStripeLinkedUserId(String stripeLinkedUserId);
}