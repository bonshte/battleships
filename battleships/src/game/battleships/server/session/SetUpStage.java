package game.battleships.server.session;

public enum SetUpStage {

    FIRST_SHIP_LEN2,
    SECOND_SHIP_LEN2,
    THIRD_SHIP_LEN2,
    FORTH_SHIP_LEN2,
    FIRST_SHIP_LEN3,
    SECOND_SHIP_LEN3,
    THIRD_SHIP_LEN3,
    FIRST_SHIP_LEN4,
    SECOND_SHIP_LEN4,
    FIRST_SHIP_LEN5,
    READY;

    private static final int LENGTH_2 = 2;
    private static final int LENGTH_3 = 3;
    private static final int LENGTH_4 = 4;
    private static final int LENGTH_5 = 5;
    private static final int ZERO = 0;

    public int getLength() {
        switch (this) {
            case FIRST_SHIP_LEN2:
            case SECOND_SHIP_LEN2:
            case THIRD_SHIP_LEN2:
            case FORTH_SHIP_LEN2:
                return LENGTH_2;
            case FIRST_SHIP_LEN3:
            case SECOND_SHIP_LEN3:
            case THIRD_SHIP_LEN3:
                return LENGTH_3;
            case FIRST_SHIP_LEN4:
            case SECOND_SHIP_LEN4:
                return LENGTH_4;
            case FIRST_SHIP_LEN5:
                return LENGTH_5;
        }
        return ZERO;
    }

}
