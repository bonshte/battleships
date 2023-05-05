package game.battleships.server.data;

import game.battleships.server.game.storage.AccountStorage;
import game.battleships.server.game.storage.BattleshipGameLobbyStorage;

import java.io.IOException;

public class ServerData {
    private BattleshipGameLobbyStorage gameLobbyStorage;
    private DataBaseManager dataBaseManager;
    private AccountStorage accountStorage;

    public ServerData() throws IOException {
        this.dataBaseManager = new DataBaseManager();
        this.accountStorage = new AccountStorage(dataBaseManager.loadAllAccountsData());
        this.gameLobbyStorage = new BattleshipGameLobbyStorage();
    }

    public BattleshipGameLobbyStorage getGameLobbyStorage() {
        return gameLobbyStorage;
    }

    public DataBaseManager getDataBaseManager() {
        return dataBaseManager;
    }

    public AccountStorage getAccountStorage() {
        return accountStorage;
    }
}
