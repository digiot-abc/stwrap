package digiot.stwrap.domain.repository;

import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class StripeLinkedUserRepository_DataJpaTest {

    @Autowired
    private StripeLinkedUserRepository repository;

    @Test
    void testUserIdMapping() {
        StripeLinkedUser user = new StripeLinkedUser();
        user.setUserId(UserId.valueOf("testUser"));
        // その他の設定...

        // 保存
//        StripeLinkedUser savedUser = repository.insert(user);
//
//        // 検索
//        StripeLinkedUser foundUser = repository.findById(savedUser.getId()).orElseThrow();
//        assertEquals("testUser", foundUser.getUserId().getValue());
    }
}

