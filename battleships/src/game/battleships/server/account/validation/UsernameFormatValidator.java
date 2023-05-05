package game.battleships.server.account.validation;

public class UsernameFormatValidator extends AbstractSyntaxValidator {


    private static final int MIM_LENGTH_USERNAME = 6;
    private static final int MAX_LENGTH_USERNAME = 20;
    private boolean validUsernameChar(char a) {
        return validCharNumber(a) || validCharForLowerLetter(a) || validCharForUpperLetter(a);
    }

    public boolean validUsernameFormat(String username) {
        if (username == null) {
            throw new IllegalArgumentException("null passed to method");
        }
        if (username.isEmpty() || username.isBlank()) {
            return false;
        }
        if (username.length() < MIM_LENGTH_USERNAME || username.length() > MAX_LENGTH_USERNAME) {
            return false;
        }

        for (int i = 0; i < username.length(); ++i) {
            if (!validUsernameChar(username.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
