package game.battleships.server.request.handler.menu;

import game.battleships.server.data.ServerData;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class MainMenuRequestHandler extends AbstractRequestHandler {
    private static final String LOG_OUT_COMMAND = "log out";
    private static final String CREATE_LOBBY_COMMAND = "create lobby";
    private static final String FRIENDS_COMMAND = "friends";
    private static final String FRIEND_REQUESTS_COMMAND = "friend requests";
    private static final String BROWSE_LOBBIES_COMMAND = "browse lobbies";
    private static final String PROFILE_COMMAND = "profile";
    private static final String SAVED_GAMES_COMMAND = "saved games";
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        switch (clientInputString) {
            case LOG_OUT_COMMAND:
                clientSession.logOutAssociatedAccount();
                clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
                writeClientOutput(clientSocketChannel, ConsoleUI.LOGGED_OUT_MESSAGE +
                        ConsoleUI.AUTHENTICATION_MENU);
                break;
            case FRIENDS_COMMAND:
            case FRIEND_REQUESTS_COMMAND:
                BattleshipLogger.getBattleshipLogger().config("friends service attempt");
                writeClientOutput(clientSocketChannel,
                        ConsoleUI.FUNCTIONALITY_IN_DEVELOPMENT_MESSAGE);
                break;
            case PROFILE_COMMAND:
                String profileInfo = clientSession.getAccountAssociated().getProfileInfo();
                writeClientOutput(clientSocketChannel, profileInfo);
                break;
            case CREATE_LOBBY_COMMAND:
                writeClientOutput(clientSocketChannel,  ConsoleUI.LOBBY_MENU);
                BattleshipGameLobby gameLobby =
                        new BattleshipGameLobby(clientSession.getAccountAssociated(),
                                clientSocketChannel, clientSession);
                clientSession.getAccountAssociated().setCurrentGameLobby(gameLobby);
                serverData.getGameLobbyStorage().addLobby(gameLobby);
                clientSession.getAccountAssociated().setAccountStatus(AccountStatus.IN_LOBBY);
                clientSession.setSessionStatus(SessionStatus.LOBBY_MENU);
                break;
            case BROWSE_LOBBIES_COMMAND:
                writeClientOutput(clientSocketChannel,  ConsoleUI.LOBBIES_MESSAGE);
                var lobbies = serverData.getGameLobbyStorage().getLobbies();
                for (var lobby : lobbies) {
                    writeClientOutput(clientSocketChannel,  lobby.getLobbyInformation() + ConsoleUI.HORIZONTAL_LINE);
                }
                writeClientOutput(clientSocketChannel,  ConsoleUI.BROWSE_LOBBIES_MENU);
                clientSession.setSessionStatus(SessionStatus.BROWSE_LOBBIES_MENU);
                break;
            case SAVED_GAMES_COMMAND:
                writeClientOutput(clientSocketChannel,  ConsoleUI.SAVED_GAMES_MESSAGE);
                var savedGames = clientSession.getAccountAssociated().getSavedGames();
                for (var savedGame : savedGames) {
                    writeClientOutput(clientSocketChannel,  savedGame.getGameInfo());
                }
                clientSession.setSessionStatus(SessionStatus.SAVED_GAMES_MENU);
                writeClientOutput(clientSocketChannel,
                        ConsoleUI.SAVED_GAMES_MENU);
                return;
            default:
                BattleshipLogger.getBattleshipLogger().info("client sent invalid command in main menu");
                writeClientOutput(clientSocketChannel,  ConsoleUI.ACTIONS_ALLOWED_MESSAGE +
                        ConsoleUI.MAIN_MENU);
        }
    }
}
