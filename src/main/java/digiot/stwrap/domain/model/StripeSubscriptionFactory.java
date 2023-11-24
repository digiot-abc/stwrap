package digiot.stwrap.domain.model;

import de.huxhorn.sulky.ulid.ULID;

public class StripeSubscriptionFactory {

    public StripeSubscription create(String StripeLinkedUserId, String subscriptionId, String planId, String status) {
        StripeSubscription subscription = new StripeSubscription();
        subscription.setId(new ULID().nextULID());
        subscription.setStripeLinkedUserId(StripeLinkedUserId);
        subscription.setSubscriptionId(subscriptionId);
        subscription.setPlanId(planId);
        subscription.setStatus(status);
        return subscription;
    }
}
