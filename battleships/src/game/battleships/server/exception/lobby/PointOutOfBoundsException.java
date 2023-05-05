package game.battleships.server.exception.lobby;

public class PointOutOfBoundsException extends Exception {
    public PointOutOfBoundsException(String msg) {
        super(msg);
    }
    public PointOutOfBoundsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
