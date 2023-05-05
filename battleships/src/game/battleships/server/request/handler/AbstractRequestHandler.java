package game.battleships.server.request.handler;

import game.battleships.server.account.Account;
import game.battleships.server.account.AccountStatus;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.exception.lobby.PlayerNotInLobbyException;
import game.battleships.server.game.BattleshipGameLobby;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.request.ui.ConsoleUI;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class AbstractRequestHandler implements RequestHandlingInterface {
    protected static final String DISCONNECT = "disconnect";
    protected static final String QUIT_COMMAND_CLIENT_REQUEST = "quit";
    private static final int BUFFER_SIZE = 2048;
    protected static ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    protected String getClientInputString(SocketChannel clientChannel) throws IOException, ClientDisconnectedException {
        byte[] clientInputBytes = getClientInputByteArray(clientChannel);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }
    protected byte[] getClientInputByteArray(SocketChannel clientChannel)
            throws IOException, ClientDisconnectedException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            throw new ClientDisconnectedException("client application shut down unexpectedly");
        }

        buffer.flip();
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);
        return clientInputBytes;
    }
    public static void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }


}
