package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripePayment;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Stripe payment transactions.
 */
public interface StripePaymentRepository {

    /**
     * Finds a payment transaction by its unique identifier.
     *
     * @param id The unique identifier of the payment transaction.
     * @return An Optional containing the StripePayment object if found, or an empty Optional if not found.
     */
    Optional<StripePayment> findById(String id);

    /**
     * Finds all payment transactions associated with a specific Stripe user link ID.
     *
     * @param stripeUserLinkId The identifier of the Stripe user link.
     * @return A list of StripePayment objects associated with the Stripe user link ID.
     */
    List<StripePayment> findAllByStripeUserLinkId(String stripeUserLinkId);

    /**
     * Saves or updates a payment transaction.
     *
     * @param stripePayment The StripePayment object to save or update.
     * @return The saved StripePayment object.
     */
    StripePayment create(StripePayment stripePayment);

    /**
     * Deletes a payment transaction.
     *
     * @param stripePayment The StripePayment object to delete.
     */
    void delete(StripePayment stripePayment);
}
