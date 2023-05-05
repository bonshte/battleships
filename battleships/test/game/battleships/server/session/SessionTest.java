package game.battleships.server.session;

import game.battleships.server.account.Account;
import game.battleships.server.account.AccountCredentials;
import game.battleships.server.account.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SessionTest {
    private Account account;
    @Mock
    SocketChannel socketChannel;
    private Session session;
    @BeforeEach
    public void setUp() {
        session = new Session();
        account = new Account(new AccountCredentials( "Ivancho", "Ivan123".getBytes(StandardCharsets.UTF_8)));
    }
    @Test
    void testSessionConnectsToAccount() {
        assertEquals(AccountStatus.OFFLINE, account.getAccountStatus(), "account should be offline when created");
        session.setAccountAssociated(account);
        assertEquals(account, session.getAccountAssociated(),
                "session should be associated with this account");
    }
    @Test
    void testLogoutFromAccountMakesAccountOffline() {
        account.setAccountStatus(AccountStatus.ONLINE);
        session.setAccountAssociated(account);
        session.logOutAssociatedAccount();
        assertEquals(AccountStatus.OFFLINE, account.getAccountStatus(),
                "account should become offline when the session log out of him");
        assertNull(session.getAccountAssociated(), "no account should be associated with this session");
    }

    @Test
    void testNextSetUpStageCorrectlyMoved() {
        session.nextSetUpStage();
        assertEquals(SetUpStage.SECOND_SHIP_LEN2, session.getSetUpStage(),
                "next set up stage for ship building is not correctly managed");
        session.nextSetUpStage();
        assertEquals(SetUpStage.THIRD_SHIP_LEN2, session.getSetUpStage(),
                " next set up stage for ship building is not correctly managed");
    }

    @Test
    void testPreviousSetUpStageCorrectlyMoved() {
        session.nextSetUpStage();
        session.nextSetUpStage();
        session.previousSetUpStage();
        assertEquals(SetUpStage.SECOND_SHIP_LEN2, session.getSetUpStage(),
                "previous set up stage for ship building is not correctly managed");
        session.previousSetUpStage();
        assertEquals(SetUpStage.FIRST_SHIP_LEN2, session.getSetUpStage(),
                "previous set up stage for ship building is not correctly managed");
        session.previousSetUpStage();
        assertEquals(SetUpStage.FIRST_SHIP_LEN2, session.getSetUpStage(),
                "previous set up stage for ship building is not correctly managed");
    }
    @Test
    void testClearSetUpCorrectlyManaged() {
        session.nextSetUpStage();
        session.nextSetUpStage();
        session.nextSetUpStage();
        session.nextSetUpStage();
        session.clearSetUp();
        assertEquals(SetUpStage.FIRST_SHIP_LEN2, session.getSetUpStage(),
                "clearing the set up should restart teh stages");
    }


}