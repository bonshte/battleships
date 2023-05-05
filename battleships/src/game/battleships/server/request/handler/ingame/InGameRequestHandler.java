package game.battleships.server.request.handler.ingame;

import game.battleships.server.data.ServerData;
import game.battleships.server.account.Account;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.game.GameNotStartedException;
import game.battleships.server.exception.ingame.NotOnTurnException;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.exception.ship.point.InvalidPointException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.game.StrikeStatistic;
import game.battleships.server.game.board.BattleshipFieldPoint;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.lobby.AbstractLobbyRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class InGameRequestHandler extends AbstractLobbyRequestHandler {
    private static final String WINNER = "WINNER - ";
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        Account account = clientSession.getAccountAssociated();
        BattleshipGameLobby lobby = account.getCurrentGameLobby();
        String clientInputString = getClientInputString(clientSocketChannel);
        try {
            BattleshipFieldPoint pointToStrike = new BattleshipFieldPoint(clientInputString);
            StrikeStatistic strikeStatistic = lobby.strikeEnemy(pointToStrike, account);
            notifyLobbyMembersAboutLastStrike(lobby, strikeStatistic);
            if (lobby.getGame().gameOver()) {
                notifyMembersAboutWinner(account.getUsername(), lobby);
                lobby.movePlayersToMainMenu();
                lobby.clearLobby();
                serverData.getGameLobbyStorage().removeLobby(lobby);
            }
        } catch (GameNotStartedException e) {
            BattleshipLogger.getBattleshipLogger().warning("in game request on a not started game" + e.getMessage());
            clientSession.setSessionStatus(SessionStatus.LOBBY_MENU);
        } catch (PlayerNotInLobbyException e) {
            returnForceFullyToMainMenu(clientSocketChannel, clientSession);
            BattleshipLogger.getBattleshipLogger().warning(
                    "client attempted actions on lobby he is not inside" + e.getMessage());
        } catch (InvalidPointException e) {

            writeClientOutput(clientSocketChannel, ConsoleUI.INVALID_COORDINATES_MESSAGE +
                        ConsoleUI.TRY_AGAIN_MESSAGE);
        } catch (NotOnTurnException e) {
            writeClientOutput(clientSocketChannel, ConsoleUI.WAIT_FOR_TURN_MESSAGE);
        }
    }

    private void notifyLobbyMembersAboutLastStrike(BattleshipGameLobby gameLobby, StrikeStatistic strikeStatistic)
            throws IOException, PlayerNotInLobbyException {
        var firstPlayerSocketChannel = gameLobby.getFirstPlayerSocketChannel();
        var secondPlayerSocketChannel = gameLobby.getSecondPlayerSocketChannel();
        var spectatorsSocketChannels = gameLobby.getSpectatorsSocketChannels();

        writeClientOutput(firstPlayerSocketChannel, gameLobby.getGame().visualizeFirstPlayerBoards());
        writeClientOutput(firstPlayerSocketChannel, strikeStatistic.toString());
        writeClientOutput(secondPlayerSocketChannel, gameLobby.getGame().visualizeSecondPlayerBoards());
        writeClientOutput(secondPlayerSocketChannel, strikeStatistic.toString());
        for (var spectatorSocketChannel : spectatorsSocketChannels) {
            writeClientOutput(spectatorSocketChannel, gameLobby.getGame().visualizeSpectatorBoards(
                    gameLobby.getFirstPlayerUsername(), gameLobby.getSecondPlayerUsername()));
            writeClientOutput(spectatorSocketChannel, strikeStatistic.toString());
        }
    }
    private void notifyMembersAboutWinner(String winner, BattleshipGameLobby gameLobby)
            throws IOException, PlayerNotInLobbyException {
        var firstPlayerSocketChannel = gameLobby.getFirstPlayerSocketChannel();
        var secondPlayerSocketChannel = gameLobby.getSecondPlayerSocketChannel();
        var spectatorsSocketChannels = gameLobby.getSpectatorsSocketChannels();
        writeClientOutput(firstPlayerSocketChannel, WINNER + winner);
        writeClientOutput(secondPlayerSocketChannel, WINNER + winner);
        for (var spectatorSocketChannel : spectatorsSocketChannels) {
            writeClientOutput(spectatorSocketChannel, WINNER + winner);
        }
    }

}
