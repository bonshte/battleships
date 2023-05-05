package game.battleships.board;

import game.battleships.server.exception.ship.composition.InvalidShipCompositionException;
import game.battleships.server.exception.ship.InvalidShipException;
import game.battleships.server.exception.ship.point.InvalidPointException;
import game.battleships.server.game.board.BattleshipFieldPoint;
import game.battleships.server.game.board.BattleshipPoint;
import game.battleships.server.game.board.Ship;
import game.battleships.server.game.board.ShipComposition;
import game.battleships.server.game.board.StrikeCondition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class ShipCompositionTest {
    private static Ship validShip1Len2;
    private static Ship validShip2Len2;
    private static Ship validShip3Len2;
    private static Ship validShip4Len2;
    private static Ship validShip5Len2;
    private static Ship validShip1Len3;
    private static Ship validShip2Len3;
    private static Ship validShip3Len3;
    private static Ship validShip1Len4;
    private static Ship validShip2Len4;
    private static Ship validShip1Len5;
    private static Ship collidingShip1Len2;
    private static Ship collidingShip1Len3;

    @BeforeAll
    static void setup() {
        try {
            validShip1Len2 = new Ship(Set.of(new BattleshipFieldPoint(1, 3), new BattleshipFieldPoint(1, 4)));
            validShip2Len2 = new Ship(Set.of(new BattleshipFieldPoint(0,0), new BattleshipFieldPoint(0,1)));
            validShip3Len2 = new Ship(Set.of( new BattleshipFieldPoint(0,3), new BattleshipFieldPoint(0, 4)));
            validShip4Len2 = new Ship(Set.of(new BattleshipFieldPoint(5,5), new BattleshipFieldPoint(4,5)));
            validShip5Len2 = new Ship(Set.of(new BattleshipFieldPoint(1,8), new BattleshipFieldPoint(1,9)));
            validShip1Len3 = new Ship(Set.of(new BattleshipFieldPoint(5,6), new BattleshipFieldPoint(5,7), new BattleshipFieldPoint(5,8)));
            validShip2Len3 = new Ship(Set.of(new BattleshipFieldPoint(6,0),new BattleshipFieldPoint(6 , 1), new BattleshipFieldPoint(6, 2)));
            validShip3Len3 = new Ship(Set.of(new BattleshipFieldPoint(4,4), new BattleshipFieldPoint(3,4), new BattleshipFieldPoint(2,4)));
            validShip1Len4 = new Ship(
                    Set.of(new BattleshipFieldPoint(7,7), new BattleshipFieldPoint(6,7), new BattleshipFieldPoint(8,7), new BattleshipFieldPoint(9,7)));
            validShip2Len4 = new Ship(
                    Set.of(new BattleshipFieldPoint(8,3), new BattleshipFieldPoint(8,4), new BattleshipFieldPoint(8,5), new BattleshipFieldPoint(8,6)));
            validShip1Len5 = new Ship(
                    Set.of(new BattleshipFieldPoint(9,0), new BattleshipFieldPoint(9,1),
                            new BattleshipFieldPoint(9, 2), new BattleshipFieldPoint(9, 3), new BattleshipFieldPoint(9, 4)));
            collidingShip1Len2 = new Ship(Set.of(new BattleshipFieldPoint(1,4), new BattleshipFieldPoint(1,5)));
            collidingShip1Len3 = new Ship(Set.of(new BattleshipFieldPoint(5, 7), new BattleshipFieldPoint(4,7), new BattleshipFieldPoint(6, 7)));
        } catch (InvalidShipException | InvalidPointException e) {
            fail();
        }
    }

    @Test
    void testShipCompositionWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> new ShipComposition(null),
                "illegal argument should be thrown when null argument is passed");
    }

    @Test
    void testShipCompositionWithNotEnoughShips() {
        assertThrows(InvalidShipCompositionException.class,
                () -> new ShipComposition(
                        List.of(validShip1Len2, validShip2Len2, validShip3Len2, validShip4Len2)),
                "ships must be 10");
    }

    @Test
    void testShipCompositionValid() {
        assertDoesNotThrow(() ->
                new ShipComposition(List.of(validShip1Len2, validShip2Len2, validShip3Len2, validShip4Len2,
                        validShip1Len3,validShip2Len3,validShip3Len3,
                        validShip1Len4, validShip2Len4,
                        validShip1Len5)),
                "should not throw when 10 not colliding ships with correct lengths are passed");
    }

    @Test
    void testShipCompositionWithCollidingShips() {
        assertThrows(InvalidShipCompositionException.class,
                () -> new ShipComposition(List.of(validShip1Len2, collidingShip1Len2, validShip3Len2, validShip4Len2,
                        validShip1Len3,validShip2Len3,validShip3Len3,
                        validShip1Len4, validShip2Len4,
                        validShip1Len5)),
                "ships are colliding should throw exception");
        assertThrows(InvalidShipCompositionException.class,
                () -> new ShipComposition(List.of(validShip1Len2, validShip2Len2, validShip3Len2, validShip4Len2,
                        validShip1Len3, collidingShip1Len3,validShip3Len3,
                        validShip1Len4, validShip2Len4,
                        validShip1Len5)),
                "ships are colliding should throw exception");
    }
    @Test
    void testShipCompositionWithInvalidConfiguration() {
        assertThrows(InvalidShipCompositionException.class,
                () -> new ShipComposition(List.of(validShip1Len2, validShip2Len2,
                        validShip3Len2, validShip4Len2, validShip5Len2
                        ,validShip2Len3,validShip3Len3,
                        validShip1Len4, validShip2Len4,
                        validShip1Len5)),
                "ships must be 4 len 2, 3 len 3, 2 len 4, 1 len 5");
    }

    @Test
    void testHittingShipFromTheComposition() {
        try {
            ShipComposition composition = new ShipComposition(List.of(validShip1Len2, validShip2Len2, validShip3Len2, validShip4Len2,
                    validShip1Len3,validShip2Len3,validShip3Len3,
                    validShip1Len4, validShip2Len4,
                    validShip1Len5));
            assertEquals(StrikeCondition.HIT, composition.hitOnMark(new BattleshipPoint(1, 3)), "ship should be hit");
            assertEquals(StrikeCondition.MISSED, composition.hitOnMark(new BattleshipPoint(1,2)), "no ship should be hit");
            assertEquals(StrikeCondition.ALREADY_HIT, composition.hitOnMark(new BattleshipPoint(1, 3)), "ship field was already hit");
        } catch (InvalidShipCompositionException | InvalidPointException e) {
            fail();
        }
    }

    @Test
    void testHittingShipFromTheCompositionNullPassed() {
        try {
            ShipComposition composition = new ShipComposition(List.of(validShip1Len2, validShip2Len2, validShip3Len2, validShip4Len2,
                    validShip1Len3,validShip2Len3,validShip3Len3,
                    validShip1Len4, validShip2Len4,
                    validShip1Len5));
            assertThrows(IllegalArgumentException.class,
                    () -> composition.hitOnMark(null),
                    "illegal argument should be thrown when null is passed to onMark method");
        } catch (InvalidShipCompositionException e) {
            fail();
        }
    }

    @Test
    void testRandomShipCompositionGeneration() {
        assertDoesNotThrow(() -> ShipComposition.generateRandomShipComposition());
    }

}