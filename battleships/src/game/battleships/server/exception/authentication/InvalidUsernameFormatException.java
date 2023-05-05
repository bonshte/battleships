package game.battleships.server.exception.authentication;

public class InvalidUsernameFormatException extends Exception {
    public InvalidUsernameFormatException(String msg) {
        super(msg);
    }
    public InvalidUsernameFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
