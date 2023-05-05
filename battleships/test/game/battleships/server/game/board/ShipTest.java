package game.battleships.server.game.board;

import game.battleships.server.exception.ship.InvalidShipException;
import game.battleships.server.exception.ship.point.InvalidPointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    private BattleshipFieldPoint point1;
    private BattleshipFieldPoint point2;
    private BattleshipFieldPoint point3;
    private BattleshipFieldPoint point4;
    private BattleshipFieldPoint point5;
    private BattleshipFieldPoint point6;

    @BeforeEach
    void setUp() {
        try {
            point1 = new BattleshipFieldPoint(1,1);
            point2 = new BattleshipFieldPoint(1,2);
            point3 = new BattleshipFieldPoint(2,2);
            point4 = new BattleshipFieldPoint(5,5);
            point5 = new BattleshipFieldPoint(4,5);
            point6 = new BattleshipFieldPoint(8,8);
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    void testConstructorWithValidArgument() {
        assertDoesNotThrow(() -> new Ship(Set.of(point1, point2)));
        assertDoesNotThrow(() -> new Ship(Set.of(point3, point2)));
    }
    @Test
    void testConstructorWithInvalidSize() {
        assertThrows(InvalidShipException.class, () -> new Ship(Set.of(point1)),
                "cannot construct ship with size of 1");
        assertThrows(InvalidShipException.class, () -> new Ship(
                Set.of(point1, point2, point3, point4, point5, point6)),
                "cannot construct ship with size of 6");
    }
    @Test
    void testConstructorWithInvalidShip() {
        assertThrows(InvalidShipException.class, () -> new Ship(Set.of(point1, point3)),
                "ships can only be horizontally or vertically");
    }

    @Test
    void testGameFormatConstructor() {
        assertDoesNotThrow(() -> new Ship("A1-A3"),
                "valid 3 length ship should not throw");
        assertThrows(InvalidShipException.class, () -> new Ship("A1-B2"),
                "ships can only be horizontally or vertically");
        assertThrows(InvalidPointException.class, () -> new Ship("A1-K3"),
                "K is not valid for a dimension");
        assertThrows(InvalidShipException.class, () -> new Ship("  "),
                "blank is not valid game point");
        assertThrows(InvalidShipException.class, () -> new Ship("A1-A2-A3 "),
                "string length is not valid for a game point");
        try {
            Ship ship1 = new Ship("J3-J6");
            assertEquals(4, ship1.getShipLength(), "ship length should be 4");
        } catch (Exception e) {
            fail();
        }
    }
    @RepeatedTest(10)
    @Test
    void testShipGenerator() {
        try {
            for (int i = 2; i <= 5; ++i) {
                Ship currentShip = Ship.generateRandomShip(i);
                assertEquals(i, currentShip.getShipLength(),
                        "generated ship should be of the desired length");
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testHitShip() {
        try {
            Ship ship = new Ship(Set.of(point1,point2));
            StrikeCondition condition = ship.hitField(point1);
            assertEquals(StrikeCondition.HIT, condition, "ship should be hit at that spot");
            StrikeCondition condition1 = ship.hitField(point1);
            assertEquals(StrikeCondition.ALREADY_HIT, condition1,
                    "ship must have been already hit on that field");
            StrikeCondition condition2 = ship.hitField(point4);
            assertEquals(StrikeCondition.MISSED, condition2, "nothing should be hit on that field");
            StrikeCondition condition3 = ship.hitField(point2);
            assertEquals(StrikeCondition.SANK, condition3, "ship should sink with the second hit");
            assertTrue(ship.isSank(), "ship should have sank");
        } catch (Exception e) {
            fail();
        }
    }
}