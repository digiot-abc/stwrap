package digiot.stwrap.domain.repository;


import com.stripe.exception.StripeException;
import digiot.stwrap.application.CustomerService;
import digiot.stwrap.domain.model.StripeLinkedUser;
import digiot.stwrap.domain.model.StripePaymentIntent;
import digiot.stwrap.domain.model.UserId;
import digiot.stwrap.infrastructure.StripeApiKeyInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class StripePaymentIntentRepositoryTest {

  @Autowired
  CustomerService customerService;

  @Autowired
  StripePaymentIntentRepository stripePaymentIntentRepository;

  static {
    StripeApiKeyInitializer.initialize();
  }

  @Test
  public void testFindByStripeLinkedUserId() throws StripeException {

    String testUserId = "test-user-id";
    StripeLinkedUser linkedUser = customerService.getOrCreateStripeLinkedUser(UserId.valueOf(testUserId));

    // Setup test data
    StripePaymentIntent testIntent = new StripePaymentIntent();
    testIntent.setId("test-payment-intent-id");
    testIntent.setStripeLinkedUser(linkedUser); // Assuming there is a setter for this field
    stripePaymentIntentRepository.save(testIntent);

    // Retrieve payment intents by Stripe linked user ID
    List<StripePaymentIntent> retrievedIntents = stripePaymentIntentRepository.findByStripeLinkedUserId(linkedUser.getId());

    // Assert the retrieved data
    assertThat(retrievedIntents).isNotNull();
    assertThat(retrievedIntents.size()).isEqualTo(1);
    assertThat(retrievedIntents.get(0).getId()).isEqualTo("test-payment-intent-id");
  }

  // Additional test cases for other CRUD operations can be added here
}
