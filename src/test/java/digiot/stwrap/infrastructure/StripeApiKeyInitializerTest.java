package digiot.stwrap.infrastructure;

import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StripeApiKeyInitializerTest {

    @Test
    public void testInitializeApiKeyDotenv() {
        Assertions.assertEquals(Dotenv.load().get("STRIPE_API_KEY"), Stripe.apiKey,
                "The API key should be correctly initialized from the .env file");
    }
}