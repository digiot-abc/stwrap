package digiot.stwrap.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StripeLinkedUser<T> {

    private String id;
    private T userId;
    private String stripeCustomerId;
    private Boolean primary;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
