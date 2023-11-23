package digiot.stwrap.domain.model;

import de.huxhorn.sulky.ulid.ULID;

public class StripeSubscriptionFactory {

    public StripeSubscription create(String stripeUserLinkId, String subscriptionId, String planId, String status) {
        StripeSubscription subscription = new StripeSubscription();
        subscription.setId(new ULID().nextULID());
        subscription.setStripeUserLinkId(stripeUserLinkId);
        subscription.setSubscriptionId(subscriptionId);
        subscription.setPlanId(planId);
        subscription.setStatus(status);
        return subscription;
    }
}
