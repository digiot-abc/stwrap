package digiot.stwrap.domain.repository;

import digiot.stwrap.SpringBootRunner;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.UserId;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = SpringBootRunner.class)
@DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class StripeLinkedUserRepository_DataJpaTest {

    @Autowired
    StripeLinkedUserRepository repository;

    @Test
    void testUserIdMapping() {
        StripeLinkedUser user = new StripeLinkedUser();
        user.setUserId(UserId.valueOf("testUser"));
    
        // その他の設定...

        // 保存
       StripeLinkedUser savedUser = repository.save(user);

       // 検索
       StripeLinkedUser foundUser = repository.findById(savedUser.getId()).orElseThrow();
       assertEquals("testUser", foundUser.getUserId().getValue());
    }
}

