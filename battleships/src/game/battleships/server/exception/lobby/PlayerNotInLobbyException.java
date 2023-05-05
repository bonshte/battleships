package game.battleships.server.exception.lobby;

public class PlayerNotInLobbyException extends Exception {
    public PlayerNotInLobbyException(String msg) {
        super(msg);
    }
    public PlayerNotInLobbyException(String msg, Throwable cause) {
        super(msg , cause);
    }
}
