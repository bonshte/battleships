package game.battleships.server;
import game.battleships.server.exception.ClientDisconnectedException;
import game.battleships.server.logger.BattleshipLogger;
import game.battleships.server.session.Session;
import game.battleships.server.session.SessionStatus;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class BattleshipServer implements Runnable {
    private static final String HOST = "localhost";
    private final int port;
    private boolean isServerWorking;
    private Selector selector;
    private final ServerDataHandler handler;
    public BattleshipServer(int port) throws IOException {
        this.port = port;
        this.handler = new ServerDataHandler();
    }

    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);

            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientSocketChannel = (SocketChannel) key.channel();
                            Session session = (Session) key.attachment();
                            if (session.getSessionStatus() == SessionStatus.DISCONNECTED) {
                                key.channel().close();
                                key.cancel();
                            } else {
                                try {
                                    handler.handleRequest(clientSocketChannel, session);
                                } catch (ClientDisconnectedException e) {
                                    BattleshipLogger.getBattleshipLogger().config(
                                            "client application shut down unexpectedly");
                                    session.handleLeftGame(handler.getServerData().getGameLobbyStorage());
                                    session.logOutAssociatedAccount();
                                    key.channel().close();
                                    key.cancel();
                                }
                            }
                        } else if (key.isAcceptable()) {
                            var clientKey = accept(selector, key);
                            var clientSocket = (SocketChannel) clientKey.channel();
                            Session userSession = new Session();
                            clientKey.attach(userSession);
                            try {
                                handler.handleRequest(clientSocket, userSession);
                            } catch (ClientDisconnectedException e) {
                                BattleshipLogger.getBattleshipLogger().config(
                                        "client application shut down unexpectedly");
                                key.channel().close();
                                key.cancel();
                            }
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    BattleshipLogger.getBattleshipLogger().config(
                            "error occurred while processing client request" + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
        handler.saveCurrentServerData();
    }
    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }
    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }
    private SelectionKey accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        return accept.register(selector, SelectionKey.OP_READ);
    }
}
