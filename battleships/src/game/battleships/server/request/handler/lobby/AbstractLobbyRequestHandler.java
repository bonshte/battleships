package game.battleships.server.request.handler.lobby;

import game.battleships.server.account.Account;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

public abstract class AbstractLobbyRequestHandler extends AbstractRequestHandler {
    private static final String YOUR = "your";
    private static final String ENEMY = "enemy";
    private static final String TURN = "turn";
    protected void returnForceFullyToMainMenu(SocketChannel clientSocketChannel, Session clientSession)
            throws IOException {
        writeClientOutput(clientSocketChannel, ConsoleUI.GENERAL_ERROR);
        writeClientOutput(clientSocketChannel, ConsoleUI.MAIN_MENU);
        clientSession.setSessionStatus(SessionStatus.MAIN_MENU);
        clientSession.getAccountAssociated().setAccountStatus(AccountStatus.ONLINE);
    }
    protected void notifyLobbyMembersPlayerIsReady(BattleshipGameLobby gameLobby, Account account) throws IOException {
        for (var lobbyMemberSocketChannel : gameLobby.getLobbyMembersSocketChannels()) {
            writeClientOutput(lobbyMemberSocketChannel, account.getUsername() + ConsoleUI.IS_READY_MESSAGE);
        }
    }
    protected void notifyLobbyMembersPlayerNotReady(BattleshipGameLobby gameLobby, Account account) throws IOException {
        for (var lobbyMemberSocketChannel : gameLobby.getLobbyMembersSocketChannels()) {
            writeClientOutput(lobbyMemberSocketChannel, account.getUsername() + ConsoleUI.IS_NOT_READY_MESSAGE);
        }
    }
    protected void notifyPlayersGameStarted(BattleshipGameLobby gameLobby) throws IOException {
        String firstPlayerUsername = gameLobby.getFirstPlayerUsername();
        String secondPlayerUsername = gameLobby.getSecondPlayerUsername();
        gameLobby.getFirstPlayer().setAccountStatus(AccountStatus.IN_GAME);
        gameLobby.getFirstPlayer().setAccountStatus(AccountStatus.IN_GAME);
        SocketChannel firstPlayerSocketChannel;
        SocketChannel secondPlayerSocketChannel;
        try {
            firstPlayerSocketChannel = gameLobby.getFirstPlayerSocketChannel();
            secondPlayerSocketChannel = gameLobby.getSecondPlayerSocketChannel();
        } catch (PlayerNotInLobbyException e) {
            BattleshipLogger.getBattleshipLogger().warning("game started but player is missing" + e.getMessage());
            throw new IOException("game cannot be played when players are missing");
        }
        List<SocketChannel> spectatorsSocketChannels = gameLobby.getSpectatorsSocketChannels();
        String firstPlayerView = gameLobby.getGame().visualizeFirstPlayerBoards();
        String secondPlayerView = gameLobby.getGame().visualizeSecondPlayerBoards();
        String spectatorsView = gameLobby.getGame().visualizeSpectatorBoards(
                firstPlayerUsername, secondPlayerUsername);
        writeClientOutput(firstPlayerSocketChannel, ConsoleUI.GAME_STARTED_MESSAGE + firstPlayerView
                + YOUR + TURN + System.lineSeparator());
        writeClientOutput(secondPlayerSocketChannel, ConsoleUI.GAME_STARTED_MESSAGE + secondPlayerView +
                ENEMY + TURN + System.lineSeparator());
        for (var spectatorSocketChannel : spectatorsSocketChannels) {
            writeClientOutput(spectatorSocketChannel, ConsoleUI.GAME_STARTED_MESSAGE + spectatorsView +
                    firstPlayerUsername + TURN + System.lineSeparator());
        }
    }
}
