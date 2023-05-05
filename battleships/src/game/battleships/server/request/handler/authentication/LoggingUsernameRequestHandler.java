package game.battleships.server.request.handler.authentication;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class LoggingUsernameRequestHandler extends AbstractRequestHandler {
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String usernameInput = getClientInputString(clientSocketChannel);
        clientSession.addInput(usernameInput);
        writeClientOutput(clientSocketChannel, ConsoleUI.PASSWORD_FORM);
        clientSession.setSessionStatus(SessionStatus.LOGGING_PASSWORD);

    }
}
