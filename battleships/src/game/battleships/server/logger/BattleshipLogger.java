package game.battleships.server.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BattleshipLogger {
    private static Logger logger;
    private static final Path LOG_DIRECTORY = Path.of("battleships/log");
    private static final String LOG_FILE = "battleships/log/log.txt";

    private BattleshipLogger() {

    }
    public static Logger getBattleshipLogger() {
        if (logger == null) {

            logger = Logger.getLogger(BattleshipLogger.class.getName());
            try {
                Files.createDirectories(LOG_DIRECTORY);

                Handler fileHandler = new FileHandler(LOG_FILE, true);
                logger.addHandler(fileHandler);
                logger.setLevel(Level.ALL);
            } catch (IOException e) {
                logger.warning("could not create log file" + e.getMessage());
            }
        }
        return logger;
    }
}
