package game.battleships.server.exception.game;

public class GameStatusException extends Exception {
    public GameStatusException(String msg) {
        super(msg);
    }
    public GameStatusException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
