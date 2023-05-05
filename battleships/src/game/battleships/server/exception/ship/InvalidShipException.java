package game.battleships.server.exception.ship;

public class InvalidShipException extends Exception {
    public InvalidShipException(String msg) {
        super(msg);
    }
    public InvalidShipException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
