package game.battleships.client;

import game.battleships.client.communication.RequestSender;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Client {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final String DISCONNECT = "disconnect";
    private static final int BUFFER_SIZE = 2048;
    private boolean connected;
    private ByteBuffer buffer;

    public void connect() {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            this.connected = true;
            Selector selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            Thread requestSenderThread = new Thread(new RequestSender(this, socketChannel));
            requestSenderThread.setDaemon(true);
            requestSenderThread.start();
            while (connected) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    disconnect();
                    continue;
                }
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        String serverResponse = getServerResponse(channel);
                        if (serverResponse.startsWith(DISCONNECT)) {
                            disconnect();
                        } else {
                            System.out.println(serverResponse);
                        }
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        connected = false;
    }
    public String getServerResponse(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}
