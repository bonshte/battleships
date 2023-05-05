package game.battleships.server.exception.ingame;

public class NotOnTurnException extends Exception {
    public NotOnTurnException(String msg) {
        super(msg);
    }
    public NotOnTurnException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
