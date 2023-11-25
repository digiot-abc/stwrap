package digiot.stwrap.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stripe_linked_user")
public class StripeLinkedUser {

    @Id
    @Column(name = "id")
    private String id;

    @Convert(converter = UserIdConverter.class)
    @Column(name = "user_id")
    private UserId userId;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
