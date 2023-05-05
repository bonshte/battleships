package game.battleships.server.exception.authentication;

public class InvalidPasswordFormatException extends Exception {
    public InvalidPasswordFormatException(String msg) {
        super(msg);
    }
    public InvalidPasswordFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
