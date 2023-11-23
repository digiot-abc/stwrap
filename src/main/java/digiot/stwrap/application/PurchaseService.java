package digiot.stwrap.application;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import digiot.stwrap.domain.model.StripeUserLink;
import digiot.stwrap.domain.repository.StripeUserLinkRepository;
import digiot.stwrap.domain.repository.impl.DefaultStripeUserLinkRepository;
import digiot.stwrap.infrastructure.DataSourceProvider;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PurchaseService {

    final StripeUserLinkRepository<?> stripeRepository;

    public PurchaseService() {
        this(new DefaultStripeUserLinkRepository<String>(DataSourceProvider.getDataSource()));
    }

    /**
     * Creates a charge on a customer's card.
     *
     * @param entity   the User Stripe linked entity.
     * @param amount   the amount to be charged.
     * @param currency the currency in which the charge is made.
     * @return Charge object representing the transaction.
     * @throws StripeException if an error occurs during the charge.
     * @deprecated This method uses the default credit card on file for the customer.
     * Use {@link #charge(StripeUserLink, int, String, String)} or
     * {@link #charge(StripeUserLink, int, String, Token)} to specify
     * the credit card information.
     */
    public Charge charge(StripeUserLink<?> entity, int amount, String currency) throws StripeException {
        return charge(entity, amount, currency, (String) null);
    }

    /**
     * Creates a charge on a customer's card with specified credit card information as a String token.
     *
     * @param entity         the User Stripe linked entity.
     * @param amount         the amount to be charged.
     * @param currency       the currency in which the charge is made.
     * @param creditCardInfo the credit card information as a String token.
     * @return Charge object representing the transaction.
     * @throws StripeException if an error occurs during the charge.
     */
    public Charge charge(StripeUserLink<?> entity, int amount, String currency, String creditCardInfo) throws StripeException {
        Token token = null;
        if (creditCardInfo != null) {
            Map<String, Object> tokenParams = new HashMap<>();
            tokenParams.put("card", creditCardInfo);
            token = Token.create(tokenParams);
        }
        return charge(entity, amount, currency, token);
    }

    /**
     * Creates a charge on a customer's card with specified credit card information as a Token object.
     *
     * @param entity         the User Stripe linked entity.
     * @param amount         the amount to be charged.
     * @param currency       the currency in which the charge is made.
     * @param creditCardInfo the credit card information as a Token object.
     * @return Charge object representing the transaction.
     * @throws StripeException if an error occurs during the charge.
     */
    public Charge charge(StripeUserLink<?> entity, int amount, String currency, Token creditCardInfo) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", currency);
        chargeParams.put("customer", entity.getStripeCustomerId());

        if (creditCardInfo != null) {
            chargeParams.put("source", creditCardInfo.getId());
        }

        return Charge.create(chargeParams);
    }
}

