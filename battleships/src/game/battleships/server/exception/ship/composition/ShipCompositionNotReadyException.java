package game.battleships.server.exception.ship.composition;

import game.battleships.server.game.board.ShipComposition;

public class ShipCompositionNotReadyException extends Exception {
    public ShipCompositionNotReadyException(String msg) {
        super(msg);
    }
    public ShipCompositionNotReadyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
