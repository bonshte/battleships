package game.battleships.server.request.handler.composition;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.exception.ship.InvalidShipException;
import game.battleships.server.exception.ship.composition.InvalidShipCompositionException;
import game.battleships.server.exception.ship.point.InvalidPointException;
import game.battleships.server.game.board.BattleshipGameBoard;
import game.battleships.server.game.board.Ship;
import game.battleships.server.game.board.ShipComposition;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.lobby.AbstractLobbyRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import game.battleships.server.session.SetUpStage;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class MakingCompositionRequestHandler extends AbstractLobbyRequestHandler {
    private static final int SHIP_LEN_2 = 2;
    private static final int SHIP_LEN_3 = 3;
    private static final int SHIP_LEN_4 = 4;
    private static final int SHIP_LEN_5 = 5;
    private static final String REMOVE_LAST_COMMAND = "remove last";
    private static final String LEAVE_COMMAND = "leave";
    private static final String COMPLETE_COMMAND = "complete";

    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        if (clientInputString.equals(COMPLETE_COMMAND)) {
            completeCommandHandle(clientSocketChannel, clientSession);
        } else if (clientInputString.equals(REMOVE_LAST_COMMAND)) {
            removeLastCommandHandle(clientSocketChannel, clientSession);
        } else if (clientInputString.equals(LEAVE_COMMAND)) {
            leaveCommandHandle(clientSocketChannel, clientSession);
        } else {
            if (clientSession.getSetUpStage() == SetUpStage.READY) {
                writeClientOutput(clientSocketChannel, ConsoleUI.SHIP_COMPOSITION_BUILT_MESSAGE);
                return;
            }
            shipGenerateCommandHandle(clientInputString, clientSocketChannel, clientSession);
        }
    }
    private void shipGenerateCommandHandle(String clientInputString,
            SocketChannel clientSocketChannel, Session clientSession) throws IOException {
        Ship shipCreated;
        try {
            shipCreated = new Ship(clientInputString);
        } catch (InvalidShipException | InvalidPointException e) {
            BattleshipLogger.getBattleshipLogger().info("invalid ship parameters passed" + clientInputString);
            writeClientOutput(clientSocketChannel, ConsoleUI.INVALID_SHIP_MESSAGE + ConsoleUI.TRY_AGAIN_MESSAGE);
            return;
        }
        int shipLength = shipCreated.getShipLength();
        int desiredLength = clientSession.getSetUpStage().getLength();
        if (shipLength != desiredLength) {
            BattleshipLogger.getBattleshipLogger().info("invalid length for a ship passed" + clientInputString);
            writeClientOutput(clientSocketChannel, ConsoleUI.INVALID_SHIP_LENGTH_MESSAGE +
                    ConsoleUI.TRY_AGAIN_MESSAGE);
            return;
        }
        var shipsGeneratedSoFar = clientSession.getShipSetUp();
        shipsGeneratedSoFar.add(shipCreated);
        if (ShipComposition.shipsCollide(shipsGeneratedSoFar)) {
            shipsGeneratedSoFar.remove(shipCreated);
            writeClientOutput(clientSocketChannel, ConsoleUI.GENERATED_SHIP_COLLIDES_MESSAGE +
                    ConsoleUI.TRY_AGAIN_MESSAGE);
            return;
        }
        clientSession.nextSetUpStage();
        String nextShipMessage = getNextShipExpectedMessage(clientSession.getSetUpStage());
        writeClientOutput(clientSocketChannel, BattleshipGameBoard.visualizeFleet(clientSession.getShipSetUp()));
        writeClientOutput(clientSocketChannel, nextShipMessage);
    }
    private void leaveCommandHandle(SocketChannel clientSocketChannel, Session clientSession) throws IOException {
        clientSession.clearSetUp();
        writeClientOutput(clientSocketChannel, ConsoleUI.LOBBY_MENU);
        clientSession.setSessionStatus(SessionStatus.LOBBY_MENU);
    }
    private void removeLastCommandHandle(SocketChannel clientSocketChannel, Session clientSession) throws IOException {
        clientSession.previousSetUpStage();
        writeClientOutput(clientSocketChannel, BattleshipGameBoard.visualizeFleet(clientSession.getShipSetUp()));
        writeClientOutput(clientSocketChannel, getNextShipExpectedMessage(clientSession.getSetUpStage()));
    }
    private void completeCommandHandle(SocketChannel clientSocketChannel, Session clientSession) throws IOException {
        if (clientSession.getSetUpStage() != SetUpStage.READY) {
            writeClientOutput(clientSocketChannel, ConsoleUI.COMPOSITION_NOT_READY_MESSAGE);
            return;
        }
        try {
            ShipComposition selfMadeShipComposition = new ShipComposition(clientSession.getShipSetUp());
            var gameLobby = clientSession.getAccountAssociated().getCurrentGameLobby();
            gameLobby.setShipComposition(selfMadeShipComposition, clientSession.getAccountAssociated());
            writeClientOutput(clientSocketChannel, ConsoleUI.YOUR_COMPOSITION_IS_SAVED_MESSAGE);
            clientSession.setSessionStatus(SessionStatus.LOBBY_MENU);
            writeClientOutput(clientSocketChannel, ConsoleUI.LOBBY_MENU);
            clientSession.clearSetUp();
        } catch (InvalidShipCompositionException e) {
            BattleshipLogger.getBattleshipLogger().config("invalid ship composition creation at ready stage");
            clientSession.clearSetUp();
            writeClientOutput(clientSocketChannel, ConsoleUI.GENERAL_ERROR + ConsoleUI.BEGIN_FROM_START_MESSAGE);
        } catch (PlayerNotInLobbyException e) {
            returnForceFullyToMainMenu(clientSocketChannel, clientSession);
            BattleshipLogger.getBattleshipLogger().warning(
                    "player attempted actions on lobby he is not inside" + e.getMessage());

        }
    }

    private String getNextShipExpectedMessage(SetUpStage setUpStage) {
        int length = setUpStage.getLength();
        switch (length) {
            case SHIP_LEN_2:
                return ConsoleUI.ENTER_TWO_LENGTH_SHIP;
            case SHIP_LEN_3:
                return ConsoleUI.ENTER_THREE_LENGTH_SHIP;
            case SHIP_LEN_4:
                return ConsoleUI.ENTER_FOUR_LENGTH_SHIP;
            case SHIP_LEN_5:
                return ConsoleUI.ENTER_FIVE_LENGTH_SHIP;
            default:
                return ConsoleUI.PRESS_COMPLETE_MESSAGE;
        }
    }





}
