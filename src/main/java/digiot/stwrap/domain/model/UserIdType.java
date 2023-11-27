package digiot.stwrap.domain.model;

import digiot.stwrap.infrastructure.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Slf4j
public class UserIdType implements UserType {

  private final int sqlType;

  public UserIdType() {

    String sqlType = PropertiesLoader.getSafeProperty("stwrap.user-id.sql-type")
            .orElseGet(() -> {
              log.warn("The SQL type for 'userId' was not specified; using default type 'VARCHAR'.");
              return "VARCHAR";
            })
            .toLowerCase();

    switch (sqlType) {
      case "varchar": {
        this.sqlType = Types.VARCHAR;
        break;
      }
      case "int": {
        this.sqlType = Types.INTEGER;
        break;
      }
      case "bigint": {
        this.sqlType = Types.BIGINT;
        break;
      }
      default:
        log.warn("Unsupported SQL type '{}' specified for 'userId'. Using default type 'VARCHAR'.", sqlType);
        this.sqlType = Types.VARCHAR;
    }
  }

  @Override
  public int[] sqlTypes() {
    return new int[]{sqlType};
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
      st.setNull(index, Types.OTHER);
    } else if (this.sqlType == Types.VARCHAR) {
      st.setString(index, value.toString());
    } else if (this.sqlType == Types.INTEGER) {
      st.setObject(index, Integer.parseInt(value.toString()));
    } else if (this.sqlType == Types.BIGINT) {
      st.setObject(index, Long.parseLong(value.toString()));
    } else {
      st.setObject(index, ((UserId) value).getValue(), this.sqlType);
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
    return false;
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
