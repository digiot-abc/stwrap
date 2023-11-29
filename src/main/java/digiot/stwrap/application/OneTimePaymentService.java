package digiot.stwrap.application;

import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import digiot.stwrap.domain.repository.StripePaymentIntentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class OneTimePaymentService {

    private final StripeLinkedUserRepository stripeLinkedUserRepository;
    private final StripePaymentIntentRepository stripePaymentIntentRepository;

    public void makePayment(UserId userId, String productId, String paymentMethodId) {
        // 単発決済の処理
    }
}

