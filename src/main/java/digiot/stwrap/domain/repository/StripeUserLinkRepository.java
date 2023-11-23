package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripeUserLink;

import java.util.List;

/**
 * Interface for a repository handling the links between user accounts and Stripe customer IDs.
 * Provides methods to manage the lifecycle of these links, including creation, update, retrieval, and deletion.
 */
public interface StripeUserLinkRepository<T> {

    /**
     * Finds all Stripe customer ID links associated with a given user ID.
     *
     * @param userId The unique identifier of the user.
     * @return A list of UserStripeLinkEntity objects associated with the user ID.
     */
    List<StripeUserLink<T>> findAllLinksByUserId(T userId);

    /**
     * Finds the latest Stripe customer ID link associated with a given user ID.
     *
     * @param userId The unique identifier of the user.
     * @return The latest UserStripeLinkEntity associated with the user ID, or null if no active link exists.
     */
    StripeUserLink<T> findLatestLinkByUserId(T userId);

    /**
     * Creates a new link between a user ID and a Stripe customer ID.
     *
     * @param stripeUserLink The UserStripeLinkEntity object containing user ID, Stripe customer ID, and other necessary data.
     * @return The created UserStripeLinkEntity object.
     */
    StripeUserLink<T> create(StripeUserLink<T> stripeUserLink);

    /**
     * Updates an existing link between a user and a Stripe customer.
     *
     * @param link The UserStripeLinkEntity object with updated Stripe customer ID or other details.
     */
    void update(StripeUserLink<T> link);

    /**
     * Deletes the link between a user and a Stripe customer.
     *
     * @param link The link is to be deleted.
     */
    void delete(StripeUserLink<T> link);

}
