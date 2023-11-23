# Stwrap

This library is designed to link your service's user ID with the Stripe customer ID for easy payment processing. Below
is a sample table definition which you can customize according to your user ID format.

## Sample Table Definition

Here's a sample SQL script to create the `user_stripe_link` table:

```sql
CREATE TABLE user_stripe_link
(
    id                 VARCHAR(32) PRIMARY KEY,
    user_id            VARCHAR(255) NOT NULL,
    stripe_customer_id VARCHAR(255) NOT NULL,
    is_deleted         BOOLEAN   DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

Please ensure that the user_id field type matches the type of the user ID used in your service. Adjust the VARCHAR
length accordingly.

We recommend putting indexes on user_id and stripe_customer_id.

```sql
CREATE INDEX idx_user_stripe_link_user_id ON user_stripe_link (user_id);
CREATE INDEX idx_user_stripe_link_stripe_customer_id ON user_stripe_link (stripe_customer_id);
```

## UserStripeLinkEntity

以下は、サービスのユーザーIDとStripe顧客IDの関係をモデル化する`UserStripeLinkEntity`クラスです。`userId`
フィールドはジェネリックで、ユーザーIDフィールドに合わせて任意の型を使用できます。

```java
import java.time.LocalDateTime;

public class UserStripeLinkEntity<T> {

    private String id;
    private T userId;
    private String stripeCustomerId;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ゲッターとセッター
}
```

## UserStripeLinkRepository

ユーザーとStripe顧客IDのリンクを管理するUserStripeLinkRepositoryインターフェイスも実装する必要があります。その一例を以下に示します。

```java
import digiot.stwrap.domain.model.StripeUserLink;
import digiot.stwrap.domain.model.UserStripeLink;
import digiot.stwrap.domain.model.UserStripeLinkEntity;

import java.util.List;

public interface UserStripeLinkRepository<T> {

    List<StripeUserLink<T>> findAllLinksByUserId(T userId);

    StripeUserLink<T> findLatestLinkByUserId(T userId);

    StripeUserLink<T> create(StripeUserLink<T> stripeUserLink);

    void update(StripeUserLink<T> link);

    void delete(StripeUserLink<T> link);
}
```

このインターフェイスは、ユーザーとStripeのリンクを管理するために必要な基本的なCRUD操作を定義しています。