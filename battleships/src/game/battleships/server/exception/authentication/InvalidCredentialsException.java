package game.battleships.server.exception.authentication;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String msg) {
        super(msg);
    }
    public InvalidCredentialsException(String msg, Throwable cause) {
        super(msg ,cause);
    }
}
