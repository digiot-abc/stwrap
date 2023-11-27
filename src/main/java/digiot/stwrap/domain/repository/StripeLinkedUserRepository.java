package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface for a repository handling the links between user accounts and Stripe customer IDs.
 * Provides methods to manage the lifecycle of these links, including creation, read, update, and deletion.
 */
@Repository
public interface StripeLinkedUserRepository extends PagingAndSortingRepository<StripeLinkedUser, String> {

    /**
     * Finds the primary Stripe customer ID link associated with a given user ID.
     *
     * @param userId The unique identifier of the user.
     * @return The latest StripeLinkedUser associated with the user ID, or an empty Optional if no active link exists.
     */
    Optional<StripeLinkedUser> findByUserId(UserId userId);
}
