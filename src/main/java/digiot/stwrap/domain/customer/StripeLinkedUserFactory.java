package digiot.stwrap.domain.customer;

import de.huxhorn.sulky.ulid.ULID;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;

import java.time.LocalDateTime;

public class StripeLinkedUserFactory {

    public StripeLinkedUser create(UserId userId, String stripeCustomerId) {

        LocalDateTime now = LocalDateTime.now();

        StripeLinkedUser link = new StripeLinkedUser();
        link.setId(new ULID().nextULID());
        link.setUserId(userId);
        link.setStripeCustomerId(stripeCustomerId);
        link.setDeleted(false);
        link.setCreatedAt(now);
        link.setUpdatedAt(now);

        return link;
    }
}
