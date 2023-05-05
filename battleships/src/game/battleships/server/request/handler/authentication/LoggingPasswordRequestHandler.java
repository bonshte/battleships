package game.battleships.server.request.handler.authentication;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.authentication.InvalidCredentialsException;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import game.battleships.server.account.Account;
import game.battleships.server.account.AccountCredentials;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.account.cryptation.Encryptor;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

public class LoggingPasswordRequestHandler extends AbstractRequestHandler {
    private static final Encryptor PASSWORD_ENCRYPTOR = new Encryptor();
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {

        byte[] passwordInput = getClientInputByteArray(clientSocketChannel);
        byte[] encryptedPassword;
        try {
            encryptedPassword = PASSWORD_ENCRYPTOR.encryptPassword(passwordInput);
        } catch (NoSuchAlgorithmException e) {
            BattleshipLogger.getBattleshipLogger().warning(e.getMessage());
            writeClientOutput(clientSocketChannel,  ConsoleUI.CANNOT_LOGIN_NOW_MESSAGE +
                    ConsoleUI.ACTIONS_ALLOWED_MESSAGE + ConsoleUI.AUTHENTICATION_MENU);
            clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
            return;
        }
        AccountCredentials userCredentials = new AccountCredentials(
                clientSession.getLastSessionInput(), encryptedPassword);
        try {
            Account account = serverData.getAccountStorage().getUserByCredentials(userCredentials);
            if (account.isLogged()) {
                BattleshipLogger.getBattleshipLogger().config(account.getUsername() +
                        " attempted to be logged from another destination");
                writeClientOutput(clientSocketChannel,  ConsoleUI.ACCOUNT_ALREADY_LOGGED_MESSAGE +
                        ConsoleUI.AUTHENTICATION_MENU);
                clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
                return;
            }
            account.setAccountStatus(AccountStatus.ONLINE);
            clientSession.setAccountAssociated(account);
            clientSession.setSessionStatus(SessionStatus.MAIN_MENU);
            writeClientOutput(clientSocketChannel,  ConsoleUI.MAIN_MENU);
        } catch (InvalidCredentialsException e) {
            BattleshipLogger.getBattleshipLogger().info("failed log in");
            writeClientOutput(clientSocketChannel,  ConsoleUI.INVALID_CREDENTIALS_MESSAGE +
                    ConsoleUI.ACTIONS_ALLOWED_MESSAGE + ConsoleUI.AUTHENTICATION_MENU);
            clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
        }
    }
}
