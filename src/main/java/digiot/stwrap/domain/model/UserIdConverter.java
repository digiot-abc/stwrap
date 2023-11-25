package digiot.stwrap.domain.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserIdConverter implements AttributeConverter<UserId, Object> {

    @Override
    public Object convertToDatabaseColumn(UserId userId) {
        if (userId == null) {
            return null;
        }
        return userId.getValue();
    }

    @Override
    public UserId convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        return UserId.valueOf(dbData);
    }
}
