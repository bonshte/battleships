package game.battleships.server.request.handler.authentication;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class AuthenticationMenuRequestHandler extends AbstractRequestHandler {
    private static final String REGISTRATION_COMMAND = "register";
    private static final String LOGIN_COMMAND = "log in";

    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession,
                       ServerData serverData) throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        switch (clientInputString) {
            case QUIT_COMMAND_CLIENT_REQUEST :
                writeClientOutput(clientSocketChannel, DISCONNECT);
                clientSession.setSessionStatus(SessionStatus.DISCONNECTED);
                break;
            case REGISTRATION_COMMAND:
                writeClientOutput(clientSocketChannel, ConsoleUI.USERNAME_FORM);
                clientSession.setSessionStatus(SessionStatus.REGISTERING_USERNAME);
                break;
            case LOGIN_COMMAND:
                writeClientOutput(clientSocketChannel,  ConsoleUI.USERNAME_FORM);
                clientSession.setSessionStatus(SessionStatus.LOGGING_USERNAME);
                break;
            default:
                BattleshipLogger.getBattleshipLogger().info(
                        "client sent invalid command in authentication menu");
                writeClientOutput(clientSocketChannel, ConsoleUI.ACTIONS_ALLOWED_MESSAGE
                        + ConsoleUI.AUTHENTICATION_MENU);
        }

    }
}
