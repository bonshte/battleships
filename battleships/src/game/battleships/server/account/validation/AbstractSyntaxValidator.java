package game.battleships.server.account.validation;

public class AbstractSyntaxValidator {
    protected static final char ZERO = '0';
    protected static final char NINE = '9';
    protected static final char UPPER_A = 'A';
    protected static final char UPPER_Z = 'Z';
    protected static final char LOWER_Z = 'z';
    protected static final char LOWER_A = 'a';

    protected boolean validCharNumber(char a) {
        return a >= ZERO && a <= NINE;
    }
    protected boolean validCharForUpperLetter(char a) {
        return a >= UPPER_A && a <= UPPER_Z;
    }
    protected boolean validCharForLowerLetter(char a) {
        return a >= LOWER_A && a <= LOWER_Z;
    }
}
