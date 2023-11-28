package digiot.stwrap.domain.repository;

import static org.junit.jupiter.api.Assertions.*;


import com.stripe.exception.StripeException;
import digiot.stwrap.application.CustomerService;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripeSetupIntent;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class StripeSetupIntentRepositoryTest {

  @Autowired
  CustomerService customerService;

  @Autowired
  StripeSetupIntentRepository stripeSetupIntentRepository;

  static {

    StripeApiKeyInitializer.initialize();
  }

  @Test
  public void testFindByStripeLinkedUserId() throws StripeException {

    String testUserId = "test-user-id";
    StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(testUserId));

    // Setup test data
    StripeSetupIntent testIntent = new StripeSetupIntent();
    testIntent.setId("test-setup-intent-id");
    testIntent.setStripeLinkedUser(linkedUser); // Assuming there is a setter for this field
    stripeSetupIntentRepository.save(testIntent);

    // Retrieve setup intents by Stripe linked user ID
    List<StripeSetupIntent> retrievedIntents = stripeSetupIntentRepository.findByStripeLinkedUserId(linkedUser.getId());

    // Assert the retrieved data
    assertThat(retrievedIntents).isNotNull();
    assertThat(retrievedIntents.size()).isEqualTo(1);
    assertThat(retrievedIntents.get(0).getId()).isEqualTo("test-setup-intent-id");
  }

  // Additional test cases for other CRUD operations can be added here
}
