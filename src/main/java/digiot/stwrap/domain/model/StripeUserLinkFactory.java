package digiot.stwrap.domain.model;

import de.huxhorn.sulky.ulid.ULID;

public class StripeUserLinkFactory<T> {

    public StripeUserLink<T> create(T userId, String stripeCustomerId) {
        StripeUserLink<T> link = new StripeUserLink<>();
        link.setId(new ULID().nextULID());
        link.setUserId(userId);
        link.setStripeCustomerId(stripeCustomerId);
        link.setDeleted(false);
        return link;
    }
}
