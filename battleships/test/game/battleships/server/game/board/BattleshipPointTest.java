package game.battleships.server.game.board;

import game.battleships.server.exception.ship.point.InvalidPointException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleshipPointTest {
    @Test
    void testCorrectPointConstruction() {
        assertDoesNotThrow(() -> new BattleshipPoint(1,1), "a valid point should not throw");
        assertDoesNotThrow(() -> new BattleshipPoint(0,0),"a valid point should not throw");
        assertDoesNotThrow(() -> new BattleshipPoint(9,9),"a valid point should not throw");
    }

    @Test
    void testInvalidPointPassed() {
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint(-1, 3),
                "out of bounds point should throw");
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint(10,4),"out of bounds point should throw");
    }

    @Test
    void testStringConstructorWithValidString() {
        assertDoesNotThrow(() -> new BattleshipPoint("A1"),
                "should not throw with a valid game format string");
        assertDoesNotThrow(() -> new BattleshipPoint("J10"),
                "should not throw with a valid game format string");
    }
    @Test
    void testStringConstructorWithNullString() {
        assertThrows(IllegalArgumentException.class, () -> new BattleshipPoint(null),
                "should throw illegal argument when null passed");
    }
    @Test
    void testStringConstructorWithInvalidString() {
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint("a1"),
                "if letter is not capital it should throw invalid point");
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint("A"),
                "if length is less than 2 it should throw");
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint("A101"),
                "if length is greater than 3 it should throw");
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint("A-1"),
                "if format is not matches it should throw");
        assertThrows(InvalidPointException.class, () -> new BattleshipPoint("1A"),
                "if arguments are reversed it should throw");
    }

    @Test
    void testConvertingToGameFormat() {
        try {
            BattleshipPoint point1 = new BattleshipPoint(9,9);
            BattleshipPoint point2 = new BattleshipPoint(4,7);
            assertEquals("[J,10]",point1.toGameFormat(), "point differs from game format");
            assertEquals("[E,8]",point2.toGameFormat(), "point differs from game format");
        } catch (Exception e) {
            fail();
        }
    }

}