package game.battleships.server.request.handler.saved;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.game.SavedGameNotFoundException;
import game.battleships.server.game.BattleshipsGame;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class SavedGamesRequestHandler extends AbstractRequestHandler {
    private static final String MAIN_MENU = "main menu";
    private static final String WORD_SEPARATOR = " ";
    private static final String CONTINUE = "continue";

    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        if (clientInputString.equals(MAIN_MENU)) {
            writeClientOutput(clientSocketChannel, ConsoleUI.MAIN_MENU);
            clientSession.setSessionStatus(SessionStatus.MAIN_MENU);
            return;
        }
        String[] clientWords = clientInputString.split(WORD_SEPARATOR);
        if (clientWords.length != 2) {
            BattleshipLogger.getBattleshipLogger().info("client sent invalid command in saved games menu");
            writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                    ConsoleUI.SAVED_GAMES_MENU);
            return;
        }
        if (clientWords[0].equals(CONTINUE)) {
            BattleshipsGame gameToContinue;
            try {
                int gameId = Integer.parseInt(clientWords[1]);
                gameToContinue = clientSession.getAccountAssociated().getSavedGameByID(gameId);
                BattleshipGameLobby newGameLobby =
                        new BattleshipGameLobby(clientSession.getAccountAssociated(),
                                gameToContinue,  clientSocketChannel, clientSession);
                clientSession.getAccountAssociated().setCurrentGameLobby(newGameLobby);
                clientSession.setSessionStatus(SessionStatus.LOBBY_MENU);
                writeClientOutput(clientSocketChannel,  ConsoleUI.LOBBY_MENU);
            } catch (NumberFormatException e) {
                BattleshipLogger.getBattleshipLogger().info(
                        "user sent string which is not number for id of a saved game");
                writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                        ConsoleUI.SAVED_GAMES_MENU);
            } catch (SavedGameNotFoundException e) {
                BattleshipLogger.getBattleshipLogger().config(e.getMessage());
                writeClientOutput(clientSocketChannel,  ConsoleUI.SAVED_GAME_NOT_FOUND_MESSAGE +
                        ConsoleUI.ACTIONS_ALLOWED_MESSAGE + ConsoleUI.SAVED_GAMES_MENU);
            }
        } else {
            BattleshipLogger.getBattleshipLogger().info("client send invalid command in saved games menu");
            writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                    ConsoleUI.SAVED_GAMES_MENU);
        }
    }
}
