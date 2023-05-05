package game.battleships.server.request.handler;

import game.battleships.server.data.ServerData;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.session.Session;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface RequestHandlingInterface {
    void handle(SocketChannel clientSocketChannel, Session clientSession, ServerData serverData)
            throws IOException, ClientDisconnectedException;
}

