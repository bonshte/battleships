package game.battleships.server.request.handler.ingame;

import game.battleships.server.data.ServerData;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.request.handler.AbstractRequestHandler;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class SpectatingRequestHandler extends AbstractRequestHandler {
    private static final String STOP_SPECTATING_COMMAND = "stop spectating";
    @Override
    public void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException {
        String clientInputString = getClientInputString(clientSocketChannel);
        if (clientInputString.equals(STOP_SPECTATING_COMMAND)) {
            var lobbySpectating = clientSession.getAccountAssociated().getCurrentGameLobby();
            lobbySpectating.removeSpectator(clientSession.getAccountAssociated());
            clientSession.getAccountAssociated().setAccountStatus(AccountStatus.ONLINE);
            clientSession.setSessionStatus(SessionStatus.MAIN_MENU);
            writeClientOutput(clientSocketChannel, ConsoleUI.MAIN_MENU);
        } else {
            writeClientOutput(clientSocketChannel, ConsoleUI.INVALID_COMMAND + ConsoleUI.ACTIONS_ALLOWED_MESSAGE +
                    ConsoleUI.SPECTATING_MENU);
        }
    }
}
