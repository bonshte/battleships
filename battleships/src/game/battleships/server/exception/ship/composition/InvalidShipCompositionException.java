package game.battleships.server.exception.ship.composition;

public class InvalidShipCompositionException extends Exception {
    public InvalidShipCompositionException(String msg) {
        super(msg);
    }
    public InvalidShipCompositionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
