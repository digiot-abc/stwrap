package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripeSubscription;

import java.util.List;
import java.util.Optional;

/**
 * Interface for a repository handling Stripe subscriptions.
 * Provides methods to manage the lifecycle of Stripe subscriptions, including finding, saving, and deleting.
 */
public interface StripeSubscriptionRepository {

    /**
     * Finds a subscription by its unique identifier.
     *
     * @param id The unique identifier of the subscription.
     * @return An Optional containing the StripeSubscription object if found, or an empty Optional if not found.
     */
    Optional<StripeSubscription> findById(String id);

    /**
     * Finds a subscription by its unique identifier.
     *
     * @param subscriptionId The unique identifier of the Stripe's subscription.
     * @return An Optional containing the StripeSubscription object if found, or an empty Optional if not found.
     */
    Optional<StripeSubscription> findBySubscriptionId(String subscriptionId);

    /**
     * Finds all subscriptions associated with a specific plan ID.
     *
     * @param planId The identifier of the plan for which subscriptions are to be found.
     * @return A list of StripeSubscription objects associated with the plan ID. Returns an empty list if no subscriptions are found.
     */
    List<StripeSubscription> findAllByPlanId(String planId);

    /**
     * Finds all subscriptions associated with a specific Stripe user link ID.
     *
     * @param StripeLinkedUserId The identifier of the Stripe user link for which subscriptions are to be found.
     * @return A list of StripeSubscription objects associated with the Stripe user link ID. Returns an empty list if no subscriptions are found.
     */
    List<StripeSubscription> findAllByStripeLinkedUserId(String StripeLinkedUserId);
   
    /**
     * Inserts a new Stripe subscription into the repository.
     *
     * @param stripeSubscription The StripeSubscription object to insert.
     * @return The number of rows affected.
     */
    int insert(StripeSubscription stripeSubscription);

    /**
     * Updates an existing Stripe subscription in the repository.
     *
     * @param stripeSubscription The StripeSubscription object to update.
     * @return The number of rows affected by the update.
     */
    int update(StripeSubscription stripeSubscription);

    /**
     * Deletes an existing Stripe subscription from the repository.
     *
     * @param stripeSubscription The StripeSubscription object to delete.
     * @return The number of rows affected by the deletion.
     */
    int delete(StripeSubscription stripeSubscription);
}
