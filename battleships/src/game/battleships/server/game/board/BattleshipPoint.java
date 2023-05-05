package game.battleships.server.game.board;

import game.battleships.server.exception.ship.point.InvalidPointException;

import java.io.Serializable;
import java.util.Objects;

public class BattleshipPoint implements Serializable {
    private static final long serialVersionUID = -918006246711304698L;
    private static final int LOWER_BOUND_Y_GAME_INT = 1;
    private static final char LOWER_BOUND_Y_GAME = '1';
    private static final char LOWER_BOUND_X_GAME = 'A';
    private static final String UPPER_BOUND_Y_GAME = "10";

    private static final int LOWER_BOUND_REAL = 0;
    private static final int UPPER_BOUND_REAL = 9;
    private static final int GAME_FORMAT_FIELD_MAX_LENGTH = 3;
    private static final int GAME_FORMAT_FIELD_MIN_LENGTH = 2;
    private static final int X_POSITION_DATA = 0;
    private static final int Y_POSITION_DATA = 1;
    private int x;
    private int y;

    public BattleshipPoint(int x, int y) throws InvalidPointException {
        if (!validCoordinates(x, y)) {
            throw new InvalidPointException("point is out of bounds");
        }
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BattleshipPoint(String pointGameFormat) throws InvalidPointException {
        if (pointGameFormat == null) {
            throw new IllegalArgumentException("null passed to constructor");
        }
        if (pointGameFormat.length() == GAME_FORMAT_FIELD_MIN_LENGTH ||
                pointGameFormat.length() == GAME_FORMAT_FIELD_MAX_LENGTH) {
            try {
                int x = pointGameFormat.charAt(X_POSITION_DATA) - LOWER_BOUND_X_GAME;
                int y = Integer.parseInt(pointGameFormat.substring(Y_POSITION_DATA)) - LOWER_BOUND_Y_GAME_INT;
                if (!validCoordinates(x, y)) {
                    throw new InvalidPointException("point is out of bounds");
                }
                this.x = x;
                this.y = y;
                return;
            } catch (NumberFormatException e) {
                throw new InvalidPointException("invalid game format point passed");
            }
        }
        throw new InvalidPointException("invalid game format point passed");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleshipPoint battleshipPoint)) return false;
        return x == battleshipPoint.x && y == battleshipPoint.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toGameFormat() {
        return "[" + transformToGameFormatX() + "," + transformToGameFormatY() + "]";
    }

    @Override
    public String toString() {
        return toGameFormat();
    }

    private char transformToGameFormatX() {
        return (char) (LOWER_BOUND_X_GAME + x);
    }
    private String transformToGameFormatY() {
        if (y == UPPER_BOUND_REAL) {
            return UPPER_BOUND_Y_GAME;
        }
        return ((Character)((char)(LOWER_BOUND_Y_GAME + y))).toString();
    }

    private boolean validCoordinates(int x, int y) {
        return x >= LOWER_BOUND_REAL && y >= LOWER_BOUND_REAL &&
                x <= UPPER_BOUND_REAL && y <= UPPER_BOUND_REAL;
    }

}
