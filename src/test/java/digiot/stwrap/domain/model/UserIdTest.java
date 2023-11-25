package digiot.stwrap.domain.model;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    public void testStringSerialization() {

        String value = "text";
        UserId originalUserId = new UserId(value);

        // Serialize the object
        ByteArrayOutputStream baos = toByteArrayOutputStream(originalUserId);
        // Deserialize the object
        UserId deserializedUserId = fromByteArrayOutputStream(baos, UserId.class);

        // Verify the object state
        assertNotNull(deserializedUserId);
        assertEquals(value, deserializedUserId.getValue());
    }

    @Test
    public void testIntegerSerialization() {

        Integer value = 12345;
        UserId originalUserId = new UserId(value);

        // Serialize the object
        ByteArrayOutputStream baos = toByteArrayOutputStream(originalUserId);
        // Deserialize the object
        UserId deserializedUserId = fromByteArrayOutputStream(baos, UserId.class);

        // Verify the object state
        assertNotNull(deserializedUserId);
        assertEquals(value, deserializedUserId.getValue());
    }

    @Test
    public void testLongSerialization() {

        Long value = 12345L;
        UserId originalUserId = new UserId(value);

        // Serialize the object
        ByteArrayOutputStream baos = toByteArrayOutputStream(originalUserId);
        // Deserialize the object
        UserId deserializedUserId = fromByteArrayOutputStream(baos, UserId.class);

        // Verify the object state
        assertNotNull(deserializedUserId);
        assertEquals(value, deserializedUserId.getValue());
    }
    
    @Test
    public void testDoubleSerialization() {

        Double value = 12345.0;
        UserId originalUserId = new UserId(value);

        // Serialize the object
        ByteArrayOutputStream baos = toByteArrayOutputStream(originalUserId);
        // Deserialize the object
        UserId deserializedUserId = fromByteArrayOutputStream(baos, UserId.class);

        // Verify the object state
        assertNotNull(deserializedUserId);
        assertEquals(value, deserializedUserId.getValue());
    }

    private ByteArrayOutputStream toByteArrayOutputStream(UserId userId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(userId);
        } catch (IOException e) {
            fail("Exception occurred during serialization.", e);
        }
        return baos;
    }

    private <T> T fromByteArrayOutputStream(ByteArrayOutputStream baos, Class<T> clazz) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            fail("Exception occurred during deserialization.", e);
            return null;
        }
    }
}