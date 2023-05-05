package game.battleships.server.request.handler.accepted;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ConnectedRequestHandler extends AbstractRequestHandler {
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        writeClientOutput(clientSocketChannel, ConsoleUI.WELCOME_TEXT_MESSAGE +
                        ConsoleUI.ACTIONS_ALLOWED_MESSAGE + ConsoleUI.AUTHENTICATION_MENU);
        clientSession.setSessionStatus(SessionStatus.AUTHENTICATION_MENU);
    }
}
