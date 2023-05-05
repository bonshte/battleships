package game.battleships.server.account;
import game.battleships.server.exception.game.SavedGameNotFoundException;
import game.battleships.server.game.BattleshipsGame;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.game.board.BattleshipGameBoard;
import game.battleships.server.logger.BattleshipLogger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
public class Account implements Serializable {

    private static final long serialVersionUID = -2407736139357564841L;
    private AccountCredentials accountCredentials;
    private List<BattleshipsGame> savedGames;
    private transient BattleshipGameLobby currentGameLobby;
    private transient AccountStatus accountStatus;

    public Account(AccountCredentials userCredentials) {
        if (userCredentials == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        this.accountCredentials = userCredentials;
        this.accountStatus = AccountStatus.OFFLINE;
        this.savedGames = new LinkedList<>();
    }
    public BattleshipsGame getSavedGameByID(int id) throws SavedGameNotFoundException {
        for (var savedGame : savedGames) {
            if (savedGame.getGameId() == id) {
                return savedGame;
            }
        }
        throw new SavedGameNotFoundException("game with such id not found");
    }
    public void saveGame(BattleshipsGame game) {
        if (game == null) {
            BattleshipLogger.getBattleshipLogger().warning("attempted to save null game");
            throw new IllegalArgumentException("null passed to saveGame");
        }
        savedGames.add(game);
    }
    public void deleteSavedGameById(int gameId) {
        var iterator = savedGames.iterator();
        while (iterator.hasNext()) {
            var game = iterator.next();
            if (game.getGameId() == gameId) {
                iterator.remove();
            }
        }
    }
    public String getUsername() {
        return accountCredentials.username();
    }
    public BattleshipGameLobby getCurrentGameLobby() {

        return currentGameLobby;
    }
    public List<BattleshipsGame> getSavedGames() {
        return savedGames;
    }
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    public void setAccountStatus(AccountStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("null passed to method");
        }
        this.accountStatus = status;
    }
    public void setCurrentGameLobby(BattleshipGameLobby currentGameLobby) {
        if (currentGameLobby == null) {
            throw new IllegalArgumentException("null passed to method");
        }
        this.currentGameLobby = currentGameLobby;
    }
    public boolean isLogged() {
        return accountStatus != AccountStatus.OFFLINE;
    }
    public boolean matchesCredentials(AccountCredentials userCredentials) {
        if (userCredentials == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        return this.accountCredentials.equals(userCredentials);
    }

    public String getProfileInfo() {
        return getUsername() + System.lineSeparator() + getAccountStatus() + System.lineSeparator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountCredentials, account.accountCredentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountCredentials);
    }
}
