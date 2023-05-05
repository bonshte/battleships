package game.battleships.server.game.board;

import game.battleships.server.exception.ship.point.InvalidPointException;

import java.io.Serializable;

public class BattleshipFieldPoint extends BattleshipPoint implements Serializable {

    private static final long serialVersionUID = 5247273400847876793L;
    private boolean hit;

    public BattleshipFieldPoint(int x, int y) throws InvalidPointException {
        super(x, y);
    }
    public BattleshipFieldPoint(String gameFormat) throws InvalidPointException {
        super(gameFormat);
    }

    public boolean isHit() {
        return hit;
    }

    public void hit() {
        hit = true;
    }
}
