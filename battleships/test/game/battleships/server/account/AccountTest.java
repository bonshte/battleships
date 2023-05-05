package game.battleships.server.account;

import game.battleships.server.exception.game.SavedGameNotFoundException;
import game.battleships.server.game.BattleshipsGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private AccountCredentials accountCredentials;

    @BeforeEach
    public void setUp() {
        try {
            accountCredentials = new AccountCredentials("Ivancho", "Ivancho123".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void testWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Account(null));
    }


    @Test
    void getCorrectSavedGameById() {
        BattleshipsGame game1 = new BattleshipsGame();
        BattleshipsGame game2 = new BattleshipsGame();
        try {
            Account account = new Account(accountCredentials);
            int gameId = game1.getGameId();
            account.saveGame(game1);
            account.saveGame(game2);
            BattleshipsGame gameDesired = account.getSavedGameByID(gameId);
            assertEquals(game1, gameDesired, "the desired game should be game1");
            assertEquals(2, account.getSavedGames().size(), "only two games must be saved");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testGetSavedGameByIdForMissingGame() {
        try {
            Account account = new Account(accountCredentials);
            assertThrows(SavedGameNotFoundException.class, () -> account.getSavedGameByID(1),
                    "no games are saved");

        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void testNewPlayerHaveNoSavedGames() {
        try {
            Account account = new Account(accountCredentials);
            assertTrue(account.getSavedGames().isEmpty(), "new account should have no saved games");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testDeleteSavedGame() {
        BattleshipsGame game1 = new BattleshipsGame();
        BattleshipsGame game2 = new BattleshipsGame();
        try {
            Account account = new Account(accountCredentials);
            int gameId = game1.getGameId();
            account.saveGame(game1);
            account.saveGame(game2);
            account.deleteSavedGameById(gameId);
            assertThrows(SavedGameNotFoundException.class, () -> account.getSavedGameByID(gameId),
                    "game should be deleted");


        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testMatchesCredentialsWorksCorrectly() {
        try {
            Account account = new Account(accountCredentials);
            assertTrue(account.matchesCredentials(accountCredentials), "credentials should match");
            assertThrows(IllegalArgumentException.class, () -> account.matchesCredentials(null),
                    "method should throw invalid arugment when null is passed");

        } catch (Exception e) {
            fail();
        }
    }

}