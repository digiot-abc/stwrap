package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripeUserLink;
import digiot.stwrap.domain.repository.StripeUserLinkRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Default JDBC implementation of the UserStripeLinkRepository.
 */
public class DefaultStripeUserLinkRepository<T> implements StripeUserLinkRepository<T> {

    private final DataSource dataSource;

    public DefaultStripeUserLinkRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<StripeUserLink<T>> findAllLinksByUserId(T userId) {
        List<StripeUserLink<T>> links = new ArrayList<>();
        String sql = "SELECT * FROM stripe_user_link WHERE user_id = ? AND is_deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    links.add(mapRowToUserStripeLinkEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_user_link", e);
        }

        return links;
    }

    @Override
    public StripeUserLink<T> findLatestLinkByUserId(T userId) {
        StripeUserLink<T> link = null;
        String sql = "SELECT * FROM stripe_user_link WHERE user_id = ? AND is_deleted = FALSE ORDER BY updated_at DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    link = mapRowToUserStripeLinkEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_user_link", e);
        }

        return link;
    }

    @Override
    public StripeUserLink<T> create(StripeUserLink<T> stripeUserLink) {
        String sql = "INSERT INTO stripe_user_link (id, user_id, stripe_customer_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, stripeUserLink.getId());
            stmt.setObject(2, stripeUserLink.getUserId());
            stmt.setString(3, stripeUserLink.getStripeCustomerId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating stripe_user_link failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stripeUserLink.setId(generatedKeys.getString(1));
                } else {
                    throw new SQLException("Creating stripe_user_link failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating a new stripe_user_link", e);
        }

        return stripeUserLink;
    }

    @Override
    public void update(StripeUserLink<T> link) {
        String sql = "UPDATE stripe_user_link SET stripe_customer_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND is_deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, link.getStripeCustomerId());
            stmt.setObject(2, link.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating stripe_user_link", e);
        }
    }

    @Override
    public void delete(StripeUserLink<T> link) {
        String sql = "UPDATE stripe_user_link SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, link.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stripe_user_link", e);
        }
    }

    private StripeUserLink<T> mapRowToUserStripeLinkEntity(ResultSet rs) throws SQLException {
        StripeUserLink<T> stripeUserLink = new StripeUserLink<>();
        stripeUserLink.setId(rs.getString("id"));
        stripeUserLink.setUserId((T) rs.getObject("stripe_user_link_id"));
        stripeUserLink.setStripeCustomerId(rs.getString("stripe_customer_id"));
        stripeUserLink.setDeleted(rs.getBoolean("is_deleted"));
        stripeUserLink.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        stripeUserLink.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return stripeUserLink;
    }
}

