package game.battleships.server.game.storage;

import game.battleships.server.account.Account;
import game.battleships.server.account.AccountCredentials;
import game.battleships.server.exception.authentication.InvalidCredentialsException;
import game.battleships.server.exception.authentication.UsernameAlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.CredentialException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AccountStorageTest {
    private Account account1;
    private AccountCredentials credentials1;
    private Account account2;
    private AccountCredentials credentials2;

    @BeforeEach
    public void setUp() {
        try {
            credentials1 = new AccountCredentials(
                    "ivancho","Ivancho1".getBytes(StandardCharsets.UTF_8));
            credentials2 = new AccountCredentials(
                    "ivancho","Ivancho2".getBytes(StandardCharsets.UTF_8));
            account1 = new Account(credentials1);
            account2 = new Account(credentials2);
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    void testRegisterUser() {
        AccountStorage accountStorage = new AccountStorage();
        assertDoesNotThrow(() -> accountStorage.registerUser(account1),
                "registering first user should not throw exception");
        assertThrows(UsernameAlreadyTakenException.class, () -> accountStorage.registerUser(account2),
                "there is already a user with this username");

    }
    @Test
    void testRegisterWithNullArgument() {
        AccountStorage accountStorage = new AccountStorage();
        assertThrows(IllegalArgumentException.class, () -> accountStorage.registerUser(null),
                "should throw illegal argument when null is passed");
    }

    @Test
    void testGetUserByCredentials() {
        AccountStorage accountStorage = new AccountStorage();
        assertDoesNotThrow(() -> accountStorage.registerUser(account1),
                "registering first user should not throw exception");
        try {
            Account account = accountStorage.getUserByCredentials(credentials1);
            assertEquals(account1, account, "the returned account should be acount1");
            assertThrows(InvalidCredentialsException.class, () -> accountStorage.getUserByCredentials(credentials2),
                    "no account matching those credentials should be in that storage");
        } catch (InvalidCredentialsException e) {
            fail();
        }
    }

}