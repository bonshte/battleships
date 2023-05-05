package game.battleships.server.account.validation;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PasswordFormatValidatorTest {
    @Test
    void testWithNullPassword() {
        PasswordFormatValidator passwordFormatValidator = new PasswordFormatValidator();
        assertThrows(IllegalArgumentException.class, () -> passwordFormatValidator.validPassword(null),
                "should throw illegal argument exception when null is passed");
    }


    @Test
    void testWithShortPassword() {
        PasswordFormatValidator passwordFormatValidator = new PasswordFormatValidator();
        assertFalse(passwordFormatValidator.validPassword("A1A".getBytes(StandardCharsets.UTF_8)),
                "password is too short");
    }
    @Test
    void testWithLongPassword() {
        PasswordFormatValidator passwordFormatValidator = new PasswordFormatValidator();
        assertFalse(passwordFormatValidator.validPassword(
                "AAAAAAAAAAAAAAAAAAAAAAAA12123123a".getBytes(StandardCharsets.UTF_8)),
                "password is too long");
    }
    @Test
    void testWithOnlyUpperCaseLettersPassword() {
        PasswordFormatValidator passwordFormatValidator = new PasswordFormatValidator();
        assertFalse(passwordFormatValidator.validPassword("AAAAAAA121AA".getBytes(StandardCharsets.UTF_8)),
                "password must contain both upper and lower case letters");
    }

    @Test
    void testPasswordWithValidPassword() {
        PasswordFormatValidator passwordFormatValidator = new PasswordFormatValidator();
        assertTrue(passwordFormatValidator.validPassword("AAaa123".getBytes(StandardCharsets.UTF_8)),
                "password should be considered in valid format");
    }
}