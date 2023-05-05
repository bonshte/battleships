package game.battleships.server.game.board;

import game.battleships.server.exception.ship.point.InvalidPointException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleshipFieldPointTest {
    @Test
    void testCorrectPointConstruction() {
        assertDoesNotThrow(() -> new BattleshipFieldPoint(1,1), "a valid point should not throw");
        assertDoesNotThrow(() -> new BattleshipFieldPoint(0,0),"a valid point should not throw");
        assertDoesNotThrow(() -> new BattleshipFieldPoint(9,9),"a valid point should not throw");
    }

    @Test
    void testInvalidPointPassed() {
        assertThrows(InvalidPointException.class, () -> new BattleshipFieldPoint(-1, 3),
                "out of bounds point should throw");
        assertThrows(InvalidPointException.class, () -> new BattleshipFieldPoint(10,4),"out of bounds point should throw");
    }

    @Test
    void testStringConstructorWithValidString() {
        assertDoesNotThrow(() -> new BattleshipFieldPoint("A1"),
                "should not throw with a valid game format string");
        assertDoesNotThrow(() -> new BattleshipFieldPoint("J10"),
                "should not throw with a valid game format string");
    }
    @Test
    void testStringConstructorWithNullString() {
        assertThrows(IllegalArgumentException.class, () -> new BattleshipFieldPoint(null),
                "should throw illegal argument when null passed");
    }
}