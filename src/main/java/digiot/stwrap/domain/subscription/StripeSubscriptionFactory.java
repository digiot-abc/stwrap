package digiot.stwrap.domain.subscription;

import de.huxhorn.sulky.ulid.ULID;
import digiot.stwrap.domain.model.StripeSubscription;

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
