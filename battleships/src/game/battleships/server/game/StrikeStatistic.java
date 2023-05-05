package game.battleships.server.game;

import game.battleships.server.game.board.BattleshipPoint;
import game.battleships.server.game.board.StrikeCondition;

public class StrikeStatistic {
    private BattleshipPoint battleshipPointStruck;
    private StrikeCondition resultOfStrike;

    public StrikeStatistic(BattleshipPoint battleshipPoint, StrikeCondition strikeCondition) {
        if (battleshipPoint == null || strikeCondition == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        this.battleshipPointStruck = battleshipPoint;
        this.resultOfStrike = strikeCondition;
    }

    public BattleshipPoint getPointStruck() {
        return battleshipPointStruck;
    }

    public StrikeCondition getResultOfStrike() {
        return resultOfStrike;
    }

    @Override
    public String toString() {
        return "Strike statistic:" + System.lineSeparator() +
                "battleshipPointStruck =" + battleshipPointStruck + System.lineSeparator() +
                " resultOfStrike:" + resultOfStrike;
    }
}
