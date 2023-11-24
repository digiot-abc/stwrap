package digiot.stwrap.domain.customer;

import de.huxhorn.sulky.ulid.ULID;
import digiot.stwrap.domain.model.StripeLinkedUser;

public class StripeLinkedUserFactory<T> {

    public StripeLinkedUser<T> create(T userId, String stripeCustomerId) {
        StripeLinkedUser<T> link = new StripeLinkedUser<>();
        link.setId(new ULID().nextULID());
        link.setUserId(userId);
        link.setStripeCustomerId(stripeCustomerId);
        link.setDeleted(false);
        return link;
    }
}
