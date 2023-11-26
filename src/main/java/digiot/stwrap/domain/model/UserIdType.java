package digiot.stwrap.domain.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserIdType implements UserType {

  @Override
  public int[] sqlTypes() {
    return new int[] { Types.OTHER };
  }

  @Override
  public Class<?> returnedClass() {
    return UserId.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == null || y == null) {
      return false;
    }
    return x.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    Object value = rs.getObject(names[0]);
    return value != null ? UserId.valueOf(value) : null;
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.OTHER); // JDBCのOTHER型を使用
    } else {
      // setObjectの第3引数を省略して、JDBCに型推測を任せる
      st.setObject(index, ((UserId) value).getValue());
    }
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    if (value == null) {
      return null;
    }
    return UserId.valueOf(((UserId) value).getValue());
  }

  @Override
  public boolean isMutable() {
    return false; // UserIdは不変オブジェクトとして扱います
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) deepCopy(value);
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return deepCopy(cached);
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return deepCopy(original);
  }
}
