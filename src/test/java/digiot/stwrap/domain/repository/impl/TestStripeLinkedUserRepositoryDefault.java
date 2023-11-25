package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.domain.repository.StripeLinkedUserRepository;
import digiot.stwrap.infrastructure.DataSourceProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestStripeLinkedUserRepositoryDefault {

    private StripeLinkedUserRepository repository;

    @BeforeEach
    public void setUpEach() throws SQLException {
        DataSource dataSource = DataSourceProvider.getDataSource();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS stripe_linked_user (" +
                    "id VARCHAR(255) PRIMARY KEY," +
                    "user_id VARCHAR(255)," +
                    "stripe_customer_id VARCHAR(255)," +
                    "is_primary BOOLEAN," +
                    "deleted BOOLEAN," +
                    "created_at TIMESTAMP," +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        this.repository = new DefaultStripeLinkedUserRepository(dataSource);
    }

    @AfterEach
    public void tearDownEach() throws SQLException {
        DataSource dataSource = DataSourceProvider.getDataSource();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM stripe_linked_user");
        }
    }

    @Test
    void saveAndFind() throws Exception {
        StripeLinkedUser user = createTestUser("1", "user1", "cust1", true, false);
        assertEquals("1", repository.save(user).getId());

        Optional<StripeLinkedUser> foundUser = repository.findPrimaryByUserId(UserId.valueOf("user1"));
        assertTrue(foundUser.isPresent());
        assertEquals("cust1", foundUser.get().getStripeCustomerId());
    }

    @Test
    void testFindAll() {
        StripeLinkedUser user1 = createTestUser("1", "user1", "cust1", true, false);
        StripeLinkedUser user2 = createTestUser("2", "user2", "cust2", false, false);
        repository.save(user1);
        repository.save(user2);

        Iterable<StripeLinkedUser> users = repository.findAll();
        assertTrue(users.iterator().hasNext());
    }

    @Test
    void testDeleteById() {
        StripeLinkedUser user = createTestUser("1", "user1", "cust1", true, false);
        repository.save(user);

        repository.deleteById(user.getId());
        Optional<StripeLinkedUser> deletedUser = repository.findById(user.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void testExistsById() {
        StripeLinkedUser user = createTestUser("1", "user1", "cust1", true, false);
        repository.save(user);

        assertTrue(repository.existsById(user.getId()));
    }

    @Test
    void testCount() {
        StripeLinkedUser user1 = createTestUser("1", "user1", "cust1", true, false);
        StripeLinkedUser user2 = createTestUser("2", "user2", "cust2", false, false);
        repository.save(user1);
        repository.save(user2);

        long count = repository.count();
        assertEquals(2, count);
    }

    private StripeLinkedUser createTestUser(String id, String userId, String stripeCustomerId, boolean isPrimary, boolean deleted) {
        StripeLinkedUser user = new StripeLinkedUser();
        user.setId(id);
        user.setUserId(UserId.valueOf(userId));
        user.setStripeCustomerId(stripeCustomerId);
        user.setIsPrimary(isPrimary);
        user.setDeleted(deleted);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
