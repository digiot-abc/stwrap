package digiot.stwrap.domain.model;

import lombok.Data;

@Data
public class Subscription<T> {
    private T userId;
    private String stripeSubscriptionId;
    private String planId;
    private String status;
}
