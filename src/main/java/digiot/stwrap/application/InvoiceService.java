package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.param.InvoiceUpcomingParams;
import digiot.stwrap.domain.model.UserId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvoiceService {

    final StripeLinkService linkService;

    /**
     * Retrieves the next upcoming invoice for a given customer.
     *
     * @param userId The user ID for whom the invoice is to be retrieved.
     * @return The upcoming Invoice object for the specified user, or null if there is no upcoming invoice.
     * @throws StripeException If an error occurs during communication with the Stripe API.
     */
    public Invoice getNextInvoice(UserId userId) throws StripeException {

        // Retrieve the Stripe customer ID for the given user ID.
        String customerId = linkService.getOrCreateStripeLinkedUser(userId).getStripeCustomerId();

        InvoiceUpcomingParams invoiceParams = InvoiceUpcomingParams.builder()
                .setCustomer(customerId)
                .build();

        // Return the upcoming invoice if it exists.
        return Invoice.upcoming(invoiceParams);
    }
}
