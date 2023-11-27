package digiot.stwrap.domain.repository;

import de.huxhorn.sulky.ulid.ULID;
import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringBootRunner.class)
public class StripeLinkedUserRepository_DataJpaTest {

    @Autowired
    StripeLinkedUserRepository repository;

    @Test
    void testUserIdMapping() {

        StripeLinkedUser user = new StripeLinkedUser();
        user.setId(new ULID().nextULID());
        user.setUserId(UserId.valueOf("testUser"));
        user.setStripeCustomerId("stripeCustomerId");
        user.setDeleted(false);
        user.setIsPrimary(true);

        // 保存
        StripeLinkedUser savedUser = repository.save(user);

        // 検索
        StripeLinkedUser foundUser = repository.findById(savedUser.getId()).orElseThrow();
        assertEquals("testUser", foundUser.getUserId().getValue());

        System.out.println("saved: " + savedUser);
        System.out.println("found: " + foundUser);
    }
}

