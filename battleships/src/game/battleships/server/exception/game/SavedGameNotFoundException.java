package game.battleships.server.exception.game;

public class SavedGameNotFoundException extends Exception {
    public SavedGameNotFoundException(String msg) {
        super(msg);
    }
    public SavedGameNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
