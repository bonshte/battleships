package game.battleships.server.account.validation;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PasswordFormatValidator extends AbstractSyntaxValidator {
    private static final int MIN_PASSWORD_LEN = 6;
    private static final int MAX_PASSWORD_LEN = 15;
    public boolean validPassword(byte[] password) {
        if (password == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        StringBuilder passwordStringBuilder = convertToStringBuilder(password);
        if (passwordStringBuilder.length() < MIN_PASSWORD_LEN || passwordStringBuilder.length() > MAX_PASSWORD_LEN) {
            return false;
        }
        boolean lowerLetterFound = false;
        boolean upperLetterFound = false;
        boolean numberFound = false;
        for (int i = 0; i < passwordStringBuilder.length(); ++i) {
            if (validCharNumber(passwordStringBuilder.charAt(i))) {
                numberFound = true;
            } else if (validCharForLowerLetter(passwordStringBuilder.charAt(i))) {
                lowerLetterFound = true;
            } else if (validCharForUpperLetter(passwordStringBuilder.charAt(i))) {
                upperLetterFound = true;
            }
        }
        return upperLetterFound && lowerLetterFound && numberFound;
    }
    private StringBuilder convertToStringBuilder(byte[] password) {
        Charset charset = StandardCharsets.UTF_8;
        CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(password));
        char[] charArray = charBuffer.array();
        StringBuilder result = new StringBuilder();
        for (char c : charArray) {
            result.append(c);
        }
        return result;
    }

}
