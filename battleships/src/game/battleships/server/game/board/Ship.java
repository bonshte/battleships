package game.battleships.server.game.board;



import game.battleships.server.exception.ship.InvalidShipException;
import game.battleships.server.exception.ship.point.InvalidPointException;
import game.battleships.server.logger.BattleshipLogger;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Ship implements Serializable {
    private static final String DASH = "-";
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 5;
    private static final long serialVersionUID = -1557880682963939618L;
    private Set<BattleshipFieldPoint> shipFields;
    private static final Random RANDOM_GENERATOR = new Random();


    public Ship(Set<BattleshipFieldPoint> fields) throws InvalidShipException {
        if (fields == null) {
            throw new IllegalArgumentException("null fields passed to ship constructor");
        }
        if (fields.size() < MIN_LENGTH || fields.size() > MAX_LENGTH) {
            throw new InvalidShipException("invalid size for a ship passed");
        }
        if (!validShipPresentation(fields)) {
            throw new InvalidShipException("invalid ordering for ship fields");
        }
        this.shipFields = fields;
    }


    public Ship(String endPointsGameFormat) throws InvalidShipException, InvalidPointException {
        if (endPointsGameFormat == null) {
            BattleshipLogger.getBattleshipLogger().warning("null string passed to Ship constructor");
            throw new IllegalArgumentException("null passed to constructor");
        }
        if (endPointsGameFormat.isEmpty() || endPointsGameFormat.isBlank()) {
            throw new InvalidShipException("invalid ship parameters passed");
        }
        String[] clientWords = endPointsGameFormat.split(DASH);
        if (clientWords.length != 2) {
            throw new InvalidShipException("invalid ship parameters passed");
        }
        BattleshipFieldPoint firstBattleshipFieldPoint = new BattleshipFieldPoint(clientWords[0]);
        BattleshipFieldPoint secondBattleshipFieldPoint = new BattleshipFieldPoint(clientWords[1]);
        this.shipFields = new Ship(firstBattleshipFieldPoint, secondBattleshipFieldPoint).shipFields;
    }
    public Ship(BattleshipFieldPoint firstPoint, BattleshipFieldPoint secondPoint)
            throws InvalidShipException {
        if (firstPoint == null || secondPoint == null) {
            throw new IllegalArgumentException("null point passed to constructor");
        }

        int xDif = Math.abs(firstPoint.getX() - secondPoint.getX());
        int yDif = Math.abs(firstPoint.getY() - secondPoint.getY());
        if (xDif != 0 && yDif != 0) {
            throw new InvalidShipException("ship can grow only in one dimension");
        }
        int length = Math.max(xDif, yDif) + 1;
        if (length > MAX_LENGTH || length < MIN_LENGTH) {
            throw new InvalidShipException("invalid ship length");
        }
        Set<BattleshipFieldPoint> battleshipFieldPoints = new HashSet<>();
        BattleshipFieldPoint lowerPoint = firstPoint.getX() <= secondPoint.getX() &&
                firstPoint.getY() <= secondPoint.getY() ?
                firstPoint :
                secondPoint;
        BattleshipFieldPoint upperPoint = lowerPoint == firstPoint ? secondPoint : firstPoint;
        battleshipFieldPoints.add(lowerPoint);
        boolean xDimension = upperPoint.getX() > lowerPoint.getX();
        try {
            for (int i = 1; i < length - 1; ++i) {
                if (xDimension) {
                    battleshipFieldPoints.add(new BattleshipFieldPoint(lowerPoint.getX() + i, lowerPoint.getY()));
                } else {
                    battleshipFieldPoints.add(new BattleshipFieldPoint(lowerPoint.getX(), lowerPoint.getY() + i));
                }
            }
            battleshipFieldPoints.add(upperPoint);
            this.shipFields = battleshipFieldPoints;
        } catch (InvalidPointException e) {
            BattleshipLogger.getBattleshipLogger().warning("ship constructor created invalid point");
            throw new InvalidShipException("ship cannot contain invalid points");
        }
    }

    public static Ship generateRandomShip(int length) throws InvalidShipException {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new InvalidShipException("ships can only be of size 2-5");
        }
        int expandingDimension = RANDOM_GENERATOR.nextInt(BattleshipGameBoard.BOARD_SIZE - length);
        int otherDimension = RANDOM_GENERATOR.nextInt(BattleshipGameBoard.BOARD_SIZE);
        boolean vertical = RANDOM_GENERATOR.nextBoolean();

        Set<BattleshipFieldPoint> shipFields = new HashSet<>();
        try {
            if (vertical) {
                for (int i = 0; i < length; ++i) {
                    shipFields.add(new BattleshipFieldPoint(otherDimension, expandingDimension + i));
                }
            } else {
                for (int i = 0; i < length; ++i) {
                    shipFields.add(new BattleshipFieldPoint(expandingDimension + i, otherDimension));
                }
            }
        } catch (InvalidPointException e) {
            BattleshipLogger.getBattleshipLogger().warning("generateRandomShip created invalid point");
        }
        return new Ship(shipFields);
    }

    public int getShipLength() {
        return shipFields.size();
    }

    public Set<BattleshipFieldPoint> getShipFields() {
        return shipFields;
    }

    public StrikeCondition hitField(BattleshipPoint field) {
        if (field == null) {
            throw new IllegalArgumentException("null point passed");
        }
        for (var shipField : shipFields) {
            if (shipField.equals(field)) {
                if (!shipField.isHit()) {
                    shipField.hit();
                    if (isSank()) {
                        return StrikeCondition.SANK;
                    }
                    return StrikeCondition.HIT;
                }
                return StrikeCondition.ALREADY_HIT;
            }
        }
        return StrikeCondition.MISSED;
    }
    public boolean isSank() {
        for (var shipField : shipFields) {
            if (!shipField.isHit()) {
                return false;
            }
        }
        return true;
    }

    private static boolean validShipPresentation(Set<BattleshipFieldPoint> shipFields) {
        if (shipFields.size() < MIN_LENGTH || shipFields.size() > MAX_LENGTH) {
            return false;
        }
        int minX;
        int minY;
        int maxX;
        int maxY;
        minX = maxX = shipFields.iterator().next().getX();
        minY = maxY = shipFields.iterator().next().getY();
        for (var field: shipFields) {
            int x = field.getX();
            int y = field.getY();
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        int sizeByX = maxX - minX + 1;
        int sizeByY = maxY - minY + 1;
        if (sizeByY > 1 && sizeByX > 1) {
            return false;
        }
        int length = Math.max(sizeByX, sizeByY);
        return length == shipFields.size();
    }
}
