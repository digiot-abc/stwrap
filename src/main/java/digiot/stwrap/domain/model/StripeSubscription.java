package digiot.stwrap.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StripeSubscription {
    private String id;
    private String StripeLinkedUserId;
    private String subscriptionId;
    private String planId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
