package digiot.stwrap.domain.repository;

import de.huxhorn.sulky.ulid.ULID;
import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

@ActiveProfiles("test")
@SpringBootTest(classes = SpringBootRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class StripeLinkedUserRepository_DataJpaTest {

    @Autowired
    StripeLinkedUserRepository repository;

    @Test
    void testUserIdMapping() {
        StripeLinkedUser user = new StripeLinkedUser();
        user.setUserId(UserId.valueOf("testUser"));
        user.setId(new ULID().nextULID());
        user.setStripeCustomerId("stripeCustomerId");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(false);
        user.setIsPrimary(true);
    
        // その他の設定...

        // 保存
       StripeLinkedUser savedUser = repository.save(user);

       // 検索
       StripeLinkedUser foundUser = repository.findById(savedUser.getId()).orElseThrow();
       assertEquals("testUser", foundUser.getUserId().getValue());
    }
}

