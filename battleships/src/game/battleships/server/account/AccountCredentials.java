package game.battleships.server.account;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public record AccountCredentials(String username, byte[] password) implements Serializable {

    private static final long serialVersionUID = 2930337154172403564L;

    public AccountCredentials {
        if (username == null || password == null) {
            throw new IllegalArgumentException("null argument passed to constructor");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountCredentials that)) return false;
        return username.equals(that.username) && Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(username);
        result = 31 * result + Arrays.hashCode(password);
        return result;
    }
}
