package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripeSubscription;
import digiot.stwrap.domain.repository.StripeSubscriptionRepository;

import javax.sql.DataSource;
import java.sql.*;
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
        String sql = "SELECT * FROM subscription WHERE id = ? AND is_deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StripeSubscription subscription = new StripeSubscription();
                subscription.setId(rs.getString("id"));
                subscription.setStripeUserLinkId(rs.getString("stripe_user_link_id"));
                subscription.setSubscriptionId(rs.getString("subscription_id"));
                subscription.setPlanId(rs.getString("plan_id"));
                subscription.setStatus(rs.getString("status"));
                return Optional.of(subscription);
            }
        } catch (SQLException e) {
            // 例外処理を適切に行う
            throw new RuntimeException("Error finding subscription by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<StripeSubscription> findBySubscriptionId(String subscriptionId) {
        String sql = "SELECT * FROM subscription WHERE subscription_id = ? AND is_deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subscriptionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StripeSubscription subscription = new StripeSubscription();
                subscription.setId(rs.getString("id"));
                subscription.setStripeUserLinkId(rs.getString("stripe_user_link_id"));
                subscription.setSubscriptionId(rs.getString("subscription_id"));
                subscription.setPlanId(rs.getString("plan_id"));
                subscription.setStatus(rs.getString("status"));
                return Optional.of(subscription);
            }
        } catch (SQLException e) {
            // 例外処理を適切に行う
            throw new RuntimeException("Error finding subscription by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public List<StripeSubscription> findAllByPlanId(String planId) {
        List<StripeSubscription> stripeSubscriptions = new ArrayList<>();
        String sql = "SELECT * FROM stripe_subscription WHERE plan_id = ? AND is_deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, planId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StripeSubscription stripeSubscription = new StripeSubscription();
                stripeSubscription.setId(rs.getString("id"));
                stripeSubscription.setStripeUserLinkId(rs.getString("stripe_user_link_id"));
                stripeSubscription.setSubscriptionId(rs.getString("subscription_id"));
                stripeSubscription.setPlanId(rs.getString("plan_id"));
                stripeSubscription.setStatus(rs.getString("status"));
                stripeSubscriptions.add(stripeSubscription);
            }
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error finding subscriptions by user ID", e);
        }

        return stripeSubscriptions;
    }

    @Override
    public List<StripeSubscription> findAllByStripeUserLinkId(String stripeUserLinkId) {
        List<StripeSubscription> stripeSubscriptions = new ArrayList<>();
        String sql = "SELECT * FROM stripe_subscription WHERE stripe_user_link_id = ? AND is_deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, stripeUserLinkId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StripeSubscription stripeSubscription = new StripeSubscription();
                stripeSubscription.setId(rs.getString("id"));
                stripeSubscription.setStripeUserLinkId(rs.getString("stripe_user_link_id"));
                stripeSubscription.setSubscriptionId(rs.getString("subscription_id"));
                stripeSubscription.setPlanId(rs.getString("plan_id"));
                stripeSubscription.setStatus(rs.getString("status"));
                stripeSubscriptions.add(stripeSubscription);
            }
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error finding subscriptions by user ID", e);
        }

        return stripeSubscriptions;
    }


    @Override
    public StripeSubscription create(StripeSubscription stripeSubscription) {
        String sql = "INSERT INTO stripe_subscription (id, stripe_user_link_id, subscription_id, plan_id, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, stripeSubscription.getId());
            stmt.setObject(2, stripeSubscription.getStripeUserLinkId());
            stmt.setString(3, stripeSubscription.getSubscriptionId());
            stmt.setString(4, stripeSubscription.getPlanId());
            stmt.setString(5, stripeSubscription.getStatus());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating stripe_subscription failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stripeSubscription.setId(generatedKeys.getString(1));
                } else {
                    throw new SQLException("Creating stripe_subscription failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error saving subscription", e);
        }

        return stripeSubscription;
    }

    @Override
    public void delete(StripeSubscription userStripeSubscriptionEntity) {
        String sql = "UPDATE stripe_subscription SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userStripeSubscriptionEntity.getId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting stripe_subscription failed, no rows affected.");
            }
        } catch (SQLException e) {
            // Handle exception
            throw new RuntimeException("Error deleting subscription", e);
        }
    }
}
