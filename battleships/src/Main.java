import game.battleships.server.BattleshipServer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int PORT = 7777;
    public static void main(String[] args) {

        try {
            BattleshipServer server = new BattleshipServer(PORT);
            Thread serverThread = new Thread(server);
            ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
            serverExecutor.execute(serverThread);
            Scanner scanner = new Scanner(System.in);

            String adminCommand = "command";
            while (!adminCommand.equals("stop")) {
                System.out.println("enter stop if you want to shutdown");
                adminCommand = scanner.nextLine();
            }
            server.stop();

            serverExecutor.shutdown();
            try {
                serverExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("jvm interrupted the thread before all tasks were finished", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
