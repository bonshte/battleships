package game.battleships.server.request.handler.authentication;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import game.battleships.server.account.validation.UsernameFormatValidator;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class RegisterUsernameRequestHandler extends AbstractRequestHandler {

    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String username = getClientInputString(clientSocketChannel);
        UsernameFormatValidator usernameFormatValidator = new UsernameFormatValidator();
        if (serverData.getAccountStorage().usernameTaken(username)) {
            BattleshipLogger.getBattleshipLogger().info("client tried to register with already taken username");
            writeClientOutput(clientSocketChannel, ConsoleUI.USERNAME_TAKEN_MESSAGE +
                            ConsoleUI.TRY_AGAIN_MESSAGE + ConsoleUI.USERNAME_FORM);
        } else if (!usernameFormatValidator.validUsernameFormat(username)) {
            BattleshipLogger.getBattleshipLogger().info("client tried to register with invalid username format");
            writeClientOutput(clientSocketChannel,
                    ConsoleUI.INVALID_USERNAME_FORMAT_MESSAGE + ConsoleUI.TRY_AGAIN_MESSAGE +
                    ConsoleUI.USERNAME_FORM);
        } else {
            writeClientOutput(clientSocketChannel, ConsoleUI.PASSWORD_FORM);
            clientSession.addInput(username);
            clientSession.setSessionStatus(SessionStatus.REGISTERING_PASSWORD);
        }
    }
}
