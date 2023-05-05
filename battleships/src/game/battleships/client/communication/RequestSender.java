package game.battleships.client.communication;
import game.battleships.client.Client;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class RequestSender implements Runnable {
    private static final int BUFFER_SIZE = 1024;
    private ByteBuffer buffer;
    private Client clientSending;
    private SocketChannel clientSocketChannel;
    private static final String COMMUNICATION_ERROR = "communication error";
    public RequestSender(Client clientSending, SocketChannel clientSocketChannel) {
        this.clientSending = clientSending;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.clientSocketChannel = clientSocketChannel;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (clientSending.isConnected()) {
            String request = scanner.nextLine();
            try {
                sendMessage(request, clientSocketChannel);
            } catch (IOException e) {
                System.out.println(COMMUNICATION_ERROR);
                clientSending.disconnect();
                return;
            }
        }
    }
    private void sendMessage(String message, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }
}
