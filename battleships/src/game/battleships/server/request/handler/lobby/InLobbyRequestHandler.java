package game.battleships.server.request.handler.lobby;

import game.battleships.server.data.ServerData;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.exception.ship.composition.ShipCompositionNotReadyException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.game.board.BattleshipGameBoard;
import game.battleships.server.game.board.ShipComposition;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class InLobbyRequestHandler extends AbstractLobbyRequestHandler {
    private static final String READY_COMMAND = "ready";
    private static final String NOT_READY_COMMAND = "not ready";
    private static final String MAKE_SHIP_COMPOSITION_COMMAND = "make ship composition";
    private static final String RANDOM_SHIP_COMPOSITION_COMMAND = "random ship composition";
    private static final String LEAVE_COMMAND = "leave";


    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        BattleshipGameLobby lobby = clientSession.getAccountAssociated().getCurrentGameLobby();
        switch (clientInputString) {
            case READY_COMMAND:
                try {
                    lobby.ready(clientSession.getAccountAssociated());
                    notifyLobbyMembersPlayerIsReady(lobby, clientSession.getAccountAssociated());
                    if (lobby.getGame().isPlaying()) {
                        notifyPlayersGameStarted(lobby);
                    }
                } catch (ShipCompositionNotReadyException e) {
                    writeClientOutput(clientSocketChannel, ConsoleUI.SHIP_COMPOSITION_MISSING_MESSAGE);
                    return;
                } catch (PlayerNotInLobbyException e) {
                    returnForceFullyToMainMenu(clientSocketChannel, clientSession);
                    BattleshipLogger.getBattleshipLogger().warning(
                            "player attempted actions on lobby he is not inside" + e.getMessage());
                    return;
                }
                break;
            case NOT_READY_COMMAND:
                try {
                    lobby.notReady(clientSession.getAccountAssociated());
                    notifyLobbyMembersPlayerNotReady(lobby, clientSession.getAccountAssociated());
                } catch (PlayerNotInLobbyException e ) {
                    returnForceFullyToMainMenu(clientSocketChannel, clientSession);
                    BattleshipLogger.getBattleshipLogger().warning(
                            "player attempted actions on lobby he is not inside" + e.getMessage());
                }
                break;
            case MAKE_SHIP_COMPOSITION_COMMAND:
                try {
                    if (lobby.playerIsReady(clientSession.getAccountAssociated())) {
                        writeClientOutput(clientSocketChannel, ConsoleUI.CANNOT_COMPOSE_WHEN_READY);
                        return;
                    }
                } catch (PlayerNotInLobbyException e) {
                    returnForceFullyToMainMenu(clientSocketChannel, clientSession);
                    BattleshipLogger.getBattleshipLogger().warning(
                            "client attempted actions on lobby he is not inside" + e.getMessage());
                    return;
                }
                clientSession.setSessionStatus(SessionStatus.MAKING_COMPOSITION);
                writeClientOutput(clientSocketChannel, ConsoleUI.MAKING_COMPOSITION_MESSAGE);
                writeClientOutput(clientSocketChannel, ConsoleUI.MAKING_SHIP_COMPOSITION_MENU);
                writeClientOutput(clientSocketChannel, ConsoleUI.SHIP_CONSTRUCTION_TIP);
                writeClientOutput(clientSocketChannel, ConsoleUI.ENTER_TWO_LENGTH_SHIP);
                writeClientOutput(clientSocketChannel,
                        BattleshipGameBoard.visualizeFleet(clientSession.getShipSetUp()));
                break;
            case RANDOM_SHIP_COMPOSITION_COMMAND:
                ShipComposition generatedShipComposition = ShipComposition.generateRandomShipComposition();
                String visualizedBoard = new BattleshipGameBoard(generatedShipComposition).getVisualizedBoard();
                var gameLobby = clientSession.getAccountAssociated().getCurrentGameLobby();
                try {
                    gameLobby.setShipComposition(generatedShipComposition, clientSession.getAccountAssociated());
                } catch (PlayerNotInLobbyException e) {
                    returnForceFullyToMainMenu(clientSocketChannel, clientSession);
                    BattleshipLogger.getBattleshipLogger().warning(
                            "player attempted actions on lobby he is not inside" + e.getMessage());
                    return;
                }
                writeClientOutput(clientSocketChannel, visualizedBoard);
                break;
            case LEAVE_COMMAND:
                try {
                    lobby.removePlayer(clientSession.getAccountAssociated());
                    clientSession.getAccountAssociated().setAccountStatus(AccountStatus.ONLINE);
                    clientSession.setSessionStatus(SessionStatus.MAIN_MENU);
                    writeClientOutput(clientSocketChannel, ConsoleUI.MAIN_MENU);
                } catch (PlayerNotInLobbyException e) {
                    returnForceFullyToMainMenu(clientSocketChannel, clientSession);
                    BattleshipLogger.getBattleshipLogger().warning(
                            "player attempted actions on lobby he is not inside" + e.getMessage());
                    return;
                }
                break;
            default:
                writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_COMMAND +
                        ConsoleUI.LOBBY_MENU);
        }
    }










}
