package digiot.stwrap.domain.subscription;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionItemFactory {
    
    /**
     * Create a subscription item for a subscription plan and quantity.
     *
     * @param planId   the ID of the subscription plan.
     * @param quantity the quantity of the subscription.
     * @return Subscription item object.
     */
    public static Map<String, Object> createSubscriptionItem(String planId, int quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put("plan", planId);
        item.put("quantity", quantity);
        return item;
    }
}
