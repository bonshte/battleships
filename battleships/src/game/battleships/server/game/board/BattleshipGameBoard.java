package game.battleships.server.game.board;
import game.battleships.server.game.StrikeStatistic;
import java.io.Serializable;
import java.util.List;

public class BattleshipGameBoard implements  Serializable {
    private static final String BLANK_SPACE = " ";
    private static final String COLUMN = "|";
    private static final String X_AXIS_IDENTIFIERS = "   1 2 3 4 5 6 7 8 9 10";
    private static final String BOARD_TOP_EDGE = "- - - - - - - - - - - - - -";
    private static final char Y_AXIS_START = 'A';
    private static final char WATER_FIELD = '_';
    private static final char HIT_FIELD = 'X';
    private static final char HIT_EMPTY_FIELD = 'O';
    private static final char OWNED_SHIP_FIELD = '*';
    private static final char SANK_SHIP_FIELD = '#';
    public static final int BOARD_SIZE = 10;
    private static final long serialVersionUID = 3008711900319543698L;
    private char[][] board;
    public BattleshipGameBoard(ShipComposition shipComposition) {
        if (shipComposition == null) {
            throw new IllegalArgumentException("null argument passed to method");
        }
        this.board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard(board);
        putShipsOnBoard(board, shipComposition.getShips());
    }
    public BattleshipGameBoard() {
        this.board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard(board);
    }
    public static String visualizeFleet(List<Ship> ships) {
        BattleshipGameBoard gameBoard = new BattleshipGameBoard();
        putShipsOnBoard(gameBoard.board, ships);
        return gameBoard.getVisualizedBoard();
    }
    public void markStrike(StrikeStatistic strikeStatistic, ShipComposition shipComposition) {
        if (strikeStatistic == null) {
            throw new IllegalArgumentException("invalid point to strike passed");
        }
        BattleshipPoint battleshipPointStruck = strikeStatistic.getPointStruck();
        if (strikeStatistic.getResultOfStrike() == StrikeCondition.HIT) {
            board[battleshipPointStruck.getX()][battleshipPointStruck.getY()] = HIT_FIELD;
        } else if (strikeStatistic.getResultOfStrike() == StrikeCondition.MISSED) {
            board[battleshipPointStruck.getX()][battleshipPointStruck.getY()] = HIT_EMPTY_FIELD;
        } else if (strikeStatistic.getResultOfStrike() == StrikeCondition.SANK) {
            Ship shipSank = shipComposition.getShip(battleshipPointStruck);
            markSankShip(board, shipSank);
        }
    }


    private void initializeBoard(char[][] gameBoard) {
        int rows = gameBoard.length;
        int cols = gameBoard[0].length;
        for (int i = 0 ; i < rows; ++i) {
            for (int j = 0 ; j < cols; ++j) {
                gameBoard[i][j] = WATER_FIELD;
            }
        }
    }

    public String getVisualizedBoard() {
        StringBuilder displayedBoard = new StringBuilder();
        displayedBoard.append(X_AXIS_IDENTIFIERS + System.lineSeparator());
        displayedBoard.append(BOARD_TOP_EDGE + System.lineSeparator());
        for (int i = 0 ; i < BOARD_SIZE; ++i) {
            displayedBoard.append((char)(Y_AXIS_START + i) + getVisualizedRow(board[i]) + System.lineSeparator());
        }
        return displayedBoard.toString();
    }
    private String getVisualizedRow(char[] boardRow) {
        StringBuilder row = new StringBuilder(BLANK_SPACE + COLUMN);
        for (int i = 0; i < boardRow.length; ++i) {
            row.append(boardRow[i]);
            row.append(COLUMN);
        }
        return row.toString();
    }
    private static void putShipsOnBoard(char[][] gameBoard, List<Ship> ships) {
        for (var ship : ships) {
            for (var shipField : ship.getShipFields()) {
                gameBoard[shipField.getX()][shipField.getY()] = OWNED_SHIP_FIELD;
            }
        }
    }
    private void markSankShip(char[][] board, Ship ship) {
        for (var shipField : ship.getShipFields()) {
            board[shipField.getX()][shipField.getY()] = SANK_SHIP_FIELD;
        }
    }
}
