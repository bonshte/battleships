package game.battleships.server.exception.game;

public class GameNotFoundException extends Exception {
    public GameNotFoundException(String msg) {
        super(msg);
    }
    public GameNotFoundException(String msg, Throwable cause) {
        super(msg, cause);

    }
}
