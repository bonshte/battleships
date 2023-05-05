package game.battleships.server.exception.game;

public class GameNotStartedException extends Exception {
    public GameNotStartedException(String msg) {
        super(msg);
    }
    public GameNotStartedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
