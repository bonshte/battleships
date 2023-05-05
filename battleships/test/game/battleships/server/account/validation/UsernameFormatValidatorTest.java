package game.battleships.server.account.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameFormatValidatorTest {

    @Test
    void testValidUsernameOnValidUsername() {
        UsernameFormatValidator usernameFormatValidator = new UsernameFormatValidator();
        assertTrue(usernameFormatValidator.validUsernameFormat("Ivancho"),
                "username should be considered valid format");
    }
    @Test
    void testValidUsernameOnNullUsername() {
        UsernameFormatValidator usernameFormatValidator = new UsernameFormatValidator();
        assertThrows(IllegalArgumentException.class, () -> usernameFormatValidator.validUsernameFormat(null),
                "null passed should throw illegal argument exception");
    }
    @Test
    void testValidUsernameOnSmallUsername() {
        UsernameFormatValidator usernameFormatValidator = new UsernameFormatValidator();
        assertFalse(usernameFormatValidator.validUsernameFormat("Ivan"),
                "username should be more than 5 letters");
    }
    @Test
    void testValidUsernameOnBigUsername() {
        UsernameFormatValidator usernameFormatValidator = new UsernameFormatValidator();
        assertFalse(usernameFormatValidator.validUsernameFormat("Ivanchooooooooooooooooooooooooo"),
                "username should be less than 20 letters");
    }
}