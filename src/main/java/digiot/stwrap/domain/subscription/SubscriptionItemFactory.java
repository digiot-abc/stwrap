package digiot.stwrap.domain.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionItemFactory {
    
    /**
     * Create a subscription item for a subscription plan and quantity.
     *
     * @param planId   the ID of the subscription plan.
     * @param quantity the quantity of the subscription.
     * @return Subscription item object.
     */
    public static List<Map<String, Object>> createSubscriptionItem(String planId, int quantity) {
        // アイテムリストの作成
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("plan", planId);
        item.put("quantity", quantity);
        items.add(item);
        return items;
    }
}
