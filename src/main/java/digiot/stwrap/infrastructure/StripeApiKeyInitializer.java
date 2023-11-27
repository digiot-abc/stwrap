package digiot.stwrap.infrastructure;

import com.stripe.Stripe;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.Optional;

@SuppressWarnings("unused")
public class StripeApiKeyInitializer {

    private static final String STRIPE_API_KEY_ENV_NAME = "STRIPE_API_KEY";

    static {
        initialize();
    }

    public static void initialize() {
        Stripe.apiKey = Optional.ofNullable(EnvUtils.getEnv(STRIPE_API_KEY_ENV_NAME))
                .orElseThrow(() -> new IllegalStateException("Stripe API key not found in environment variables"));
    }
}