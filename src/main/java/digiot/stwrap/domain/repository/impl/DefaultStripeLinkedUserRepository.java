package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default JDBC implementation of the UserStripeLinkRepository.
 */
public class DefaultStripeLinkedUserRepository implements StripeLinkedUserRepository {

    private final DataSource dataSource;

    public DefaultStripeLinkedUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <S extends StripeLinkedUser> S save(S entity) {
        String sql = "MERGE INTO stripe_linked_user KEY (id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getId());
            stmt.setObject(2, entity.getUserId().getValue());
            stmt.setString(3, entity.getStripeCustomerId());
            stmt.setBoolean(4, entity.getIsPrimary());
            stmt.setBoolean(5, entity.getDeleted());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(entity.getUpdatedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error in upsert operation for stripe_linked_user", e);
        }

        return entity;
    }

    @Override
    public <S extends StripeLinkedUser> Iterable<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<StripeLinkedUser> findById(String id) {
        String sql = "SELECT * FROM stripe_linked_user WHERE id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapRowToUserStripeLinkEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying stripe_linked_user by id", e);
        }

        return Optional.empty();
    }

    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM stripe_linked_user WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of stripe_linked_user", e);
        }

        return false;
    }

    @Override
    public Optional<StripeLinkedUser> findPrimaryByUserId(UserId userId) {
        String sql = "SELECT * FROM stripe_linked_user WHERE user_id = ? AND is_primary = true AND deleted = FALSE ORDER BY updated_at DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId.getValue());
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
    public List<StripeLinkedUser> findAll() {
        List<StripeLinkedUser> links = new ArrayList<>();
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
    public Iterable<StripeLinkedUser> findAllById(Iterable<String> ids) {
        List<StripeLinkedUser> users = new ArrayList<>();
        for (String id : ids) {
            findById(id).ifPresent(users::add);
        }
        return users;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM stripe_linked_user";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting stripe_linked_user", e);
        }

        return 0;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM stripe_linked_user WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stripe_linked_user with ID " + id, e);
        }
    }

    @Override
    public List<StripeLinkedUser> findAllLinksByUserId(UserId userId) {
        List<StripeLinkedUser> links = new ArrayList<>();
        String sql = "SELECT * FROM stripe_linked_user WHERE user_id = ? AND deleted = FALSE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId.getValue());
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
    public Optional<StripeLinkedUser> findLatestLinkByUserId(UserId userId) {
        String sql = "SELECT * FROM stripe_linked_user WHERE user_id = ? AND deleted = FALSE ORDER BY updated_at DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId.getValue());
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
    public void delete(StripeLinkedUser link) {
        String sql = "UPDATE stripe_linked_user SET deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, link.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stripe_linked_user", e);
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        String sql = "DELETE FROM stripe_linked_user WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String id : ids) {
                stmt.setString(1, id);
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stripe_linked_user entities", e);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends StripeLinkedUser> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM stripe_linked_user";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all stripe_linked_users", e);
        }
    }

    @Override
    public Iterable<StripeLinkedUser> findAll(Sort sort) {
        // このメソッドはJDBCでの実装には適していないため、未サポートです。
        throw new UnsupportedOperationException("Sort not supported in JDBC implementation");
    }

    @Override
    public Page<StripeLinkedUser> findAll(Pageable pageable) {
        // ページングはJDBCでの実装には適していないため、未サポートです。
        throw new UnsupportedOperationException("Pageable not supported in JDBC implementation");
    }

    private StripeLinkedUser mapRowToUserStripeLinkEntity(ResultSet rs) throws SQLException {
        StripeLinkedUser StripeLinkedUser = new StripeLinkedUser();
        StripeLinkedUser.setId(rs.getString("id"));
        StripeLinkedUser.setUserId(UserId.valueOf(rs.getObject("user_id")));
        StripeLinkedUser.setStripeCustomerId(rs.getString("stripe_customer_id"));
        StripeLinkedUser.setIsPrimary(rs.getBoolean("is_primary"));
        StripeLinkedUser.setDeleted(rs.getBoolean("deleted"));
        StripeLinkedUser.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        StripeLinkedUser.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return StripeLinkedUser;
    }
}

