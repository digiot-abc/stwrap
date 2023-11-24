package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default JDBC implementation of the UserStripeLinkRepository.
 */
public class DefaultStripeLinkedUserRepository<T> implements StripeLinkedUserRepository<T> {

    private final DataSource dataSource;

    public DefaultStripeLinkedUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<StripeLinkedUser<T>> findPrimaryByUserId(T userId) {
        String sql = "SELECT * FROM stripe_linked_user WHERE user_id = ? AND is_primary = true AND deleted = FALSE ORDER BY updated_at DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapRowToUserStripeLinkEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_linked_user", e);
        }

        return Optional.empty();
    }

    @Override
    public List<StripeLinkedUser<T>> findAll() {
        List<StripeLinkedUser<T>> links = new ArrayList<>();
        String sql = "SELECT * FROM stripe_linked_user WHERE deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    links.add(mapRowToUserStripeLinkEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_linked_user", e);
        }

        return links;
    }

    @Override
    public List<StripeLinkedUser<T>> findAllLinksByUserId(T userId) {
        List<StripeLinkedUser<T>> links = new ArrayList<>();
        String sql = "SELECT * FROM stripe_linked_user WHERE user_id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    links.add(mapRowToUserStripeLinkEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_linked_user", e);
        }

        return links;
    }

    @Override
    public Optional<StripeLinkedUser<T>> findLatestLinkByUserId(T userId) {
        String sql = "SELECT * FROM stripe_linked_user WHERE user_id = ? AND deleted = FALSE ORDER BY updated_at DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapRowToUserStripeLinkEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_linked_user", e);
        }

        return Optional.empty();
    }

    @Override
    public int insert(StripeLinkedUser<T> stripeLinkedUser) {
        String sql = "INSERT INTO stripe_linked_user (id, user_id, stripe_customer_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stripeLinkedUser.getId());
            stmt.setObject(2, stripeLinkedUser.getUserId());
            stmt.setString(3, stripeLinkedUser.getStripeCustomerId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating stripe_linked_user failed, no rows affected.");
            }

            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating a new stripe_linked_user", e);
        }
    }

    @Override
    public int update(StripeLinkedUser<T> link) {
        String sql = "UPDATE stripe_linked_user SET stripe_customer_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, link.getStripeCustomerId());
            stmt.setObject(2, link.getUserId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating stripe_linked_user", e);
        }
    }

    @Override
    public int delete(StripeLinkedUser<T> link) {
        String sql = "UPDATE stripe_linked_user SET deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, link.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stripe_linked_user", e);
        }
    }

    private StripeLinkedUser<T> mapRowToUserStripeLinkEntity(ResultSet rs) throws SQLException {
        StripeLinkedUser<T> StripeLinkedUser = new StripeLinkedUser<>();
        StripeLinkedUser.setId(rs.getString("id"));
        StripeLinkedUser.setUserId((T) rs.getObject("user_id"));
        StripeLinkedUser.setStripeCustomerId(rs.getString("stripe_customer_id"));
        StripeLinkedUser.setIsPrimary(rs.getBoolean("is_primary"));
        StripeLinkedUser.setDeleted(rs.getBoolean("deleted"));
        StripeLinkedUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        StripeLinkedUser.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return StripeLinkedUser;
    }

}

