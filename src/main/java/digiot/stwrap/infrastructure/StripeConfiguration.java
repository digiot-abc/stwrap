package digiot.stwrap.infrastructure;

import com.stripe.Stripe;
import java.util.Optional;

public class StripeConfiguration {
    
    private static final String STRIPE_API_KEY_ENV_NAME = "STRIPE_API_KEY";

    public StripeConfiguration() {
        String apiKey = Optional.ofNullable(System.getenv(STRIPE_API_KEY_ENV_NAME))
            .orElseThrow(() -> new IllegalStateException("Stripe API key not found in environment variables"));
                                
        Stripe.apiKey = apiKey;
    }
}