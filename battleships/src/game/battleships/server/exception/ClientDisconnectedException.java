package game.battleships.server.exception;

public class ClientDisconnectedException extends Exception {
    public ClientDisconnectedException(String msg) {
        super(msg);
    }
    public ClientDisconnectedException(String msg, Throwable cause) {
        super(msg, cause);
    }


}
