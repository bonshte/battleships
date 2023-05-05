package game.battleships.server.game.board;

import game.battleships.server.exception.ship.InvalidShipException;
import game.battleships.server.exception.ship.composition.InvalidShipCompositionException;
import game.battleships.server.logger.BattleshipLogger;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ShipComposition implements Serializable {
    private static final int TOTAL_SHIPS_PER_PLAYER = 10;
    private static final int SIZE_5_SHIPS_PER_PLAYER = 1;
    private static final int SIZE_4_SHIPS_PER_PLAYER = 2;
    private static final int SIZE_3_SHIPS_PER_PLAYER = 3;
    private static final int SIZE_2_SHIPS_PER_PLAYER = 4;
    private static final int LENGTH_2 = 2;
    private static final int LENGTH_3 = 3;
    private static final int LENGTH_4 = 4;
    private static final int LENGTH_5 = 5;
    private static final long serialVersionUID = -5910640088595606815L;

    private final List<Ship> ships;

    public List<Ship> getShips() {
        return ships;
    }
    public ShipComposition(List<Ship> ships) throws InvalidShipCompositionException {
        if (ships == null) {
            throw new IllegalArgumentException("null ship list passed");
        }
        if (!validShipListForComposition(ships)) {
            throw new InvalidShipCompositionException("invalid composition passed");
        }
        this.ships = ships;
    }
    private boolean validShipListForComposition(List<Ship> ships) {
        if (ships.size() != TOTAL_SHIPS_PER_PLAYER) {
            return false;
        }
        if (shipsCollide(ships)) {
            return false;
        }
        int fiveLenShip = 0;
        int fourLenShip = 0;
        int threeLenShip = 0;
        int twoLenShip = 0;
        for (var ship : ships) {
            if (ship.getShipLength() == LENGTH_2) {
                twoLenShip++;
            } else if (ship.getShipLength() == LENGTH_3) {
                threeLenShip++;
            } else if (ship.getShipLength() == LENGTH_4) {
                fourLenShip++;
            } else if (ship.getShipLength() == LENGTH_5) {
                fiveLenShip++;
            } else {
                return false;
            }
        }
        if (fiveLenShip != SIZE_5_SHIPS_PER_PLAYER || fourLenShip != SIZE_4_SHIPS_PER_PLAYER ||
                threeLenShip != SIZE_3_SHIPS_PER_PLAYER || twoLenShip != SIZE_2_SHIPS_PER_PLAYER) {
            return false;
        }
        return true;
    }
    public static ShipComposition generateRandomShipComposition() {
        List<Ship> ships = new LinkedList<>();
        int length2Ships = 0;
        while (length2Ships < SIZE_2_SHIPS_PER_PLAYER) {
            try {
                Ship generatedShip = Ship.generateRandomShip(LENGTH_2);
                ships.add(generatedShip);
                if (shipsCollide(ships)) {
                    ships.remove(generatedShip);
                } else {
                    length2Ships++;
                }
            } catch (InvalidShipException e) {
                BattleshipLogger.getBattleshipLogger().warning(
                        "invalid ship generated bu generator" + e.getMessage());
                throw new IllegalArgumentException("invalid ship attempted to be added to composition");
            }
        }
        int length3Ships = 0;
        while (length3Ships < SIZE_3_SHIPS_PER_PLAYER) {
            try {
                Ship generatedShip = Ship.generateRandomShip(LENGTH_3);
                ships.add(generatedShip);
                if (shipsCollide(ships)) {
                    ships.remove(generatedShip);
                } else {
                    length3Ships++;
                }
            } catch (InvalidShipException e) {
                BattleshipLogger.getBattleshipLogger().warning(
                        "invalid ship generated bu generator" + e.getMessage());
                throw new IllegalArgumentException("invalid ship attempted to be added to composition");
            }
        }
        int length4Ships = 0;
        while (length4Ships < SIZE_4_SHIPS_PER_PLAYER) {
            try {
                Ship generatedShip = Ship.generateRandomShip(LENGTH_4);
                ships.add(generatedShip);
                if (shipsCollide(ships)) {
                    ships.remove(generatedShip);
                } else {
                    length4Ships++;
                }
            } catch (InvalidShipException e) {
                BattleshipLogger.getBattleshipLogger().warning(
                        "invalid ship generated bu generator" + e.getMessage());
                throw new IllegalArgumentException("invalid ship attempted to be added to composition");
            }
        }
        int length5Ships = 0;
        while (length5Ships < SIZE_5_SHIPS_PER_PLAYER) {
            try {
                Ship generatedShip = Ship.generateRandomShip(LENGTH_5);
                ships.add(generatedShip);
                if (shipsCollide(ships)) {
                    ships.remove(generatedShip);
                } else {
                    length5Ships++;
                }
            } catch (InvalidShipException e) {
                BattleshipLogger.getBattleshipLogger().warning(
                        "invalid ship generated bu generator" + e.getMessage());
                throw new IllegalArgumentException("invalid ship attempted to be added to composition");
            }
        }
        try {
            ShipComposition composition = new ShipComposition(ships);
            return composition;
        } catch (InvalidShipCompositionException e) {
            BattleshipLogger.getBattleshipLogger().warning("illegal ship composition constructed" +
                    e.getMessage());
            throw new IllegalStateException("error creating random shipComposition");
        }
    }
    public boolean allAreSank() {
        for (var ship : ships) {
            if (!ship.isSank()) {
                return false;
            }
        }
        return true;
    }

    public StrikeCondition hitOnMark(BattleshipPoint field) {
        if (field == null) {
            throw new IllegalArgumentException("null point passed");
        }
        for (var ship : ships) {
            StrikeCondition attempt = ship.hitField(field);
            if (attempt != StrikeCondition.MISSED) {
                return attempt;
            }
        }
        return StrikeCondition.MISSED;
    }

    Ship getShip(BattleshipPoint field) {
        if (field == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        for (var ship : ships) {
            if (ship.getShipFields().contains(field)) {
                return ship;
            }
        }
        throw new IllegalStateException("ship on this filed had to be present");
    }
    public static boolean shipsCollide(List<Ship> ships) {
        Set<BattleshipPoint> fieldsTaken = new HashSet<>();
        for (var ship : ships) {
            Set<BattleshipFieldPoint> fields = ship.getShipFields();
            for (var field : fields) {
                if (fieldsTaken.contains(field)) {
                    return true;
                }
                fieldsTaken.add(field);
            }
        }
        return false;
    }
}
