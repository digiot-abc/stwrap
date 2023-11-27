package digiot.stwrap.domain.repository;

import de.huxhorn.sulky.ulid.ULID;
import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.infrastructure.PropertiesLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:spring/user_id_string/application.properties")
@SpringBootTest(classes = SpringBootRunner.class)
public class StripeLinkedUserRepositoryDataJpaUserIdStringTest {

    @Autowired
    StripeLinkedUserRepository repository;

    @BeforeAll
    public static void setUp() {
        PropertiesLoader.load("spring/user_id_string/stwrap.properties");
    }

    @Test
    void testUserIdMapping() {

        StripeLinkedUser user = new StripeLinkedUser();
        user.setId(new ULID().nextULID());
        user.setUserId(UserId.valueOf("1"));
        user.setStripeCustomerId("stripeCustomerId");
        user.setDeleted(false);

        // 保存
        StripeLinkedUser savedUser = repository.save(user);

        // 検索
        StripeLinkedUser foundUser = repository.findById(savedUser.getId()).orElseThrow();
        assertEquals("1", foundUser.getUserId().getValue());

        System.out.println("saved: " + savedUser);
        System.out.println("found: " + foundUser);
    }
}

