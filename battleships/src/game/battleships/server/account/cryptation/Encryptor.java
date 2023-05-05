package game.battleships.server.account.cryptation;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    private static final String HASHING_ALGO = "MD5";
    private static byte[] salt;
    private static final Path SALT_PATH = Path.of("battleships/encryption.txt");
    static {
        try (FileInputStream saltFileInputStream = new FileInputStream(SALT_PATH.toString())) {
            salt = saltFileInputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("encryption file missing");
        }
    }
    public byte[] encryptPassword(byte[] password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASHING_ALGO);
        md.update(salt);
        return md.digest(password);
    }
}
