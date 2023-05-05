package game.battleships.server.data;

import game.battleships.server.account.AccountStatus;
import game.battleships.server.game.BattleshipsGame;
import game.battleships.server.game.storage.AccountStorage;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.account.Account;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataBaseManager {
    private static final Path USERS_DATABASE_DIRECTORY_PATH = Path.of("battleships/data/users");
    private static final String ACCOUNT_DATA_EXTENSION = ".txt";
    public static int latestGameID = 0;

    static {
        try {
            Files.createDirectories(USERS_DATABASE_DIRECTORY_PATH);
        } catch (IOException e) {
            BattleshipLogger.getBattleshipLogger().warning("could not create database");
        }
    }
    public void updateAccountDataFile(Account account) {
        try (ObjectOutputStream userObjectWriter = new ObjectOutputStream(
                new FileOutputStream(USERS_DATABASE_DIRECTORY_PATH.resolve(
                        account.getUsername()) + ACCOUNT_DATA_EXTENSION))) {
            userObjectWriter.writeObject(account);
            userObjectWriter.flush();
        } catch (IOException e) {
            //write something to console
        }

    }
    public void updateAccountsDataFiles(AccountStorage accountStorage) {
        for (var account : accountStorage.getUsers()) {
            updateAccountDataFile(account);
        }
    }
    public Set<Account> loadAllAccountsData() {
        Set<Account> accounts = new HashSet<>();
        try (var userFiles = Files.newDirectoryStream(USERS_DATABASE_DIRECTORY_PATH)) {
            for (var accountPath : userFiles) {
                try {
                    Account loadedAccount = getAccountFromPath(accountPath);
                    updateLatestGameID(loadedAccount.getSavedGames());
                    loadedAccount.setAccountStatus(AccountStatus.OFFLINE);
                    accounts.add(loadedAccount);
                } catch (FileNotFoundException e) {
                    BattleshipLogger.getBattleshipLogger().warning("user data file missing" + e.getMessage());
                } catch ( IOException e) {
                    BattleshipLogger.getBattleshipLogger().config("error reading from file" + e.getMessage());
                    throw new IOException("could not read " + accountPath.toString(), e);
                }
            }
        } catch (IOException e) {
            BattleshipLogger.getBattleshipLogger().warning(e.getMessage());
            throw new RuntimeException("could no read from user data directory");
        }
        BattleshipsGame.setGlobalGameId(latestGameID);
        return accounts;
    }

    static Account getAccountFromPath(Path accountPath) throws  IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(accountPath.toString()))) {
            return (Account) objectInputStream.readObject();
        } catch (ClassNotFoundException x) {
            BattleshipLogger.getBattleshipLogger().warning("user data file does not contain the expected data");
            throw new IllegalStateException("user data file does not contain the expected data");
        }
    }

    static void updateLatestGameID(List<BattleshipsGame> savedGames) {
        for (var savedGame : savedGames) {
            latestGameID = Math.max(latestGameID, savedGame.getGameId());
        }
    }
}
