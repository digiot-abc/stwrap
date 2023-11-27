package digiot.stwrap.domain.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stripe_linked_user")
@Getter
@Setter
public class StripeLinkedUser extends BaseEntity {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Type(type = "digiot.stwrap.domain.model.UserIdType")
    @Column(name = "user_id", nullable = false, unique = true)
    private UserId userId;

    @Column(name = "stripe_customer_id", nullable = false)
    private String stripeCustomerId;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "stripeLinkedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StripePaymentIntent> paymentIntents = new HashSet<>();

    @OneToMany(mappedBy = "stripeLinkedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StripeSetupIntent> setupIntents = new HashSet<>();
}
