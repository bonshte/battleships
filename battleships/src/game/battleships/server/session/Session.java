package game.battleships.server.session;

import game.battleships.server.account.Account;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.game.board.Ship;
import game.battleships.server.game.storage.BattleshipGameLobbyStorage;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Session {
    private SessionStatus sessionStatus;
    private Account accountAssociated;
    private String lastInput;
    private SetUpStage setUpStage;
    private List<Ship> shipSetUp;

    public Session() {
        this.sessionStatus = SessionStatus.CONNECTED;
        this.setUpStage = SetUpStage.FIRST_SHIP_LEN2;
        this.shipSetUp = new LinkedList<>();
    }
    public void logOutAssociatedAccount() {
        if (accountAssociated != null) {
            accountAssociated.setAccountStatus(AccountStatus.OFFLINE);
        }
        accountAssociated = null;
    }

    public SetUpStage getSetUpStage() {
        return setUpStage;
    }
    public List<Ship> getShipSetUp() {
        return shipSetUp;
    }
    public void nextSetUpStage() {
        SetUpStage[] values = SetUpStage.values();
        setUpStage = values[(setUpStage.ordinal() + 1) % values.length];
    }
    public void previousSetUpStage() {
        if (setUpStage == SetUpStage.FIRST_SHIP_LEN2) {
            return;
        }
        SetUpStage[] values = SetUpStage.values();
        int currentIndex = setUpStage.ordinal();
        setUpStage = values[currentIndex - 1];
        if (shipSetUp.isEmpty()) {
            return;
        }
        shipSetUp.remove(shipSetUp.size() - 1);
    }
    public void clearSetUp() {
        setUpStage = SetUpStage.FIRST_SHIP_LEN2;
        shipSetUp = new LinkedList<>();
    }


    public void handleLeftGame(BattleshipGameLobbyStorage lobbyStorage) throws IOException {
        if (accountAssociated != null) {
            if (accountAssociated.getAccountStatus() == AccountStatus.IN_GAME) {
                handleLeavingFromPlaying(lobbyStorage);
            } else if (accountAssociated.getAccountStatus() == AccountStatus.IN_LOBBY) {
                handleLeavingFromLobbySetUp(lobbyStorage);
            } else if (accountAssociated.getAccountStatus() == AccountStatus.SPECTATING) {
                handleLeavingFromSpectating(lobbyStorage);
            }
        }
    }
    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }
    public void setSessionStatus(SessionStatus status) {
        this.sessionStatus = status;
    }
    public String getLastSessionInput() {
        return lastInput;
    }
    public void addInput(String input) {
        lastInput = input;
    }
    public Account getAccountAssociated() {
        return accountAssociated;
    }
    public void setAccountAssociated(Account accountAssociated) {
        this.accountAssociated = accountAssociated;
    }

    private void handleLeavingFromSpectating(BattleshipGameLobbyStorage lobbyStorage) {
        BattleshipGameLobby lobbySpectating = accountAssociated.getCurrentGameLobby();
        lobbySpectating.removeSpectator(accountAssociated);
        if (lobbySpectating.isEmpty()) {
            lobbyStorage.removeLobby(lobbySpectating);
        }
    }

    private void handleLeavingFromPlaying(BattleshipGameLobbyStorage lobbyStorage) throws IOException {
        BattleshipGameLobby lobby = accountAssociated.getCurrentGameLobby();
        try {
            lobby.removePlayer(accountAssociated);
            var membersSocketChannels = lobby.getLobbyMembersSocketChannels();
            for (var memberSocketChannel : membersSocketChannels) {
                AbstractRequestHandler.writeClientOutput(memberSocketChannel,
                        accountAssociated.getUsername() + ConsoleUI.LEFT_THE_GAME_MESSAGE);
            }
        } catch (PlayerNotInLobbyException e) {
            BattleshipLogger.getBattleshipLogger().warning(
                    "removing left player from lobby he was inside, error player is not in the lobby" +
                            e.getMessage());
        }
    }
    private void handleLeavingFromLobbySetUp(BattleshipGameLobbyStorage lobbyStorage) throws IOException {
        BattleshipGameLobby lobby = accountAssociated.getCurrentGameLobby();
        try {
            lobby.removePlayer(accountAssociated);
            if (lobby.isEmpty()) {
                lobbyStorage.removeLobby(lobby);
            }
        } catch (PlayerNotInLobbyException e) {
            BattleshipLogger.getBattleshipLogger().warning(
                    "removing left player from lobby he was inside, error player is not in the lobby" +
                            e.getMessage());
        }
    }
}
