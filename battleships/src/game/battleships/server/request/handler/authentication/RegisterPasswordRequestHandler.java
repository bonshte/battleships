package game.battleships.server.request.handler.authentication;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.authentication.UsernameAlreadyTakenException;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import game.battleships.server.account.Account;
import game.battleships.server.account.AccountCredentials;
import game.battleships.server.account.cryptation.Encryptor;
import game.battleships.server.account.validation.PasswordFormatValidator;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

public class RegisterPasswordRequestHandler extends AbstractRequestHandler {
    private static final Encryptor PASSWORD_ENCRYPTOR = new Encryptor();
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        byte[] passwordInput = getClientInputByteArray(clientSocketChannel);
        PasswordFormatValidator passwordValidator = new PasswordFormatValidator();
        if (passwordValidator.validPassword(passwordInput)) {
            try {
                byte[] encryptedPassword = PASSWORD_ENCRYPTOR.encryptPassword(passwordInput);
                AccountCredentials newUserCredentials =
                        new AccountCredentials(clientSession.getLastSessionInput(), encryptedPassword);
                Account newAccount = new Account(newUserCredentials);
                serverData.getAccountStorage().registerUser(newAccount);
                serverData.getDataBaseManager().updateAccountDataFile(newAccount);
                clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
                writeClientOutput(clientSocketChannel,  ConsoleUI.REGISTRATION_COMPLETE_MESSAGE +
                         ConsoleUI.AUTHENTICATION_MENU);
            } catch ( UsernameAlreadyTakenException e) {
                BattleshipLogger.getBattleshipLogger().config(
                        "client reached password registration page with taken username" + e.getMessage());
                clientSession.setSessionStatus(SessionStatus.REGISTERING_USERNAME);
                writeClientOutput(clientSocketChannel,  ConsoleUI.USERNAME_TAKEN_MESSAGE +
                        ConsoleUI.TRY_AGAIN_MESSAGE + ConsoleUI.USERNAME_FORM);
            } catch (NoSuchAlgorithmException e) {
                BattleshipLogger.getBattleshipLogger().warning("password encryption error" + e.getMessage());
                writeClientOutput(clientSocketChannel,  ConsoleUI.CANNOT_REGISTER_NOW_MESSAGE +
                        ConsoleUI.AUTHENTICATION_MENU);
                clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
            }
        } else {
            BattleshipLogger.getBattleshipLogger().info("client tried to register with invalid password format");
            writeClientOutput(clientSocketChannel, ConsoleUI.INVALID_PASSWORD_FORMAT_MESSAGE +
                    ConsoleUI.TRY_AGAIN_MESSAGE + ConsoleUI.PASSWORD_FORM);
        }
    }
}
