package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripeLinkedUser;

import java.util.List;
import java.util.Optional;

/**
 * Interface for a repository handling the links between user accounts and Stripe customer IDs.
 * Provides methods to manage the lifecycle of these links, including creation, update, retrieval, and deletion.
 */
public interface StripeLinkedUserRepository<T> {

    /**
     * Finds the primary Stripe customer ID link associated with a given user ID.
     *
     * @param userId The unique identifier of the user.
     * @return The latest UserStripeLinkEntity associated with the user ID, or null if no active link exists.
     */
    Optional<StripeLinkedUser<T>> findPrimaryByUserId(T userId);

    /**
     * Finds all Stripe customer ID links associated with a given user ID.
     *
     * @param userId The unique identifier of the user.
     * @return A list of UserStripeLinkEntity objects associated with the user ID.
     */
    List<StripeLinkedUser<T>> findAllLinksByUserId(T userId);

    /**
     * Finds all.
     *
     * @return A list of UserStripeLinkEntity objects.
     */
    List<StripeLinkedUser<T>> findAll();

    /**
     * Finds the latest Stripe customer ID link associated with a given user ID.
     *
     * @param userId The unique identifier of the user.
     * @return The latest UserStripeLinkEntity associated with the user ID, or null if no active link exists.
     */
    Optional<StripeLinkedUser<T>> findLatestLinkByUserId(T userId);
    
    /**
     * Inserts a new StripeLinkedUser into the repository.
     *
     * @param stripeLinkedUser The StripeLinkedUser object to insert.
     * @return The number of rows affected by the insertion.
     */
    int insert(StripeLinkedUser<T> stripeLinkedUser);

    /**
     * Updates an existing StripeLinkedUser in the repository.
     *
     * @param stripeLinkedUser The StripeLinkedUser object to update.
     * @return The number of rows affected by the update.
     */
    int update(StripeLinkedUser<T> stripeLinkedUser);

    /**
     * Deletes an existing StripeLinkedUser from the repository.
     *
     * @param stripeLinkedUser The StripeLinkedUser object to delete.
     * @return The number of rows affected by the deletion.
     */
    int delete(StripeLinkedUser<T> stripeLinkedUser);

}
