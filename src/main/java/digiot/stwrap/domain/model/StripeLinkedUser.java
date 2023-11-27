package digiot.stwrap.domain.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stripe_linked_user")
public class StripeLinkedUser {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Type(type = "digiot.stwrap.domain.model.UserIdType")
    @Column(name = "user_id", nullable = false)
    private UserId userId;

    @Column(name = "stripe_customer_id", nullable = false)
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
