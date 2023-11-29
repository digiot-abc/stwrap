package digiot.stwrap.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "stripeLinkedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StripePaymentIntent> paymentIntents = new HashSet<>();

    @OneToMany(mappedBy = "stripeLinkedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StripeSetupIntent> setupIntents = new HashSet<>();
}
