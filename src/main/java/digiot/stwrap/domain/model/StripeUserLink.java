package digiot.stwrap.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StripeUserLink<T> {

    private String id;
    private T userId;
    private String stripeCustomerId;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
