package digiot.stwrap.domain.model.factory;

import com.stripe.model.SetupIntent;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripeSetupIntent;

public class StripeSetupIntentFactory {

    public static StripeSetupIntent create(StripeLinkedUser linkedUser, SetupIntent setupIntent) {
        StripeSetupIntent stripeSetupIntent = new StripeSetupIntent();
        stripeSetupIntent.setId(setupIntent.getId());
        stripeSetupIntent.setStatus(setupIntent.getStatus());
        stripeSetupIntent.setStripeLinkedUser(linkedUser);
        return stripeSetupIntent;
    }
}
