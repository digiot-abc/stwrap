package digiot.stwrap.domain.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "stripe_setup_intents")
@Getter
@Setter
public class StripeSetupIntent extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stripe_linked_user_id")
    private StripeLinkedUser stripeLinkedUser;

    @Column(name = "status")
    private String status;

}