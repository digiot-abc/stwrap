package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripeSubscription;
import digiot.stwrap.domain.repository.StripeSubscriptionRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultStripeSubscriptionRepository implements StripeSubscriptionRepository {

    private final DataSource dataSource;

    public DefaultStripeSubscriptionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<StripeSubscription> findById(String id) {
        String sql = "SELECT * FROM stripe_subscription WHERE id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToStripeSubscription(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding subscription by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<StripeSubscription> findBySubscriptionId(String subscriptionId) {
        String sql = "SELECT * FROM stripe_subscription WHERE subscription_id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subscriptionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapToStripeSubscription(rs));
            }
        } catch (SQLException e) {
            // 例外処理を適切に行う
//            throw new RuntimeException("Error finding subscription by ID", e);
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<StripeSubscription> findAllByPlanId(String planId) {
        List<StripeSubscription> stripeSubscriptions = new ArrayList<>();
        String sql = "SELECT * FROM stripe_subscription WHERE plan_id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, planId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StripeSubscription stripeSubscription = mapToStripeSubscription(rs);
                stripeSubscriptions.add(stripeSubscription);
            }
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error finding subscriptions by user ID", e);
        }

        return stripeSubscriptions;
    }

    @Override
    public List<StripeSubscription> findAllByStripeLinkedUserId(String StripeLinkedUserId) {
        List<StripeSubscription> stripeSubscriptions = new ArrayList<>();
        String sql = "SELECT * FROM stripe_subscription WHERE stripe_linked_user_id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, StripeLinkedUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StripeSubscription stripeSubscription = mapToStripeSubscription(rs);
                stripeSubscriptions.add(stripeSubscription);
            }
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error finding subscriptions by user ID", e);
        }

        return stripeSubscriptions;
    }

    @Override
    public int insert(StripeSubscription stripeSubscription) {
        String sql = "INSERT INTO stripe_subscription (id, stripe_linked_user_id, subscription_id, plan_id, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stripeSubscription.getId());
            stmt.setObject(2, stripeSubscription.getStripeLinkedUserId());
            stmt.setString(3, stripeSubscription.getSubscriptionId());
            stmt.setString(4, stripeSubscription.getPlanId());
            stmt.setString(5, stripeSubscription.getStatus());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error inserting subscription", e);
        }
    }

    @Override
    public int update(StripeSubscription stripeSubscription) {
        String sql = "UPDATE stripe_subscription SET stripe_linked_user_id = ?, subscription_id = ?, plan_id = ?, status = ?, deleted = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ensure the order and the number of parameters match the SQL statement
            stmt.setObject(1, stripeSubscription.getStripeLinkedUserId()); // Assuming stripeLinkedUserId is an object type
            stmt.setString(2, stripeSubscription.getSubscriptionId());
            stmt.setString(3, stripeSubscription.getPlanId());
            stmt.setString(4, stripeSubscription.getStatus());
            stmt.setBoolean(5, stripeSubscription.getDeleted()); // Update deleted status
            stmt.setString(6, stripeSubscription.getId()); // The 'id' should be last to match the WHERE clause

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating subscription", e);
        }
    }


    @Override
    public int delete(StripeSubscription stripeSubscription) {
        String sql = "UPDATE stripe_subscription SET deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stripeSubscription.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting subscription", e);
        }
    }

    private StripeSubscription mapToStripeSubscription(ResultSet rs) throws SQLException {
        StripeSubscription subscription = new StripeSubscription();
        subscription.setId(rs.getString("id"));
        subscription.setStripeLinkedUserId(rs.getString("stripe_linked_user_id"));
        subscription.setSubscriptionId(rs.getString("subscription_id"));
        subscription.setPlanId(rs.getString("plan_id"));
        subscription.setStatus(rs.getString("status"));
        subscription.setDeleted(rs.getBoolean("deleted"));
        subscription.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        subscription.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return subscription;
    }
}
