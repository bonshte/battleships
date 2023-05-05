package game.battleships.server.exception.ship.point;

public class InvalidPointException extends Exception {
    public InvalidPointException(String msg) {
        super(msg);
    }
    public InvalidPointException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
