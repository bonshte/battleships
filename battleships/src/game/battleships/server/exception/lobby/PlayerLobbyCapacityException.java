package game.battleships.server.exception.lobby;

public class PlayerLobbyCapacityException extends Exception {
    public PlayerLobbyCapacityException(String msg) {
        super(msg);
    }
    public PlayerLobbyCapacityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
