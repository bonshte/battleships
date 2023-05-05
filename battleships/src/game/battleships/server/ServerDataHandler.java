package game.battleships.server;
import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.request.handler.composition.MakingCompositionRequestHandler;
import game.battleships.server.request.handler.ingame.InGameRequestHandler;
import game.battleships.server.request.handler.ingame.SpectatingRequestHandler;
import game.battleships.server.request.handler.lobby.BrowseLobbiesRequestHandler;
import game.battleships.server.request.handler.authentication.AuthenticationMenuRequestHandler;
import game.battleships.server.request.handler.accepted.ConnectedRequestHandler;
import game.battleships.server.request.handler.authentication.LoggingPasswordRequestHandler;
import game.battleships.server.request.handler.authentication.LoggingUsernameRequestHandler;
import game.battleships.server.request.handler.lobby.InLobbyRequestHandler;
import game.battleships.server.request.handler.menu.MainMenuRequestHandler;
import game.battleships.server.request.handler.authentication.RegisterPasswordRequestHandler;
import game.battleships.server.request.handler.authentication.RegisterUsernameRequestHandler;
import game.battleships.server.request.handler.RequestHandlingInterface;
import game.battleships.server.request.handler.saved.SavedGamesRequestHandler;
import game.battleships.server.session.Session;
import java.io.IOException;
import java.nio.channels.SocketChannel;
public class ServerDataHandler {
    private RequestHandlingInterface handlingStrategy;
    private ServerData serverData;

    public ServerDataHandler() throws IOException {
        this.serverData = new ServerData();
    }

    public ServerData getServerData() {
        return serverData;
    }
    public void handleRequest(SocketChannel clientSocketChannel, Session clientSession)
            throws IOException, ClientDisconnectedException {
        switch (clientSession.getSessionStatus()) {
            case CONNECTED:
                handlingStrategy = new ConnectedRequestHandler();
                break;
            case AUTHENTICATION_MENU:
                handlingStrategy = new AuthenticationMenuRequestHandler();
                break;
            case REGISTERING_USERNAME:
                handlingStrategy = new RegisterUsernameRequestHandler();
                break;
            case REGISTERING_PASSWORD:
                handlingStrategy = new RegisterPasswordRequestHandler();
                break;
            case LOGGING_USERNAME:
                handlingStrategy = new LoggingUsernameRequestHandler();
                break;
            case LOGGING_PASSWORD:
                handlingStrategy = new LoggingPasswordRequestHandler();
                break;
            case MAIN_MENU:
                handlingStrategy = new MainMenuRequestHandler();
                break;
            case SAVED_GAMES_MENU:
                handlingStrategy = new SavedGamesRequestHandler();
                break;
            case BROWSE_LOBBIES_MENU:
                handlingStrategy = new BrowseLobbiesRequestHandler();
                break;
            case LOBBY_MENU:
                handlingStrategy = new InLobbyRequestHandler();
                break;
            case SPECTATING:
                handlingStrategy = new SpectatingRequestHandler();
                break;
            case MAKING_COMPOSITION:
                handlingStrategy = new MakingCompositionRequestHandler();
                break;
            case IN_GAME:
                handlingStrategy = new InGameRequestHandler();
                break;
            case DISCONNECTED:
                //disconnected clients should not be able to communicate with server
                return;
        }
        handlingStrategy.handle(clientSocketChannel, clientSession, serverData);
    }
    public void saveCurrentServerData() {
        serverData.getDataBaseManager().updateAccountsDataFiles(serverData.getAccountStorage());
    }

}
