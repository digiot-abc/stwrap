package digiot.stwrap.domain.model;

import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.WrapperOptions;

public class UserIdTypeDescriptor extends AbstractTypeDescriptor<UserId> {

  public UserIdTypeDescriptor() {
    super(UserId.class);
  }

  @Override
  public String toString(UserId value) {
    return value == null ? null : value.toString();
  }

  @Override
  public UserId fromString(String string) {
    return string == null ? null : UserId.valueOf(string);
  }

  @Override
  public <X> X unwrap(UserId value, Class<X> type, WrapperOptions options) {
    if (value == null) {
      return null;
    }
    if (type.isInstance(value.getValue())) {
      return (X) value.getValue();
    }
    throw unknownUnwrap(type);
  }

  @Override
  public <X> UserId wrap(X value, WrapperOptions options) {
    if (value == null) {
      return null;
    }
    return UserId.valueOf(value);
  }
}
