package digiot.stwrap.domain.model;

import lombok.Data;

@Data
public class UserStripeLink<T> {
    private T userId;
    private String stripeCustomerId;
}
