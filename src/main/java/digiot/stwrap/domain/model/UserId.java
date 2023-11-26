package digiot.stwrap.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@AllArgsConstructor
public class UserId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    private Object value;

    public static UserId valueOf(Object value) {
        return new UserId(value);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(value);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.value = in.readObject();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
