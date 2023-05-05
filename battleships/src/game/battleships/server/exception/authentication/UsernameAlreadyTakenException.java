package game.battleships.server.exception.authentication;

public class UsernameAlreadyTakenException extends Exception {
    public UsernameAlreadyTakenException(String msg) {
        super(msg);
    }
    public UsernameAlreadyTakenException(String msg, Throwable cause) {
        super(msg ,cause);
    }
}
